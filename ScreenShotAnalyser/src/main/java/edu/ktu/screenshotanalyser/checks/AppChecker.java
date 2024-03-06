package edu.ktu.screenshotanalyser.checks;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import edu.ktu.screenshotanalyser.context.DefaultContextProvider;
import edu.ktu.screenshotanalyser.database.DataBase.Application;

public class AppChecker
{
	public AppChecker(DefaultContextProvider contextProvider)
	{
		this.contextProvider = contextProvider;
	}
	
	public void runChecks(Application app, RulesSetChecker checker, ExecutorService exec, IResultsCollector failures) throws IOException
	{
		var context = this.contextProvider.getContext(app);

		for (var state : context.getStates())
		{
			checker.runStateChecks(state, exec, failures);
		}
		
		var appFailures = new AppCheckResults(app);
		
		checker.runAppChecks(context, exec, appFailures);
		
		// TODO: log appFailures...
	}	

	private final DefaultContextProvider contextProvider;
}
