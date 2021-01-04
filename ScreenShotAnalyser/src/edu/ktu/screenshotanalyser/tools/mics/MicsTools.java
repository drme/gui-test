package edu.ktu.screenshotanalyser.tools.mics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.ktu.screenshotanalyser.tools.StatisticsManager;

public class MicsTools extends StatisticsManager
{
	public static void main(String[] args) throws SQLException, IOException
	{
		//new MicsTools().importTooSmallResultsFromLogFile();
		//new MicsTools().importBadSpellingResultsFromLogFile();
		new MicsTools().importMisingTextsResultsFromLogFile();
	}

	private void importTooSmallResultsFromLogFile() throws SQLException, IOException
	{
		List<String> allLines = Files.readAllLines(Paths.get("E:\\_analyzer_\\logTS2.txt"));

		try (Connection connection = DriverManager.getConnection(this.connectionUrl))
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
		List<String> allLines = readAllLines("E:\\_analyzer_\\RESULTS\\logSS1.txt", null);

		try (Connection connection = DriverManager.getConnection(this.connectionUrl))
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
		try (Connection connection = DriverManager.getConnection(this.connectionUrl))
		{
			List<String> allLines = readAllLines("E:\\e1\\3\\66611", "TM1: ");

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
	
	private List<String> readAllLines(String fileName, String prefix) throws IOException
	{
		ArrayList<String> result = new ArrayList<>();

		RandomAccessFile file = new RandomAccessFile(fileName, "r");
		
		String str;
		
		while ((str = file.readLine()) != null)
		{
			if (null == prefix)
			{
				result.add(str);
			}
			else if (str.startsWith(prefix))
			{
				result.add(str);
			}
		}

		file.close();

		return result;
	}
}
