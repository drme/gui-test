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
	
	public DefaultContextProvider(File dataFolder) throws IOException
	{
		this.dataFolder = dataFolder;
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


/*


package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

public class DefaultContextProvider implements IContextProvider {
	private final IResourcesProvider provider = new RegexpBasedResourceProvider();

	

	@Override
	public AppContext getContext(String baseDir) {

		final AppContext context = new AppContext();
		final File[] values = new File(baseDir, "res").listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().startsWith("values");

			}
		});

		for (File f : values) {
			
			String[] temp = f.getName().split("-", 2);

			String lang = "default";
			if (temp.length >= 2) {
				lang = temp[1];
			}

			File[] resourceFiles = f.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.getName().startsWith("strings.xml");
				}

			});
			for (final File res : resourceFiles) {
				try {
					Resources resources = this.provider.getResource(StringUtils.toEncodedString(Files.readAllBytes(res.toPath()), StandardCharsets.UTF_8));
					List<ResourceText> textResources = Stream.of(resources.getString()).map(x -> {

						return new ResourceText(x.getName(), x.getValue(), res.getAbsolutePath());
					}).collect(Collectors.toList());

					List<String> extractedKeys = Stream.of(resources.getString()).map(x -> {
						return x.getName();
					}).collect(Collectors.toList());
					context.getResources().put(lang, textResources);

					List<String> missingKeys = extractedKeys.stream().filter(i -> !context.getKeys().contains(i))
							.collect(Collectors.toList());
					context.getKeys().addAll(missingKeys);
				} catch (Throwable e) {
System.out.println("Can't read file: "+res.getAbsolutePath());
					e.printStackTrace();
				}
			}

		}

		return context;
	}



	@JacksonXmlRootElement(localName = "resources")
	public static class Resources {

		@JacksonXmlElementWrapper(localName = "string", useWrapping = false)
		private ResourceDao[] string = new ResourceDao[0];
		
		@JacksonXmlElementWrapper(localName = "item", useWrapping = false)
		private Object[] item = new Object[0];
		public Object[] getItem() {
			return item;
		}

		public void setItem(Object[] item) {
			this.item = item;
		}

		public ResourceDao[] getString() {
			return string;
		}

		public void setString(ResourceDao[] string) {
			this.string = string;
		}

	}

	@JacksonXmlRootElement(localName = "string")
	public static class ResourceDao {

		@JacksonXmlCData
		@JacksonXmlText
		private String value;
		
		
		@Override
		public String toString() {
			return "ResourceDao [value=" + value + ", name=" + name + "]";
		}

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void setName(String name) {
			this.name = name;
		}

	

		@JacksonXmlProperty(isAttribute = true, localName = "name")
		private String name;

	}

}



*/