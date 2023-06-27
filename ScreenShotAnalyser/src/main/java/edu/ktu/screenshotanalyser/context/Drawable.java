package edu.ktu.screenshotanalyser.context;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.utils.ImageUtils;
import edu.ktu.screenshotanalyser.utils.LazyObject;

public class Drawable
{
	public Drawable(File file)
	{
		this.imageFile = file;
	}
	
	public float getTrimmedAspect()
	{
		return (float)this.trimmedImage.instance().getWidth() / (float)this.trimmedImage.instance().getHeight();				
	}
	
	public float getAspect()
	{
		if (this.image.instance() == ImageUtils.NULL_IMAGE || this.image.instance() == null)
		{
			return Float.MAX_VALUE;
		}
		
		return (float)this.image.instance().getWidth() / (float)this.image.instance().getHeight();
	}	

	public synchronized boolean canFit(Rect bounds)
	{
		return this.image.instance().getWidth() >= bounds.width && this.image.instance().getHeight() >= bounds.height;
	}
	
  public synchronized boolean is1x1()
  {
		return (this.image.instance().getWidth() <= 4) || (this.image.instance().getHeight() <= 5);
  }
  
  public synchronized boolean is300x300()
  {
		return (this.image.instance().getWidth() >= 300) && (this.image.instance().getHeight() >= 300);
  }  

	public synchronized Mat getScaledTo300x300()
	{
		return this.scaledImage.instance();
	}
  
	public boolean isIcon()
	{
		if (this.imageFile.getName().startsWith("abc_") || this.imageFile.getName().endsWith(".9.png"))
		{
			return false;
		}
		
		var aspect = getAspect();
		
		return aspect > 0.95f && aspect < 1.05f;
	}
  
  public File getImageFile()
  {
  	return this.imageFile;
  }
  
  public int getWidth()
  {
  	return this.image.instance().getWidth();
  }

  public int getHeight()
  {
  	return this.image.instance().getHeight();
  }
  
  public String toString()
  {
		return this.image.instance().getWidth() + "x" + this.image.instance().getHeight();
  }
  
	private final File imageFile;
	private LazyObject<BufferedImage> trimmedImage = new LazyObject<>(() -> ImageUtils.trimImage(this.image.instance()));
	private final LazyObject<Mat> scaledImage = new LazyObject<>(() -> ImageUtils.scale(ImageUtils.bufferedImage2Mat(this.image.instance()), 300, 300));
	private final LazyObject<BufferedImage> image = new LazyObject<>(() -> ImageUtils.loadImage(getImageFile()));
}
