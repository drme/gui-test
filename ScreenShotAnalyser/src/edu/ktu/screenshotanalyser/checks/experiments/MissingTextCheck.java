package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.List;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
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
		String faulty = "";
		
		//StringBuilder allMessages = new StringBuilder();
		//state.getImageControls().stream().filter(p -> p.getText() != null).forEach(p -> allMessages.append(" " + p.getText()));
		String allMessages = state.getImageTexts();

		if (null == allMessages)
		{
			return;
		}
		
		String bigMessage = allMessages.toString().toLowerCase().replaceAll("\n", " ").replaceAll("  ", " ").trim();
		
		for (Control expected : state.getActualControls())
		{
			if (null != expected.getText())
			{
			String expectedMessage = expected.getText().toLowerCase().replaceAll("  ", " ").trim();
			
			if (expectedMessage.length() > 0)
			{
			if (false == bigMessage.contains(expectedMessage))
			{
				faulty += " " + ("missing: [" + expectedMessage + "] in [" + bigMessage + "] " + state.getImageFile().getName());
			}
			else
			{
//				System.out.println("found: " + expected + " in " + bigMessage);
			}
			}
			}
		}
		
		if (faulty.length() > 0)
		{
//			state.dumpRecognitionDebug();
			
			failures.addFailure(new CheckResult(state, this, faulty));

			
		}
	
		// TODO Auto-generated method stub
//		return null;
		
		
		
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
	


}
