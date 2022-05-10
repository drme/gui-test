package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class LocalizedMessages
{
	public LocalizedMessages(File resourcesFolder, AppContext appContext)
	{
		loadResources(resourcesFolder, appContext);
	}

	public Set<String> getKeys()
	{
		return this.messages.keySet();
	}
	
	public Set<String> getLanguages()
	{
		return this.languages;
	}
	
	public String getMessage(String key, String language)
	{
		return getTranslations(key).get(language);
	}

	public Map<String, String> getTranslations(String key)
	{
		return this.messages.get(key);
	}
	
	private String getLanguageCode(File resourceFileName)
	{
		String[] code = resourceFileName.getName().split("-", 2);

		if (code.length >= 2)
		{
			return code[1];
		}	
		
		return "default";
	}
	
	private void loadResources(File appFolder, AppContext appContext)
	{
		var valueFolders = new File(appFolder, "res").listFiles(pathName -> pathName.isDirectory() && pathName.getName().startsWith("values"));

		if (null != valueFolders)
		{
			for (var valueFolder : valueFolders)
			{
				var languageCode = getLanguageCode(valueFolder);
				var resourceFiles = valueFolder.listFiles(pathName -> pathName.getName().equals("strings.xml"));

				for (var resourceFile : resourceFiles)
				{
					try
					{
						var resourceText = new String(Files.readAllBytes(resourceFile.toPath()), StandardCharsets.UTF_8);
						var resources = getResource(resourceText);

						if (languageCode.equals("default"))
						{
							var bigMessage = new StringBuilder("");

							for (var resource : resources.getString())
							{
								bigMessage.append(resource.getValue());
							}

							var predictedLanguage = appContext.getSystemContext().predictLanguage(bigMessage.toString());

							if (null != predictedLanguage)
							{
								switch (predictedLanguage)
								{
									case "eng":
										languageCode = "en";
										break;
									default:
										break;
								}
							}
						}

						this.languages.add(languageCode);

						for (var resource : resources.getString())
						{
							addMessage(languageCode, resource.getName(), resource.getValue());
						}

						/*
						 * List<ResourceText> textResources = Stream.of(resources.getString()).map(x -> { return new ResourceText(x.getName(), x.getValue(), resourceFile.getAbsolutePath()); }).collect(Collectors.toList()); List<String> extractedKeys = Stream.of(resources.getString()).map(x -> { return x.getName(); }).collect(Collectors.toList()); context.getResources().put(lang, textResources); List<String> missingKeys = extractedKeys.stream().filter(i -> !context.getKeys().contains(i)) .collect(Collectors.toList()); context.getKeys().addAll(missingKeys);
						 */
					}
					catch (Throwable e)
					{
						System.out.println("Can't read file: " + resourceFile.getAbsolutePath());
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void addMessage(String languageCode, String key, String value)
	{
		var keyMessages = this.messages.computeIfAbsent(key, p -> new HashMap<>());
		
		keyMessages.put(languageCode, value);
	}
	
	private Resources getResource(String contents) throws Throwable
	{
		var name = "\\s+name=\"([^\"]*)\\\"";
		var pattern = "<string\\b" + name + "[^>]*>(.*?)<\\/string>";
		var resources = new Resources();
		var matcher = Pattern.compile(pattern).matcher(contents);
		var resourcesList = new ArrayList<ResourceDao>();
		
		Pattern.compile(pattern, Pattern.MULTILINE);
		
		while (matcher.find())
		{
			var dao = new ResourceDao();
			
			dao.setName(matcher.group(1));
			dao.setValue(matcher.group(2));
			
			resourcesList.add(dao);
		}

		resources.setString(resourcesList.toArray(new ResourceDao[0]));
		
		return resources;
	}
	
	@JacksonXmlRootElement(localName = "resources")
	public static class Resources
	{
		public Object[] getItem()
		{
			return this.item;
		}

		public void setItem(Object[] item)
		{
			this.item = item;
		}

		public ResourceDao[] getString()
		{
			return this.string;
		}

		public void setString(ResourceDao[] string)
		{
			this.string = string;
		}

		@JacksonXmlElementWrapper(localName = "string", useWrapping = false)
		private ResourceDao[] string = new ResourceDao[0];
		@JacksonXmlElementWrapper(localName = "item", useWrapping = false)
		private Object[] item = new Object[0];
	}
	
	@JacksonXmlRootElement(localName = "string")
	public static class ResourceDao
	{
		@Override
		public String toString()
		{
			return "ResourceDao [value=" + this.value + ", name=" + this.name + "]";
		}

		public String getValue()
		{
			return this.value;
		}

		public String getName()
		{
			return this.name;
		}

		public void setValue(String value)
		{
			this.value = value;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		@JacksonXmlProperty(isAttribute = true, localName = "name")
		private String name;
		@JacksonXmlCData
		@JacksonXmlText
		private String value;		
	}
	
	private final Map<String, Map<String, String>> messages = new HashMap<>();
	private final Set<String> languages = new HashSet<>();
}
