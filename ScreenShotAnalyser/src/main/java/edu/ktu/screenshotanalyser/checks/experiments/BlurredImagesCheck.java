package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.image.BufferedImage;
import java.util.List;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.DefectAnnotation;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.Drawable;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.utils.BlurDetector;
import edu.ktu.screenshotanalyser.utils.ImageUtils;

public class BlurredImagesCheck extends BaseRuleCheck implements IStateRuleChecker
{
	public BlurredImagesCheck()
	{
		super(10, "LowResImage");
	}

	@Override
	public void analyze(State state, StateCheckResults result)
	{
//		if (!isReference(state.getImageFile())) return result;
		
		if (state.isLauncherScreen())
		{
			return;
		}

		var imageControls = state.getActualControls().stream().filter(BaseRuleCheck::isVisibleImage).filter(p -> !Control.LAUNCHER_PACKAGE.equals(p.getPackageName())).filter(p -> p.getType() != null).toList();
		var adControls = state.getActualControls().stream().filter(p -> p.isAd()).toList();

		if (checkImageControls(result, state, imageControls, adControls, state.getNavigationBarY()))
		{
			return;
		}		

		checkForSplashScreen(state, result);
	}

	private boolean checkForSplashScreen(State state, StateCheckResults result)
	{
		if (!state.isSplashScreen())
		{
			return false;
		}
		
		var contentControl = state.getControl("android:id/content");
			
		if (null == contentControl)
		{
			return false;
		}
		
		var splashLayout = state.getAppContext().getSplashLayout();
				
		if (null == splashLayout)
		{
			return false;
		}

		var rootElement = splashLayout.getRoot();
					
		if (null == rootElement)
		{
			return false;
		}
		
		var backGround = rootElement.getBackground();
						
		if (null != backGround)
		{
			var images = state.getAppContext().findDrawable(backGround, "drawable", true);
							
			if (images.isEmpty())
			{
				return false;
			}
			
			if (!hasHighResolutionImage(images, contentControl, 0.9f))
			{
				result.addAnnotation(new DefectAnnotation(this, contentControl.getBounds(), contentControl.getBounds().width + "x" + contentControl.getBounds().height + " -> " + images.get(0).toString()));

				return true;
			}

			return checkForSplashBlur(state, contentControl, result, 200.0f);
		}
		else if (isChildrenStacked(contentControl))
		{
			return checkForSplashBlur(state, contentControl, result, 50.0f);
		}
		
		return false;
	}
	
