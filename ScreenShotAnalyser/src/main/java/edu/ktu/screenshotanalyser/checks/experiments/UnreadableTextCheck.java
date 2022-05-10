package edu.ktu.screenshotanalyser.checks.experiments;

import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;

public class UnreadableTextCheck extends BaseRuleCheck implements IStateRuleChecker
{
	/**
	 * Minimum readable text size in millimeters.
	 */
	private final double minHeight = 2.0;

	public UnreadableTextCheck()
	{
		super(19, "TS2");
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		var tooSmall = new StringBuilder("");

		for (var message : state.getActualControls())
		{
			String result = isTextTooSmall(message, state);

			if (null != result)
			{
				tooSmall.append(" " + result);
			}
		}

		var tooSmallText = tooSmall.toString().trim();

		if (tooSmallText.length() > 0)
		{
			// ???
			failures.addFailure(new CheckResult(state, this, tooSmallText.replace('\n', ' '), tooSmallText.length()));
		}
	}

	private String isTextTooSmall(Control message, State state)
	{
		if (message.getText() != null)
		{
			if (message.getText().trim().length() > 0)
			{
				if ((message.getBounds().height > 3) && (message.getBounds().width > 3))
				{
					var actualHeight = state.getTestDevice().getPhysicalSize(message.getBounds().height);

					if (actualHeight < this.minHeight)
					{
						if (message.getBounds().y + message.getBounds().height >= state.getImageSize().height)
						{
							System.out.println("skipp - screen edge");
						}
						else
						{
							if ((null != message.getParent()) && (message.getBounds().y + message.getBounds().height >= message.getParent().getBounds().y + message.getParent().getBounds().height))
							{
								System.out.println("skipp - parent edge");
							}
							else
							{
								return "Text : [" + message.getText() + "] too small " + actualHeight + "mm " + message.getBounds().toString();
							}
						}
					}
				}
			}
		}

		return null;
	}
}
