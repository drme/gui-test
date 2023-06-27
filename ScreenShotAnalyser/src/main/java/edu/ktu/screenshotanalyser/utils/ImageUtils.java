package edu.ktu.screenshotanalyser.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.model.fit.RANSAC;
import com.aliasi.util.Pair;
import org.openimaj.feature.local.matcher.BasicMatcher;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Range;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;






import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;





public class ImageUtils
{
	
	
	
	
	
	
	
	
	
	
	
	
	

	    private static final double KMEANS_IMG_SIZE = 256;












			public void run(String[] args) {
	        if (args.length != 3) {
	            System.err.println("You must supply 3 arguments that correspond to the paths to 3 images.");
	            System.exit(0);
	        }
	        Mat srcBase = Imgcodecs.imread(args[0]);
	        Mat srcTest1 = Imgcodecs.imread(args[1]);
	        Mat srcTest2 = Imgcodecs.imread(args[2]);
	        if (srcBase.empty() || srcTest1.empty() || srcTest2.empty()) {
	            System.err.println("Cannot read the images");
	            System.exit(0);
	        }
	        Mat hsvBase = new Mat(), hsvTest1 = new Mat(), hsvTest2 = new Mat();
	        Imgproc.cvtColor( srcBase, hsvBase, Imgproc.COLOR_BGR2HSV );
	        Imgproc.cvtColor( srcTest1, hsvTest1, Imgproc.COLOR_BGR2HSV );
	        Imgproc.cvtColor( srcTest2, hsvTest2, Imgproc.COLOR_BGR2HSV );
	        Mat hsvHalfDown = hsvBase.submat( new Range( hsvBase.rows()/2, hsvBase.rows() - 1 ), new Range( 0, hsvBase.cols() - 1 ) );
	        int hBins = 50, sBins = 60;
	        int[] histSize = { hBins, sBins };
	        // hue varies from 0 to 179, saturation from 0 to 255
	        float[] ranges = { 0, 180, 0, 256 };
	        // Use the 0-th and 1-st channels
	        int[] channels = { 0, 1 };
	        Mat histBase = new Mat(), histHalfDown = new Mat(), histTest1 = new Mat(), histTest2 = new Mat();
	        List<Mat> hsvBaseList = Arrays.asList(hsvBase);
	        Imgproc.calcHist(hsvBaseList, new MatOfInt(channels), new Mat(), histBase, new MatOfInt(histSize), new MatOfFloat(ranges), false);
	        Core.normalize(histBase, histBase, 0, 1, Core.NORM_MINMAX);
	        List<Mat> hsvHalfDownList = Arrays.asList(hsvHalfDown);
	        Imgproc.calcHist(hsvHalfDownList, new MatOfInt(channels), new Mat(), histHalfDown, new MatOfInt(histSize), new MatOfFloat(ranges), false);
	        Core.normalize(histHalfDown, histHalfDown, 0, 1, Core.NORM_MINMAX);
	        List<Mat> hsvTest1List = Arrays.asList(hsvTest1);
	        Imgproc.calcHist(hsvTest1List, new MatOfInt(channels), new Mat(), histTest1, new MatOfInt(histSize), new MatOfFloat(ranges), false);
	        Core.normalize(histTest1, histTest1, 0, 1, Core.NORM_MINMAX);
	        List<Mat> hsvTest2List = Arrays.asList(hsvTest2);
	        Imgproc.calcHist(hsvTest2List, new MatOfInt(channels), new Mat(), histTest2, new MatOfInt(histSize), new MatOfFloat(ranges), false);
	        Core.normalize(histTest2, histTest2, 0, 1, Core.NORM_MINMAX);
	       
	        for( int compareMethod = 0; compareMethod < 4; compareMethod++ ) {
	            double baseBase = Imgproc.compareHist( histBase, histBase, compareMethod );
	            double baseHalf = Imgproc.compareHist( histBase, histHalfDown, compareMethod );
	            double baseTest1 = Imgproc.compareHist( histBase, histTest1, compareMethod );
	            double baseTest2 = Imgproc.compareHist( histBase, histTest2, compareMethod );
	            System.out.println("Method " + compareMethod + " Perfect, Base-Half, Base-Test(1), Base-Test(2) : " + baseBase + " / " + baseHalf
	                    + " / " + baseTest1 + " / " + baseTest2);
	        }
	    }
	
	
	    public static void main(String[] args) throws IOException {
	        // Load the native OpenCV library
	        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	  //      new ImageUtils().run(new String[] { "e:/1.png", "e:/2.png", "e:/3.png"  } );
	        
	//        isSimillar(ImageIO.read(new File("e:/1.png")), new File("e:/1.png"));

	  //      isSimillar(ImageIO.read(new File("e:/2.png")), new File("e:/1.png"));
	        
//	        isSimillar(ImageIO.read(new File("e:/3.png")), new File("e:/1.png"));
	        
	    }
	
	
	
	
	
	
	
	
	

