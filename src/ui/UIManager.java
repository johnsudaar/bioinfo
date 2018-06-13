package ui;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tree.Tree;

import configuration.Configuration;

public class UIManager {
	
	private static int max_progress;
	private static int current_progress = 0;
	private static Lock progressLock = new ReentrantLock();
	private static Lock logLock = new ReentrantLock();

	
	private static UIConsole console;
	private static UIGraphics graphics;
	
	private static void check(){
		if(UIManager.console == null && ! Configuration.USE_GUI){
			UIManager.console = new UIConsole();
		}
		if(UIManager.graphics == null && Configuration.USE_GUI){
			UIManager.graphics = new UIGraphics();
		}
	}
	
	public static void setMaxProgress(int p){
		UIManager.max_progress = p;
	}
	
	public static void addProgress(int n){
		progressLock.lock();
		UIManager.current_progress += n;
		UIManager.setProgress(100.0f * (double) UIManager.current_progress / (double) UIManager.max_progress);
		progressLock.unlock();
	}
	
	public static void log(String message){
		logLock.lock();
		UIManager.check();
		if(Configuration.USE_GUI){
			UIManager.graphics.log(message);
		}else {
			UIManager.console.log(message);
		}
		logLock.unlock();
	}
	
	public static void setProgress(double progress){
		UIManager.check();
		if(Configuration.USE_GUI){
			UIManager.graphics.setProgress(progress, UIManager.current_progress, UIManager.max_progress);
		} else {
			UIManager.console.setProgress(progress);
		}
	}
	
	public static void startPreloading(){
		if(Configuration.USE_GUI){
			UIManager.check();
			UIManager.graphics.startPreloader();
		}
	}
	
	public static void startMainProcess(Tree t){
		UIManager.current_progress = 0;
		if(Configuration.USE_GUI){
			UIManager.check();
			UIManager.graphics.startMainProcess(t);
		} else {
			UIManager.check();
			UIManager.console.launchProcess(t);
		}
	}
	
	public static void launchProcess(ArrayList<InfoNode> nodes){
		if(Configuration.USE_GUI){
			UIManager.check();
			UIManager.graphics.launchProcess(nodes);
		}
	}

}
