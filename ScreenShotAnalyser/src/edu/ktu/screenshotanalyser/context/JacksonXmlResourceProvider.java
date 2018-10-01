package edu.ktu.screenshotanalyser.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import edu.ktu.screenshotanalyser.context.DefaultContextProvider.Resources;

public class JacksonXmlResourceProvider implements IResourcesProvider {
	private final ObjectMapper mapper;

	public JacksonXmlResourceProvider() {
		final JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		this.mapper = new XmlMapper(xmlModule);
	}

	@Override
	public Resources getResource(String contents) throws Throwable {
		return mapper.readValue(contents, Resources.class);
	}

}
