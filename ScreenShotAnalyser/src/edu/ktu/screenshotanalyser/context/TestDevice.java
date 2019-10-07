package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Test device that app was executed on. 
 */
public class TestDevice
{
	public final String name = "";
	public int screenWidth = 0;
	public int screenHeight = 0;
	private int screenDpi = 0;
	public final String locale = "";
	public final File folder;
	
	public TestDevice(File folder)
	{
		this.folder = folder;
		
		try (InputStream input = new FileInputStream(folder.getAbsoluteFile() + "/dev.txt"))
		{
      Properties properties = new Properties();

      properties.load(input);

      this.screenDpi = Integer.parseInt(properties.getProperty("dpi"));
      this.screenWidth = Integer.parseInt(properties.getProperty("width"));
      this.screenHeight = Integer.parseInt(properties.getProperty("height"));
      
  } catch (IOException ex) {
      ex.printStackTrace();
  }	}
	
	public File getFolder()
	{
		return this.folder;
	}

	/**
	 * Physical size in mm
	 */
	public double getPhysicalSize(int pixels)
	{
		return (double)pixels * 25.4 / (double)this.screenDpi; 
	}
}
