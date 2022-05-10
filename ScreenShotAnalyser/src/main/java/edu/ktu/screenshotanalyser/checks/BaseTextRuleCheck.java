package edu.ktu.screenshotanalyser.checks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;
import org.slf4j.LoggerFactory;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.optimaize.langdetect.LanguageDetectorImpl;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.Control;

public abstract class BaseTextRuleCheck extends BaseRuleCheck
{
	protected BaseTextRuleCheck(long id, String ruleCode)
	{
		super(id, ruleCode);
	}
	
	protected static synchronized List<Language> getLanguageByCode(String languageCode)
	{
		languageCode = languageCode.toUpperCase();
		
		var languages = languagesCache.get(languageCode);

		if (null == languages)
		{
			languages = new ArrayList<>();
			
			for (var language : Languages.get())
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
		var languages = getLanguageByCode(languageCode);
		
		if (languages.isEmpty())
		{
			return true;
		}
		
		var mistypes = isSpellingCorrect(appContext, null, languages, message, false);
		
		return mistypes.trim().length() == 0;
	}

	protected String isSpellingCorrect(AppContext appContext, String mistypes, List<Language> languages, String message, boolean last)
	{
		var errors = new HashMap<Language, HashSet<String>>();

		for (var lang : languages)
		{
			errors.put(lang, new HashSet<String>());

			try
			{
				var langTool = getSpellChecker(lang);
				var matches = langTool.check(message);

				for (var match : matches)
				{
					var bad = message.substring(match.getFromPos(), match.getToPos());

					if (!ignoreSpellingError(bad, appContext))
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

		var bad = new HashSet<String>(errors.get(languages.get(0)));

		for (var i = 1; i < languages.size(); i++)
		{
			bad.retainAll(errors.get(languages.get(i)));
		}

		if ((!bad.isEmpty()) && (null != mistypes))
		{
			var sentenceLanguages = getLanguage(message);

			if ((sentenceLanguages != languages) && (!sentenceLanguages.isEmpty()))
			{
				var sentenceMistypes = isSpellingCorrect(appContext, null, sentenceLanguages, message, false);

				if (sentenceMistypes.length() == 0)
				{
					return mistypes;
				}
			}
		}

		if (null == mistypes)
		{
			mistypes = "";
		}

		for (var badWord : bad)
		{
			if (last || (!isSpellingCorrectAnyLanguage(appContext, badWord)))
			{
				mistypes += " " + badWord + "[" + languages.get(0).getShortCode() + "]";
			}
		}

		return mistypes;
	}
	
	private boolean isSpellingCorrectAnyLanguage(AppContext appContext, String message)
	{
		var strange = new String[] { "ą", "č", "ę", "ė", "į", "š", "ų", "ū", "ž" };

		var m = message.toLowerCase();

		for (var s : strange)
		{
			if (m.contains(s))
			{
				System.out.println("==== OK: " + message + " --> " + "//");

				return true;
			}
		}

		var languages = new Language[] { Languages.getLanguageForShortCode("en-US"), Languages.getLanguageForShortCode("en-GB"), Languages.getLanguageForShortCode("es") };

		int fail = 0;

		for (var lang : languages)
		{
			try
			{
				var langTool = getSpellChecker(lang);

				List<RuleMatch> matches;

				matches = langTool.check(message);

				if (matches.isEmpty())
				{
					System.out.println("==== OK: " + message + " --> " + lang.getName());
				}

				for (RuleMatch match : matches)
				{
					String bad = message.substring(match.getFromPos(), match.getToPos());

					if (false == ignoreSpellingError(bad, appContext))
					{
						fail++;
					}
				}

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return fail != languages.length;
	}
	
	protected String getText(Control control)
	{
		var result = new StringBuilder("");
		
		if (control.getText() != null)
		{
			result.append(control.getText() + ". ");
		}

		if (control.getContentDescription() != null)
		{
			result.append(control.getContentDescription() + ". ");
		}
		
		return result.toString().trim();
	}	
	
	private static String determineLanguage(String message)
	{
		// final LanguageIdentifier langIdentifier = new LanguageIdentifier(message);
		// return langIdentifier.getLanguage();

		synchronized (languagesCache)
		{
			if (languageDetector == null)
			{
				languageDetector = (OptimaizeLangDetector) new OptimaizeLangDetector().loadModels();

				var logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				var log = logContext.getLogger("com.optimaize.langdetect.LanguageDetectorImpl");
				log.setLevel(ch.qos.logback.classic.Level.OFF);

				var logger = LoggerFactory.getLogger(LanguageDetectorImpl.class);

				logger.debug("nnnn");
			}
		}

		return languageDetector.detect(message).getLanguage();
	}
	
	public static List<String> determineLanguageAll(String message)
	{
		return determineLanguageAll(message, 0.95f);
	}
	
	public static List<String> determineLanguageAll(String message, float level)
	{
		synchronized (languagesCache)
		{
			if (languageDetector == null)
			{
				languageDetector = (OptimaizeLangDetector) new OptimaizeLangDetector().loadModels();

				var logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				var log = logContext.getLogger("com.optimaize.langdetect.LanguageDetectorImpl");

				log.setLevel(Level.OFF);
			}
		}

		var lang = new ArrayList<String>();
		var result = languageDetector.detectAll(message);

		for (var r : result)
		{
			if (r.getRawScore() > level)
			{
				lang.add(r.getLanguage());
			}
		}

		return lang;
	}
	
	protected static Map<com.github.pemistahl.lingua.api.Language, Double> determineLanguageShort(String message)
	{
		if (message.equalsIgnoreCase("OK"))
		{
			var result = new HashMap<com.github.pemistahl.lingua.api.Language, Double>();
			
			result.put(com.github.pemistahl.lingua.api.Language.ENGLISH, 1.0);
			result.put(com.github.pemistahl.lingua.api.Language.GERMAN, 1.0);
			result.put(com.github.pemistahl.lingua.api.Language.FRENCH, 1.0);
			
			return result;
		}

		return languageDetectorShort.computeLanguageConfidenceValues(message);
	}	
	
	protected static List<String> determineLanguageShort(String message, float level)
	{
		var result = new ArrayList<String>();
		var languages = determineLanguageShort(message);
		
		for (var language : languages.keySet())
		{
			if (languages.get(language) > level)
			{
				result.add(language.getIsoCode639_1().name().toLowerCase());
			}
		}
		
		return result;
	}		

	protected static synchronized List<Language> getLanguage(String message)
	{
		var code = determineLanguage(message);

		// if (false == code.equals("en"))
		// {
		// return new ArrayList<>();
		// }

		return getLanguageByCode(code);
	}

	private static List<Language> removeBaseLanguageClass(List<Language> languages)
	{
		var result = new ArrayList<Language>();
		
		for (var language : languages)
		{
			var baseClass = language.getClass().getSuperclass();
			
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
		
		for (var ignoredFragment : ignoredFragments)
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
		
		for (var i = 0; i < badWord.length(); i++)
		{
			if (!Character.isAlphabetic(badWord.charAt(i)))
			{
				return true;
			}
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
		
		for (var ignoredWord : ignoredWords)
		{
			if (badWord.equalsIgnoreCase(ignoredWord))
			{
				return true;
			}
		}
		
		return badWord.contains("@");
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
		int distance = LevenshteinDistance.getDefaultInstance().apply(left, right);

		int length = Math.max(left.length(), right.length());

		float d1 = 0;

		if (length == 0)
		{
			d1 = 0;
		}
		else
		{
			d1 = (float) distance / (float) length;
		}
		
		return d1 < 0.15;
	}
	
	protected static boolean isUpperCase(String string)
	{
		var chars = string.toCharArray();

		for (var i = 0; i < chars.length; i++)
		{
			if ((Character.isLetter(chars[i])) && (!Character.isUpperCase(chars[i])))
			{
				return false;
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
		
		var words = message.split("[ \t]");
		
		int wordsCount = 0;
		
		for (var word : words)
		{
			var newWord = new StringBuilder("");
			
			for (var i = 0; i < word.length(); i++)
			{
				if (Character.isLetter(word.charAt(i)) || ('\'' == word.charAt(i)))
				{
					newWord.append(word.charAt(i));
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
			//System.out.println("ignoring: " + message);
			
			return false;
		}

		return true;
	}	
	
	private static synchronized JLanguageTool getSpellChecker(Language language)
	{
		var checker = spellCheckers.get(language);
		
		if (null == checker)
		{
			checker = new JLanguageTool(language);
			
			for (var rule : checker.getAllRules())
			{
				if (!rule.isDictionaryBasedSpellingRule())
				{
					checker.disableRule(rule.getId());
				}
			}			
			
			spellCheckers.put(language, checker);
		}
		else
		{
			//System.out.println("cached: " + language.getName());
		}
		
		return checker;
	}
	
	private static HashMap<Language, JLanguageTool> spellCheckers = new HashMap<>();
	private static String[] ignoredWords = new String[] {"dd-mm-yy", "apk", "facebook", "sdcard", "bluetooth", "png", "gif", "microsoft", "youtube", "paypal", "ru", "iCloud", "AppleId", "nl", "yyyy", "javascript", "js", "wikipedia", "uk", "edu", "wifi", "iTouch", "url", "tv", "github", "linkedin", "google", "twitter", "email", "wizzair", "wi-fi", "csv", "mBar", "mmHg", "latin", "hPa", "reCAPTCHA", "app", "mAh", "kg", "ft.", "Google Drive", "lb", "lbs", "Kcal", "Apple Watch", "MB", "dd/mm", "mm", "min", "km/h", "mph", "kph", "cm", "mi/h", "lat/long", "hh:mm", "mmmm yyyy", "sec", "Pinterest", "Creative Cloud", "rgb", "Adobe ID", "Adobe", "Adobe Photoshop Express", "Instagram", "Tumblr", "Dropbox", "mbps", "Alipay", "WeChat", "Android", "Mastercard", "iban", "sms", "Weibo", "Maestro", "Visa", "AMEX", "min", "max", "sin", "tan", "cos", "det", "asin", "atan", "USB", "jpeg", "mjpeg", "cpu", "led", "km", "google play", "google" };
	private static String[] ignoredFragments = new String[] { "�", "�", "?", "$", "@", "�", "�", "_", "http://", "https://" };	
	private static HashMap<String, List<Language>> languagesCache = new HashMap<>();
	private static OptimaizeLangDetector languageDetector = null;
	private static final com.github.pemistahl.lingua.api.LanguageDetector languageDetectorShort = LanguageDetectorBuilder.fromAllLanguages().build();
}
