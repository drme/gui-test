package edu.ktu.screenshotanalyser;

import edu.ktu.screenshotanalyser.checks.CheckContext;

public class DummyCheckContextProvider implements ICheckContextProvider {

	@Override
	public CheckContext getContext(String baseDir) {
		
		// TODO read resources and other stuff....
		return new CheckContext();
	}

}
