package edu.ktu.screenshotanalyser.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import edu.ktu.screenshotanalyser.context.AppContext;

public class StatisticsManager
{
  private String connectionUrl = "jdbc:sqlserver://localhost;database=defects-db;integratedSecurity=true;";

	public void saveAppInfo(AppContext appContext)
	{
		if ((null == appContext.getPackage()) || (null == appContext.getApkFile()) || (null == appContext.getLocales()))
		{
			return;
		}
		
    try (Connection connection = DriverManager.getConnection(connectionUrl)) 
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
	
	private Long getId(Connection connection, String query, Object... arguments) throws SQLException
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
	
	private long insert(Connection connection, String query, Object... arguments) throws SQLException
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
}
