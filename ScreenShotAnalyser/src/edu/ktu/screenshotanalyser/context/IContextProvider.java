package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.IOException;

public interface IContextProvider
{
	AppContext getContext(File appFolder) throws IOException;
}
