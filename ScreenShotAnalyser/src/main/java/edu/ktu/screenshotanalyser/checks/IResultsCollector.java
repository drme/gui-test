package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.context.State;

/**
 * Collects all analysis results in a thread safe manner.
 */
public interface IResultsCollector
{
	public void finishRun();
	public void finishedState(State state, StateCheckResults results);
}
