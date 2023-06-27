/*

package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.avformat.AVOutputFormat.Control_message_AVFormatContext_int_Pointer_long;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.DefectAnnotation;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.IResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.Drawable;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.context.UIElement;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.utils.ImageUtils;
import edu.ktu.screenshotanalyser.utils.HashSetFloat;

public class BadScalingCheck extends BaseRuleCheck implements IStateRuleChecker
{
	public BadScalingCheck()
	{
		super(9, "Bad Scaling");
	}

	@Override
	public void analyze(State state, StateCheckResults results)
	{
		var imageControls = state.getActualControls().stream().filter(BaseRuleCheck::isVisibleImage).filter(p -> !Control.LAUNCHER_PACKAGE.equals(p.getPackageName())).toList();
		var adControls = state.getActualControls().stream().filter(p -> p.isAd()).toList();
		var navigatioBar = state.getActualControls().stream().filter(p -> "android:id/navigationBarBackground".equals(p.getResourceId())).toList();
		var navigationBarY = navigatioBar.isEmpty() ? null : navigatioBar.get(0).getBounds().y;
		
		if (checkDistortedButtons(results, state, imageControls, navigationBarY))
		{
			return;
		}
		
		if (checkEmptySpace(results, state))
		{
			return;
		}
		
		if (checkImageControls(results, state, imageControls, adControls, navigationBarY))
		{
			return;
		}
	
		if (checkImageConrolsRaw(results, state, imageControls, adControls, navigationBarY))
		{
			return;
		}

		
		
		
		

		
		/*
		
		if (imageControls.isEmpty())
		{
			result = new CheckResult(state, this, null, 1);
			
			var debugImage = result.getResultImage();
			
			var c = state.getImageControls();
			
			

			
			
			
			
			
			for (var control : c)
			{
		 		var matchedAspect = false;
	   		Float wrongAspect = null;
	   		var tooSmall = "";
				
	    	var expectedAspect = (float)control.getBounds().width / (float)control.getBounds().height; 
	   		
	    	int x = control.getBounds().x;
	    	int y = control.getBounds().y;
	    	int w = control.getBounds().width;
	    	int h = control.getBounds().height;

	    	if (x+w > state.getImage().getWidth())
	    	{
	    		w = state.getImage().getWidth() - x;
	    	}

	    	if (y+h > state.getImage().getHeight())
	    	{
	    		h = state.getImage().getHeight() - y;
	    	}
	    	
	    	
	    	
				debugImage.drawBounds(control.getBounds());
	    	
	    	
				
				var imgs = state.getAppContext().findDrawable(state.getImage().getSubimage(x, y, w, h));
				
				if (imgs.isEmpty())
				{
					continue;
				}

	   		var aspect = getAspect(imgs);

	   		if (null == aspect)
		   	{
		    	matchedAspect = true;
	   			continue;
		   	}
	   		
	   		wrongAspect = aspect.aspect();
	   		
	   		if (aspect.missMatchedImages())
	   		{
   // 			var debugImage = result.getResultImage();
  //  			debugImage.drawBounds(control.getBounds());
 // 				debugImage.drawText(expectedAspect + "->" + aspect.aspect(), control.getBounds());				    			
//    			haveDefect = true;
    			
    //			break;
	   		}

	   		if (control.isAtTheScreenEdge())
	   		{
		    	matchedAspect = true;
	   			continue;
	   		}

	   		for (var a : aspect.aspects())
	   		{
	   			if (Math.abs(expectedAspect - a) < 0.3)
	   			{
	   				matchedAspect = true;
	   				
	   				break;
	   			}
	   		}
	   		
	   		if (matchedAspect)
	   		{
	   			continue;
	   		}

	   		
	   		if (!matchedAspect)
				{
					if (!isCutByAd(control, adControls) && !isCutByParent(control))
					{
				   // 		if (ImageUtils.isSimillar(state.getImage().getSubimage(control.getBounds().x, control.getBounds().y, control.getBounds().width, control.getBounds().height), image.getImageFile()))
//				    			System.out.println("" + state.getImageFile().getAbsolutePath());
						
						result = new CheckResult(state, this, null, 1);
				    			
	    		//	var debugImage = result.getResultImage();

	    			debugImage.drawBounds(control.getBounds());
	  				debugImage.drawText(tooSmall + " " + expectedAspect + "->" + wrongAspect, control.getBounds());				    			
					}
				}					   		

    	}			
			
			
			
			
			
			
			
		
			/-*
			
			result = new CheckResult(state, this, null, 1);
			
			for (var a : c)
			{
			var debugImage = result.getResultImage();

			debugImage.drawBounds(a.getBounds());
			
			}
			
			result.getResultImage().save("d:/debug/" + "a_" + state.getAppContext().getPackage() + "-" + state.getImageFile().getName() + UUID.randomUUID().toString() + "1.png");
			
			
			*-/
			
		} *-/
	}

	private boolean checkImageConrolsRaw(StateCheckResults result, State state, List<Control> imageControls, List<Control> adControls, Integer navigationBarY)
	{
		var stateWidth = (float)state.getImage().getWidth();
		var stateHeight = (float)state.getImage().getHeight();
		
		var filteredImageControls = imageControls.stream().filter(p -> "android.widget.ImageView".equals(p.getType())).filter(p -> (float)p.getBounds().width / stateWidth > 0.3 || (float)p.getBounds().height / stateHeight > 0.3).toList();
		
		for (var control : filteredImageControls)
		{
			if (checkImageControlRaw(result, state, control, adControls, navigationBarY))
			{
				return true;
			}
		}
		
		return false;
	}

	private boolean checkImageControlRaw(StateCheckResults result, State state, Control control, List<Control> adControls, Integer navigationBarY)
	{
		if (control.isAtTheScreenEdge(navigationBarY))
 		{
 			return false;
 		}
 		
 		if (isCutByScrollView(control))
 		{
 			return false;
 		} 		

  	var actualAspect = (float)control.getBounds().width / (float)control.getBounds().height; 
 		
  	var x = control.getBounds().x;
  	var y = control.getBounds().y;
  	var w = control.getBounds().width;
  	var h = control.getBounds().height;

  	if (x + w > state.getImage().getWidth())
  	{
  		w = state.getImage().getWidth() - x;
  	}

  	if (y + h > state.getImage().getHeight())
  	{
  		h = state.getImage().getHeight() - y;
  	}

		var subImage = ImageUtils.scale(ImageUtils.bufferedImage2Mat(state.getImage().getSubimage(x, y, w, h)), 300, 300);
		var targetImages = state.getAppContext().findDrawable(subImage);
			
		if (targetImages.size() != 1)
		{
			return false;
		}
		
		var expectedAspect = targetImages.get(0).getAspect();
		var delta = 0.3f;
   		
		if ((Math.abs(expectedAspect - actualAspect) > delta) && !isCutByAd(control, adControls) && !isCutByParent(control))
		{
			result.addAnnotation(new DefectAnnotation(this, control.getBounds(), "" + expectedAspect + " != " + actualAspect));
	   			
			return true;
		}
		
		return false;
	}
	
	private boolean checkImageControls(StateCheckResults result, State state, List<Control> imageControls, List<Control> adControls, Integer navigationBarY)
	{
		for (var control : imageControls)
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
		if (control.isAtTheScreenEdge(navigationBarY))
 		{
 			return false;
 		}
 		
 		if (isCutByScrollView(control))
 		{
 			return false;
 		} 		
 		
		var elements = state.getAppContext().findElement(control);
		
		if (elements.isEmpty())
		{
			return false;
		}

		var backgroundElements = elements.stream().filter(p -> p.getBackground() != null && !p.getBackground().isEmpty() && p.getBackground().startsWith("@drawable/")).toList();
		
		if ((backgroundElements.isEmpty()) || (backgroundElements.size() != elements.size()))
		{
			return false;
		}
		
  	var expectedAspect = (float)control.getBounds().width / (float)control.getBounds().height; 

		for (var element : backgroundElements)
		{
	    var images = state.getAppContext().findDrawable(element.getBackground()).stream().filter(p -> !p.is1x1()).toList();
	    
	    if (images.isEmpty())
	    {
	    	continue;
	    }
	    
	    var hasNinePatch = images.stream().anyMatch(p -> p.getImageFile().getName().endsWith(".9.png"));

	    if (hasNinePatch)
	    {
	    	return false;
	    }
	    
	    if (!hasHighResolutionImage(images, control))
	    {
	    	result.addAnnotation(new DefectAnnotation(control.getBounds(), control.getBounds().width + "x" + control.getBounds().height + " -> " + images.get(0).toString()));
	    	
	    	return true;
	    }

   		var aspect = getAspect(images);

   		if (aspect.missMatchedImages())
   		{
   			result.addAnnotation(new DefectAnnotation(control.getBounds(), "missmatched aspects"));
   			
   			return true;
   		}
   		
   		float delta = 0.3f;
   		
 			if ((Math.abs(expectedAspect - aspect.aspect()) > delta) && (Math.abs(expectedAspect - aspect.trimmedAspect()) > delta))
 			{
 				if (state.isLandscape() && aspect.aspectLandscape() != null && ((Math.abs(expectedAspect - aspect.aspectLandscape()) < delta) || (Math.abs(expectedAspect - aspect.aspectLandscapeTrimmed()) < delta)))
 				{
 					// ok for landscape
 				}
 				else
 				{
 					if (!isCutByAd(control, adControls) && !isCutByParent(control))
 					{ 					
 						result.addAnnotation(new DefectAnnotation(control.getBounds(), "" + expectedAspect + " != " + aspect.aspect()));
 	   			
 						return true;
 					}
 				}
 			}
		}
		
		return false;
	}
	
	private boolean hasHighResolutionImage(List<Drawable> images, Control control)
	{
  	for (var image : images)
  	{
  		if (image.canFit(control.getBounds()))
  		{
  			return true;
  		}
  	}
  	
  	return false;
	}

	private boolean checkEmptySpace(StateCheckResults result, State state)
	{
		if (!state.getTestDevice().device().tablet())
		{
			return false;
		}
		
		var image = state.getImage();
		
		if (null == image)
		{
			return false;
		}

		var emptyArea = findEmptyArea(image);

		if (null == emptyArea)
		{
			return false;
		}
		
		var ratio = (float)emptyArea.height / (float)image.getHeight();
		
		if (ratio < 0.57 || ratio > 0.9)
		{
			return false;
		}
		
		result.addAnnotation(new DefectAnnotation(emptyArea, "empty"));
		
		return true;
	}

	private boolean checkDistortedButtons(StateCheckResults result, State state, List<Control> imageControls, Integer navigatioBarY)
	{
		if (!state.getTestDevice().device().tablet())
		{
			return false;
		}
		
		var buttons = imageControls.stream().filter(Control::isClickable);
		var wideButtons = buttons.filter(p -> (float)p.getBounds().width / (float)p.getBounds().height > 10.0).filter(p -> !p.isAtTheScreenEdge(navigatioBarY)).toList();

		if (wideButtons.isEmpty())
		{
			return false;
		}

		wideButtons.forEach(p -> result.addAnnotation(new DefectAnnotation(p.getBounds(), null)));
		
		return true;
	}

	private boolean isCutByParent(Control control)
	{
	//	if (control.getParent() == null)
	//	{
			return false;
//		}
		
//		if (control.getBounds().y + control.getBounds().height == control.getParent().getBounds().y + control.getParent().getBounds().height)
//		{
	//		return true;
		//}
		
//		return false;
	}	
	
	private boolean isCutByAd(Control control, List<Control> adControls)
	{
		if (adControls.isEmpty())
		{
			return false;
		}
		
		for (var adControl : adControls)
		{
			if (control.getBounds().y + control.getBounds().height == adControl.getBounds().y)
			{
				return true;
			}
		}
		
		return false;
	}

	private record ImageAspect(float aspect, float trimmedAspect, Float aspectLandscape, Float aspectLandscapeTrimmed, boolean missMatchedImages)
	{
	}
	
	private ImageAspect getAspect(List<Drawable> images)
	{
		var delta = 0.0001f;
		var aspectsPortrait = new HashSetFloat(delta); 
		var aspectsLandscape = new HashSetFloat(delta);
		var aspectsPortraitTrimmed = new HashSetFloat(delta); 
		var aspectsLandscapeTrimmed = new HashSetFloat(delta);
		
		for (var image : images)
		{
			var imageAspect = image.getAspect();
			var imageAspectTrimmed = image.getTrimmedAspect();
			
			if (image.getImageFile().getAbsolutePath().contains("-land"))
			{
				aspectsLandscape.add(imageAspect);
				aspectsLandscapeTrimmed.add(imageAspectTrimmed);
			}
			else
			{
				aspectsPortrait.add(imageAspect);
				aspectsPortraitTrimmed.add(imageAspectTrimmed);
			}				
		}

		return new ImageAspect(aspectsPortrait.first(), aspectsPortraitTrimmed.first(), aspectsLandscape.first(), aspectsLandscapeTrimmed.first(), aspectsPortrait.size() > 1); 
	}
	

}


*/