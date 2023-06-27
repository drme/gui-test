package edu.ktu.screenshotanalyser.tools;

import net.dongliu.apk.parser.ApkFile;

public class ApkReader
{

	/*
	
	
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
	} */
}
