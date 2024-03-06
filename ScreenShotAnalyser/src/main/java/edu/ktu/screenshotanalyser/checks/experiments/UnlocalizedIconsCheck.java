package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import org.languagetool.Language;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultImage;
import edu.ktu.screenshotanalyser.checks.IResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.TextExtractor;

public class UnlocalizedIconsCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public UnlocalizedIconsCheck()
	{
		super(33, "UnlocalizedIconsCheck");
	}

	@Override
	public void analyze(@Nonnull State state, @Nonnull StateCheckResults result)
	{/*
		try
		{
			
			
	//		System.out.println(state.getImageFile().getAbsolutePath());
		
		
		var allTexts = state.getActualControls().stream().map(this::getText).collect(Collectors.joining(". "));
	//	var languages = getLanguage(allTexts);

//		if (0 == languages.size())
//		{
//			return;
//		}
		
		var lang = state.predictLanguage();

	//	var textsExtractor = new TextExtractor(0.65f, lang);

		var images = state.getActualControls().stream().filter(p -> p.getType().contains("Image")).collect(Collectors.toList());

		if (images.size() > 0)
		{
//			System.out.println("img...");

			for (var control : images)
			{
				try
				{

					var bounds = control.getBounds();

					if (bounds.x < 0 || bounds.y < 0 || bounds.width <= 5 || bounds.height <= 5)
					{
						continue;
					}

					if (bounds.width - bounds.height > 100)
					{
						
				//		var icon1 = state.getImage().getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);

						
					//	var msg = textsExtractor.extract(icon1); 
								
								//textsExtractor.extract(state.getImage(), bounds, (x) -> true);

						var msg = getBestLanguage(state.getImageFile(), bounds, state.getAppContext(), lang);
						
						
						if ((null != msg) && (msg.length() > 0))
						{
							//System.out.println("[" + msg + "]");

							if (!msg.equalsIgnoreCase(lang))
							{
							
						var icon = state.getImage().getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);

						File outputfile = new File(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1-------" + msg + "--" + lang + ".png");
						ImageIO.write(icon, "png", outputfile);

						
						/*
						var result = new StateCheckResults(state, this, msg + " != " + lang, 1);
				//		failures.addFailure(result);									
						
					//	if (failures.acceptsResultImages)
				//		{
					//		var debugImage = result.getResultImage();
							
						//	debugImage.drawBounds(bounds);

					//		failures.addFailureImage(debugImage);									
					//	}
						
						return result;  *0/
							} 
						} 
					}
					else if ((Math.abs(bounds.width - bounds.height) < 3) && (bounds.width <= 256))
					{
						if (lang == "heb") // RTL lang only...
						{
						
						
						var icon = state.getImage().getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
						
		        AffineTransform at = new AffineTransform();
		        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
		        at.concatenate(AffineTransform.getTranslateInstance(-icon.getWidth(), 0));

		        
		        
		        BufferedImage newImage = new BufferedImage(
		        		icon.getWidth(), icon.getHeight(),
		            BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g = newImage.createGraphics();
		        g.transform(at);
		        g.drawImage(icon, 0, 0, null);
		        g.dispose();
		        
		        

		        
		        
		        BufferedImage img1 = icon;
		        BufferedImage img2 = newImage;
		        int w1 = img1.getWidth();
		        int w2 = img2.getWidth();
		        int h1 = img1.getHeight();
		        int h2 = img2.getHeight();
		        if ((w1!=w2)||(h1!=h2)) {
		           System.out.println("Both images should have same dimwnsions");
		        } else {
		           long diff = 0;
		           for (int j = 0; j < h1; j++) {
		              for (int i = 0; i < w1; i++) {
		                 //Getting the RGB values of a pixel
		                 int pixel1 = img1.getRGB(i, j);
		                 Color color1 = new Color(pixel1, true);
		                 int r1 = color1.getRed();
		                 int g1 = color1.getGreen();
		                 int b1 = color1.getBlue();
		                 int pixel2 = img2.getRGB(i, j);
		                 Color color2 = new Color(pixel2, true);
		                 int r2 = color2.getRed();
		                 int g2 = color2.getGreen();
		                 int b2= color2.getBlue();
		                 //sum of differences of RGB values of the two images
		                 long data = Math.abs(r1-r2)+Math.abs(g1-g2)+ Math.abs(b1-b2);
		                 diff = diff+data;
		              }
		           }
		           double avg = diff/(w1*h1*3);
		           double percentage = (avg/255)*100;
		       //    System.out.println("Difference: "+percentage);
		           
		           
		           if (percentage >= 45.0)
		           {
								File outputfile1 = new File(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1-------" +  ".png");
								ImageIO.write(img1, "png", outputfile1);
								
								File outputfile2 = new File(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1-------" +  ".png");
								ImageIO.write(img2, "png", outputfile2);
		           }
		        }
		        
		        
		        
						}
		        
		        
					}
					

				}
				catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
		*/
	}
	/*
	private String getBestLanguage(File image, Rect bounds, AppContext context, String firstLanguage)
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
				"dan",*-/
				"deu",
/*				"div",
				"dzo",
				"ell", *-/
				"eng",
/*				"enm",
				"epo",
				"est",
				"eus",
				"fao",
				"fas",
				"fil",
				"fin", *-/
				"fra",
/*				"frk",
				"frm",
				"fry",
				"gla",
				"gle",
				"glg",
				"grc",
				"guj",
				"hat", *-/
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
				"kmr", *-/
				"kor",
/*				"kor_vert",
				"lao",
				"lat",
				"lav", *-/
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
				"ron", *-/
				"rus",
/*				"san",
				"sin",
				"slk",
				"slv",
				"snd", *-/
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
				
				 *-/
				 };

		var ll = new ArrayList<String>();
		
		//ll.add(firstLanguage);
		
		ll.addAll(Arrays.asList(languages));
		
		if (ll.contains(firstLanguage))
		{
			ll.remove(firstLanguage);
			ll.add(0, firstLanguage);
		}
		else
		{
			return null;
		}
		
		
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
			
			System.err.println("lng: " + language);
			
			var msg = textsExtractor.extract(image, bounds);
			
			if (msg.length() > 0)
			{
				if (isSpellingCorrect(language, msg, context))
				{
					System.out.println(msg);
					
					if (!isIgnoredWord(msg, context))
					{
						return language;
					}
				}
			}
		}
		
		return null;
	}
	*/
	private static HashMap<String, TextExtractor> vv = new HashMap<>();
}
