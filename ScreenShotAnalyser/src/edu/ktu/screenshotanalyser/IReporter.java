package edu.ktu.screenshotanalyser;

import edu.ktu.screenshotanalyser.FoundTextsReporter.ReportRequest;

public interface IReporter {
	void save(ReportRequest request) throws Throwable;
}
