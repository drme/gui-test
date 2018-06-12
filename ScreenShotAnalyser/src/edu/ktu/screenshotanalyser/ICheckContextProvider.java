package edu.ktu.screenshotanalyser;

import edu.ktu.screenshotanalyser.checks.CheckContext;

public interface ICheckContextProvider {
	CheckContext getContext(String baseDir);
}
