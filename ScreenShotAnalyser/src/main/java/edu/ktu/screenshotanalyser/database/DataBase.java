package edu.ktu.screenshotanalyser.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.utils.BaseLogger;;

public class DataBase
{
	static
	{
  	var config = new HikariConfig();

  	config.setJdbcUrl("jdbc:sqlserver://localhost;database=defects-db;integratedSecurity=true;encrypt=true;trustServerCertificate=true");
  	config.addDataSourceProperty("cachePrepStmts", "true");
  	config.addDataSourceProperty("prepStmtCacheSize", "250");
  	config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
  	
  	config.setMaximumPoolSize(10000);

  	dataSource = new HikariDataSource(config);
  }	
	
	public long startTestsRun(String description, boolean resume)
	{
		try (var connection = beginTransaction())
		{
			if (resume)
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
			BaseLogger.logException("", ex);

			return -1;
		}
	}	
	
	public record TestDevice(long id, boolean tablet, String name)
	{
	}
	
	public List<TestDevice> getTestDevices() throws SQLException
	{
		var query = "SELECT Id, Tablet, Name FROM TestDevice";
		return getList(rs -> new TestDevice(rs.getLong(1), rs.getBoolean(2), rs.getString(3)), query);
	}	
	
	public long insertFile(@Nonnull String fileName, @Nonnull byte[] data, @Nonnull FileType type, long applicationId) throws SQLException
	{
		return insert("INSERT INTO ApplicationFile (ApplicationId, FileData, FileName, FileTypeId) VALUES(?, ?, ?, ?)", applicationId, data, fileName, type.id());
	}
	
	public long insert(String query, Object... arguments) throws SQLException
	{
		try (var connection = beginTransaction()) 
    {
			var id = insert(connection, query, arguments);
			
			connection.commit();
			
			return id;
    }
	}
	
	public record Application(long id, String folder, boolean noEmulator, String apkFile, String packageName, String name)
	{
	}
	
	public List<Application> getApplications() throws SQLException
	{
		var query = "SELECT Id, Folder, NoEmulator, ApkFile, Package, Name FROM Application";
		return getList(rs -> new Application(rs.getLong(1), rs.getString(2), rs.getBoolean(3), rs.getString(4), rs.getString(5), rs.getString(6)), query);
	}
	
	public record ApplicationFile(long id, byte[] fileData, String fileName, long fileTypeId)
	{
	}
	
	public List<ApplicationFile> getApplicationFiles(long applicationId) throws SQLException
	{
		var query = "SELECT Id, FileData, FileName, FileTypeId FROM ApplicationFile WHERE ApplicationId = ?";
		return getList(rs -> new ApplicationFile(rs.getLong(1), rs.getBytes(2), rs.getString(3), rs.getLong(4)), query, applicationId);
	}	

	protected Long insertNoThrow(Connection connection, String query, Object... arguments)
	{
		try
		{
			return insert(connection, query, arguments);
		}
		catch (SQLException ex)
		{
			BaseLogger.logException("", ex);
			
			return null;
		}
	}
	
	protected long insert(Connection connection, String query, Object... arguments) throws SQLException
	{
		try (var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
		{
			int id = 1;
			
			for (var argument : arguments)
			{
				statement.setObject(id++, argument);
			}
			
			statement.execute();
			
		  try (var resultSet = statement.getGeneratedKeys())
		  {
		  	resultSet.next();
		  	
		  	return resultSet.getLong(1);
		  }
		}
	}
	
	public void finishRun(long testsRunId) throws SQLException
	{
		update("UPDATE TestRun SET Finished = 1, StopDate = GetDate() WHERE Id = ?", testsRunId);
	}	

	public void logDetectedDefects(long testRunId, StateCheckResults results)
	{
		if (results.getAnnotations().isEmpty())
		{
			return;
		}

		var distinctDefects = results.getAnnotations().stream().map(p -> p.defect()).distinct();
		
		try (var connection = beginTransaction())
		{
			var screenshotId = getScreenShotId(results.getState(), connection);
			
			distinctDefects.forEach(p -> 
			{
				try
				{
					var testRunDefectId = insert(connection, "INSERT TestRunDefect (DefectTypeId, ScreenshotId, TestRunId, DefectsCount, Message) VALUES (?, ?, ?, ?, ?)", p.getId(), screenshotId, testRunId, 1, null);
					//insert("INSERT INTO TestRunDefectImage (TestRunDefectId, ImageData) VALUES(?, ?)", testRunDefectId, results.getState().getResultImage().encodeToPng());
					
					var state = results.getState(); 
					
					state.getResultImage().save(Settings.debugFolder + testRunId + "/s_" + state.getAppContext().getPackage() + "-" + state.getImageFile().getName() + "-" + testRunDefectId + ".png");
				}
				catch (SQLException ex)
				{
					BaseLogger.logException("", ex);
				}
			});
		}
		catch (Exception ex)
		{
			BaseLogger.logException("", ex);
		}		
	}	
	
	protected Long getScreenShotId(State state, Connection connection) throws SQLException
	{
		var fileName = state.getImageFile().getAbsolutePath();

		if (fileName.startsWith(Settings.appImagesFolder.getAbsolutePath()))
		{
			fileName = fileName.substring(Settings.appImagesFolder.getAbsolutePath().length() + 1);
		}

		return getId(connection, "SELECT Id FROM ScreenShot WHERE FileName = ?", fileName);
	}	
	
	public Long getId(String query, Object... arguments) throws SQLException
	{
		try (var connection = beginTransaction()) 
    {
			return getId(connection, query, arguments);
    }		
	}

	protected Long getId(Connection connection, String query, Object... arguments) throws SQLException
	{
		try (var statement = connection.prepareStatement(query))
		{
			int id = 1;
			
			for (var argument : arguments)
			{
				statement.setObject(id++, argument);
			}
			
		  try (var resultSet = statement.executeQuery())
		  {
		  	if (resultSet.next())
		  	{
		  		return resultSet.getLong(1);
		  	}
		  }
		}
		
		return null;
	}
	
	public void update(String query, Object... arguments) throws SQLException
	{
		try (var connection = beginTransaction()) 
    {
			update(connection, query, arguments);
			
			connection.commit();
    }
	}
	
	protected void update(Connection connection, String query, Object... arguments) throws SQLException
	{
		try (var statement = connection.prepareStatement(query))
		{
			int id = 1;
			
			for (var argument : arguments)
			{
				statement.setObject(id++, argument);
			}

			statement.execute();
		}
	}
	
	public <R> List<R> getList(@Nonnull IRecordBuilder<R> recordBuilder, @Nonnull String query, Object... arguments) throws SQLException
	{
		try (var connection = beginTransaction())
		{
			return getList(connection, recordBuilder, query, arguments);
		}
	}
	
	public interface IRecordBuilder<T>
	{
		T build(ResultSet rs) throws SQLException;
	}
	
	protected <R> List<R> getList(@Nonnull Connection connection, @Nonnull IRecordBuilder<R> recordBuilder, @Nonnull String query, Object... arguments) throws SQLException
	{
		var result = new ArrayList<R>();
		
		try (var statement = connection.prepareStatement(query))
		{
			int id = 1;
			
			for (Object argument : arguments)
			{
				statement.setObject(id++, argument);
			}
			
		  try (var resultSet = statement.executeQuery())
		  {
		  	while (resultSet.next())
		  	{
		  		result.add(recordBuilder.build(resultSet));
		  	}
		  }
		}
		
		return result;
	}
	
	protected Connection beginTransaction() throws SQLException
	{
		return dataSource.getConnection();		
	}

  private static HikariDataSource dataSource;
}
