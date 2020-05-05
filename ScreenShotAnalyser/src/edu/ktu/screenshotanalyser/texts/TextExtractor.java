package edu.ktu.screenshotanalyser.texts;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.LoggerFactory;
import edu.ktu.screenshotanalyser.utils.ImageUtils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.LoggHelper;

public class TextExtractor implements ITextExtractor
{
	private final ITesseract tesseract;
	private final float confidenceLevel;

	public TextExtractor(float confidenceLevel, String language)
	{
		ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Tesseract.class.getName());
		logger.setLevel(ch.qos.logback.classic.Level.ALL);
		
		//logger.getRootLogger().setLevel(Level.OFF);		
		
		this.confidenceLevel = confidenceLevel;

		this.tesseract = new Tesseract();
		this.tesseract.setDatapath(new File("./tessdata_best").getAbsolutePath()); // TODO: folder in app settings
		this.tesseract.setLanguage(language);
	
		
		List<String> config = new ArrayList<>();
		
		String[] f = new String[] {"load_system_dawg", "load_freq_dawg",
				"load_punc_dawg",
				"load_number_dawg",
				"load_unambig_dawg",
				"load_bigram_dawg",
				"load_fixed_length_dawgs", };
		
		for (String a : f)
		{
			//config.add(a + " F");
		
		//	tesseract.setTessVariable(a, "F");
		}
		
	//	tesseract.setTessVariable("user_words_suffix", "user-words");
		
	//	config.add("e:/aa.txt");
		