	private boolean checkForSplashBlur(State state, Control contentControl, StateCheckResults result, float blurThreshold)
	{
		var subImage = state.getControlImage(contentControl);
		var blurValue = isBlurry(subImage , blurThreshold);
		
		if (blurValue != null)
		{
			var emptyArea = ImageUtils.findEmptyArea(subImage);
								
			if (emptyArea == null || (float)emptyArea.height / (float)state.getImage().getHeight() < 0.3)
			{
				result.addAnnotation(new DefectAnnotation(this, contentControl.getBounds(), "Blur:" + blurValue));

				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkImageControls(StateCheckResults result, State state, List<Control> imageControls, List<Control> adControls, Integer navigationBarY)
	{
//		var stateWidth = state.getImageSize().width;
//		var stateHeight = state.getImageSize().height;
		
		
		// android.widget.ImageButton
		
		var filteredImageControls = imageControls.stream().filter(p -> p.getResourceId() != null)
//				.filter(p -> "android.widget.ImageView".equals(p.getType()))
//			.filter(p -> (float)p.getBounds().width < stateWidth && (float)p.getBounds().height < stateHeight)
//			.filter(p-> p.getBounds().width > 3)
				.toList();
		
		for (var control : filteredImageControls)
		{
			if (checkImageControl(result, state, control, adControls, navigationBarY))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkImageControl(StateCheckResults result, State state, Control control, List<Control> adControls, Integer navigationBarY)
	{
		//if (control.getChildren() != null && !control.getChildren().isEmpty())
		//{
		//	return false;
		//}

		if (control.isAtTheScreenEdge(navigationBarY))
		{
			return false;
		}

		if (isCutByScrollView(control))
		{
			return false;
		}

		var controlType = control.getType();
		
		if (controlType.contains("."))
		{
			var packageNames = controlType.split("[.]");
			
			controlType = packageNames[packageNames.length - 1];
		}
		
		var elements = state.getAppContext().findElement(control);
		final var controlTypeF = controlType;
		
		elements = elements.stream().filter(p -> isSameType(p.getType(), controlTypeF)).toList();

		if (elements.isEmpty())
		{
			return false;
		}

		var backgroundElements = elements.stream().filter(p -> p.hasImage()/* && FILL_PARENT.equals(p.getLayoutWidth()) && FILL_PARENT.equals(p.getLayoutHeight())*/).toList();

		if ((!backgroundElements.isEmpty()) && (backgroundElements.size() == elements.size()))
		{
			for (var element : backgroundElements)
			{
				if (element.getType().equals("Button"))
				{
					return false;
				}

				var images = state.getAppContext().findDrawable(element).stream().filter(p -> !p.is1x1()).toList();

				if (images.isEmpty())
				{
					continue;
				}

				var hasNinePatch = images.stream().anyMatch(p -> p.getImageFile().getName().endsWith(".9.png"));

				if (hasNinePatch)
				{
					return false;
				}

				if (!hasHighResolutionImage(images, control, 0.7f))
				{
					var maxImage = images.stream().max((a, b) -> a.getWidth() - b.getWidth());
					
					result.addAnnotation(new DefectAnnotation(this, control.getBounds(), control.getBounds().width + "x" + control.getBounds().height + " -> " + maxImage.get().toString()));

					return true;
				}
			}
		}

		var smallestIcon = state.getAppContext().getSmallestIcon();

		if (null != smallestIcon && smallestIcon.getWidth() > 30 && control.getBounds().width <= 15 && control.getBounds().height < 15 && control.getType().equals("android.widget.ImageView"))
		{
			var aspect = (float) control.getBounds().width / (float) control.getBounds().height;

			if (aspect > 0.95 && aspect < 1.05)
			{
				result.addAnnotation(new DefectAnnotation(this, control.getBounds(), "tiny"));

				return true;
			}
		}
		
		if (control.getBounds().width <= 200 || control.getBounds().height < 200)
		{
			return false;
		}
		
		
		if (control.getChildren() != null && !control.getChildren().isEmpty())
		{
			return false;
		}		
		
		
		var stateWidth = (float)state.getImageSize().width;
		var stateHeight = (float)state.getImageSize().height;
		
		
		
		
		if ((float)control.getBounds().width / stateWidth > 0.5f)
		{
//			return false;
		}
		
		if (!"android.widget.ImageView".equals(control.getType()))
		{
			return false;
		}
		
		var image = state.getControlImage(control);

		if (image.getWidth() != image.getHeight() && image.getWidth() > 300)
		{
			var blurValue = isBlurry(image, 5);
			
			if (blurValue != null)
			{
				result.addAnnotation(new DefectAnnotation(this, control.getBounds(), "eBlur:" + blurValue));
				
				return true;
			}
		}

		return false;
	}
	
	private boolean isSameType(String type1, String type2)
	{
		if (type1.contains(type2))
		{
			return true;
		}
		
		//System.out.println(type1 + " <> " + type2);
		
		return false;
	}

	private Double isBlurry(BufferedImage image, float minBlurValue)
	{
		if (null == image)
 		{
			return null;
 		}
		
		var matImage = ImageUtils.bufferedImage2Mat(image);
		var blurValue = BlurDetector.getBlurryVariance(matImage);
 			
		if (blurValue > 0.9 && blurValue < minBlurValue)
		{
			ImageUtils.saveTempImage(image, String.valueOf(blurValue));
			
    	return blurValue;
		}
		
		return null;
	}
	
	private boolean hasHighResolutionImage(List<Drawable> images, Control control, float tolerance)
	{
  	for (var image : images)
  	{
  		if (image.canFit(control.getBounds()))
  		{
  			return true;
  		}
  		
  		if (image.getWidth() >= control.getBounds().width)
  		{
  			return true;
  		}

  		if (image.getHeight() >= control.getBounds().height)
  		{
  			return true;
  		}
  	}

  	for (var image : images)
  	{
  		var diffX = (float)image.getWidth() / (float)control.getBounds().width;
  		var diffY = (float)image.getHeight() / (float)control.getBounds().height;
  		
  		if (diffX > tolerance && diffY > tolerance)
  		{
  			return true;
  		}
  	}
  	
  	return false;
	}
	
	private boolean isChildrenStacked(Control parent)
	{
		if (parent.getChildren().isEmpty())
		{
			return true;
		}
		
		if (parent.getChildren().size() == 1)
		{
			return parent.getBounds().equals(parent.getChildren().get(0).getBounds()) && isChildrenStacked(parent.getChildren().get(0)); 
		}
		
		return false;
	}
	
	private final static String FILL_PARENT = "fill_parent";
}
