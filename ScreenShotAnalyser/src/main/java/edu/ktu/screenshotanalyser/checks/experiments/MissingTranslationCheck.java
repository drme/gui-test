package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.languagetool.Language;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class MissingTranslationCheck extends BaseTextRuleCheck implements IAppRuleChecker, IStateRuleChecker 
{
	public MissingTranslationCheck()
	{
		super(14, "SL1");
	}

	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
		var messages = appContext.getMessages();
		var languages = messages.getLanguages().stream().sorted((x, y) -> x.length() - y.length()).collect(Collectors.toList()).toArray(new String[0]);
		
		for (var key : messages.getKeys())
		{
			var translations = messages.getTranslations(key);
			var missingLanguages = "";
			var sameTranslations = new HashMap<String, HashSet<String>>();
			
			for (int i = 0; i < languages.length; i++)
			{
				var sourceLanguage = languages[i];
				var sourceMessage = translations.get(sourceLanguage);
				
				if (null == sourceMessage)
				{
					missingLanguages += " " + sourceLanguage;
					
					continue;
				}
				
				if (!isTranslateable(sourceMessage, appContext))
				{
					continue;
				}				
				
				for (int j = i + 1; j < languages.length; j++)
				{
					var targetLanguage = languages[j];
					
					if (targetLanguage.startsWith(sourceLanguage.substring(0, 2) + "-"))
					{
						break;
					}
					
					var targetMessage = translations.get(targetLanguage);
					
					if (null == sourceMessage)
					{
						missingLanguages += " " + targetLanguage;
					
						continue;
					}
					
					if (sourceMessage.equals(targetMessage))
					{
						if ((false == isSpellingCorrect(sourceLanguage, sourceMessage.toLowerCase(), appContext)) || (false == isSpellingCorrect(targetLanguage, targetMessage.toLowerCase(), appContext)))
						{
							var sameLanguages = sameTranslations.get(sourceMessage);
							
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
			
			for (var sameTranslation : sameTranslations.keySet())
			{
				var sameLanguages = sameTranslations.get(sameTranslation).stream().collect(Collectors.joining(","));

				failures.addFailure(new CheckResult(appContext, this, "Same for [" + key + "]: [" + sameTranslation + "]: " + sameLanguages));
			}
		}
	}
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		var placeholders = new ArrayList<String>();
		var allTexts = state.getActualControls().stream().map(this::getText).filter(x -> x != null && x.length() > 0).collect(Collectors.joining(". "));
		var languages = getLanguage(allTexts);

		for (var control : state.getActualControls())
		{
			if ((isTranslateable(control.getText(), state.getAppContext())) && (isPlaceholder(control.getText(), state, languages)))
			{
				placeholders.add(control.getText());
			}
		}

		if (!placeholders.isEmpty())
		{
			failures.addFailure(new CheckResult(state, this, "unstranslated: " + String.join(", ", placeholders.toArray(new String[0])), placeholders.size()));
		}
	}
	
	private boolean isPlaceholder(String message, State state, List<Language> stateLanguages)
	{
		if ((null == message) || (message.length() == 0))
		{
			return false;
		}
		
		if (isUpperCase(message))
		{
			return false;
		}
		
		var words = message.split(" ");
		
		if (words.length > 1)
		{
			return false;
		}
		
		var messages = state.getAppContext().getMessages();
		
		if (null != messages)
		{
			var keys = messages.getKeys();
			
			if (keys.contains(message))
			{
				if (!isSpellingCorrect(message, stateLanguages))
				{
					return true;
				}
				/*
				
				var aa = messages.getTranslations(message);
				
				for (var key : aa.keySet())
				{
					var translation = aa.get(key);
					
					var languages = getLanguageByCode(key);
					
					for (var language : languages)
					{
					
					JLanguageTool langTool = new JLanguageTool(language);

						List<RuleMatch> matches = new ArrayList<>();
						try
						{
							matches = langTool.check(translation);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						for (RuleMatch match : matches) {

							System.out.println("Grammar check failed: " + match.getMessage()
							
							//+". Possible fixes: "+match.getSuggestedReplacements()
							
							+". Text was: "+translation);
							
							
					//		results.add(CheckResult.Nok(type, "Grammar check failed: " + match.getMessage()+". Possible fixes: "+match.getSuggestedReplacements()+". Text was: "+resourceText.getValue(),
			//					resourceText.getFile() + "@" + resourceText.getKey(), langKey));
							
							return true;
						}
					
					
					
					}
					
					
				}
				*/
				
				
				
				
			//	return aa != null;  //true;
				
				return false;
			}
		}		
		
		return false;
	}
}
