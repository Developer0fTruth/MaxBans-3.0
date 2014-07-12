package org.maxgamer.maxbans;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Eloquent{
	/** The name of the table this comes from */
	private String tableName;
	/** The name of the column that contains the unique ID */
	private String idField;
	/** The object which is the unique id for this table. Could be string/integer/etc */
	private Object id;
	
	/**
	 * Constructs a new Eloquent class. This is an easy way of
	 * reading data from a database and updating records while
	 * avoiding hard querying. The class is not optimal when
	 * calling load() many times, but is optimal when using
	 * load(ResultSet rs).
	 * @param table The table name to pull the record from
	 * @param idField The name of the column with the unique ID
	 * @param id The unique ID of this. Could be int/string.
	 */
	public Eloquent(String table, String idField, Object id){
		this.tableName = table;
		this.idField = idField;
		this.id = id;
	}
	
	/**
	 * Fetches the unique ID for this eloquent. This is the
	 * ID supplied in the constructor. You should override
	 * this to specify what type of object the ID is.
	 * @return The unique ID
	 */
	public Object getId(){
		return this.id;
	}
	
	/**
	 * Checks if the unique ID exists in the database.
	 * @param con The connection
	 * @return true if it does, false if it does not.
	 * @throws SQLException if there was an error reaching the database
	 */
	public boolean exists(Connection con) throws SQLException{
		PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tableName + " WHERE " + idField + " = ? LIMIT 0,1");
		ps.setObject(1, id);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()){
			load(rs);
			rs.close();
			return true;
		}
		//Failure.
		rs.close(); 
		return false;
	}
	
	/**
	 * Loads this element from the database, using it's unique id.
	 * @param con The connection to load from
	 * @throws SQLException If the database does not contain a key or database is unavailable
	 */
	public void load(Connection con) throws SQLException{
		PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tableName + " WHERE " + idField + " = ? LIMIT 0,1");
		ps.setObject(1, id);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()){
			load(rs);
			rs.close();
			return;
		}
		//Failure.
		rs.close();
		throw new SQLException("The class could not be loaded from database, as no result was given by SELECT * FROM " + tableName + " WHERE " + idField + " = " + id);
	}
	
	/**
	 * Loads the record from the given result. This is optimal
	 * if you're loading in many Eloquent objects.
	 * @param rs The result set
	 * @throws SQLException If something went wrong
	 */
	public void load(ResultSet rs) throws SQLException{
		int cols = rs.getMetaData().getColumnCount();
		for(int i = 1; i <= cols; i++){
			Class<?> c = this.getClass();
			
			while(c != null && c != Object.class){
				try{
					Field f = c.getDeclaredField(rs.getMetaData().getColumnLabel(i));
					boolean access = f.isAccessible();
					
					if(access == false) f.setAccessible(true);
					try {
						if(f.getName().equals(this.idField)){
							this.id = rs.getObject(i);
						}
						if(f.getType() == int.class){
							f.setInt(this, rs.getInt(i));
						}
						else if(f.getType() == byte.class){
							f.setByte(this, rs.getByte(i));
						}
						else if(f.getType() == short.class){
							f.setShort(this, rs.getShort(i));
						}
						else if(f.getType() == long.class){
							f.setLong(this, rs.getLong(i));
						}
						else if(f.getType() == float.class){
							f.setFloat(this, rs.getFloat(i));
						}
						else if(f.getType() == double.class){
							f.setDouble(this, rs.getDouble(i));
						}
						else if(f.getType() == boolean.class){
							f.setBoolean(this, rs.getBoolean(i));
						}
						else if(f.getType() == String.class){
							f.set(this, rs.getString(i));
						}
						else if(f.getType() == char.class){
							try{
								f.setChar(this, rs.getString(i).charAt(0));
							}
							catch(IndexOutOfBoundsException e){
								//No string data?
								f.setChar(this, (char) 0x00);
							}
						}
						else{
							System.out.println("Only byte, short, int, long, float, double, and String classes may be loaded by a datamodel from databases. Given " + f.getName() + " which is a " + f.getType());
						}
					}
					catch(IllegalArgumentException e){
						System.out.println("Type mismatch. Field " + f.getName() + " is " + f.getType() + " but could not take that type from the database.");
						e.printStackTrace();
					}
					catch(SQLException e){
						System.out.println("Type mismatch. Field " + f.getName() + " is " + f.getType() + " but could not take that type from the database.");
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if(access == false) f.setAccessible(false);
				}
				catch(NoSuchFieldException e){
					//System.out.println("Query returned field " + rs.getMetaData().getColumnLabel(i) + " but class has no such field.");
				}
				c = c.getSuperclass();
			}
			
		}
		onLoad(rs);
	}
	
	public void onLoad(ResultSet rs) throws SQLException{}
	
	/**
	 * Removes this object from the database.
	 * @param con The connection to the database.
	 * @throws SQLException Usually not. If there was an issue contacting the database.
	 */
	public void delete(Connection con) throws SQLException{
		PreparedStatement ps = con.prepareStatement("DELETE FROM " + this.tableName + " WHERE " + this.idField + " = ? LIMIT 1");
		ps.setObject(1, this.id);
		ps.execute();
	}
	
	/**
	 * Adds this eloquent to the database.
	 * @param con The database connection
	 * @throws SQLException If the database is unreachable.
	 */
	public void insert(Connection con) throws SQLException{
		ArrayList<String> columns = getColumns(con);
		columns.remove(idField);
		
		StringBuilder sb = new StringBuilder("INSERT INTO " + tableName + " (" + idField + ", ");
		
		for(int i = 0; i < columns.size() - 1; i++){
			sb.append(columns.get(i) + ", ");
		}
		sb.append(columns.get(columns.size() - 1) + ") VALUES (?, ");
		
		for(int i = 0; i < columns.size() - 1; i++){
			sb.append("?, ");
		}
		sb.append("?)");
		
		PreparedStatement ps = con.prepareStatement(sb.toString());
		
		ps.setObject(1, this.id);
		int i;
		for(i = 0; i < columns.size(); i++){
			ps.setObject(i + 2, getField(columns.get(i)));
		}
		
		ps.execute();
		ps.close();
	}
	
	/**
	 * UPDATES this record in the database, assuming it exists.
	 * @param con The connection
	 * @throws SQLException If the database was unavailable.
	 */
	public void update(Connection con) throws SQLException{
		StringBuilder sb = new StringBuilder("UPDATE " + tableName + " SET ");

		ArrayList<String> columns = getColumns(con);
		columns.remove(idField);
		
		for(int i = 0; i < columns.size() - 1; i++){
			sb.append(columns.get(i) + " = ?, ");
		}
		sb.append(columns.get(columns.size() - 1) + " = ? WHERE " + this.idField + " = ? LIMIT 1");
		
		
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int i;
		for(i = 0; i < columns.size(); i++){
			ps.setObject(i + 1, getField(columns.get(i)));
		}
		ps.setObject(i + 1, this.id);
		
		ps.execute();
		ps.close();
	}
	
	@Override
	public boolean equals(Object o){
		if(this.getClass().isInstance(o) || o.getClass().isInstance(this)){ //This extends that, or that extends this.
			Eloquent p = (Eloquent) o;
			//getId() is required to be unique, this is a cheap way of doing it.
			if(p.getId().equals(getId())) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		//Required to be unique
		return getId().hashCode();
	}
	
	//Fetches all columns from the database
	private ArrayList<String> getColumns(Connection con) throws SQLException{
		ResultSet rs = con.getMetaData().getColumns(null, null, this.tableName, null);
		ArrayList<String> columns = new ArrayList<String>(rs.getFetchSize());
		while(rs.next()){
			columns.add(rs.getString(4));
		}
		return columns;
	}
	
	//Gets the value of the given field or returns null if unavailable.
	private Object getField(String name){
		try {
			Field f = this.getClass().getDeclaredField(name);
			boolean access = f.isAccessible();
			
			if(access == false) f.setAccessible(true);
			Object o = f.get(this);
			if(access == false) f.setAccessible(false);
			
			return o;
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
		catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}