package edu.ktu.screenshotanalyser.checks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.Control;

public abstract class BaseTextRuleCheck extends BaseRuleCheck
{
	protected BaseTextRuleCheck(long id, String ruleCode)
	{
		super(id, ruleCode);
	}

	protected synchronized static List<Language> getLanguageByCode(String languageCode)
	{
		languageCode = languageCode.toUpperCase();
		
		List<Language> languages = languagesCache.get(languageCode);

		if (null == languages)
		{
			languages = new ArrayList<>();
			
			for (Language language : Languages.get())
			{
				if (language.getShortCode().toUpperCase().equals(languageCode))
				{
					languages.add(language);
				}
			}

			if (languages.size() > 1)
			{
				languages = removeBaseLanguageClass(languages);
			}
			
			languagesCache.put(languageCode, languages);
		}

		return languages;
	}	
	
	protected boolean isSpellingCorrect(String languageCode, String message, AppContext appContext)
	{
		List<Language> languages = getLanguageByCode(languageCode);
		
		if (languages.size() == 0)
		{
			return true;
		}
		
		String mistypes = isSpellingCorrect(appContext, null, languages, message);
		
		return mistypes.trim().length() == 0;
	}

	protected String isSpellingCorrect(AppContext appContext, String mistypes, List<Language> languages, String message)
	{
		HashMap<Language, HashSet<String>> errors = new HashMap<>();

		for (Language lang : languages)
		{
			errors.put(lang, new HashSet<String>());

			JLanguageTool langTool = new JLanguageTool(lang);

			for (Rule rule : langTool.getAllRules())
			{
				if (!rule.isDictionaryBasedSpellingRule())
				{
					langTool.disableRule(rule.getId());
				}
			}

			try
			{
				List<RuleMatch> matches = langTool.check(message);

				for (RuleMatch match : matches)
				{
					String bad = message.substring(match.getFromPos(), match.getToPos());

					if (false == ignoreSpellingError(bad, appContext))
					{
						errors.get(lang).add(bad);
					}
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

		HashSet<String> bad = new HashSet<String>(errors.get(languages.get(0)));

		for (int i = 1; i < languages.size(); i++)
		{
			bad.retainAll(errors.get(languages.get(i)));
		}

		if ((bad.size() > 0) && (null != mistypes))
		{
			List<Language> sentenceLanguages = getLanguage(message);

			if (sentenceLanguages != languages)
			{
				if (sentenceLanguages.size() > 0)
				{
					String sentenceMistypes = isSpellingCorrect(appContext, null, sentenceLanguages, message);

					if (sentenceMistypes.length() == 0)
					{
						return mistypes;
					}
				}
			}
		}

		if (null == mistypes)
		{
			mistypes = "";
		}

		for (String badWord : bad)
		{
			mistypes += " " + badWord + "[" + languages.get(0).getShortCode() + "]";
		}

		return mistypes;
	}
	
	protected String getText(Control control)
	{
		String result = "";
		
		if (control.getText() != null)
		{
			result += control.getText() + ". ";
		}

		if (control.getContentDescription() != null)
		{
			result += control.getContentDescription() + ". ";
		}
		
		return result.trim();
	}	
	
	
	
	
	
	
	
	
	private static String determineLanguage(String message)
	{
		//final LanguageIdentifier langIdentifier = new LanguageIdentifier(
		//		message);
	//	return langIdentifier
//				.getLanguage();
		
		
    LanguageDetector detector = new OptimaizeLangDetector().loadModels();
         LanguageResult result = detector.detect(message);
         return result.getLanguage();		
		
	}
	
	protected synchronized static List<Language> getLanguage(String message)
	{
		String code = determineLanguage(message);

//		if (false == code.equals("en"))
	//	{
	//		return new ArrayList<>();
	//	}
		
		return getLanguageByCode(code);
	}

	private static List<Language> removeBaseLanguageClass(List<Language> languages)
	{
		List<Language> result = new ArrayList<>();
		
		for (Language language : languages)
		{
			Class<?> baseClass = language.getClass().getSuperclass();
			
			if (baseClass != Language.class)
			{
				result.add(language);
			}
		}
		
		return result;
	}

	protected static boolean ignoreSpellingError(String badWord, AppContext appContext)
	{
		if (badWord == null || badWord.length() == 0)
		{
			return true;
		}
		
		for (String ignoredFragment : ignoredFragments)
		{
			if (badWord.contains(ignoredFragment))
			{
				return true;
			}
		}
		
		if (Character.isUpperCase(badWord.charAt(0)))
		{
			return true;
		}
		
		return isIgnoredWord(badWord, appContext);
	}
	
	protected static boolean isIgnoredWord(String badWord, AppContext appContext)
	{
		badWord = badWord.toUpperCase();
		
		if (appContext.getName().toUpperCase().equals(badWord))
		{
			return true;
		}
		
		for (String ignoredWord : ignoredWords)
		{
			if (badWord.equals(ignoredWord.toUpperCase()))
			{
				return true;
			}
		}
		
		if (badWord.contains("@"))
		{
			return true;
		}
		
		return false;
	}
	
	protected String getString(String string)
	{
		if (null == string)
		{
			return "";
		}
		
		return string;
	}
	
	protected boolean isSimillar(String left, String right)
	{
		int d = LevenshteinDistance.getDefaultInstance().apply(left, right);

		int m_len = Math.max(left.length(), right.length());

		float d1 = 0;

		if (m_len == 0)
		{
			d1 = 0;
		}
		else
		{
			d1 = (float) d / (float) m_len;
		}
		
		return d1 < 0.15;
	}
	
	protected static boolean isUpperCase(String string)
	{
		char[] chars = string.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isLetter(chars[i]))
			{
				if (!Character.isUpperCase(chars[i]))
				{
					return false;
				}
			}
		}

		return true;
	}
	
	protected static boolean isSpellingCorrect(String message, List<Language> languages)
	{
		for (var language : languages)
		{
			var langTool = new JLanguageTool(language);

			try
			{
				var matches = langTool.check(message);
			
				for (var match : matches)
				{
					System.out.println("Grammar check failed: " + match.getMessage() + ". Possible fixes: " + match.getSuggestedReplacements() + ". Text was: " + message);				
				
					return false;
				}			
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		return true;
	}
	
	protected boolean isTranslateable(String message, AppContext appContext)
	{
		if (null == message)
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
		
		if ((wordsCount == 0) || (isIgnoredWord(message, appContext)))
		{
			System.out.println("ignoring: " + message);
			
			return false;
		}

		return true;
	}	
	
	private static String[] ignoredWords = new String[] {"dd-mm-yy", "apk", "facebook", "sdcard", "bluetooth", "png", "gif", "microsoft", "youtube", "paypal", "ru", "iCloud", "AppleId", "nl", "yyyy", "javascript", "js", "wikipedia", "uk", "edu", "wifi", "iTouch", "url", "tv", "github", "linkedin", "google", "twitter", "email", "wizzair", "wi-fi", "csv", "mBar", "mmHg", "latin", "hPa", "reCAPTCHA", "app", "mAh", "kg", "ft.", "Google Drive", "lb", "lbs", "Kcal", "Apple Watch", "MB", "dd/mm", "mm", "min", "km/h", "mph", "kph", "cm", "mi/h", "lat/long", "hh:mm", "mmmm yyyy", "sec", "Pinterest", "Creative Cloud", "rgb", "Adobe ID", "Adobe", "Adobe Photoshop Express", "Instagram", "Tumblr", "Dropbox", "mbps", "Alipay", "WeChat", "Android", "Mastercard", "iban", "sms", "Weibo", "Maestro", "Visa", "AMEX", "min", "max", "sin", "tan", "cos", "det", "asin", "atan", "USB", "jpeg", "mjpeg", "cpu", "led", "km" };
	private static String[] ignoredFragments = new String[] { "®", "™", "?", "$", "@", "°", "©", "_", "http://", "https://" };	
	private static HashMap<String, List<Language>> languagesCache = new HashMap<>();
}
