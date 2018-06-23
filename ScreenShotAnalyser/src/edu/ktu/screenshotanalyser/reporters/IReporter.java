package edu.ktu.screenshotanalyser.reporters;

import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter.AppExtractInfo;

public interface IReporter {
	void save(AppExtractInfo request) throws Throwable;
}
