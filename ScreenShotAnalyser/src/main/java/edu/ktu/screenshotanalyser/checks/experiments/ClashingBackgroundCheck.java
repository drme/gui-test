package edu.ktu.screenshotanalyser.checks.experiments;



import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultImage;
import edu.ktu.screenshotanalyser.checks.IResultsCollector;
import edu.ktu.screenshotanalyser.context.Color;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.TextExtractor;

public class ClashingBackgroundCheck extends BaseRuleCheck implements IStateRuleChecker
{
	public ClashingBackgroundCheck()
	{
		super(20, "ClashingBackground");
	}

	@Override
	public void analyze(State state, StateCheckResults results)
	{
		if (state.getImageFile().getAbsolutePath().endsWith("nx5-240x320-de\\com.coolringtones.topringtones2017.ringtonesforoppo\\states\\screen_2018-12-28_044139.png"))
		{
			System.out.println("" + state.getStateFile().getAbsolutePath());
			System.out.println("" + state.getStateFile().getAbsolutePath());
			
			List<Control> textControls = state.getActualControls().stream().filter(p -> !shouldSkipControl(p, state) && hasText(p)).toList();
			
			for (var control : textControls)
			{
				System.out.print(control.getText() + "-" + control.getResourceId());
				
	 {
	/*
				    
				    var element = state.getAppContext().findElement(control);
				 
				    if (null != element)
				    {
				    	
				    	

							
							var intersection = control.getBounds();
									

							
							var image = state.getImage().getSubimage(intersection.x, intersection.y, intersection.width, intersection.height);				    	
				    	
							var averageColor = averageColor(image, 0,0, image.getWidth(), image.getHeight(), element.getTextColor());

							
				    	System.out.println("[" + element.getTextColor() + "] >  [" + averageColor + "]");
							
				    }
				    */
				    
				}
				

				
				Rect bounds = control.getBounds();
				
				String language = state.predictLanguage();
				
				if (false == language.equals("eng"))
				{
//					continue;
				}
				
				TextExtractor textsExtractor = new TextExtractor(0.65f, language);
				
				//System.out.println(language);
				
				//break;
				
				
				var result = textsExtractor.extract(state.getImage(), bounds, (x) -> true);
		
					
				if (result != null)
				{
					System.out.println(" -> " + result);
				}
				else
				{
					System.out.println("");
				}
			}
		}
		

	}
	
	
	/*
	 * Where bi is your image, (x0,y0) is your upper left coordinate, and (w,h)
	 * are your width and height respectively
	 */
	public static Color averageColor(BufferedImage bi, int x0, int y0, int w,
	        int h, Color skipColor) {
	    int x1 = x0 + w;
	    int y1 = y0 + h;
	    long sumr = 0, sumg = 0, sumb = 0;
	    
	    int skipped = 0;
	    
	    for (int x = x0; x < x1; x++) {
	        for (int y = y0; y < y1; y++) {
	        	java.awt.Color pixel = new java.awt.Color(bi.getRGB(x, y));
	        	
	        	if (pixel.getRed() != skipColor.r && pixel.getGreen() != skipColor.g && pixel.getBlue() != skipColor.b)
	        	{
	        	
	            sumr += pixel.getRed();
	            sumg += pixel.getGreen();
	            sumb += pixel.getBlue();
	            
	        	}
	        	else
	        	{
	        		skipped++;
	        	}
	        }
	    }
	    int num = w * h - skipped;
	    return new Color((int)(sumr / num), (int)(sumg / num), (int)(sumb / num));
	}	
	
	
	private boolean hasText(Control control)
	{
		return (control.getText() != null) && (!control.getText().isEmpty());
	}
}
