package org.maxgamer.maxbans.database;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseWatcher{
	private Database db;
	private ArrayList<DatabaseTask> tasks = new ArrayList<DatabaseTask>();
	private Thread thread = new Thread(){
		@Override
		public void run(){
			try {
				synchronized(tasks){
					tasks.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ArrayList<DatabaseTask> list;
			synchronized(tasks){
				list = new ArrayList<DatabaseTask>(tasks);
			}
			
			for(DatabaseTask task : list){
				try{
					task.execute(db);
				}
				catch(SQLException e){
					e.printStackTrace();
					System.out.println("Error executing database task.");
				}
			}
		}
	};
	
	public DatabaseWatcher(Database db){
		this.db = db;
		thread.start();
	}
	
	public void queue(DatabaseTask task){
		synchronized(tasks){
			tasks.add(task);
		}
		tasks.notify();
	}
	
	public static interface DatabaseTask{
		public void execute(Database db) throws SQLException;
	}
}