	   public static Scalar zero = new Scalar(0, 0, 0);
	
	
	public static float isSimillar(Mat srcBase, Mat dst, Pair<Scalar, Scalar> mainColor) throws IOException
	{
 	 try
 	 {
 	 var guid = UUID.randomUUID().toString();
 	 
 //	  Imgcodecs.imwrite("e:/2/" + guid + "-1.png", srcBase); 

   // Imgcodecs.imwrite("e:/2/" + guid + "-2.png", dst); 
 	 
 	 		
		
		
     Mat srcTest1 = dst;//Imgcodecs.imread(dst.getAbsolutePath());

     if (srcTest1 == null)
     {
    	 return 0.0f;
     }
     
     
     var aa = getMinMaxHsv(   srcTest1, 2);  

     if (aa == null)
     {
    	 return 0;
     }
     
 //    System.out.println(">" + aa.a() + " " + mainColor.a());
     
     
     if (!mainColor.a().equals(aa.a()) || !mainColor.b().equals(aa.b()))
     {
 //   	 return 0;
     }
     
     if (aa.a().equals(zero))
     {
    	 return 0;
     }
     
     
     if (srcTest1.width() < 100 || srcTest1.height() < 100)
     {
  //  	 return 0;
     }
     
     

     
     Mat hsvBase = new Mat(), hsvTest1 = new Mat();//, hsvTest2 = new Mat();
     Imgproc.cvtColor( srcBase, hsvBase, Imgproc.COLOR_BGR2HSV );
     Imgproc.cvtColor( srcTest1, hsvTest1, Imgproc.COLOR_BGR2HSV );
//     Imgproc.cvtColor( srcTest2, hsvTest2, Imgproc.COLOR_BGR2HSV );
     
     
     Mat hsvHalfDown = hsvBase.submat( new Range( hsvBase.rows()/2, hsvBase.rows() - 1 ), new Range( 0, hsvBase.cols() - 1 ) );
     int hBins = 50, sBins = 60;
     int[] histSize = { hBins, sBins };
     // hue varies from 0 to 179, saturation from 0 to 255
     float[] ranges = { 0, 180, 0, 256 };
     // Use the 0-th and 1-st channels
     int[] channels = { 0, 1 };
     Mat histBase = new Mat(), histHalfDown = new Mat(), histTest1 = new Mat();//, histTest2 = new Mat();
     
     List<Mat> hsvBaseList = Arrays.asList(hsvBase);
     Imgproc.calcHist(hsvBaseList, new MatOfInt(channels), new Mat(), histBase, new MatOfInt(histSize), new MatOfFloat(ranges), false);
     Core.normalize(histBase, histBase, 0, 1, Core.NORM_MINMAX);

     List<Mat> hsvHalfDownList = Arrays.asList(hsvHalfDown);
     Imgproc.calcHist(hsvHalfDownList, new MatOfInt(channels), new Mat(), histHalfDown, new MatOfInt(histSize), new MatOfFloat(ranges), false);
     Core.normalize(histHalfDown, histHalfDown, 0, 1, Core.NORM_MINMAX);

     List<Mat> hsvTest1List = Arrays.asList(hsvTest1);
     Imgproc.calcHist(hsvTest1List, new MatOfInt(channels), new Mat(), histTest1, new MatOfInt(histSize), new MatOfFloat(ranges), false);
     Core.normalize(histTest1, histTest1, 0, 1, Core.NORM_MINMAX);

//     List<Mat> hsvTest2List = Arrays.asList(hsvTest2);
//     Imgproc.calcHist(hsvTest2List, new MatOfInt(channels), new Mat(), histTest2, new MatOfInt(histSize), new MatOfFloat(ranges), false);
  //   Core.normalize(histTest2, histTest2, 0, 1, Core.NORM_MINMAX);
    
//     for( int compareMethod = 0; compareMethod < 4; compareMethod++ ) {
     
     int compareMethod = 0;
     
         double baseBase = -1;// Imgproc.compareHist( histBase, histBase, compareMethod );
         double baseHalf = -1;//= Imgproc.compareHist( histBase, histHalfDown, compareMethod );
         double baseTest1 = Imgproc.compareHist( histBase, histTest1, compareMethod );
         double baseTest2 = -1;//Imgproc.compareHist( histBase, histTest2, compareMethod );
   //    System.out.println("Method " + compareMethod + " Perfect, Base-Half, Base-Test(1), Base-Test(2) : " + baseBase + " / " + baseHalf
     //            + " / " + baseTest1 + " / " + baseTest2);
  //   }		
		
		
		
         var c = baseTest1;
         
         if (c > 0.7)
         {
        	 
    //    	 var guid = UUID.randomUUID().toString();
        	 
        	 
        	 
           
           Imgcodecs.imwrite("e:/2/" + guid + "-1-"+c+".png", srcBase); 

           Imgcodecs.imwrite("e:/2/" + guid + "-2-"+c+".png", srcTest1); 
        	 
        	 
      //     var aa = getMinMaxHsv(   srcTest1, 2);  
    //       
  //         var ab = getMinMaxHsv(   srcBase, 2);  
           
           
        	 
        	 return (float)c;
         }
       
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return 0.0f;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		
		
		
		
		
		
		
		
		
		
		
		
		
		try
		{
		
		var os = new ByteArrayOutputStream();
		ImageIO.write(src, "png", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
		var is = new ByteArrayInputStream(os.toByteArray());		
		
		var target = ImageUtilities.readMBF(dst);		
			var query = ImageUtilities.readMBF(is);

		
		DoGSIFTEngine engine = new DoGSIFTEngine();	
		LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
		LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());		
		
		
		
//	var matcher = new BasicMatcher<Keypoint>(80);
	//	matcher.setModelFeatures(queryKeypoints);
		//matcher.findMatches(targetKeypoints);
		
		
	//	MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
		//DisplayUtilities.display(basicMatches);
		
		
		var modelFitter = new RobustAffineTransformEstimator(50.0, 1500,
			  new RANSAC.PercentageInliersStoppingCondition(0.5));
			
var		matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
			  new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);

			matcher.setModelFeatures(queryKeypoints);
			matcher.findMatches(targetKeypoints);

			MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), 
			  RGBColour.RED);

//			DisplayUtilities.display(consistentMatches);
		

