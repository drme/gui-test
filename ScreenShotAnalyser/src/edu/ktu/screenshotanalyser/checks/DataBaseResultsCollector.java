package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.tools.StatisticsManager;

public class DataBaseResultsCollector extends ResultsCollector
{
	public DataBaseResultsCollector()
	{
		this.testsRunId = this.statisticsManager.startTestsRun("");
	}
	
	@Override
	public synchronized void addFailure(CheckResult result)
	{
		this.statisticsManager.logDetectedDefect(this.testsRunId, result);
	}
	
	private long testsRunId;
	private StatisticsManager statisticsManager = new StatisticsManager();
}
