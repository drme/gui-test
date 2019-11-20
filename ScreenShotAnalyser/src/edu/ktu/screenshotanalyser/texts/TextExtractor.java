package edu.ktu.screenshotanalyser.texts;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.opencv.core.Rect;
import org.slf4j.LoggerFactory;
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

	public String extract(BufferedImage image)
	{
		try
		{
			return tesseract.doOCR(image);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	public String extract(BufferedImage image, Rect area)
	{
		BufferedImage subImage = image.getSubimage(area.x, area.y, area.width, area.height);
				
		StringBuilder result = new StringBuilder();

		try
		{
			for (Word word : this.tesseract.getWords(subImage, 0))
			{
			System.out.println("" + word.getConfidence() + " -> " + word.getText());

				if (word.getConfidence() > this.confidenceLevel)
				{
					result.append(" " + word.getText());
				}
			}
		}
		catch (Throwable ex)
		{
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
