package edu.ktu.screenshotanalyser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.opencv.core.Rect;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;

public class TextExtractor implements ITextExtractor{
	private static final Logger logger = Logger.getGlobal();
	private final ITesseract instance;
	private final float confidenceLevel;

	public TextExtractor(float confidenceLevel) {
		
		this.confidenceLevel = confidenceLevel;
		instance = new Tesseract();
		instance.setDatapath(new File(System.getProperty("user.dir"), "tessdata").getAbsolutePath());
		instance.setLanguage("nor");
	}

	public TextExtractResponse extract(final TextExtractRequest request) throws Throwable {
		final File file = request.getFile();
		final BufferedImage image = ImageIO.read(file);
		final List<Rect> bounds = request.getBounds();
		final List<ExtractedText> extractedTexts = new ArrayList<>();
		for (final Rect area : bounds) {
			try {
				final BufferedImage img = image.getSubimage(area.x, area.y, area.width, area.height);
				final List<Word> words = instance.getWords(img, 0);

				final StringBuilder sb = new StringBuilder();

				for (final Word word : words) {

					System.out.println("" + word.getConfidence() + " -> " + word.getText());

					if (word.getConfidence() > this.confidenceLevel) {
						sb.append(" " + word.getText());
					}
				}
				extractedTexts.add(new ExtractedText(words.toArray(new Word[0]), sb.toString(), area));

			} catch (Exception ex) {
				logger.log(Level.WARNING,
						String.format("Unexpected exception while analysing %s", file.getAbsolutePath()), ex);
			}
		}
		return new TextExtractResponse(extractedTexts);

	}

	public static class ExtractedText {

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

		public TextExtractResponse(List<ExtractedText> extractedTexts) {
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

		public File getFile() {
			return file;
		}
	}
}
