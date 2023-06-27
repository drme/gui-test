package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultImage;
import edu.ktu.screenshotanalyser.checks.IResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.TextExtractor;

public class ObscuredControlCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public ObscuredControlCheck()
	{
		super(16, "Obscured Control");
	}

	//@Override
	public StateCheckResults analyze(State state)
	{
		var controls = state.getActualControls().stream().filter(p -> !shouldSkipControl(p, state)).collect(Collectors.toList());
		var overlapped = new ArrayList<Control>();
		
		for (var control1 : controls)
		{
	//		if ((control1.getText() == null) || (control1.getText().length() <= 0))
		//	{
	//			continue;
	//		}
			
			var found = false;
			
			for (var control2 : controls)
			{
				if (control1 != control2)
				{
					if (control1.getParent() == control2.getParent())
					{
						if ((control1.isOverlapping(control2)) && (!isInside(control1.getBounds(), control2.getBounds())) && (!isInside(control2.getBounds(), control1.getBounds()))) 
						{
							if (!control2.getSignature().contains(".View"))
							{
//								if (!hasText(state, control1.getBounds(), control1.getText()))
								{		
							overlapped.add(control1);
							overlapped.add(control2);
							
							System.out.println(control1.getSignature());
							System.out.println(control2.getSignature());
							
							
							found = true;
							
							break;
								}
							}
						}
					}
				}
			}
			
			if (found)
			{
				break;
			}
		}
		
		if (!overlapped.isEmpty())
		{
			var r1 = overlapped.get(0).getBounds();
			var r2 = overlapped.get(0).getBounds();
			
			var rect1 = new Rectangle(r1.x, r1.y, r1.width, r1.height);
			var rect2 = new Rectangle(r2.x, r2.y, r2.width, r2.height);
			
			var intersection = rect1.intersection(rect2);
			var image = state.getImage().getSubimage(intersection.x, intersection.y, intersection.width, intersection.height);
			var pixel = image.getRGB(0, 0);
			var uniform = true;
			
			for (var x = 0; x < image.getWidth(); x++)
			{
				for (var y = 0; y < image.getHeight(); y++)
				{
					var p = image.getRGB(x, y);
					
					if (p != pixel)
					{
						uniform = false;
						break;
					}
				}
				
				if (!uniform)
				{
					break;
				}
			}
			
			if (!uniform)
			{
				var resultImage = new ResultImage(state.getImageFile());
				var i = 0;

				for (var control : overlapped)
				{
					if (i++ % 2 == 0)
					{
						resultImage.drawBounds(control.getBounds(), 255, 0, 0);
					}
					else
					{
						resultImage.drawBounds(control.getBounds(), 0, 255, 0);
					}
				}

				// System.out.println(state.getStateFile().toString());

				resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");

				return null;//new StateCheckResults(state, this, "1", overlapped.size());

			}
		}
		
		return null;
	}

	public boolean contains(Rect r, Point p)
	{
		return r.x <= p.x && p.x <= r.x + r.width && r.y <= p.y && p.y <= r.y + r.height;
	}
	
	private boolean isInside(Rect a, Rect b)
	{
		if (!contains(a, b.tl()))
		{
			return false;
		}

		if (!contains(a, b.br()))
		{
			return false;
		}

		if (!contains(a, new Point(b.x, b.y + b.height)))
		{
			return false;
		}

		if (!contains(a, new Point(b.x + b.width, b.y)))
		{
			return false;
		}
		
		return true;
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

		if (control.getSignature().contains("Layout"))
		{
			return true;
		}

		var bounds = control.getBounds();

		if ((bounds.width <= 0) || (bounds.height <= 0))
		{
			return true;
		}

		if ((bounds.width <= 3) || (bounds.height <= 5))
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
	
	private boolean hasText(State state, Rect bounds, String expected)
	{
		
		
	var languages = new String[] { 
				
				/* "afr",
				"amh",
				"ara",
				"asm",
				"aze",
				"aze_cyrl",
				"bel",
				"ben",
				"bod",
				"bos",
				"bre",
				"bul",
				"cat",
				"ceb",
				"ces",
				"chi_sim",
				"chi_sim_vert",
				"chi_tra",
				"chi_tra_vert",
				"chr",
				"cos",
				"cym",
				"dan",*/
				"deu",
/*				"div",
				"dzo",
				"ell", */
				"eng",
/*				"enm",
				"epo",
				"est",
				"eus",
				"fao",
				"fas",
				"fil",
				"fin", */
				"fra",
/*				"frk",
				"frm",
				"fry",
				"gla",
				"gle",
				"glg",
				"grc",
				"guj",
				"hat", */
				"heb",
				"hin",
/*				"hrv",
				"hun",
				"hye",
				"iku",
				"ind",
				"isl",
				"ita",
				"ita_old",
				"jav",
				"jpn",
				"jpn_vert",
				"kan",
				"kat",
				"kat_old",
				"kaz",
				"khm",
				"kir",
				"kmr", */
				"kor",
/*				"kor_vert",
				"lao",
				"lat",
				"lav", */
				"lit",
/*				"ltz",
				"mal",
				"mar",
				"mkd",
				"mlt",
				"mon",
				"mri",
				"msa",
				"mya",
				"nep",
				"nld",
				"nor",
				"oci",
				"ori",
				"osd",
				"pan",
				"pol",
				"por",
				"pus",
				"que",
				"ron", */
				"rus",
/*				"san",
				"sin",
				"slk",
				"slv",
				"snd", */
				"spa"
/*				"spa_old",
				"sqi",
				"srp",
				"srp_latn",
				"sun",
				"swa",
				"swe",
				"syr",
				"tam",
				"tat",
				"tel",
				"tgk",
				"tha",
				"tir",
				"ton",
				"tur",
				"uig",
				"ukr",
				"urd",
				"uzb",
				"uzb_cyrl",
				"vie",
				"yid",
				"yor"
				
				 */
				 };

		var ll = new ArrayList<String>();
		
		//ll.add(firstLanguage);
		
		ll.addAll(Arrays.asList(languages));
		
		/*
		
		if (ll.contains(firstLanguage))
		{
			ll.remove(firstLanguage);
			ll.add(0, firstLanguage);
		}
		else
		{
			return null;
		}
		*/
		
		for (var language : ll)
		{
			TextExtractor textsExtractor;
			
		//	synchronized(vv)
			{
//				textsExtractor  = vv.get(language);
			
	//		if (textsExtractor == null)
			{
				textsExtractor = new TextExtractor(95f, language);
				
		//		vv.put(language, textsExtractor);
			}
			}
			
		//	System.err.println("lng: " + language);
			
			var msg = textsExtractor.extract(state.getImage(), bounds);
		
			if (msg.equalsIgnoreCase(expected))
			{
				return true;
			}
		}
		
		return false;		
	}	
	
	
	
	
	
	/*
	
	
	
	
	
	
	
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		var controls = state.getActualControls().stream().filter(p -> !shouldSkipControl(p, state)).collect(Collectors.toList());

		var overlapped = new ArrayList<Control>();
		
		for (var control1 : controls)
		{
			var found = false;
			
			for (var control2 : controls)
			{
				if (control1 != control2)
				{
					if (control1.getParent() == control2.getParent())
					{
						if ((control1.isOverlapping(control2)) && (!isInside(control1.getBounds(), control2.getBounds())) && (!isInside(control2.getBounds(), control1.getBounds()))) 
						{
							overlapped.add(control1);
							overlapped.add(control2);
							
							found = true;
							
							break;
						}
					}
				}
			}
			
			if (found)
			{
				break;
			}
		}
		
		if (overlapped.size() > 0)
		{
			ResultImage resultImage = new ResultImage(state.getImageFile());						
						
			int i = 0;
			
			for (var control : overlapped)
			{
				if (i++ % 2 == 0)
				{
					resultImage.drawBounds(control.getBounds(), 255, 0, 0);
				}
				else
				{
					resultImage.drawBounds(control.getBounds(), 0, 255, 0);
				}
			}	

			System.out.println(state.getStateFile().toString());
				
			resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");

			failures.addFailure(new CheckResult(state, this, "1", overlapped.size()));						
		}
	}
	
  public boolean contains(Rect r, Point p) {
    return r.x <= p.x && p.x <= r.x + r.width && r.y <= p.y && p.y <= r.y + r.height;
}

	
	private boolean isInside(Rect a, Rect b)
	{
		if (!contains(a, b.tl()))
		{
			return false;
		}

		if (!contains(a, b.br()))
		{
			return false;
		}

		if (!contains(a, new Point(b.x, b.y + b.height)))
		{
			return false;
		}

		if (!contains(a, new Point(b.x + b.width, b.y)))
		{
			return false;
		}
		
		return true;
	}
	
	private boolean shouldSkipControl(Control control, State state)
	{
		if (false == control.isVisible())
		{
			return true;
		}
		
		if (("Test Ad".equals(control.getText())) || (isAd(control)))
		{
			return true;
		}
		
		
		if (control.getSignature().contains("Layout"))
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
		
		
			if ((bounds.x  + bounds.width <= 0) || (bounds.y + bounds.height <= 0))
			{
				return true;
			}

			
			
		
	//	if ((control.getBounds().x + control.getBounds().width >= state.getImageSize().width) || (control.getBounds().y + control.getBounds().height >= state.getImageSize().height))
	//	{
	//		return true;
	//	}
		
		return false;
	}
	
		
		*/
}
