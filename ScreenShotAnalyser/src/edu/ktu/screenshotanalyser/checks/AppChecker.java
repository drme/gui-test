package edu.ktu.screenshotanalyser.checks;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.DefaultContextProvider;
import edu.ktu.screenshotanalyser.context.State;

public class AppChecker
{
	public void runChecks(File appName, RulesSetChecker checker, ExecutorService exec, ResultsCollector failures) throws IOException, InterruptedException
	{
		DefaultContextProvider contextProvider = new DefaultContextProvider(rootFolder);
		AppContext context = contextProvider.getContext(appName);

		for (State state : context.getStates())
		{
			checker.runStateChecks(state, exec, failures);
		}
		
		checker.runAppChecks(context, exec, failures);
	}	
	
	private static String rootFolder = "d:/_r/";
}