			return matcher.getMatches().size() == queryKeypoints.size();
			
	//		System.out.println(matcher.getMatches().size() + " == " + queryKeypoints.size());
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;  */
		
 	 }
 	 catch (Throwable ex)
 	 {
 		 ex.printStackTrace();
 		 
 		 return 0.0f;
 	 }
	}
	
	/**
	 * Finds the dominant colour in an image, and returns two values in HSV colour space to represent similar colours,
	 * e.g. so you can keep all colours similar to the dominant colour.
	 *
	 * How the algorithm works://from   w ww . j  av  a2  s .c o m
	 *
	 * 1. Scale the frame down so that algorithm doesn't take too long.
	 * 2. Segment the frame into different colours (number of colours determined by k)
	 * 3. Find dominant cluster (largest area) and get its central colour point.
	 * 4. Get range (min max) to represent similar colours.
	 *
	 * @param bgr The input frame, in BGR colour space.
	 * @param k The number of segments to use (2 works well).
	 * @return The min and max HSV colour values, which represent the colours similar to the dominant colour.
	 */
	
  private static int colourRange = 9;
	
	public static Pair<Scalar, Scalar> getMinMaxHsv(Mat bgr, int k) {
		
		try
		{
		
	    //Convert to HSV
	    Mat input = new Mat();
	    Imgproc.cvtColor(bgr, input, Imgproc.COLOR_BGR2BGRA, 3);

	    //Scale image
	    Size bgrSize = bgr.size();
	    Size newSize = new Size();

	    if (bgrSize.width > KMEANS_IMG_SIZE || bgrSize.height > KMEANS_IMG_SIZE) {

	        if (bgrSize.width > bgrSize.height) {
	            newSize.width = KMEANS_IMG_SIZE;
	            newSize.height = KMEANS_IMG_SIZE / bgrSize.width * bgrSize.height;
	        } else {
	            newSize.width = KMEANS_IMG_SIZE / bgrSize.height * bgrSize.width;
	            newSize.height = KMEANS_IMG_SIZE;
	        }

	        Imgproc.resize(input, input, newSize);
	    }

	    //Image quantization using k-means, see here for details of k-means algorithm: http://bit.ly/1JIvrlB
	    Mat clusterData = new Mat();

	    Mat reshaped = input.reshape(1, input.rows() * input.cols());
	    reshaped.convertTo(clusterData, CvType.CV_32F, 1.0 / 255.0);
	    Mat labels = new Mat();
	    Mat centres = new Mat();
	    TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 50, 1);
	    Core.kmeans(clusterData, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centres);

	    //Get num hits for each category
	    int[] counts = new int[k];

	    for (int i = 0; i < labels.rows(); i++) {
	        int label = (int) labels.get(i, 0)[0];
	        counts[label] += 1;
	    }

	    //Get cluster index with maximum number of members
	    int maxCluster = 0;
	    int index = -1;

	    for (int i = 0; i < counts.length; i++) {
	        int value = counts[i];

	        if (value > maxCluster) {
	            maxCluster = value;
	            index = i;
	        }
	    }

	    //Get cluster centre point hsv
	    int r = (int) (centres.get(index, 2)[0] * 255.0);
	    int g = (int) (centres.get(index, 1)[0] * 255.0);
	    int b = (int) (centres.get(index, 0)[0] * 255.0);
	    int sum = (r + g + b) / 3;

	    //Get colour range
	    Scalar min;
	    Scalar max;

	    int rg = Math.abs(r - g);
	    int gb = Math.abs(g - b);
	    int rb = Math.abs(r - b);
	    int maxDiff = Math.max(Math.max(rg, gb), rb);

	    if (maxDiff < 35 && sum > 120) { //white
	        min = new Scalar(0, 0, 0);
	        max = new Scalar(180, 40, 255);
	    } else if (sum < 50 && maxDiff < 35) { //black
	        min = new Scalar(0, 0, 0);
	        max = new Scalar(180, 255, 40);
	    } else {
	        Mat bgrColour = new Mat(1, 1, CvType.CV_8UC3, new Scalar(r, g, b));
	        Mat hsvColour = new Mat();

	        Imgproc.cvtColor(bgrColour, hsvColour, Imgproc.COLOR_BGR2HSV, 3);
	        double[] hsv = hsvColour.get(0, 0);

	        int addition = 0;
	        int minHue = (int) hsv[0] - colourRange;
	        if (minHue < 0) {
	            addition = Math.abs(minHue);
	        }

	        int maxHue = (int) hsv[0] + colourRange;

	        min = new Scalar(Math.max(minHue, 0), 60, Math.max(35, hsv[2] - 30));
	        max = new Scalar(Math.min(maxHue + addition, 180), 255, 255);
	    }

	    return new Pair<>(min, max);
	    
		}
		catch (Throwable ex)
		{
			return null;
		}
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
		
	

	
	
	
	
	
	public static void saveTempImage(Mat image)
	{
		Imgcodecs.imwrite("e:/2/" + UUID.randomUUID().toString() + "-x.png", image); 		
	}
	
	public static Mat scale(Mat source, int width, int height)
	{
	   var result = new Mat();
	   
	   Imgproc.resize(source, result, new Size(width, height) , 0, 0, Imgproc.INTER_AREA);
	   
	   return result; 
	}
	
	public static Mat bufferedImage2Mat(BufferedImage image)
	{
		try
		{
			var byteArrayOutputStream = new ByteArrayOutputStream();
		
			ImageIO.write(image, "png", byteArrayOutputStream);
		
			byteArrayOutputStream.flush();
		
			return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static BufferedImage trimImage(BufferedImage image)
	{
		if (!isTransparent(image.getRGB(0,0)))
		{
			return image;
		}

		int startX = -1;
		int startY = -1;
		int endX = image.getWidth();
		int endY = image.getHeight();

		for (int x = 0; x < image.getWidth(); x++)
		{
			if (!isColumnTransparent(x, image))
			{
				startX = x - 1;
				break;
			}
		}

		for (int x = image.getWidth() - 1; x >= 0; x--)
		{
			if (!isColumnTransparent(x, image))
			{
				endX = x + 1;
				break;
			}
		}

		for (int y = 0; y < image.getHeight(); y++)
		{
			if (!isRowTransparent(y, image))
			{
				startY = y - 1;
				break;
			}
		}

		for (int y = image.getHeight() - 1; y >= 0; y--)
		{
			if (!isRowTransparent(y, image))
			{
				endY = y + 1;
				break;
			}
		}

		if (!isValidX(startX, image))
		{
			startX = 0;
		}

		if (!isValidY(startY, image))
		{
			startY = 0;
		}

		if (!isValidX(endX, image))
		{
			endX = image.getWidth() - 1;
		}

		if (!isValidY(endY, image))
		{
			endY = image.getHeight() - 1;
		}

		return image.getSubimage(startX, startY, endX - startX, endY - startY);
	}
	
  private static boolean isTransparent(int pixel)
  {
		return (pixel >> 24) == 0x00;
  }
  
	private static boolean isColumnTransparent(final int x, BufferedImage image)
	{
		for (int y = 0; y < image.getHeight(); y++)
		{
			if (!isTransparent(image.getRGB(x, y)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean isRowTransparent(final int y, BufferedImage image)
	{
		for (int x = 0; x < image.getWidth(); x++)
		{
			if (!isTransparent(image.getRGB(x, y)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean isValidX(int x, BufferedImage image)
	{
		return x >= 0 && x <= image.getWidth();
	}

	private static boolean isValidY(int y, BufferedImage image)
	{
		return y >= 0 && y <= image.getHeight();
	}
	
	public static BufferedImage loadImage(File imageFile)
	{
		try
		{
			return ImageIO.read(imageFile);
		}
		catch (IOException ex)
		{
			BaseLogger.logException(imageFile.getAbsolutePath(), ex);
			
			return NULL_IMAGE;
		}
	}
	
	public static Rect findEmptyArea(BufferedImage image)
	{
		class SearchResult
		{
			Integer startColor = null;
			Integer startY = null;
			Integer endY = null;
			
			int height()
			{
				return endY - startY + 1;
			}
		}
		
		SearchResult result = null;
		SearchResult maxResult = null;
		
		for (var y = 0; y < image.getHeight(); y++)
		{
			var lineColor = getLineColor(image, y);
			
			if (lineColor != null)
			{
				if (result == null)
				{
					result = new SearchResult();
					result.startColor = lineColor;
					result.startY = y;
					result.endY = y;
				}
				else
				{
					if (lineColor.intValue() == result.startColor.intValue())
					{
						result.endY = y;
					}
					else
					{
						if (maxResult == null)
						{
							maxResult = result;
						}
						else
						{
							if (maxResult.height() < result.height())
							{
								maxResult = result;
							}
						}

						result = new SearchResult();
						result.startColor = lineColor;
						result.startY = y;
						result.endY = y;
					}
				}
			}
			else
			{
				if (maxResult == null)
				{
					maxResult = result;
				}
				else
				{
					if (result != null && maxResult.height() < result.height())
					{
						maxResult = result;
					}
				}

				result = null;
			}
		}

		if (maxResult != null)
		{
			return new Rect(0, maxResult.startY, image.getWidth(), maxResult.height());
		}
		
		return null;
	}
	
	private static Integer getLineColor(BufferedImage image, int y)
	{
		var startColor = image.getRGB(20, y);
		
		for (var x = 21; x < image.getWidth()-20; x++)
		{
			if (startColor != image.getRGB(x, y))
			{
				return null;
			}
		}
		
		return startColor;
	}	
	
	public static void saveTempImage(BufferedImage image, String suffix)
	{
		try
		{
			ImageIO.write(image, "png", new java.io.File("e:/2/" + UUID.randomUUID().toString() + "-" + suffix + ".png"));
		}
		catch (IOException ex)
		{
			BaseLogger.logException("", ex);
		}		
	}
	
	public static final BufferedImage NULL_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
}
