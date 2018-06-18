package edu.ktu.screenshotanalyser.context;

public class DummyContextProvider implements IContextProvider {

	@Override
	public AppContext getContext(String baseDir) {
		
		// TODO read resources and other stuff....
		return new AppContext();
	}

}
