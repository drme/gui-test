package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.aliasi.util.Pair;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import opennlp.tools.util.featuregen.WordClusterFeatureGenerator;

public class WrongLanguageCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public WrongLanguageCheck()
	{
		super(37, "WrongLanguageCheck");
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
//		HashMap<Language, List<String>> uniqueLanguages = new HashMap<>();
		
		var messages = state.getActualControls().stream().map(this::getText).filter(p -> isTranslateable(p, state.getAppContext())).map(message -> new Pair<>(message, determineLanguageShort(message))).collect(Collectors.toList());
		var locales = state.getAppContext().getLocales();
		
		for (var message : messages)
		{
			var languageFound = false;
			
			for (var language : message.b().keySet())
			{
				var prob = message.b().get(language);

				if (prob > 0.8)
				{
					if (isAppLanguage(locales, language))
					{
						languageFound = true;
						break;
					}					
					
					
					
					/*
				if (uniqueLanguages.containsKey(language))
				{
					var m = uniqueLanguages.get(language);
					
					m.add(message.a());
					
					uniqueLanguages.put(language, m);
				}
				else
				{
					var m = new ArrayList<String>();

					m.add(message.a());
					
					uniqueLanguages.put(language, m);
				}		*/		
				}
			}
			
			if (false == languageFound)
			{
				failures.addFailure(new CheckResult(state, this, "missing language " + message.a() + " " + message.b().toString(), 1));
				
				return;
			}
		}

		
		/*
		
		for (var language : uniqueLanguages.keySet())
		{
			if (false == isAppLanguage(locales, language))
			{
				failures.addFailure(new CheckResult(state, this, "missing language " + language.name(), 1));			
				
				return;
			}
			
			
//			var languageMessages = uniqueLanguages.get(language);
	//		
		//	if (languageMessages.size() == messages.size())
			//{
//				return;
	//		}
			
			
			
			
		}
		
		*/
		
		/*
		
		
		var maxMessages = -1;
		Language maxLangauge = null; 
		
		for (var language : uniqueLanguages.keySet())
		{
			var m = uniqueLanguages.get(language);
			
			if (m.size() >= maxMessages)
			{
				maxMessages = m.size();
				maxLangauge = language;
			}
		}

		var allFound = true;
		var ok = uniqueLanguages.get(maxLangauge);
		
		for (var message : messages)
		{
			if (ok.contains(message.a()))
			{
				continue;
			}
			
			if (false == isSpellingCorrect(maxLangauge.getIsoCode639_1().toString(), message.a(), state.getAppContext()))
			{
				allFound = false;
				break;
			}
		}
		
		if (allFound)
		{
			return;
		}
		
		
		/=*
		//forEach(message -> mergeLanguages(message, uniqueLanguages));
		
		if (uniqueLanguages.keySet().size() > 1)
		{
			var message = "multiple langauges: ";
			
			for (var key : uniqueLanguages.keySet())
			{
				message += key + uniqueLanguages.get(key) + "; ";
			}
			
			failures.addFailure(new CheckResult(state, this, message, 1));			
		} *=/
		
		var error = "";
		
		for (var message : messages)
		{
			var maxScore = -1.0;
			Language language = null;
			
			for (var l : message.b().keySet())
			{
				var p = message.b().get(l);
				
				if (p >= maxScore)
				{
					language = l;
					maxScore = p;
				}
			}
			
			error += message.a().replace("\n", " ").replace("\r", " ") + "[" + language.getIsoCode639_1() + " " + maxScore + "] ";
		}
		
//		failures.addFailure(new CheckResult(state, this, error, 1));			
		
		
		*/
	}
	
	
	
	private boolean isAppLanguage(Set<Locale> appLanguages, Language language)
	{
		if ("eng".equalsIgnoreCase(language.getIsoCode639_3().name()))
		{
			return true;
		}
		
		for (var appLanguage : appLanguages)
		{
			try
			{
				if (appLanguage.getISO3Language().equalsIgnoreCase(language.getIsoCode639_3().name()))
				{
					return true;
				}
			}
			catch (MissingResourceException ex)
			{
			}
		}
		
		return false;
	}
	
	
	
	
	
	@Override
	protected String getText(Control control)
	{
		var message = super.getText(control);
		
		message = message.trim();
		
		if (message.endsWith("."))
		{
			message = message.substring(0, message.length() - 1); 
		}		
		
		if (message.length() == 0)
		{
			return null;
		}		
		
		String[] words = message.split("[ \t]");
		
		for (String word : words)
		{
			if (word.length() == 0)
			{
				continue;
			}
			
			int letters = 0;
			int caps = 0;
			
			for (int i = 0; i < word.length(); i++)
			{
				char c = word.charAt(i);
				
				if (Character.isAlphabetic(c))
				{
					letters++;
				}
				
				if (Character.isUpperCase(c))
				{
					caps++;
				}
			}
			
			if (letters == 0)
			{
				message = message.replace(word, " ");
			}
			
			if (caps == word.length())
			{
				message = message.replace(word, " ");
			}
		}
		
		return message.trim();
	}
	
	private void mergeLanguages(String message, HashMap<String, String> uniqueLanguages)
	{
		var languages = determineLanguageShort(message);
		
		if (languages.size() == 0)
		{
			return;
		}
		
/*			var l = languages.get(0).getShortCode();
			
			if (uniqueLanguages.containsKey(l))
			{
				uniqueLanguages.put(l, uniqueLanguages.get(l) + "[" + message.replace("\n", "").replace("\r", "") + "]");
			}
			else
			{
				uniqueLanguages.put(l, "[" + message.replace("\n", "").replace("\r", "") + "]");
			}
			
			*/
	}
	
	
	
	

	
	/*
	
	protected synchronized static List<Language> getLanguageShort(String message)
	{
		String code = determineLanguageShort(message);

//		if (false == code.equals("en"))
	//	{
	//		return new ArrayList<>();
	//	}
		
		return getLanguageByCode(code);
	}*/
}
