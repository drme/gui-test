package edu.ktu.screenshotanalyser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import edu.ktu.screenshotanalyser.checks.AppChecker;
import edu.ktu.screenshotanalyser.checks.DataBaseResultsCollector;
import edu.ktu.screenshotanalyser.checks.IResultsCollector;
import edu.ktu.screenshotanalyser.checks.RulesSetChecker;
import edu.ktu.screenshotanalyser.checks.experiments.BlurredImagesCheck;
import edu.ktu.screenshotanalyser.context.DefaultContextProvider;
import edu.ktu.screenshotanalyser.database.DataBase;
import edu.ktu.screenshotanalyser.database.DataBase.Application;
import edu.ktu.screenshotanalyser.tools.Settings;

public class StartUp
{
	static
	{
		//nu.pattern.OpenCV.loadShared();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);				
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, SQLException
	{
		enableLogs();
		
		runExperiments();
	}
	
	private static void runExperiments() throws IOException, InterruptedException, SQLException
	{
		defaultContextProvider = new DefaultContextProvider(Settings.appImagesFolder);
		
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
		//checker.addRule(new MissingTextCheck());          +
		
		
//		checker.addRule(new BadScalingCheck());
		checker.addRule(new BlurredImagesCheck());
//		checker.addRule(new ClashingBackgroundCheck());
		
		var failures = new DataBaseResultsCollector(checker.buildRunName(), false);
		var dataBase = new DataBase();
		var apps = dataBase.getApplications();
		var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());		
//		var exec = Executors.newFixedThreadPool(1);		

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
	
	private static void runChecks(Application app, ExecutorService exec, RulesSetChecker rules, IResultsCollector failures) throws IOException, InterruptedException
	{
		var appChecker = new AppChecker(defaultContextProvider);
		
		appChecker.runChecks(app, rules, exec, failures);
	}
	
	private static void enableLogs()
	{
		var logContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		
		setLogLevelToError(logContext, "com.jayway.jsonpath.internal.path.CompiledPath");
		setLogLevelToError(logContext, "com.zaxxer.hikari.HikariConfig");
		setLogLevelToError(logContext, "com.zaxxer.hikari.pool.HikariPool");
		setLogLevelToError(logContext, "com.zaxxer.hikari.HikariDataSource");
		setLogLevelToError(logContext, "com.zaxxer.hikari.util.DriverDataSource");
	}
	
	private static void setLogLevelToError(LoggerContext logContext, String logger)
	{
		var log = logContext.getLogger(logger);

		log.setLevel(Level.ERROR);
	}
	
	private static DefaultContextProvider defaultContextProvider;
}
