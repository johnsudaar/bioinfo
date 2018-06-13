package manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import Bdd.Bdd;
import Parser.Parser;
import configuration.Configuration;
import exceptions.ScannerNullException;
import tree.Organism;
import ui.UIManager;

public class RepliconParserManager {

	private Retryer<Bdd> retryer;
	
	private Organism organism;
	private String replicon;
	private Bdd mainDb;
	
	public Callable<Bdd> parser = new Callable<Bdd>(){
		public Bdd call() throws MalformedURLException, IOException, ScannerNullException{
			Bdd db = new Bdd();
			String url = "";
			try{
				url = Configuration.GEN_DOWNLOAD_URL.replaceAll("<ID>", organism.getReplicons().get(replicon));
				Scanner sc = new Scanner(Resources.asByteSource(new URL(url)).openBufferedStream());
				sc.useDelimiter("\n");
				Parser parser = new Parser(db, sc);
				if (Configuration.STORE_DATA) {
					File f = new File(organism.getPath());
					if (f.isDirectory()) {
						parser.parse(replicon, organism, new FileOutputStream(organism.getPath()+Configuration.FOLDER_SEPARATOR+organism.getName()+"_"+replicon));
					}
					else {
						parser.parse(replicon, organism, null);
					}				
				}
				else {
					parser.parse(replicon, organism, null);
				}
			}catch(Exception e){
				System.out.println(url);
				e.printStackTrace();
			}
			return db;
		}
	};
	
	public RepliconParserManager(Organism o, String r, Bdd db){
		this.organism = o;
		this.replicon = r;
		this.mainDb = db;
		
		this.retryer = RetryerBuilder.<Bdd>newBuilder()
				.retryIfException()
				.retryIfRuntimeException()
				.withStopStrategy(StopStrategies.stopAfterAttempt(Configuration.NET_MAX_DOWNLOAD_TRIES))
				.withWaitStrategy(WaitStrategies.fibonacciWait())
				.build();
	}
	
	public void run() {
		UIManager.log("[RepliconParserManager] Start downloading "+this.organism.getName()+ " replicon: "+replicon);
		Bdd db = null;
		try {
			db = retryer.call(this.parser);
		} catch (Exception e) {
			UIManager.log("[RepliconParserManager] Unable to download "+this.organism.getName()+ " replicon: "+replicon);
		}
		if(db == null){
			UIManager.log("[RepliconParserManager] Unable to download "+this.organism.getName()+ " replicon: "+replicon);
		}
		this.mainDb.fusionBase(db);
		organism.addProcessedReplicon(replicon);
		UIManager.log("[RepliconParserManager] Finished downloading "+this.organism.getName()+ " replicon: "+replicon);
		UIManager.addProgress(1);
	}
}
