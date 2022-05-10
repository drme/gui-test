package edu.ktu.screenshotanalyser.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageUtils
{
	private ImageUtils()
	{
	}
	
	public static BufferedImage matToBufferedImage(Mat source)
	{
		try
		{
			var bytes = new MatOfByte();

			Imgcodecs.imencode(".png", source, bytes);

			return ImageIO.read(new ByteArrayInputStream(bytes.toArray()));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static Mat bufferedImageToMat(BufferedImage in)
	{
		int w = in.getWidth();
		int h = in.getHeight();
		
		Mat out;
    byte[] data;
    int r, g, b;
    
    
    out = new Mat(h, w, CvType.CV_8UC3);
    data = new byte[w * h * (int)out.elemSize()];
    int[] dataBuff = in.getRGB(0, 0, w, h, null, 0, w);
    for(int i = 0; i < dataBuff.length; i++)
    {
        data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
        data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
        data[i*3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
    }
    
   
   /* 

    if(in.getType() == BufferedImage.TYPE_INT_RGB)
    {
        out = new Mat(h, w, CvType.CV_8UC3);
        data = new byte[w * h * (int)out.elemSize()];
        int[] dataBuff = in.getRGB(0, 0, w, h, null, 0, w);
        for(int i = 0; i < dataBuff.length; i++)
        {
            data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
            data[i*3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
        }
    }
    else if(in.getType() == BufferedImage.TYPE_INT_ARGB)
    {
        out = new Mat(h, w, CvType.CV_8UC3);
        data = new byte[w * h * (int)out.elemSize()];
        int[] dataBuff = in.getRGB(0, 0, w, h, null, 0, w);
        for(int i = 0; i < dataBuff.length; i++)
        {
            data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
            data[i*3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
        }
    }    
    else
    {
        out = new Mat(h, w, CvType.CV_8UC1);
        data = new byte[w * h * (int)out.elemSize()];
        int[] dataBuff = in.getRGB(0, 0, w, h, null, 0, w);
        for(int i = 0; i < dataBuff.length; i++)
        {
          r = (byte) ((dataBuff[i] >> 16) & 0xFF);
          g = (byte) ((dataBuff[i] >> 8) & 0xFF);
          b = (byte) ((dataBuff[i] >> 0) & 0xFF);
          data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
        }
     }
     
     */
     
     out.put(0, 0, data);
     return out;		
		
		
		
		/*
		
	  Mat result  = new Mat(source.getHeight(), source.getWidth(), CvType.CV_8UC3);
	  
	  byte[] data = ((DataBufferByte) source.getRaster().getDataBuffer()).getData();
	  
	  result.put(0, 0, data);
	  
	  return result;		*/
	}
}
