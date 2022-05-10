package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import edu.ktu.screenshotanalyser.checks.SystemContext;
import edu.ktu.screenshotanalyser.tools.SystemUtils;
import net.dongliu.apk.parser.ApkFile;

public class AppContext
{
	public AppContext(File appFolder, File dataFolder, List<TestDevice> testDevices, SystemContext systemContext) throws IOException
	{
		this.dataFolder = dataFolder;
		this.testDevices = testDevices;
		this.systemContext = systemContext;
		
		for (var testDevice : testDevices)
		{
			var stateFiles = new File(testDevice.getFolder().getAbsolutePath() + "/" + appFolder.getName() + "/states/").listFiles(pathelement -> pathelement.getAbsolutePath().endsWith("." + stateFileExtension));
			
			if (null != stateFiles)
			{
				for (var stateFile : stateFiles)
				{
					var stateImage = new File(stateFile.getAbsolutePath().replaceAll(stateFileExtension + "$", "png").replace("state_", "screen_"));
				
					if ((stateImage.exists()) && (stateImage.isFile()) && (stateImage.length() > 0))
					{
						this.states.add(new State("", this, stateImage, stateFile, testDevice));
					}
					else
					{
						stateImage = new File(stateFile.getAbsolutePath().replaceAll(stateFileExtension + "$", "jpg").replace("state_", "screen_"));
					
						if ((stateImage.exists()) && (stateImage.isFile()) && (stateImage.length() > 0))
						{
							this.states.add(new State("", this, stateImage, stateFile, testDevice));
						}
					}
				}
			}
		}
		
		var apkFiles = appFolder.listFiles(p -> p.isFile() && p.getName().endsWith(".apk"));
		
		if (apkFiles.length > 0)
		{
			this.apkFile = apkFiles[0];
		}			
	}
	
	private synchronized void loadAppInfo()
	{
		if (this.name.equals(""))
		{
			if ((null != this.apkFile) && this.apkFile.length() > 0)
			{
				try (var apkFile = new ApkFile(this.apkFile))
				{
					var apkMeta = apkFile.getApkMeta();
					// System.out.println(apkMeta.getLabel() + "; " + apkMeta.getPackageName() + "; " + apkMeta.getVersionCode());
					// System.out.println(apkMeta.getPackageName());
					// System.out.println(apkMeta.getVersionCode());

					for (var feature : apkMeta.getUsesFeatures())
					{
						// System.out.println(feature.getName());
					}

					var name = apkMeta.getLabel();
					var version = apkMeta.getVersionName();
					var packageName = apkMeta.getPackageName();

					this.locales = apkFile.getLocales();

					for (var locale : locales)
					{
						// System.out.println(name + " Language: " + locale.getCountry() + " " + locale.toString());
					}

					if (this.name.startsWith("@string/"))
					{
						var messages = getMessages().getTranslations(name.substring("@string/".length()));
						var nameEnglish = messages.get("en");

						if (null != nameEnglish)
						{
							name = nameEnglish;
						}
						else
						{
							name = messages.get(messages.keySet().iterator().next());
						}
					}

					this.packageName = packageName;
					this.version = version;
					this.name = name;
				}
				catch (Exception ex)
				{
					ex.printStackTrace(System.err);

					this.packageName = "";
					this.version = "";
					this.name = "";
				}
			}
			else
			{
				this.packageName = "";
				this.version = "";
				this.name = "";
			}

		}
	}
	
	public File getApkFile()
	{
		return this.apkFile;
	}
	
	public String getName()
	{
		loadAppInfo();
		
		return this.name;
	}
	
	public String getVersion()
	{
		loadAppInfo();
		
		return this.version;
	}
	
	public String getPackage()
	{
		loadAppInfo();
		
		return this.packageName;
	}
	
	public File getDataFolder()
	{
		return this.dataFolder;
	}
	
	public List<State> getStates()
	{
		return this.states;
	}
	
	public Set<Locale> getLocales()
	{
		loadAppInfo();
		
		return this.locales;
	}	
	
	public synchronized LocalizedMessages getMessages()
	{
		if (null == messages)
		{
			var tempFolder = new File("e:/3/" + this.apkFile.getName());

			try
			{
				if (!tempFolder.exists())
				{
					SystemUtils.executeCommand("java -jar ./tools/apktool_2.3.4.jar d " + this.apkFile.getAbsolutePath() + " -o " + tempFolder.getAbsolutePath());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			this.messages = new LocalizedMessages(tempFolder, this);
		}

		return this.messages;
	}
	
	public SystemContext getSystemContext()
	{
		return this.systemContext;
	}

	public Map<String, List<ResourceText>> getResources()
	{
		return this.resources;
	}
	
	private static final String stateFileExtension = "json";
	private String name = "";
	private String version = "";
	private String packageName = "";
	private final File dataFolder;	
	private final List<TestDevice> testDevices;
	private final List<State> states = new ArrayList<>();
	private final SystemContext systemContext;
	private LocalizedMessages messages = null;
	private File apkFile = null;
	private Set<Locale> locales = null;
	private final Map<String, List<ResourceText>> resources = new HashMap<String, List<ResourceText>>();

	public static class ResourceText
	{
		public ResourceText(String key, String value, String file)
		{
			this.key = key;
			this.value = value;
			this.file = file;
		}

		public String getKey()
		{
			return this.key;
		}

		public String getValue()
		{
			return this.value;
		}

		public String getFile()
		{
			return this.file;
		}

		private final String key;
		private final String value;
		private final String file;
	}
}
