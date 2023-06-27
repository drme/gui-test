package edu.ktu.screenshotanalyser.tools;

import java.io.IOException;

import edu.ktu.screenshotanalyser.database.DataBase;
import edu.ktu.screenshotanalyser.utils.BaseLogger;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MicsTools extends StatisticsManager
{
	public static void main(String[] args) throws SQLException, IOException
	{
		deleteEmptyTestRuns();
		
		//new MicsTools().importTooSmallResultsFromLogFile();
		//new MicsTools().importBadSpellingResultsFromLogFile();
//		new MicsTools().importMisingTextsResultsFromLogFile();
	}
	
	private static void deleteEmptyTestRuns() throws SQLException
	{
		var dataBase = new DataBase();
		
		var emptyTestsRuns = dataBase.getList(rs -> rs.getLong(1), "SELECT Id FROM TestRun WHERE Id NOT IN (SELECT DISTINCT TestRunId FROM TestRunDefect)");
		
		for (var testRunId : emptyTestsRuns)
		{
			BaseLogger.logInfo("Removing test run: " + testRunId);
			dataBase.update("DELETE FROM TestRun WHERE Id = ?", testRunId);
		}
	}	
	
	

	private void importTooSmallResultsFromLogFile() throws SQLException, IOException
	{
		List<String> allLines = Files.readAllLines(Paths.get("E:\\_analyzer_\\logTS2.txt"));

		try (Connection connection = beginTransaction())
		{
			long testRunId = insert(connection, "INSERT TestRun ([Description]) VALUES (?)", "Too Small Results Import");
			long defectId = 19;

			for (String line : allLines)
			{
				line = line.substring("TS2: d:\\_r\\".length());
				line = line.substring(0, line.indexOf(' '));

				String screenShotFileName = line;

				long screenShotId = getId(connection, "SELECT Id FROM ScreenShot WHERE FileName = ?", screenShotFileName);

				insert(connection, "INSERT TestRunDefect (DefectTypeId, ScreenshotId, TestRunId, DefectsCount) VALUES (?, ?, ?, ?)", defectId, screenShotId, testRunId, 1);
			}
			
			System.out.println("TestRunId: " + testRunId);
		}
	}
	
	private void importBadSpellingResultsFromLogFile() throws SQLException, IOException
	{
		List<String> allLines = SystemUtils.readAllLines("E:\\_analyzer_\\RESULTS\\logSS1.txt", null);

		try (Connection connection = beginTransaction())
		{
			long testRunId = insert(connection, "INSERT TestRun ([Description]) VALUES (?)", "Bad Spelling Results Import");
			long defectId = 2;
			long resourceDefects = 0;

			for (String line : allLines)
			{
				line = line.substring("SS1: d:\\_r\\".length());
				line = line.substring(0, line.indexOf(' '));

				String screenShotFileName = line;

				Long screenShotId = getId(connection, "SELECT Id FROM ScreenShot WHERE FileName = ?", screenShotFileName);

				if (null == screenShotId)
				{
					resourceDefects++;
				}
				else
				{
					insert(connection, "INSERT TestRunDefect (DefectTypeId, ScreenshotId, TestRunId, DefectsCount) VALUES (?, ?, ?, ?)", defectId, screenShotId, testRunId, 1);
				}
			}
			
			System.out.println("TestRunId: " + testRunId + ", resource defects: " + resourceDefects);
		}
	}
	
	private void importMisingTextsResultsFromLogFile() throws SQLException, IOException
	{
		try (Connection connection = beginTransaction())
		{
			List<String> allLines = SystemUtils.readAllLines("E:\\e1\\3\\66611", "TM1: ");

			long testRunId = insert(connection, "INSERT TestRun ([Description]) VALUES (?)", "Missing Text Results Import");
			long defectId = 26;
			long resourceDefects = 0;

			for (String line : allLines)
			{
				line = line.substring("TM1: ".length());
				
				String screenShotFileName = line.substring(0, line.indexOf(' '));
				
				screenShotFileName = screenShotFileName.substring("d:\\_r\\".length());
				
				Long screenShotId = getId(connection, "SELECT Id FROM ScreenShot WHERE FileName = ?", screenShotFileName);

				if (null == screenShotId)
				{
					resourceDefects++;
				}
				else
				{
					insert(connection, "INSERT TestRunDefect (DefectTypeId, ScreenshotId, TestRunId, DefectsCount) VALUES (?, ?, ?, ?)", defectId, screenShotId, testRunId, 1);
				}
			}
			
			System.out.println("TestRunId: " + testRunId + ", resource defects: " + resourceDefects);				
		}
	}
	
	
}
