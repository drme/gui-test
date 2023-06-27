package edu.ktu.screenshotanalyser.tools;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.database.DataBase;

public class StatisticsManager extends DataBase
{
	public void saveAppInfo(AppContext appContext)
	{
	/*	if ((null == appContext.getPackage()) || (null == appContext.getApkFile()) || (null == appContext.getLocales()))
		{
			return;
		}
		
    try (var connection = beginTransaction()) 
    {
    	var applicationId = getId(connection, "SELECT Id FROM Application WHERE Package = ?", appContext.getPackage());
    	
    	if (null == applicationId)
    	{
    		applicationId = insert(connection, "INSERT INTO Application (Name, Package, Version, ApkFile) VALUES (?, ?, ?, ?)", appContext.getName(), appContext.getPackage(), appContext.getVersion(), appContext.getApkFile().getName());
    		
    		for (var locale : appContext.getLocales())
    		{
    			var localeId = getId(connection, "SELECT Id FROM Locale WHERE Code = ?", locale.toString());
    			
    			if (null == localeId)
    			{
    				localeId = insert(connection, "INSERT INTO Locale (Code) VALUES (?)", locale.toString());
    			}
    			
    			insert(connection, "INSERT INTO ApplicationLocale (ApplicationId, LocaleId) VALUES (?, ?)", applicationId, localeId);
    		}
    	}
    }
    catch (SQLException ex)
    {
    	ex.printStackTrace();
    } */
	}
	

	

	

	
	public boolean wasChecked(long testRunId, State state)
	{
		try (var connection = beginTransaction())
		{
			var screenshotId = getScreenShotId(state, connection);
			
			var logId = getId(connection, "SELECT TOP 1 Id FROM TestRunDefect WHERE TestRunId = ? AND ScreenShotId = ? AND DefectTypeId = 34", testRunId, screenshotId);
			
			return null != logId;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public List<String> getCheckedStates(long testRunId)
	{
		try
		{
			var query = "SELECT DISTINCT ScreenShot.FileName FROM TestRunDefect JOIN ScreenShot ON TestRunDefect.ScreenShotId = ScreenShot.Id AND TestRunDefect.TestRunId = ?";
			return getList(rs -> rs.getString(1), query, testRunId);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			return new ArrayList<>();
		}				
	}
}
