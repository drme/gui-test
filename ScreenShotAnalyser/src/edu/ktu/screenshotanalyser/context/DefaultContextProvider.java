package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import edu.ktu.screenshotanalyser.SystemContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

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
		final AppContext context = new AppContext(appFolder, this.dataFolder, this.testDevices, this.systemContext);
		
		

		
		return context;
	}





}
