package edu.ktu.screenshotanalyser.utils;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class JsonParser
{
	static
	{
		Configuration.setDefaults(new Configuration.Defaults()
		{
			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();
			private final Set<Option> options = EnumSet.noneOf(Option.class);

			public JsonProvider jsonProvider()
			{
				return this.jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider()
			{
				return this.mappingProvider;
			}

			@Override
			public Set<Option> options()
			{
				return this.options;
			}
		});				
	}
	
	private JsonParser()
	{
	}
	
	public static DocumentContext parse(File file) throws IOException
	{
		return JsonPath.parse(file);
	}
}
