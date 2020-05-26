package edu.ktu.screenshotanalyser.checks;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import edu.ktu.screenshotanalyser.Settings;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.DefaultContextProvider;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.StatisticsManager;

public class AppChecker
{
	//static int a= 0;
	
	public void runChecks(File appName, RulesSetChecker checker, ExecutorService exec, ResultsCollector failures) throws IOException, InterruptedException
	{
		DefaultContextProvider contextProvider = new DefaultContextProvider(Settings.appImagesFolder);
		AppContext context = contextProvider.getContext(appName);

		if (null != context.getApkFile())
		{
//			SystemUtils.logMessage("e:/files.csv", "" + (a++) + ";" + context.getName().trim() + ";" + context.getPackage()+ ";" + context.getVersion() + ";" + appName.getAbsolutePath());
//			SystemUtils.logMessage("e:/files.txt", "| " + (a) + " | " + context.getName().trim() + " | " + context.getPackage()+ " | " + context.getVersion() + " |");
		}
		
		//new StatisticsManager().saveAppInfo(context);
	
		for (State state : context.getStates())
		{
			checker.runStateChecks(state, exec, failures);
		}
		
		checker.runAppChecks(context, exec, failures);
	}	
}
