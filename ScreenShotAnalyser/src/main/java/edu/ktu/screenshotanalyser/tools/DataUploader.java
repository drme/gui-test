package edu.ktu.screenshotanalyser.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import edu.ktu.screenshotanalyser.database.DataBase;
import edu.ktu.screenshotanalyser.database.DataBase.Application;
import edu.ktu.screenshotanalyser.database.FileType;

public class DataUploader
{
	public static boolean appToDataBase(File appName)
	{
		return true;
		
		
		
	/*	try
		{
			var dataBase = new DataBase();
			var apkFiles = appName.listFiles(p -> p.isFile() && p.getName().endsWith(".apk"));

			if (apkFiles.length > 0)
			{
				var bytes = Files.readAllBytes(Paths.get(apkFiles[0].getPath()));
				var applicationId = dataBase.getId("SELECT Id FROM Application WHERE ApkFile = ?", apkFiles[0].getName());

				if (applicationId != null)
				{
					dataBase.insertFile(apkFiles[0].getName(), bytes, FileType.APPLICATION_PACKAGE, applicationId);

				//	System.out.println(applicationId);

					dataBase.update("UPDATE Application SET Folder = ? WHERE Id = ?", appName.getName(), applicationId);
					
					var files = appName.listFiles(p -> p.isFile() && p.getName().endsWith(".png"));

					if (files.length > 0)
					{
						var iconFile = files[0].getAbsolutePath();

						var iconBytes = Files.readAllBytes(Paths.get(iconFile));

						dataBase.insertFile(files[0].getName(), iconBytes, FileType.STORE_ICON, applicationId);

					}

					return true;
				}
				else
				{
					System.out.println("Can not find application for " + apkFiles[0].getName());
					
					return false;
				}
			}
			else
			{
				System.out.println("No apk files in: " + appName.getPath());
				
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			return false;
		} */
	}
	
	public static void appsFromDataBase()
	{
		try
		{
			var dataBase = new DataBase();
			
			/*
			
			var emptyAppFiles = new File("D:/1/").listFiles(p -> p.isDirectory());
			
			for (var file : emptyAppFiles)
			{
				dataBase.insert("INSERT INTO Application (Name, Package, Version, ApkFile, Folder) VALUES(?, ?, ?, ?, ?)", "", file.getName(), "", "", file.getName());
			}*/
			
			
//		
//		if (null != obbFiles)
//		{
//	for (var obbFile : obbFiles)
//	{
//		var fileBytes = Files.readAllBytes(Paths.get(obbFile.getAbsolutePath()));
//
//		dataBase.insertFile(obbFile.getName(), fileBytes, FileType.ADDITIONAL_CONTENT, application.id());		
//	}
			/*

			var apps = new File(Settings.appsFolder).listFiles(p -> p.isDirectory());
			
			for (var app : apps)
			{
				var appId = dataBase.getList(rs -> rs.getLong(1), "SELECT Id From Application WHERE Folder = ?", app.getName());
				
				if (appId.isEmpty())
				{
					var appContext = new edu.ktu.screenshotanalyser.context.AppContext(app, null, new ArrayList<>(), null);
					
					var applicationId = dataBase.insert("INSERT INTO Application (Name, Package, Version, ApkFile) VALUES (?, ?, ?, ?)", appContext.getName(), appContext.getPackage(), appContext.getVersion(), appContext.getApkFile().getName());
	    		
	    		for (var locale : appContext.getLocales())
	    		{
	    			var localeId = dataBase.getId("SELECT Id FROM Locale WHERE Code = ?", locale.toString());
	    			
	    			if (null == localeId)
	    			{
	    				localeId = dataBase.insert("INSERT INTO Locale (Code) VALUES (?)", locale.toString());
	    			}
	    			
	    			dataBase.insert("INSERT INTO ApplicationLocale (ApplicationId, LocaleId) VALUES (?, ?)", applicationId, localeId);
	    		}
					
					
				}
				
			}			
			
			
			*/
			
			
			/*
			
var applications = dataBase.getApplications();
			
			for (var application : applications)
			{
			saveApplication(application, dataBase);
			} */ 
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		} 
	}

	private static void saveApplication(Application application, DataBase dataBase) throws IOException, SQLException
	{
		//if (new File(Settings.appsFolder + application.folder() + "\\_results_\\no_emulator").exists())
//		{
//			dataBase.update("UPDATE Application SET NoEmulator = 1 WHERE Id = ?", application.id());
//		}

		
//		var obbFiles = new File(Settings.appsFolder + application.folder()).listFiles(p -> p.isFile() && p.getName().endsWith(".obb"));
//		
//		if (null != obbFiles)
//		{
//	for (var obbFile : obbFiles)
//	{
//		var fileBytes = Files.readAllBytes(Paths.get(obbFile.getAbsolutePath()));
//
//		dataBase.insertFile(obbFile.getName(), fileBytes, FileType.ADDITIONAL_CONTENT, application.id());		
//	}
//		}
		
		
/*
		
	  var path = Paths.get("d:/_foo/apps/" + application.folder());

    Files.createDirectories(path);

    var files = dataBase.getApplicationFiles(application.id());
    
    for (var file : files)
    {
    	Files.write(Paths.get("d:/_foo/apps/" + application.folder() + "/" + file.fileName()), file.fileData());
    }
    
    if (application.noEmulator())
    {
      Files.createDirectories(Paths.get("d:/_foo/apps/" + application.folder() + "/_results_/"));
    	Files.write(Paths.get("d:/_foo/apps/" + application.folder() + "/_results_/no_emulator"), new byte[] {});
    } */
	}
	

	
	public static void main(String[] args) throws SQLException
	{
//		deleteEmptyTestRuns();
		
		
	//	appsFromDataBase();
	}
}
