package edu.ktu.screenshotanalyser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import edu.ktu.screenshotanalyser.checks.AppChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.checks.RulesSetChecker;
import edu.ktu.screenshotanalyser.checks.experiments.ClippedTextCheck;

public class StartUp
{
	static
	{
		//nu.pattern.OpenCV.loadShared();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);				
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		runExperiments();
	}
	
	private static void runExperiments() throws IOException, InterruptedException
	{
		ResultsCollector failures = new ResultsCollector();
		RulesSetChecker checker = new RulesSetChecker();

		//checker.addRule(new GrammarCheck());
		//checker.addRule(new UnreadableTextCheck());
		//checker.addRule(new MissingTextCheck());
		//checker.addRule(new MissingTranslationCheck());
		//checker.addRule(new TooHardToUnderstandCheck());
		
		checker.addRule(new ClippedTextCheck());
		
		File[] apps = new File(Config.appsFolder).listFiles(p -> p.isDirectory());
		
		for (File app : apps)
		{
			int threads = Runtime.getRuntime().availableProcessors();
		
			ExecutorService exec = Executors.newFixedThreadPool(threads);		
		
			runChecks(app, exec, checker, failures);

			exec.shutdown();
			exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		
			//break;
		}
	}
	
	@SuppressWarnings("unused")
	private static void runChecks(File appName) throws IOException, InterruptedException
	{
		int threads = Runtime.getRuntime().availableProcessors();
		
		ExecutorService exec = Executors.newFixedThreadPool(threads);		
		
		//runChecks(appName, exec);
		
		exec.shutdown();
		exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);		
	}
	
	private static void runChecks(File appName, ExecutorService exec, RulesSetChecker rules, ResultsCollector failures) throws IOException, InterruptedException
	{
		AppChecker appChecker = new AppChecker();
		
		appChecker.runChecks(appName, rules, exec, failures);
	}
}
