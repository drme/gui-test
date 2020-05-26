/*

package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.Russian;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;
import edu.ktu.screenshotanalyser.rules.checkers.CheckRequest;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.rules.checkers.IRuleChecker;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

public class SD1_SynonymsUsage implements IRuleChecker {
	private final String type = "SD1";

	public static class FoundNoun {

		public FoundNoun(ResourceText text, String noun) {
			this.noun = noun;
			this.resourceText = text;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((noun == null) ? 0 : noun.hashCode());
			result = prime * result + ((resourceText == null) ? 0 : resourceText.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FoundNoun other = (FoundNoun) obj;
			if (noun == null) {
				if (other.noun != null)
					return false;
			} else if (!noun.equals(other.noun))
				return false;
			if (resourceText == null) {
				if (other.resourceText != null)
					return false;
			} else if (!resourceText.equals(other.resourceText))
				return false;
			return true;
		}

		String noun;
		ResourceText resourceText;
	}

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		final List<CheckResult> results = new ArrayList<>();
		for (Entry<String, List<ResourceText>> entrySet : context.getResources().entrySet()) {
			String langKey = entrySet.getKey();
		    if (!langKey.startsWith("default") && !langKey.startsWith("en") && !langKey.startsWith("uk")) {
		    	continue;
		    }
			List<ResourceText> texts = entrySet.getValue();
			List<FoundNoun> nouns = new ArrayList<FoundNoun>();
			for (ResourceText resourceText : texts) {
				String sentence = resourceText.getValue();
				Document doc = new Document(sentence);
				for (Sentence s : doc.sentences()) {
					for (int i = 0; i < s.words().size(); i++) {
						String tag = s.posTag(i);
						String word = s.word(i).toLowerCase();
						FoundNoun n = new FoundNoun(resourceText, word);
						if (tag == null || !tag.contains("NN") || nouns.contains(n)) {
							continue;
						}
						nouns.add(n);
					}
				}

			}
			try {

				Dictionary dictionary = Dictionary.getDefaultResourceInstance();
				for (FoundNoun nn : nouns) {
					String noun = nn.noun;
					ResourceText resourceText = nn.resourceText;
					IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, noun);
					if (indexWord == null) {
						//System.out.println("Can't find word: "+noun);
						continue;
					}
					for (Synset x : indexWord.getSenses()) {
						for (Word xW : x.getWords()) {
							String lemma = xW.getLemma().toLowerCase();
							if (noun.startsWith(lemma)) {
								continue;
							}
							for (FoundNoun nn2 : nouns) {
								if (nn2.equals(nn)) {
									continue;
								}
								ResourceText resourceText2 = nn2.resourceText;
								if (nn2.noun.equals(lemma)) {
									results.add(CheckResult.Nok(type,
											"Synonym usage check failed: " + noun + ". Used synonym was: " + lemma
													+ ". Text was: " + resourceText2.getValue() + "@"
													+ resourceText2.getKey() + ". Originally found at: "
													+ resourceText.getValue() + "@" + resourceText.getKey(),
											resourceText2.getFile() + "@" + resourceText2.getKey(), langKey));
								}
							}

						}
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		return results.toArray(new CheckResult[0]);

	}
}

*/
