package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import com.aliasi.util.Pair;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.State;

public class OffensiveMessagesCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public OffensiveMessagesCheck()
	{
		super(32, "OffensiveMessages");
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		var messages = state.getActualControls().stream().map(this::getText).filter(p -> isTranslateable(p, state.getAppContext()));
		
		var offensiveMessages = messages.filter(p -> isOffensive(p)).map(p -> p.replace("\r", " ").replace("\n", " ")).collect(Collectors.toList());
		
		if (offensiveMessages.size() > 0)
		{
			var error = ">> " + String.join(" | ", offensiveMessages.toArray(new String[0]));
			
			failures.addFailure(new CheckResult(state, this, error, offensiveMessages.size()));						
		}
	}
	
	private static boolean isOffensive(String message)
	{
		if (message.contains("!!"))
		{
			return true;
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
}
