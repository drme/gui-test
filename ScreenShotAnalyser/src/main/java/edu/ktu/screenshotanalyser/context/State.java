package edu.ktu.screenshotanalyser.context;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import com.jayway.jsonpath.DocumentContext;
import edu.ktu.screenshotanalyser.checks.ResultImage;
import edu.ktu.screenshotanalyser.tools.ImageContoursProvider;
import edu.ktu.screenshotanalyser.tools.TextExtractor;
import edu.ktu.screenshotanalyser.utils.ImageUtils;
import edu.ktu.screenshotanalyser.utils.JsonParser;
import edu.ktu.screenshotanalyser.utils.LazyObject;

/**
 * Application's screenshot window state.
 */
public class State
{
	public State(String name, AppContext context, File imageFile, File stateFile, TestDevice testDevice)
	{
		this.name = name;
		this.context = context;
		this.imageFile = imageFile;
		this.stateFile = stateFile;
		this.testDevice = testDevice;
	}
	
	public File getImageFile()
	{
		return this.imageFile;
	}

	public File getStateFile()
	{
		return this.stateFile;
	}
	
	public AppContext getAppContext()
	{
		return this.context;
	}
	
	public boolean isLauncherScreen()
	{
		try (var scanner = new Scanner(this.stateFile))
		{
			while (scanner.hasNextLine())
			{
				var line = scanner.nextLine();
					
				if (line.contains("foreground_activity") && line.contains("com.google.android.apps.nexuslauncher/.NexusLauncherActivity"))
				{
					return true;
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace(System.err);
		}	  	
	  	
		return false;
	}
	
	public boolean isSplashScreen()
	{
		var stackLine = 0;
		var splashLine = 0;
		
		try (var scanner = new Scanner(this.stateFile))
		{
			var lineNo = 0;
			
			while (scanner.hasNextLine())
			{
				var line = scanner.nextLine();
					
				lineNo++;
				
				if (line.contains("activity_stack"))
				{
					stackLine = lineNo;
				}
				
				if (line.contains(".Splash"))
				{
					splashLine = lineNo;
					
					return splashLine > stackLine && splashLine - stackLine == 1;
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace(System.err);
		}	  	
	  	
		return false;
	}
	
	public synchronized List<Control> getActualControls()
	{
		if (null == this.actualControls)
		{
			var result = new ArrayList<Control>();
			
			try
			{
				var document = JsonParser.parse(this.stateFile);
				var controls = getControls(document);

				assignParents(controls);
				
				controls.values().stream().forEach(result::add);
				
				this.rootControl = result.isEmpty() ? null : result.get(0);
				this.actualControls = result;
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				
				this.actualControls = new ArrayList<>();
			}
		}
		
		return this.actualControls;
	}
	
	private void assignParents(HashMap<Long, Control> views)
	{
		for (var view : views.values())
		{
			var parent = views.get(view.getParentId());
			var children = views.values().stream().filter(p -> p.getParentId() == view.getId()).toList();
			
			view.setLinks(parent, children);
		}
	}

	private HashMap<Long, Control> getControls(DocumentContext document)
	{
		var views = new HashMap<Long, Control>();
		
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> viewObjects = document.read("$.views", List.class);
		
		for (var viewObject : viewObjects)
		{
			HashMap<String, Object> m = viewObject;
			
			var resourceId = (String)m.get("resource_id");
			var signature = getText(m, "signature");
			var packageName = getText(m, "package");
			var text = getText(m, "text");
			var contentDescription = getText(m, "content_description");
			var className = (String)m.get("class");
			var visible = (boolean)m.get("visible");
			var clickable = (boolean)m.get("clickable");
			var children = (List<Integer>)m.get("children");
			
			var view = new Control(this, text, contentDescription, getBounds(m), (Integer)m.get("parent"), (Integer)m.get("temp_id"), className, visible, signature, resourceId, packageName, clickable, children);
			
			views.put(view.getId(), view);
		}
		
		return views;
	}
	
	private static String getText(HashMap<String, Object> view, String key)
	{
		var text = (String)view.get(key);

		if (null != text)
		{
			text = text.trim();
		}
		
		if ("null".equals(text))
		{
			text = null;
		}

		if ("".equals(text))
		{
			text = null;
		}
	
		return text;
	}
	
	private Rect getBounds(HashMap<String, Object> view)
	{
		@SuppressWarnings("unchecked")
		var b = (List<List<Integer>>)view.get("bounds");
		var r = new Rect();
			
		r.x = b.get(0).get(0); 
		r.y = b.get(0).get(1); 

		r.width = b.get(1).get(0) - r.x; 
		r.height = b.get(1).get(1) - r.y; 
			
		return r;
	}
	
	public synchronized List<Control> getImageControls()
	{
		if (null == this.imageControls)
		{
			var result = new ArrayList<Control>();
	//		var textsExtractor = new TextExtractor(0.65f, predictLanguage());
				
			for (var area : imageContoursProvider.instance().getContours(this.imageFile))
			{
//				var text = textsExtractor.extract(this.imageFile, area);
					
	//			if (text.length() > 0)
		//		{
				var text = "";
				
				var control = new Control(this, text, null, area, null, null, null, true, null, null, null, false, null);
				
			//		result.add();
//				}
				
				result.add(control);
			}
			
			this.imageControls = result;
		}
		
		return this.imageControls;		
	}
	
	public synchronized String getImageTexts()
	{
		if (null == this.imageTexts)
		{
			var language = predictLanguage();
				
			if (!language.equals("deu") && !language.equals("eng"))
			{
				return null;
			}
				
			var textsExtractor = new TextExtractor(0.65f, language);
				
			this.imageTexts = textsExtractor.extract(this.imageFile);
		}
		
		return this.imageTexts;		
	}
	
	public String predictLanguage()
	{
		var message = getActualControls().stream().filter(x -> x.getText() != null).map(Control::getText).collect(Collectors.joining(". "));
		
		return this.context.getSystemContext().predictLanguage(message);		
	}
	
	public void dumpRecognitionDebug()
	{
		var original = Imgcodecs.imread(this.imageFile.getAbsolutePath());

		for (var text : getImageControls())
		{
			var rectangle = text.getBounds();
			
			Imgproc.rectangle(original, new Point(rectangle.x, rectangle.y), new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height), new Scalar(0, 0, 255), 2);
			
			if (text.getText().length() > 0)
			{
				Imgproc.putText(original, text.getText(), rectangle.tl(), Imgproc.FONT_HERSHEY_PLAIN, 0.6, new Scalar(255));
			}			
		}

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + ".png").getAbsolutePath(), original);
	}

	public TestDevice getTestDevice()
	{
		return this.testDevice;
	}
	
	public Rect getImageSize()
	{
		return this.imageSize.instance();
	}
	
	public BufferedImage getImage()
	{
		return this.image.instance();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean isLandscape()
	{
		return this.isLandscape.instance();
	}

	public ResultImage getResultImage()
	{
		return this.resultImage.instance();
	}
	
	public BufferedImage getControlImage(Control control)
	{
		var stateImage = getImage();
		
		if (stateImage == null || stateImage.getWidth() == 0 || stateImage.getHeight() == 0)
		{
			return null;
		}
		
  	var x = control.getBounds().x;
  	var y = control.getBounds().y;
  	var w = control.getBounds().width;
  	var h = control.getBounds().height;
  	
  	var stateImageWidth = stateImage.getWidth();
  	var stateImageHeight = stateImage.getHeight();

  	if (x < 0)
  	{
  		w += x;
  		x = 0;
  	}

  	if (y < 0)
  	{
  		h += y;
  		y = 0;
  	}
  	
  	if (x + w > stateImageWidth)
  	{
  		w = stateImageWidth - x;
  	}

  	if (y + h > stateImageHeight)
  	{
  		h = stateImageHeight - y;
  	}

  	if (h <= 0 || w <= 0)
  	{
  		return null;
  	}

  	try
  	{
  		return stateImage.getSubimage(x, y, w, h);
  	}
  	catch (Throwable ex)
  	{
  		ex.printStackTrace();
  		
  		return null;
  	}
	}
	
	public Integer getNavigationBarY()
	{
		return this.navigationBarY.instance();
	}

	private Rect loadImageSize()
	{
		if (this.image.instance() == ImageUtils.NULL_IMAGE)
		{
			return new Rect(0, 0, this.testDevice.screenWidth, this.testDevice.screenHeight);
		}
		
		return new Rect(0, 0, this.image.instance().getWidth(), this.image.instance().getHeight());
	}

	public Control getControl(String id)
	{
		for (var control : getActualControls())
		{
			if (id.equals(control.getResourceId()))
			{
				return control;
			}
		}
		
		return null;
	}
	
	public Control getRootControl()
	{
		return this.rootControl;
	}
	
	public void unloadData()
	{
		this.actualControls = null;
		this.imageControls = null;
		this.image.unload();
		this.resultImage.unload();
	}
	
	private final String name;
	private final File imageFile;
	private final File stateFile;
	private final AppContext context;
	private final TestDevice testDevice;
	private List<Control> actualControls = null;
	private List<Control> imageControls = null;
	private String imageTexts = null;
	private Control rootControl = null;
	private final LazyObject<Rect> imageSize = new LazyObject<>(() -> loadImageSize());
	private final LazyObject<BufferedImage> image = new LazyObject<>(() -> ImageUtils.loadImage(getImageFile()));
	private final LazyObject<Boolean> isLandscape = new LazyObject<>(() -> getImage().getWidth() > getImage().getHeight());
	private final LazyObject<ResultImage> resultImage = new LazyObject<>(() -> new ResultImage(getImageFile()));
	private final LazyObject<Integer>navigationBarY = new LazyObject<>(() -> getActualControls().stream().filter(p -> "android:id/navigationBarBackground".equals(p.getResourceId())).findFirst().map(p -> p.getBounds().y).orElse(null));
	private static LazyObject<ImageContoursProvider> imageContoursProvider = new LazyObject<>(ImageContoursProvider::new);
}
