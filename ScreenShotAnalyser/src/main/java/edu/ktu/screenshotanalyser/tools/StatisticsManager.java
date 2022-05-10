package edu.ktu.screenshotanalyser.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class StatisticsManager
{
  protected String connectionUrl = "jdbc:sqlserver://ME;database=defects-db;integratedSecurity=true;";

	public void saveAppInfo(AppContext appContext)
	{
		if ((null == appContext.getPackage()) || (null == appContext.getApkFile()) || (null == appContext.getLocales()))
		{
			return;
		}
		
    try (Connection connection = DriverManager.getConnection(this.connectionUrl)) 
    {
    	Long applicationId = getId(connection, "SELECT Id FROM Application WHERE Package = ?", appContext.getPackage());
    	
    	if (null == applicationId)
    	{
    		applicationId = insert(connection, "INSERT INTO Application (Name, Package, Version, ApkFile) VALUES (?, ?, ?, ?)", appContext.getName(), appContext.getPackage(), appContext.getVersion(), appContext.getApkFile().getName());
    		
    		for (Locale locale : appContext.getLocales())
    		{
    			Long localeId = getId(connection, "SELECT Id FROM Locale WHERE Code = ?", locale.toString());
    			
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
    }
	}
	
	protected Long getId(Connection connection, String query, Object... arguments) throws SQLException
	{
		try (PreparedStatement statement = connection.prepareStatement(query))
		{
			int id = 1;
			
			for (Object argument : arguments)
			{
				statement.setObject(id++, argument);
			}
			
		  try (ResultSet resultSet = statement.executeQuery())
		  {
		  	if (resultSet.next())
		  	{
		  		return resultSet.getLong(1);
		  	}
		  }
		}
		
		return null;
	}
	
	protected long insert(Connection connection, String query, Object... arguments) throws SQLException
	{
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
		{
			int id = 1;
			
			for (Object argument : arguments)
			{
				statement.setObject(id++, argument);
			}
			
			statement.execute();
			
		  try (ResultSet resultSet = statement.getGeneratedKeys())
		  {
		  	resultSet.next();
		  	
		  	return resultSet.getLong(1);
		  }
		}
	}
	
	public List<String> getList(String query, Object... arguments) throws SQLException
	{
		try (var connection = DriverManager.getConnection(connectionUrl))
		{
			return getList(connection, query, arguments);
		}
	}
	
	protected List<String> getList(Connection connection, String query, Object... arguments) throws SQLException
	{
		ArrayList<String> result = new ArrayList<>();
		
		try (PreparedStatement statement = connection.prepareStatement(query))
		{
			int id = 1;
			
			for (Object argument : arguments)
			{
				statement.setObject(id++, argument);
			}
			
		  try (ResultSet resultSet = statement.executeQuery())
		  {
		  	while (resultSet.next())
		  	{
		  		result.add(resultSet.getString(1));
		  	}
		  }
		}
		
		return result;
	}	

	
	public long startTestsRun(String description, boolean resume)
	{
		try (var connection = DriverManager.getConnection(connectionUrl))
		{
			if (true == resume)
			{
				var id = getId(connection, "SELECT TOP 1 Id FROM TestRun WHERE [Description] = ? AND Finished = 0 ORDER BY Id DESC", description);
				
				if (null != id)
				{
					return id.longValue();
				}
			}
			
			return insert(connection, "INSERT TestRun ([Description]) VALUES (?)", description);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();

			return -1;
		}
	}
	
	public boolean wasChecked(long testRunId, State state)
	{
		try (var connection = DriverManager.getConnection(connectionUrl))
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
		try (var connection = DriverManager.getConnection(connectionUrl))
		{
			var query = "SELECT DISTINCT ScreenShot.FileName FROM TestRunDefect JOIN ScreenShot ON TestRunDefect.ScreenShotId = ScreenShot.Id AND TestRunDefect.TestRunId = ?";
			
			return getList(connection, query, testRunId);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			return new ArrayList<>();
		}				
	}
	
	public void logDetectedDefect(long testRunId, CheckResult result)
	{
		try (var connection = DriverManager.getConnection(connectionUrl))
		{
			if (null != result.getState())
			{
				var screenshotId = getScreenShotId(result.getState(), connection);
			
				insert(connection, "INSERT TestRunDefect (DefectTypeId, ScreenshotId, TestRunId, DefectsCount, Message) VALUES (?, ?, ?, ?, ?)", result.getRule().getId(), screenshotId, testRunId, result.getDefectsCount(), result.getMessage());
			}
			else
			{
				var applicationId = getId(connection, "SELECT Id FROM Application WHERE Package = ?", result.getAppContext().getPackage());
				
				insert(connection, "INSERT TestRunDefect (DefectTypeId, ApplicationId, TestRunId, DefectsCount, Message) VALUES (?, ?, ?, ?, ?)", result.getRule().getId(), applicationId, testRunId, result.getDefectsCount(), result.getMessage());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}		
	}

	private Long getScreenShotId(State state, Connection connection) throws SQLException
	{
		var fileName = state.getImageFile().getAbsolutePath();

		if (fileName.startsWith(Settings.appImagesFolder.getAbsolutePath()))
		{
			fileName = fileName.substring(Settings.appImagesFolder.getAbsolutePath().length() + 1);
		}

		var screenshotId = getId(connection, "SELECT Id FROM ScreenShot WHERE FileName = ?", fileName);
		
		return screenshotId;
	}

	public void finishRun(long testsRunId)
	{
		try (var connection = DriverManager.getConnection(connectionUrl))
		{
			try (PreparedStatement statement = connection.prepareStatement("UPDATE TestRun SET Finished = 1 WHERE Id = ?"))
			{
				statement.setObject(1, testsRunId);

				statement.executeUpdate();
				
				connection.commit();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
