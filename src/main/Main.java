package main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import configuration.Configuration;
import tree.*;
import ui.UIManager;
public class Main {

	public static void main(String[] args){
		
		parseArgs(args);

		UIManager.startPreloading();
		Tree tree = TreeManager.construct();
		UIManager.startMainProcess(tree);
	}
	
	public static void parseArgs(String[] args){
		Option help = new Option("h","help",false, "print this message");
		Option nogui = new Option("g", "no-gui",false, "Text mode only");
		Option path = new Option("p","path", true, "Path to the ressources");
		Option windows = new Option("w", "windows", false, "Use windows style path");
		Option threads = new Option("t", "threads", true, "Specify how many threads will be launched");
		
		Options options = new Options();
		options.addOption(help);
		options.addOption(nogui);
		options.addOption(path);
		options.addOption(windows);
		options.addOption(threads);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		boolean showHelp = false;
		try{
			line = parser.parse(options, args);
		}catch(ParseException exp){
			showHelp = true;
		}
		
		if(! showHelp && line.hasOption("help")){
			showHelp = true;
		}
		
		if(showHelp){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("BioInfo", options );
			System.exit(1);
		}
		
		if(line.hasOption("no-gui")){
			Configuration.USE_GUI = false;
		}
		
		if(line.hasOption("path")){
			if(line.getOptionValue("path") != null){
				Configuration.BASE_FOLDER = line.getOptionValue("path");
			}
		}
		
		if(line.hasOption("threads")){
			int a = Integer.parseInt(line.getOptionValue("threads"));
			if(a > 0){
				Configuration.THREADS_NUMBER = a;
			}
		}
		
		if(line.hasOption("windows")){
			Configuration.FOLDER_SEPARATOR = "\\";
		}
		
	}
}