	//	this.tesseract.setConfigs(config);
	}

	public String extract(File imageFile)
	{
		try
		{
			Mat sourceImage = Imgcodecs.imread(imageFile.getAbsolutePath());
			Mat grayScaleImage = new Mat();
			
			Imgproc.cvtColor(sourceImage, grayScaleImage, Imgproc.COLOR_BGR2GRAY);

			Mat gaussianBlurredImage = new Mat();
			Imgproc.GaussianBlur(grayScaleImage, gaussianBlurredImage, new Size(3, 3), 0);

			Mat adaptiveThresholdImage = new Mat();
			Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
			//Imgcodecs.imwrite("e:/4.png", adaptiveThresholdImage);

			String result1 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));

			Core.bitwise_not(gaussianBlurredImage, gaussianBlurredImage);

			Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
			//Imgcodecs.imwrite("e:/5.png", adaptiveThresholdImage);

			String result2 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));
			
			Size sz = new Size(sourceImage.width() * 2, sourceImage.height() * 2);
			Imgproc.resize(sourceImage, sourceImage, sz);
			
			Imgproc.cvtColor(sourceImage, grayScaleImage, Imgproc.COLOR_BGR2GRAY);
			Imgproc.GaussianBlur(grayScaleImage, gaussianBlurredImage, new Size(3, 3), 0);
			Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
			//Imgcodecs.imwrite("e:/14.png", adaptiveThresholdImage);

			String result3 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));

			Core.bitwise_not(gaussianBlurredImage, gaussianBlurredImage);

			Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
			//Imgcodecs.imwrite("e:/15.png", adaptiveThresholdImage);

			String result4 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));
			
			return result1 + " " + result2 + " " + result3 + " " + result4;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	private String clean(String text)
	{
		if (null == text)
		{
			text = "";
		}
		
		return text.trim();
	}
	
	public String extract(BufferedImage image, Rect bounds)
	{
		try
		{
			
		if (null != bounds)
		{
			try
			{
			image = image.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
			}
			catch (java.awt.image.RasterFormatException e) {
e.printStackTrace();;
			}
		}
		
		
		Mat sourceImage = ImageUtils.bufferedImageToMat(image);
		Mat grayScaleImage = new Mat();
		
		Imgproc.cvtColor(sourceImage, grayScaleImage, Imgproc.COLOR_BGR2GRAY);

		Mat gaussianBlurredImage = new Mat();
		Imgproc.GaussianBlur(grayScaleImage, gaussianBlurredImage, new Size(3, 3), 0);

		Mat adaptiveThresholdImage = new Mat();
		Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
//		Imgcodecs.imwrite("d:/4.png", adaptiveThresholdImage);

		String result1 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));

		Core.bitwise_not(gaussianBlurredImage, gaussianBlurredImage);

		Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
	//	Imgcodecs.imwrite("d:/5.png", adaptiveThresholdImage);

		String result2 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));
		
		Size sz = new Size(sourceImage.width() * 2, sourceImage.height() * 2);
		Imgproc.resize(sourceImage, sourceImage, sz);
		
		Imgproc.cvtColor(sourceImage, grayScaleImage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(grayScaleImage, gaussianBlurredImage, new Size(3, 3), 0);
		Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
	//	Imgcodecs.imwrite("d:/14.png", adaptiveThresholdImage);

		String result3 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));

		Core.bitwise_not(gaussianBlurredImage, gaussianBlurredImage);

		Imgproc.adaptiveThreshold(gaussianBlurredImage, adaptiveThresholdImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 99, 4);
	//	Imgcodecs.imwrite("d:/15.png", adaptiveThresholdImage);

		String result4 = clean(this.tesseract.doOCR(ImageUtils.matToBufferedImage(adaptiveThresholdImage)));
		
		return result1;// + " " + result2 + " " + result3 + " " + result4;		
		
		
		
		}
		catch (Throwable e) {
e.printStackTrace();;

return "";
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	//	try
	//	{
		//	BufferedImage sourceImage = image;
			
			/*
			
			
			ImageFilter filter = new GrayFilter(true, 50);  
			ImageProducer producer = new java.awt.image.FilteredImageSource(image.getSource(), filter);
			
			sun.awt.image.ToolkitImage tt = ((sun.awt.image.ToolkitImage)Toolkit.getDefaultToolkit().createImage(producer));
			
			tt.getWidth();
			
			image = tt.getBufferedImage(); */  
			

			/* working??
			
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);  
			ColorConvertOp op = new ColorConvertOp(cs, null);  
			image = op.filter(image, null);
			
			BufferedImage image1 = new BufferedImage(image.getWidth(), image.getHeight(),  
			    BufferedImage.TYPE_INT_RGB);  
			Graphics g = image1.getGraphics();  
			g.drawImage(image, 0, 0, null);  
			g.dispose();  

			image = image1;
		
			
	    BufferedImage dbi = null;
	    
	        dbi = new BufferedImage(image.getWidth() * 5, image.getHeight() *5, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g1 = dbi.createGraphics();
	        AffineTransform at = AffineTransform.getScaleInstance(5, 5);
	        g1.drawRenderedImage(image, at);
	    
	    image = dbi;			
			
			*/
	    
	    
			
			/*
			
			Mat original = Imgcodecs.imread(imageFile.getAbsolutePath());

			Mat gray = new Mat();
			Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);

			Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-1.png").getAbsolutePath(), gray);
			
			
			Mat gradient = new Mat();
			Mat morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
			Imgproc.morphologyEx(gray, gradient, Imgproc.MORPH_GRADIENT, morphStructure);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-2.png").getAbsolutePath(), gradient);
			
			Mat binary = new Mat();
			Imgproc.threshold(gradient, binary, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

			Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-3.png").getAbsolutePath(), binary);
			
			Mat closed = new Mat();
			morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 1));
			Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, morphStructure);

			Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-4.png").getAbsolutePath(), closed);
			
			Imgcodecs.
			
			
			*/
			
	    
	    /// s3
	    
/*	    

	    
	    
	    
			Mat gray = new Mat();
			Imgproc.cvtColor(img2, gray, Imgproc.COLOR_BGR2GRAY);

			Size sz = new Size(image.getWidth() * 2, image.getHeight() * 2);
			Imgproc.resize( gray, gray, sz);
			
			
			Mat gradient = new Mat();
			Mat morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
			Imgproc.morphologyEx(gray, gradient, Imgproc.MORPH_GRADIENT, morphStructure);

			Mat binary = new Mat();
			Imgproc.threshold(gradient, binary, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

	    Imgcodecs.imwrite("e:/5.png", binary);
			
			
			Mat closed = new Mat();
			morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 1));
			Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, morphStructure);

	    Imgcodecs.imwrite("e:/6.png", closed);
	    
	   String  result2 = tesseract.doOCR(new File("e:/6.png")); 
//	    System.out.println(result);
	
	   if (result == null) result = "";
	   
	   if (result2 == null) result2 = "";
	   
	    String s3 =    result + " " + result2;
	    
	    
	    
	    //// s3
	    
	    
	    
			
			String s1 =  tesseract.doOCR(image);
			
			BufferedImage img = image;
			
	    //get image width and height
	    int width = img.getWidth();
	    int height = img.getHeight();
	    //convert to negative
	    for(int y = 0; y < height; y++){
	      for(int x = 0; x < width; x++){
	        int p = img.getRGB(x,y);
	        int a = (p>>24)&0xff;
	        int r = (p>>16)&0xff;
	        int gg = (p>>8)&0xff;
	        int b = p&0xff;
	        //subtract RGB from 255
	        r = 255 - r;
	        gg = 255 - gg;
	        b = 255 - b;
	        //set new RGB value
	        p = (a<<24) | (r<<16) | (gg<<8) | b;
	        img.setRGB(x, y, p);
	      }
	    }
	    //write image
	   // try{
	    //  File f = new File("D:\\Image\\Output.jpg");
	     // ImageIO.write(img, "jpg", f);
	  //  }catch(IOException e){
	//      System.out.println(e);
//	    }			
			
			String s2 =  tesseract.doOCR(image);
			
			
			///
			
			
			
			
			
			
			/////
			
			
			
			
			
			if (s1 == null) s1 = "";
			if (s2 == null) s2 = "";
			
			String ss = (s1 + " " + s2 + s3).trim();
			
			if (ss.length() > 0)
			{
				return ss;
			}
			
			return null;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			
			return null;
		} */
	    
//	    return null;
	}
	
	public String extract(File imageFile, Rect area)
	{
		if ((area.width <= 2) || (area.height <= 2) || (area.x < 0) || (area.y < 0))
		{
			return "";
		}
		
    Mat img2 = new Mat();
    img2 = Imgcodecs.imread(imageFile.getAbsolutePath());
    //Imgcodecs.imwrite("e:/11.png", img2);
    
    Mat imgGray = new Mat();
    Imgproc.cvtColor(img2, imgGray, Imgproc.COLOR_BGR2GRAY);
    //Imgcodecs.imwrite("e:/22.png", imgGray);
    
    Mat imgGaussianBlur = new Mat(); 
    Imgproc.GaussianBlur(imgGray,imgGaussianBlur,new Size(3, 3),0);
    //Imgcodecs.imwrite("e:/33.png", imgGaussianBlur);  
    
    Mat imgAdaptiveThreshold = new Mat();
    Imgproc.adaptiveThreshold(imgGaussianBlur, imgAdaptiveThreshold, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C ,Imgproc.THRESH_BINARY, 99, 4);
    //Imgcodecs.imwrite("e:/44.png", imgAdaptiveThreshold);
    
  //  File imageFile = new File("e:/4.png");
   // ITesseract instance = new Tesseract();
  //  instance.setLanguage("eng");
   // instance.setTessVariable("tessedit_char_whitelist", "acekopxyABCEHKMOPTXY0123456789");
 //   String result = tesseract.doOCR(imageFile); 
//    System.out.println(result);		
		
    /*
    
		try
		{
			image = ImageIO.read(imageFile);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		
    BufferedImage image = ImageUtils.matToBufferedImage(imgAdaptiveThreshold);
    
		
		BufferedImage subImage = image.getSubimage(area.x, area.y, area.width, area.height);

		if (null == subImage)
		{
			return "";
		}
		
		StringBuilder result = new StringBuilder();

		try
		{
			for (Word word : this.tesseract.getWords(subImage, 0))
			{
			//System.out.println("" + word.getConfidence() + " -> " + word.getText());

				if (word.getConfidence() > this.confidenceLevel)
				{
					result.append(" " + word.getText());
				}
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
			
		return result.toString().trim();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ExtractedText {

//		extractedTexts.add(new ExtractedText(words.toArray(new Word[0]), sb.toString(), area));

		
		private final Word[] orginalWords;

		public Word[] getOrginalWords() {
			return orginalWords;
		}

		public String getText() {
			return text;
		}

		public Rect getArea() {
			return area;
		}

		private final String text;
		private final Rect area;

		public ExtractedText(Word[] orginalWords, String text, Rect area) {
			this.orginalWords = orginalWords;
			this.text = text;
			this.area = area;

		}
	}

	public static class TextExtractResponse {
		final List<ExtractedText> extractedTexts = new ArrayList<>();
		private float confidenceLevel;

		public TextExtractResponse(List<ExtractedText> extractedTexts, float confidenceLevel) {
			this.confidenceLevel = confidenceLevel;
			this.extractedTexts.addAll(extractedTexts);
		}

		public List<ExtractedText> getExtractedTexts() {
			return extractedTexts;
		}
	}

	public static class TextExtractRequest {
		private final File file;
		private final List<Rect> bounds = new ArrayList<>();

		public List<Rect> getBounds() {
			return bounds;
		}

		public TextExtractRequest(File file, List<Rect> bounds) {
			this.file = file;
			this.bounds.addAll(bounds);
		}

		public TextExtractRequest(File file) {
			this.file = file;
		}

		public File getFile() {
			return file;
		}
	}
}
