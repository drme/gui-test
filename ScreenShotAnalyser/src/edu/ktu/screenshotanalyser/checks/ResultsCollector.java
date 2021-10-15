package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.context.State;

/**
 * Collects all analysis results in a thread safe manner.
 */
public abstract class ResultsCollector
{
	public synchronized void addFailure(CheckResult result)
	{
		if ((result.getMessage() != null) && (result.getMessage().length() > 0))
		{
			System.out.println(result.getMessage());
		}
	}

	public abstract boolean wasChecked(State state);

	public abstract void finishRun();
}
