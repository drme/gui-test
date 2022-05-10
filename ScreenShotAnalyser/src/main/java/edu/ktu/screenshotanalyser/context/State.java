package edu.ktu.screenshotanalyser.context;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import edu.ktu.screenshotanalyser.tools.ImageContoursProvider;
import edu.ktu.screenshotanalyser.tools.TextExtractor;

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
	
	public synchronized List<Control> getActualControls()
	{
		if (null == this.actualControls)
		{
			var result = new ArrayList<Control>();
			
			try
			{
				Configuration.setDefaults(new Configuration.Defaults()
				{
					private final JsonProvider jsonProvider = new JacksonJsonProvider();
					private final MappingProvider mappingProvider = new JacksonMappingProvider();
					private final Set<Option> options = EnumSet.noneOf(Option.class);

					public JsonProvider jsonProvider()
					{
						return this.jsonProvider;
					}

					@Override
					public MappingProvider mappingProvider()
					{
						return this.mappingProvider;
					}

					@Override
					public Set<Option> options()
					{
						return this.options;
					}
				});				
				
				var document = JsonPath.parse(this.stateFile);
				var controls = getControls(document);

				assignParents(controls);
				
				controls.values().stream().forEach(p -> result.add(p));
				
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
			view.setParent(views.get(view.getParentId()));
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
			
			var view = new Control(this, getText(m, "text"), getText(m, "content_description"), getBounds(m), (Integer)m.get("parent"), (Integer)m.get("temp_id"), (String)m.get("class"), (boolean)m.get("visible"), getText(m, "signature"));
			
			views.put(view.getId(), view);
		}
		
		return views;
	}
	
	private String getText(HashMap<String, Object> view, String key)
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
			var textsExtractor = new TextExtractor(0.65f, predictLanguage());
				
			for (var area : new ImageContoursProvider().getContours(this.imageFile))
			{
				var text = textsExtractor.extract(this.imageFile, area);
					
				if (text.length() > 0)
				{
					result.add(new Control(this, text, null, area, null, null, null, true, null));
				}
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
	
	public synchronized Rect getImageSize()
	{
		if (null == this.imageSize)
		{
			try
			{
				var imageData = ImageIO.read(this.imageFile);
				
				this.imageSize = new Rect(0, 0, imageData.getWidth(), imageData.getHeight());
			}
			catch (IOException ex)
			{
				ex.printStackTrace(System.err);
				
				this.imageSize = new Rect(0, 0, this.testDevice.screenWidth, this.testDevice.screenHeight);
			}
		}
		
		return this.imageSize;
	}
	
	public synchronized BufferedImage getImage()
	{
		if (false == this.imageLoaded)
		{
			try
			{
				this.image = ImageIO.read(getImageFile());
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				
				this.image = null;
			}
			
			this.imageLoaded = true;
		}
		
		return this.image;
	}
	
	public String getName()
	{
		return this.name;
	}

	private final String name;
	private final File imageFile;
	private final File stateFile;
	private final AppContext context;
	private List<Control> actualControls = null;
	private List<Control> imageControls = null;
	private String imageTexts = null;
	private TestDevice testDevice = null;
	private Rect imageSize = null;
	private BufferedImage image = null;
	private boolean imageLoaded = false;
}
