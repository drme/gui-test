package edu.ktu.screenshotanalyser.context;

import edu.ktu.screenshotanalyser.context.DefaultContextProvider.Resources;

public interface IResourcesProvider {
	Resources getResource(String contents) throws Throwable;
}
