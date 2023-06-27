package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.utils.LazyObject;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

public class SystemContext
{
	public String predictLanguage(String message)
	{
		var bestLanguage = this.categorizer.instance().predictLanguage(message);
		// System.out.println("Best language: " + bestLanguage.getLang());
		// System.out.println("Best language confidence: " + bestLanguage.getConfidence());

		// Get an array with the most probable languages
		for (var language : this.categorizer.instance().predictLanguages(message))
		{
			// System.out.println("language: " + language.getLang());
			// System.out.println("language confidence: " + language.getConfidence());
		}

		return bestLanguage.getLang();
	}

	private LazyObject<LanguageDetector> categorizer = new LazyObject<LanguageDetector>(() -> new LanguageDetectorME(new LanguageDetectorModel(getClass().getResourceAsStream("/langdetect-183.bin"))));
}
