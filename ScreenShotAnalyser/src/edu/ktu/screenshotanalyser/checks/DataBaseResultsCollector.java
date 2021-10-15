package edu.ktu.screenshotanalyser.checks;

import java.util.HashSet;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.StatisticsManager;

public class DataBaseResultsCollector extends ResultsCollector
{
	public DataBaseResultsCollector(String name, boolean resume)
	{
		this.testsRunId = this.statisticsManager.startTestsRun(name, resume);
	}
	
	@Override
	public synchronized void addFailure(CheckResult result)
	{
		super.addFailure(result);
		
		this.statisticsManager.logDetectedDefect(this.testsRunId, result);
	}

	@Override
	public boolean wasChecked(State state)
	{
		var fileName = state.getImageFile().getAbsolutePath();

		if (fileName.startsWith(Settings.appImagesFolder.getAbsolutePath()))
		{
			fileName = fileName.substring(Settings.appImagesFolder.getAbsolutePath().length() + 1);
		}
		
		synchronized (this)
		{
			if (null == this.checkedStates)
			{
				this.checkedStates = new HashSet<String>(this.statisticsManager.getCheckedStates(this.testsRunId));
			}
		}
		
		return this.checkedStates.contains(fileName);
		
		
		//return this.statisticsManager.wasChecked(this.testsRunId, state);
	}

	@Override
	public void finishRun()
	{
		this.statisticsManager.finishRun(this.testsRunId);
	}
	
	private long testsRunId;
	private StatisticsManager statisticsManager = new StatisticsManager();
	private HashSet<String> checkedStates = null;
}
