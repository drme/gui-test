package edu.ktu.screenshotanalyser.context;

import java.io.File;

public class Layout extends InterfaceDocument
{
	public Layout(File layoutFile)
	{
		super(layoutFile);
	}
	
	public File getLayoutFile()
	{
		return this.file;
	}	
}
