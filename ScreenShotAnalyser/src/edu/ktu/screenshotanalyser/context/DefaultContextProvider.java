package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.ktu.screenshotanalyser.SystemContext;

public class DefaultContextProvider implements IContextProvider
{
	private final File dataFolder;
	private final List<TestDevice> testDevices = new ArrayList<>();
	private final SystemContext systemContext;
	
	public DefaultContextProvider(String dataFolder) throws IOException
	{
		this.dataFolder = new File(dataFolder);
		this.systemContext = new SystemContext();
		
		for (File testDeviceFolder : this.dataFolder.listFiles((pathname) -> pathname.isDirectory()))
		{
			if (new File(testDeviceFolder.getAbsoluteFile() + "/dev.txt").exists())
			{
				this.testDevices.add(new TestDevice(testDeviceFolder));
			}
		}
	}

	@Override
	public AppContext getContext(File appFolder) throws IOException
	{
		return new AppContext(appFolder, this.dataFolder, this.testDevices, this.systemContext);
	}
}
