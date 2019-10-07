package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.context.State;

/**
 * Checks one application's screenshot for defects.
 */
public interface IStateRuleChecker
{
	void analyze(State state, ResultsCollector failures);
}
