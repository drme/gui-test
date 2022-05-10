package edu.ktu.screenshotanalyser.checks.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import com.aliasi.util.Pair;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class OffensiveMessagesCheck extends BaseTextRuleCheck implements IStateRuleChecker, IAppRuleChecker
{
	public OffensiveMessagesCheck()
	{
		super(32, "OffensiveMessages");
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		var messages = state.getActualControls().stream().map(this::getText).filter(p -> isTranslateable(p, state.getAppContext())).collect(Collectors.toList());
		
		var offensiveMessages = messages.stream().filter(p -> isOffensive(p)).map(p -> p.replace("\r", " ").replace("\n", " ")).collect(Collectors.toList());
		
		if (offensiveMessages.size() > 0)
		{
			var error = ">> " + String.join(" | ", offensiveMessages.toArray(new String[0]));
			
			failures.addFailure(new CheckResult(state, this, error, offensiveMessages.size()));						
		}

		var longMessage = messages.stream().collect(Collectors.joining(". ")).replace("\r", " ").replace("\n", " ");  
		
		var badWords = hasBadWords(longMessage);
		
		if (null != badWords)
		{
			var error = ">> AI: " + badWords + " " + longMessage;
			
			failures.addFailure(new CheckResult(state, this, error, 1));						
		}
	}
	
	private static boolean isOffensive(String message)
	{
		if (message.contains("!!"))
		{
			if (message.matches(".*[a-zA-Z ]!!.*"))
			{
				var words = message.split(" ");
				var skip = false;
				
				for (var word : words)
				{
					if ((word.contains("!!")) && (word.length() > 100))
					{
						skip = true;
						break;
					}
				}
				
				if (false == skip)
				{
					return true;
				}
			}
		}
		
		if (isAllCaps(message))
		{
			var words = Stream.of(message.split(" ")).filter(p -> p.length() > 2).filter(p -> p.matches("[A-Za-z]")).collect(Collectors.toList());
			
			return words.size() > 2;
		}
		
		return false;
	}

	private static boolean isAllCaps(String message)
	{
		return message.equals(message.toUpperCase());
	}
	
	private static String hasBadWords(String message)
	{
		try
		{

		//	var file = File.createTempFile("message_", ".txt");

//			Files.write(file.toPath(), message.getBytes());

			//vat language = getLanguage(message);
			
			//var command = "profanity_filter --file \"" + file.getAbsolutePath() + "\" --show -l en";
			
			message = "\"" + message.replace('\"', ' ') + "\"";
			
			var command = new String[] { "profanity_filter", "--text", message, "--show", "-l", "en" };

			var output = executeShellCommand(command);

			if (output.contains("**"))
			{
				return output;
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
		
		var messages = appContext.getMessages();
		
		if (null != messages)
		{
			var languages = messages.getLanguages().stream().sorted((x, y) -> x.length() - y.length()).collect(Collectors.toList()).toArray(new String[0]);
		
			for (var key : messages.getKeys())
			{
				for (var language : languages)
				{
//					var errors = "";
					var message = messages.getMessage(key, language).replaceAll("%s", "");
	//				var mistypes = isSpellingCorrect(appContext, "", getLanguageByCode(language), message);
					
		//			errors += " " + mistypes;
			//		errors = errors.trim();		
					
				//	if (errors.length() > 0)
					//{
						//failures.addFailure(new CheckResult(appContext, this, errors));
					//}//
					
					
					var badWords = hasBadWords(message);
					
					if (null != badWords)
					{
						var error = ">> AI: " + badWords + " " + message;
						
						failures.addFailure(new CheckResult(appContext, this, error));						
					} 					
					
					if (isOffensive(message))
					{
						var error = ">> " + message;
						
						failures.addFailure(new CheckResult(appContext, this, error));						
					}					
					
				}
			}		
		
		}
	}
	
	
	/*
	 * 
	 * 		if ((text.endsWith("!")) && (text.toLowerCase().contains("error")))
					{
					System.out.println("[" + text + "] - no shouting lz");

		//				results.add(CheckResult.Nok(type, String.format("Fairly difficult to read text: %s", text), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage));
					}
					}
	 * 
	 * 
	 */
	
	
	
}
