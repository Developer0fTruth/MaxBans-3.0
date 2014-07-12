package org.maxgamer.maxbans.test;

import org.maxgamer.maxbans.CommandParameters;

public class CommandParamsTest{
	/**
	 * A quick unit test for command parameters.
	 * @param args
	 */
	public static void main(String[] args){
		//Any command inputs you'd like to try
		String[] parameters = new String[]{
			"netherfoam 5 days With a reason!",
			"frizire -s 3 days with reason",
			"  -s lol",
			"name permanently",
			"name -s permanently silently",
			"name",
			"  ",
			"netherfoam 3 with reason",
		};
		
		for(String s : parameters){
			try{
				CommandParameters params = new CommandParameters(s.split("\\ "));
				
				System.out.println(s + " -> silent: " + params.isSilent() + ", target: " + params.getTarget() + ", duration: " + params.getDuration() + ", reason: " + params.getReason());
			}
			catch(IllegalArgumentException e){
				System.out.println(s + " -> (Error) " + e.getMessage()); //Our command was invalid.
			}
		}
	}
}