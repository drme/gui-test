package edu.ktu.screenshotanalyser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import edu.ktu.screenshotanalyser.checks.AppChecker;
import edu.ktu.screenshotanalyser.checks.DataBaseResultsCollector;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.checks.RulesSetChecker;
import edu.ktu.screenshotanalyser.checks.experiments.ClippedControlCheck;
import edu.ktu.screenshotanalyser.checks.experiments.ClippedTextCheck;
import edu.ktu.screenshotanalyser.checks.experiments.GrammarCheck;
import edu.ktu.screenshotanalyser.checks.experiments.MissingTextCheck;
import edu.ktu.screenshotanalyser.checks.experiments.MissingTranslationCheck;
import edu.ktu.screenshotanalyser.checks.experiments.MixedLanguagesAppCheck;
import edu.ktu.screenshotanalyser.checks.experiments.MixedLanguagesStateCheck;
import edu.ktu.screenshotanalyser.checks.experiments.ObscuredControlCheck;
import edu.ktu.screenshotanalyser.checks.experiments.ObscuredTextCheck;
import edu.ktu.screenshotanalyser.checks.experiments.OffensiveMessagesCheck;
import edu.ktu.screenshotanalyser.checks.experiments.UnalignedControlsCheck;
import edu.ktu.screenshotanalyser.checks.experiments.UnlocalizedIconsCheck;
import edu.ktu.screenshotanalyser.checks.experiments.WrongEncodingCheck;
import edu.ktu.screenshotanalyser.checks.experiments.WrongLanguageCheck;
import edu.ktu.screenshotanalyser.tools.Settings;
import net.sourceforge.tess4j.TessAPI1;

public class StartUp
{
	static
	{
		//nu.pattern.OpenCV.loadShared();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);				
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		enableLogs();
		
		runExperiments();
	}
	
	private static void runExperiments() throws IOException, InterruptedException
	{
		var failures = new DataBaseResultsCollector("sdssss", false);
		var checker = new RulesSetChecker();

		//checker.addRule(new UnalignedControlsCheck());    +
		//checker.addRule(new ClippedControlCheck());       +
		//checker.addRule(new ObscuredControlCheck());      +
		//checker.addRule(new WrongLanguageCheck());        +
		//checker.addRule(new ObscuredTextCheck());         +
		//checker.addRule(new GrammarCheck());              +
		//checker.addRule(new WrongEncodingCheck());        +
		//checker.addRule(new ClippedTextCheck());          +
		//checker.addRule(new UnlocalizedIconsCheck());     +
		//checker.addRule(new MissingTranslationCheck());   +
		//checker.addRule(new MixedLanguagesStateCheck());  +
		//checker.addRule(new MixedLanguagesAppCheck());    +
		//checker.addRule(new OffensiveMessagesCheck());    + 
		//checker.addRule(new UnreadableTextCheck());       +
		//checker.addRule(new TooHardToUnderstandCheck());  +
		checker.addRule(new MissingTextCheck());          //+
		
		var apps = new File(Settings.appsFolder).listFiles(p -> p.isDirectory());
		var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());		

		for (var app : apps)
		{
			runChecks(app, exec, checker, failures);
		}
		
		exec.shutdown();
		exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		
		failures.finishRun();
	}
	
	/*
	@SuppressWarnings("unused")
	private static void runChecks(File appName) throws IOException, InterruptedException
	{
		int threads = Runtime.getRuntime().availableProcessors();
		
		ExecutorService exec = Executors.newFixedThreadPool(threads);		
		
		//runChecks(appName, exec);
		
		exec.shutdown();
		exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);		
	}*/
	
	private static void runChecks(File appName, ExecutorService exec, RulesSetChecker rules, ResultsCollector failures) throws IOException, InterruptedException
	{
		var appChecker = new AppChecker();
		
		appChecker.runChecks(appName, rules, exec, failures);
	}
	
	private static void enableLogs()
	{
		var logContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		var log = logContext.getLogger("com.jayway.jsonpath.internal.path.CompiledPath");

		log.setLevel(Level.ERROR);
	}
}
