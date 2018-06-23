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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

public class DefaultContextProvider implements IContextProvider {
	private final ObjectMapper mapper;

	public DefaultContextProvider() {
		final JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		this.mapper = new XmlMapper(xmlModule);
	}

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
					Resources resources = read(res, mapper);
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
					e.printStackTrace();
				}
			}

		}

		return context;
	}

	private Resources read(File file, ObjectMapper mapper) throws Throwable {
		final String f = StringUtils.toEncodedString(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		return mapper.readValue(f, Resources.class);
	}

	@JacksonXmlRootElement(localName = "resources")
	public static class Resources {

		@JacksonXmlElementWrapper(localName = "string", useWrapping = false)
		private ResourceDao[] string = new ResourceDao[0];

		public ResourceDao[] getString() {
			return string;
		}

		public void setString(ResourceDao[] string) {
			this.string = string;
		}

	}

	@JacksonXmlRootElement(localName = "string")
	public static class ResourceDao {

		@JacksonXmlText
		private String value;

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
