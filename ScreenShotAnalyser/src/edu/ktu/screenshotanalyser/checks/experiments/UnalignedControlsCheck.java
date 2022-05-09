package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.ComponentOrientation;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.OperatingSystemMXBean;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.languagetool.Language;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultImage;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.StatisticsManager;

public class UnalignedControlsCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public UnalignedControlsCheck()
	{
		super(21, "Unaligned Controls");
	}

	public static void main(String[] args) throws IOException, SQLException
	{
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
//		var s = new State("AA", null, new File("E:\\gui\\_r\\c-2560x1080-fr\\lt.nordea.android\\states\\screen_2019-01-05_115204.png"), new File("E:\\gui\\_r\\c-2560x1080-fr\\lt.nordea.android\\states\\state_2019-01-05_115204.json"), null);
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);						
		
		var db = new StatisticsManager();
		var rule = new UnalignedControlsCheck();
		var images = db.getList("select s.FileName from ScreenShot s LEFT join TestRunDefect d on d.ScreenShotId = s.Id WHERE d.TestRunId = 10316 and d.DefectTypeId <> 34");
		
		for (var image : images)
		{
			var file = "E:\\gui\\_r\\" + image;
			var stateFile = "E:\\gui\\_r\\" + image.replace("\\screen_", "\\state_").replace(".png", ".json").replace(".jpg", ".json"); 
			
			var state = new State("AA", null, new File(file), new File(stateFile), null);

			rule.analyze(state, null);
		}
	}
	
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		if (true == checkVerticalAlingment(state, failures))
		{
			return;
		}

		if (true == checkLabelAlignment(state, failures))
		{
			return;
		}
		
		/*
		
	
		var editFields = state.getActualControls().stream().filter(p -> p.getType() != null && p.getType().contains("EditText") && p.isVisible()).collect(Collectors.toList());

		for (var editfield : editFields)
		{
			var labels = new ArrayList<Control>();
			var y = getCenterY(editfield);
			
			for (var control : state.getActualControls())
			{
				if (control != editfield)
				{
					if ((control.getText() != null) && (control.getText().trim().length() > 0)) 
					{
						if (Math.abs(y - getCenterY(control)) <= 10)
						{
							labels.add(control);
						}
					}
				}
			}
			
			if (labels.size() > 0)
			{
				
				var leftLabels = new ArrayList<Control>();
				var rightLabels = new ArrayList<Control>();
				
				for (var label : labels)
				{
					
					if (isOnLeft(editfield, label))
					{
						leftLabels.add(label);
					}
				

					if (isOnRight(editfield, label))
					{
						rightLabels.add(label);
					}

					if (isRtl)
					{
						
						if ((leftLabels.size() != 0) && (rightLabels.size() == 0)) 
						{
							ResultImage resultImage = null;
							 resultImage = new ResultImage(state.getImageFile());								
							
								resultImage.drawBounds(editfield.getBounds());
								resultImage.drawBounds(rightLabels.get(0).getBounds());
											 
								resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
							 

								return;
						}
					}
					else
					{
						
						if ((rightLabels.size() != 0) && (leftLabels.size() == 0)) 
						{
							ResultImage resultImage = null;
							 resultImage = new ResultImage(state.getImageFile());								
							
								resultImage.drawBounds(editfield.getBounds());
								resultImage.drawBounds(rightLabels.get(0).getBounds());
											 
								resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
							 

								return;
						}
						
						
						
						
					}
					
					*/
					
					
					/*
					 * 
					 * 
					 * 				System.out.println(state.getStateFile().toString());
				

				ResultImage resultImage = null;
				 resultImage = new ResultImage(state.getImageFile());								
				
					resultImage.drawBounds(editfield.getBounds());
					resultImage.drawBounds(labels.get(0).getBounds());
								 
					resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
				 

					return;
					

					 *-/
					
					
				}
			}
		}
		
		

		
		/*
		
		//var labelFields = state.getActualControls().stream().filter(p -> p.getType() != null && p.getType().contains("TextView") && p.getText() != null && !p.getText().isBlank() && isVisible(p)).collect(Collectors.toList());

		ResultImage resultImage = null;

		boolean hasDefect = false;
		
		for (var p : editFields)
		{
			var label = getLabel(p, labelFields);
		
			if (null != label)
			{
				if (false == isAligned(p, label, !isRtl, isRtl))
				{
					if (null == resultImage)
					{
						 resultImage = new ResultImage(state.getImageFile());						
					}
					
					resultImage.drawBounds(p.getBounds());
					resultImage.drawBounds(label.getBounds());
				
					hasDefect = true;
				}
			}
		}	
		
		if (hasDefect)
		{
			System.out.println(state.getStateFile().toString());
			
			resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
			
			var result = new CheckResult(state, this, "1", 1);
			
			failures.addFailure(result);
		}
		
		*/
	}
	
	private boolean isUseable(Control control)
	{
		if (false == isVisible(control))
		{
			return false;
		}
		
		if (control.getSignature().contains("Layout"))
		{
			return false;
		}
		
		var type = control.getType() != null ? control.getType() : "";
		
		if (type.contains("TextView"))
		{
			return control.getText() != null && control.getText().trim().length() > 0;
		}
		
		return true;
	}
	
	private boolean checkVerticalAlingment(State state, ResultsCollector failures)
	{
		float pixelScale = state.getImageSize().height / 1080.0f; 
		
		var centerYdelta = 50.0f * pixelScale;
		var controls = state.getActualControls().stream().filter(p -> isUseable(p)).collect(Collectors.toList());
		
		var defectiveControls = new HashSet<Control>();
		
		for (var sourceControl : controls)
		{
			if (isEmpty(sourceControl, state.getImage()))
			{
				continue;
			}
			
			var nearbyControls = controls.stream().filter(p -> p.getParent() == sourceControl.getParent()).filter(p -> sourceControl != p && (isOnLeft(sourceControl, p, centerYdelta) || isOnRight(sourceControl, p, centerYdelta))).collect(Collectors.toList());
			
			nearbyControls = nearbyControls.stream().filter(p -> Math.abs(p.getBounds().height - sourceControl.getBounds().height) < 10 * pixelScale).collect(Collectors.toList());
			
			var sourceControlCenterY = getCenterY(sourceControl);

			Control nearest = null;
			float minDeltaY = Integer.MAX_VALUE;
			
			for (var nc : nearbyControls)
			{
				var deltaY = Math.abs(getCenterY(nc) - sourceControlCenterY);
				
				if (deltaY < minDeltaY)
				{
					if (false == isEmpty(nc, state.getImage()))
					{
						minDeltaY = deltaY;
						nearest = nc;
					}
				}
			}
			
			if (null != nearest)
			{
				if (minDeltaY > 3 * pixelScale)
				{
					defectiveControls.add(sourceControl);
					defectiveControls.add(nearest);

					if (null != failures)
					{
						failures.addFailure(new CheckResult(state, this, "unaligned vertically", 1));
					}
					
					annotateDefectImage(state, defectiveControls.stream().collect(Collectors.toList()));
				
					return true;
				}
			}
		}

		return false;
	}
	
	private boolean checkLabelAlignment(State state, ResultsCollector failures)
	{
		var allTexts = state.getActualControls().stream().map(this::getText).collect(Collectors.joining(". "));
		var languages = determineLanguageAll(allTexts, 0.8f);

		if (0 == languages.size())
		{
			languages = determineLanguageShort(allTexts, 0.8f);
		}

		if (languages.size() == 0)
		{
			return false;
		}

		var distinctLanguages = languages.stream().map(this::isRtl).distinct().collect(Collectors.toList());

		if (distinctLanguages.size() > 1)
		{
			return false;
		}

		var isRtl = distinctLanguages.get(0);

		var textFields = state.getActualControls().stream().filter(p -> p.getText() != null && p.getText().trim().length() > 0 && p.isVisible()).collect(Collectors.toList());

		var b1 = new ArrayList<Control>();

		for (var t : textFields)
		{
			if ((t.getType() != null) && (t.getType().contains("Switch")))
			{
				continue;
			}

			var txx = t.getText().trim();

			int letters = 0;

			for (int i = 0; i < txx.length(); i++)
			{
				if (Character.isAlphabetic(txx.charAt(i)))
				{
					letters++;
				}
			}

			if (letters == 0)
			{
				continue;
			}

			var r = t.getBounds();

			if ((r.width > 5) && (r.height > 5))
			{
				var image = state.getImage();

				if ((r.x >= image.getWidth()) || (r.y >= image.getHeight()) || (r.x + r.width >= image.getWidth()) || (r.y + r.height >= image.getHeight()))
				{
				}
				else
				{
					image = image.getSubimage(r.x, r.y, r.width, r.height);
					int leftMargin = getLeftMargin(image);
					int rightMargin = getRightMargin(image);

					if (!isRtl)
					{
						if (leftMargin > rightMargin)
						{
							float lp = (float) leftMargin / (float) image.getWidth();
							float rp = (float) rightMargin / (float) image.getWidth();

							if ((lp > rp * 2) && (lp > 0.2))
							{
								b1.add(t);

								if (((float) b1.size() / (float) textFields.size()) > 0.7)
								{
									annotateDefectImage(state, b1);

									return true;
								}
							}
						}
					}
				}
			}
		}

		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private int getLeftMargin(BufferedImage image)
	{
		int c = image.getRGB(0, 0);
		
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				if (c != image.getRGB(x, y))
				{
					return x;
				}
			}
		}
		
		return image.getWidth();
	}
	
	private int getRightMargin(BufferedImage image)
	{
		int c = image.getRGB(image.getWidth() - 1, 0);
		
		for (int x = image.getWidth() - 1; x >= 0; x--)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				if (c != image.getRGB(x, y))
				{
					return image.getWidth() - x;
				}
			}
		}
		
		return image.getWidth();
	}
	
	private static boolean isEmpty(Control control, BufferedImage image)
	{
		var r = control.getBounds();

		if ((r.x >= image.getWidth()) || (r.y >= image.getHeight()) || (r.x + r.width >= image.getWidth()) || (r.y + r.height >= image.getHeight()))
		{
			return true;
		}
		
		image = image.getSubimage(r.x, r.y, r.width, r.height);
		
		int c = image.getRGB(0, 0);
		
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				if (c != image.getRGB(x, y))
				{
					return false;
				}
			}
		}		
		
		return true;
	}	
		
	private boolean isRtl(@NotNull String language)
	{
		return rtlLanguages.matcher(language).find();		
	}

	private boolean isVisible(Control control)
	{
		return control.getBounds().width > 2 && control.getBounds().height > 2 && control.getBounds().x > 0 && control.getBounds().y > 0;
	}
	
	private boolean isAligned(Control editField, Control label, boolean onlyLeft, boolean onlyRight)
	{
		if ((label.getBounds().x == editField.getBounds().x) && (label.getBounds().y == editField.getBounds().y))
		{
			return true;
		}
		
		return isAlignedVertically(editField, label, onlyLeft, onlyRight);
	}
	
	private boolean isAlignedVertically(Control editField, Control label, boolean onlyLeft, boolean onlyRight)
	{
		var onLeft = isOnLeft(editField, label, 10);
		var onRight = isOnRight(editField, label, 10);

		if (onLeft || onRight)
		{
			long editFieldCenterY = editField.getBounds().y + editField.getBounds().height / 2; 
			long labelCenterY = label.getBounds().y + label.getBounds().height / 2;
		
/*			if (Math.abs(editFieldCenterY - labelCenterY) < 7)
			{
				return false;
			}
			else*/
			{
				return (onlyLeft && onLeft) || (onlyRight && onRight);
			}
		}
		else
		{
			return true;
		}
	}	
	
	private Control getLabel(Control editField, List<Control> labelFilds)
	{
		var label = labelFilds.stream().filter(p -> isNearby(editField, p)).findFirst();
		
		if (false == label.isEmpty())
		{
			return label.get();
		}

		return null;
	}

	private boolean isNearby(Control editField, Control label)
	{
		return isOnLeft(editField, label, 10) || isOnRight(editField, label, 10) || isOnTop(editField, label);
	}
	
	private boolean isOnLeft(Control editField, Control label, float centerYdelta)
	{
		if (Math.abs(editField.getBounds().y - label.getBounds().y) > centerYdelta)
		{
			return false;
		}
		
		if (editField.getBounds().y >= label.getBounds().y + label.getBounds().height)
		{
			return false;
		}

		if (editField.getBounds().y + editField.getBounds().height <= label.getBounds().y)
		{
			return false;
		}
		
		if (label.getBounds().x + label.getBounds().width <= editField.getBounds().x)
		{
			return (editField.getBounds().x - label.getBounds().x - label.getBounds().width) < 50;
		}

		return false;
	}

	private boolean isOnRight(Control editField, Control label, float centerYdelta)
	{
		if (Math.abs(editField.getBounds().y - label.getBounds().y) > centerYdelta)
		{
			return false;
		}
		
		if (editField.getBounds().y >= label.getBounds().y + label.getBounds().height)
		{
			return false;
		}

		if (editField.getBounds().y + editField.getBounds().height <= label.getBounds().y)
		{
			return false;
		}
		
		if (editField.getBounds().x + editField.getBounds().width <= label.getBounds().x)
		{
			return label.getBounds().x - editField.getBounds().x - editField.getBounds().width < 50;
		}
		
		return false;		
	}

	private boolean isOnTop(Control editField, Control label)
	{
		return false;
	}
	
	private int getCenterY(Control control)
	{
		var r = control.getBounds();
		
		if (r.height > 2)
		{
			return r.y + r.height / 2;
		}
		
		return -1;
	}
	
	static
	{
		rtlLanguages = Pattern.compile("^(ar|dv|he|iw|fa|nqo|ps|sd|ug|ur|yi|.*[-_](Arab|Hebr|Thaa|Nkoo|Tfng))(?!.*[-_](Latn|Cyrl)($|-|_))($|-|_)");
	}
	
	private final static Pattern rtlLanguages;
}
