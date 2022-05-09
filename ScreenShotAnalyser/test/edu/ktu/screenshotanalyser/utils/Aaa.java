package edu.ktu.screenshotanalyser.utils;

import java.sql.SQLException;
import java.util.HashSet;
import edu.ktu.screenshotanalyser.tools.StatisticsManager;

public class Aaa extends StatisticsManager
{
	public static void main(String[] args) throws SQLException
	{
		var manager = new Aaa();
		
		
		var msg = manager.getList("select distinct message from TestRunDefect where TestRunId = 10176");
		
		
		for (var message : msg)
		{
			int c = 0;
			System.out.println("-----------------------------------------------------------------------");
			
			var parts = message.split("\\]");
			
			var l = new HashSet<String>();
			
			for (var part : parts)
			{
				if (part.trim().length() == 0)
				{
					continue;
				}
				
				var aa = part.split("\\[");
				
//				System.out.println(aa.length);
				
				if (aa.length != 2)
				{
					continue;
					
					//System.out.println(part);
				}
	
				
				
				var lang = aa[1].split(" ")[0];
				
				if (false == l.contains(lang))
				{
					var txt = aa[0];
					
					if (!txt.contains("http://") && !txt.contains("https://"))
					{
					l.add(lang);
					
					System.out.print(lang);
					System.out.print(" - ");
					System.out.println(txt);
					
					c++;
					}
				}
			}
			
			
		//	System.out.println(message);
			
		//	break;
			
			
			for (int i = c; i < 34; i++)
			{
				System.out.println("==");
			}
					
		}
				
				
	}
}
