package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.List;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;

public class MissingTextCheck extends BaseRuleCheck implements IStateRuleChecker
{
	public MissingTextCheck()
	{
		super("TM1");
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		// TODO Auto-generated method stub
	}

	/*
	
	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		List<CheckResult> results = new ArrayList<>();

		for (String actualText : request.getActualTexts()) {
			boolean textRecognized = request.getExtractedTexts().stream()
					.anyMatch(eText -> actualText.equalsIgnoreCase(eText.getText()));
			if (!textRecognized) {
				results.add(CheckResult.Nok(type,
						String.format("[%s] device: Actual text '%s' was not recognized", request.getDevice(),
								actualText),

						request.getOriginalFile(), "-"));
			}
		}
		return results.toArray(new CheckResult[0]);
	}

	*/
	
//	@Override
//	public CheckResult[] analyze(State state)
//	{
/*		boolean faulty = false;
		
		StringBuilder allMessages = new StringBuilder();
		
		state.getActualMessages().forEach(p -> allMessages.append(" " + p.getMessage()));

		String bigMessage = allMessages.toString().toLowerCase().replaceAll("\n", " ").replaceAll("  ", " ").trim();
		
		for (String expected : state.getMessages())
		{
			expected = expected.toLowerCase().replaceAll("  ", " ").trim();
			
			if (false == bigMessage.contains(expected))
			{
				System.out.println("missing: [" + expected + "] in [" + bigMessage + "] " + state.getImageFile().getName());
				
				faulty = true;
			}
			else
			{
//				System.out.println("found: " + expected + " in " + bigMessage);
			}
		}
		
		if (faulty)
		{
//			state.dumpRecognitionDebug();
		}
	*/
		// TODO Auto-generated method stub
//		return null;
//	}

}
