package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.context.AppContext;

/**
 * Checks all application for defects in one go.
 */
public interface IAppRuleChecker
{
	void analyze(AppContext appContext, ResultsCollector failures);
}
