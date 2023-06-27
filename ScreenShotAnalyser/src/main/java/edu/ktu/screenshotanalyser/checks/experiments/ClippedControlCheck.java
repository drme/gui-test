/*
package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.stream.Collectors;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;

public class ClippedControlCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public ClippedControlCheck()
	{
		super(11, "Clipped Control");
	}

//	@Override
	public StateCheckResults analyze(State state)
	{
		var controls = state.getActualControls().stream().filter(p -> !shouldSkipControl(p, state));
		var clippedControls = controls.filter(p -> isClipped(p, state)).collect(Collectors.toList());

		if (!clippedControls.isEmpty())
		{
			return new StateCheckResults(state, this, "1", clippedControls.size());
		}
		
		return null;
	}

	private boolean isClipped(Control control, State state)
	{
		if ((control.getBounds().x + control.getBounds().width > state.getImageSize().width + 5) || (control.getBounds().y + control.getBounds().height > state.getImageSize().height + 5))
		{
			System.out.println("clipped " + control.getBounds().toString() + " | " + control.getSignature() + " | " + (control.getBounds().x + control.getBounds().width) + " > " + state.getImageSize().width + " | " + (control.getBounds().y + control.getBounds().height) + " > " + state.getImageSize().height);

			return true;
		}

		return false;
	}

	protected boolean shouldSkipControl(Control control, State state)
	{
		if (!control.isVisible())
		{
			return true;
		}

		if (("Test Ad".equals(control.getText())) || (control.isAd()))
		{
			return true;
		}

		var bounds = control.getBounds();

		if ((bounds.width <= 0) || (bounds.height <= 0))
		{
			return true;
		}

		if ((bounds.width <= 3) || (bounds.height <= 3))
		{
			return true;
		}

		if ((bounds.x >= state.getImageSize().width) || (bounds.y >= state.getImageSize().height))
		{
			return true;
		}

		if ((bounds.x + bounds.width <= 0) || (bounds.y + bounds.height <= 0))
		{
			return true;
		}

		// if ((control.getBounds().x + control.getBounds().width >= state.getImageSize().width) || (control.getBounds().y + control.getBounds().height >= state.getImageSize().height))
		// {
		// return true;
		// }

		return false;
	}
}
*/