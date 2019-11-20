package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.List;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;

/**
 * Collects all analysis results in a thread safe manner.
 */
public class ResultsCollector
{
	public synchronized void addFailure(CheckResult result)
	{
		//this.failures.add(result);
	}
	
	private List<CheckResult> failures = new ArrayList<>();	
}
