package edu.ktu.screenshotanalyser.rules.checkers;

import edu.ktu.screenshotanalyser.context.AppContext;

@Deprecated
public interface IRuleChecker
{
	CheckResult[] analyze(CheckRequest request, AppContext context);
}
