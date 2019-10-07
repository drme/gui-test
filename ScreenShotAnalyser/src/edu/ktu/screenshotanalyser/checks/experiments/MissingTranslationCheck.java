package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.LocalizedMessages;
import edu.ktu.screenshotanalyser.rules.checkers.CheckRequest;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.rules.checkers.IRuleChecker;
import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class MissingTranslationCheck extends BaseTextRuleCheck implements IAppRuleChecker,    IRuleChecker 
{
	public MissingTranslationCheck()
	{
		super("SL1");
	}
	
	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
		LocalizedMessages messages = appContext.getMessages();
		
		if (null != messages)
		{
			Set<String> keys = messages.getKeys();
			String[] languages = messages.getLanguages().stream().sorted((x, y) -> x.length() - y.length()).collect(Collectors.toList()).toArray(new String[0]);
		
			for (String key : keys)
		{
			Map<String, String> translations = messages.getTranslations(key);
			
			String missingLanguages = "";
			
			HashMap<String, HashSet<String>> sameTranslations = new HashMap<>();
			
			for (int i = 0; i < languages.length; i++)
			{
				String sourceLanguage = languages[i];
				String sourceMessage = translations.get(sourceLanguage);
				
				if (null == sourceMessage)
				{
					missingLanguages += " " + sourceLanguage;
					
					continue;
				}

				
				if (false == isTranslateable(sourceMessage, appContext))
				{
					continue;
				}				
				
				for (int j = i + 1; j < languages.length; j++)
				{
					String targetLanguage = languages[j];
					
					if (targetLanguage.startsWith(sourceLanguage.substring(0, 2) + "-"))
					{
						break;
					}
					
					
					String targetMessage = translations.get(targetLanguage);
					
					if (null == sourceMessage)
					{
						missingLanguages += " " + targetLanguage;
						
						continue;
					}
					
					if (sourceMessage.equals(targetMessage))
					{
						if ((false == isSpellingCorrect(sourceLanguage, sourceMessage.toLowerCase(), appContext)) || (false == isSpellingCorrect(targetLanguage, targetMessage.toLowerCase(), appContext)))
						{
							//logMessage(sourceMessage + "[" + sourceLanguage + "] == " + targetMessage + "[" + targetLanguage + "]");
							
							HashSet<String> sameLanguages = sameTranslations.get(sourceMessage);
							
							if (null == sameLanguages)
							{
								sameLanguages = new HashSet<>();
							
								sameTranslations.put(sourceMessage, sameLanguages);
							}
							
							sameLanguages.add(sourceLanguage);
							sameLanguages.add(targetLanguage);
						}
					}
				}
			}
			
			missingLanguages = missingLanguages.trim();
			
			if (missingLanguages.length() > 0)
			{
	//			logMessage("Missing transaltion for: " + key + " " + missingLanguages);
			}
			
			
			
			for (String sameTranslation : sameTranslations.keySet())
			{
				String sameLanguages = sameTranslations.get(sameTranslation).stream().collect(Collectors.joining(","));

				failures.addFailure(new CheckResult(appContext, this, "Same for [" + key + "]: [" + sameTranslation + "]: " + sameLanguages));
			}
		}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	


	private boolean isTranslateable(String message, AppContext appContext)
	{
		if (message.equals(appContext.getAppName()))
		{
			return false;
		}
		
		message = message.trim();
		
		if (message.length() == 0)
		{
			return false;
		}
		
		String[] words = message.split("[ \t]");
		
		int wordsCount = 0;
		
		for (String word : words)
		{
			String newWord = "";
			
			for (int i = 0; i < word.length(); i++)
			{
				if (Character.isLetter(word.charAt(i)) || ('\'' == word.charAt(i)))
				{
					newWord += word.charAt(i);
				}
				else
				{
					break;
				}
			}
			
			if (newWord.length() > 1)
			{
				wordsCount++;
			}
		}
		
		if (wordsCount == 0)
		{
		//	System.out.println("ignoring: " + message);
			
			return false;
		}

		return true;
	}
	
	
	
	
	
	
	
	
	
	

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		final List<CheckResult> results = new ArrayList<>();

		for (ExtractedText extractedText : request.getExtractedTexts()) {
			final String text = extractedText.getText();
			if (context.isPlaceholder(text)) {
		//		results.add(CheckResult.Nok(type, String.format("Found missing translation violation for: %s", text),
		//				request.getOriginalFile(), null));
			}
		}
		return results.toArray(new CheckResult[0]);
	}


}
