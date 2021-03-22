package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.UUID;
import java.util.stream.Stream;
import edu.ktu.screenshotanalyser.Settings;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.utils.ResultImage;

public class UnalignedControlsCheck extends BaseRuleCheck implements IStateRuleChecker
{
	public UnalignedControlsCheck()
	{
		super(21, "Unaligned Controls");
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
//		if (!state.getStateFile().toString().equals("e:\\gui\\_r\\n5-480x800\\app.babychakra.babychakra\\states\\state_2019-01-25_025020.json"))
	//	{
		//	return;
//		}
		
		var editFields = state.getActualControls().stream().filter(p-> p.getType().contains("EditText"));
		var labelFields = state.getActualControls().stream().filter(p-> p.getType().contains("TextView"));

		
		ResultImage resultImage = new ResultImage(state.getImageFile());
		
		var misingLabels = editFields.filter(p -> hasLabel(p, labelFields, resultImage));
		
		
		//if ((editFields.toArray().length > 0) && (labelFields.toArray().length > 0))
		if (misingLabels.toArray().length > 0)
		{
		
		System.out.println("ffff");
		
		, ResultImage resultImage
		
		resultImage.drawBounds(editField.getBounds());
		resultImage.drawBounds(label.get().getBounds());

				

		
	//	resultImage.drawText(textFound.recognizedTexts + " | " + control.getText(), bounds);		
		
			resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
		
		}
		
	}
	
	private Control getLabel(Control editField, Stream<Control> labelFilds)
	{
		var label = labelFilds.filter(p-> isNearby(editField, p)).findFirst();
		
		if (false == label.isEmpty())
		{
			return label.get();
		}

		return null;
	}

	private boolean isNearby(Control editField, Control label)
	{
		return isOnLeft(editField, label) || isOnRight(editField, label) || isOnTop(editField, label);
	}
	
	private boolean isOnLeft(Control editField, Control label)
	{
		if (Math.abs(editField.getBounds().y - label.getBounds().y) > 10)
		{
			return false;
		}
		
		if (editField.getBounds().x < label.getBounds().x)
		{
			return false;
		}
		
		return true;
	}

	private boolean isOnRight(Control editField, Control label)
	{
		return false;
	}

	private boolean isOnTop(Control editField, Control label)
	{
		return false;
	}
}
