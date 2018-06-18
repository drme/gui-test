package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.context.AppContext;

public interface ICheck {
	CheckResult[] analyze(CheckRequest request, AppContext context);
}
