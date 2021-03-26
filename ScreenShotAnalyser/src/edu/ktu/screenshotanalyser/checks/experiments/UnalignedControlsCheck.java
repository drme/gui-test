package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultImage;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;

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
		
		var editFields = state.getActualControls().stream().filter(p-> p.getType().contains("EditText")).toArray(Control[]::new);
		var labelFields = state.getActualControls().stream().filter(p-> p.getType().contains("TextView") && !p.getText().isBlank() && p.getBounds().width > 2);

//		ResultImage resultImage = new ResultImage(state.getImageFile());

		boolean hasDefect = false;
		
		for (var p : editFields)
		{
			var label = getLabel(p, labelFields);
		
			if (null != label)
			{
				if (false == isAligned(p, label))
				{
		//			resultImage.drawBounds(p.getBounds());
		//			resultImage.drawBounds(label.getBounds());
				
					hasDefect = true;
				}
			}
		}	
		
		if (hasDefect)
		{
			System.out.println(state.getStateFile().toString());
			
		//	resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
		}
	}
	
	private boolean isAligned(Control editField, Control label)
	{
		return isAlignedVertically(editField, label);
	}
	
	private boolean isAlignedVertically(Control editField, Control label)
	{
		long editFieldCenterY = editField.getBounds().y + editField.getBounds().height / 2; 
		long labelCenterY = label.getBounds().y + label.getBounds().height / 2;
		
		return Math.abs(editFieldCenterY - labelCenterY) < 2;
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
		if (Math.abs(editField.getBounds().y - label.getBounds().y) > 10)
		{
			return false;
		}
		
		if (editField.getBounds().x + editField.getBounds().width > label.getBounds().x)
		{
			return false;
		}
		
		return true;		
	}

	private boolean isOnTop(Control editField, Control label)
	{
		return false;
	}
}
