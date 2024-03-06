package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.NotImplementedException;
import org.opencv.core.Mat;
import edu.ktu.screenshotanalyser.checks.SystemContext;
import edu.ktu.screenshotanalyser.database.DataBase.Application;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.SystemUtils;
import edu.ktu.screenshotanalyser.utils.ImageUtils;
import edu.ktu.screenshotanalyser.utils.LazyObject;

public class AppContext
{
	public AppContext(Application app, File dataFolder, List<TestDevice> testDevices, SystemContext systemContext) throws IOException
	{
		this.app = app;
		this.dataFolder = dataFolder;
		this.testDevices = testDevices;
		this.systemContext = systemContext;
		this.states = loadStates(app, this.testDevices, this);
	}


	
	
	



	public synchronized List<Drawable> findDrawable(Mat source)
	{
		var mainColor = ImageUtils.getMinMaxHsv(source, 2);  		
		
		if (mainColor.a().equals(ImageUtils.zero))
		{
			return Collections.emptyList();
		}
		
//		System.out.println("" + mainColor.a());
		
		if (null == this.allImages)
		{
			var result = new ArrayList<Drawable>();
			var drawableFolders = new File(Settings.unpackedAppsFolder + this.app.apkFile() + "/res/").listFiles(p -> p.getName().startsWith("drawable") && p.isDirectory());			

			for (var folder : drawableFolders)
			{
				var images = folder.listFiles(p -> p.getName().endsWith(".png") || p.getName().endsWith(".jpg") || p.getName().endsWith(".webp"));

				if (null != images)
				{
					for (var image : images)
					{
						result.add(new Drawable(image));
					}
				}
			}
			
			this.allImages = result.stream().filter(p -> p.is300x300()).toList();
		}
		
		
		
		
		var result = new ArrayList<Drawable>();
		float similarity = 0;
		
		

	
			for (var image : this.allImages)
			{
				try
				{
					var cc = ImageUtils.isSimillar(source, image.getScaledTo300x300(), mainColor);
					
					if (cc > 0.5f)
					{
						result.add(image);
					}
					
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		return result;
	}


	
	
	
	
	
	
	
	
	

	private static List<State> loadStates(Application app, List<TestDevice> testDevices, AppContext appContext)
	{
		var result = new ArrayList<State>();
		
		for (var testDevice : testDevices)
		{
			var stateFiles = new File(testDevice.getFolder(), app.folder() + "/states/").listFiles(pathelement -> pathelement.getAbsolutePath().endsWith("." + STATE_FILE_EXTENSION));
			
			if (null != stateFiles)
			{
				for (var stateFile : stateFiles)
				{
					var stateImage = new File(stateFile.getAbsolutePath().replaceAll(STATE_FILE_EXTENSION + "$", "png").replace("state_", "screen_"));
				
					if (!stateImage.exists())
					{
						stateImage = new File(stateFile.getAbsolutePath().replaceAll(STATE_FILE_EXTENSION + "$", "jpg").replace("state_", "screen_"));
					}
					
					if ((stateImage.exists()) && (stateImage.isFile()) && (stateImage.length() > 0))
					{
						result.add(new State("", appContext, stateImage, stateFile, testDevice));
					}
				}
			}
		}
		
		return result;
	}
	
	public synchronized @Nonnull List<Drawable> findDrawable(UIElement element)
	{
		if (element.getBackground() != null)
		{
			var result = findDrawable(element.getBackground(), "drawable", true);
			
			if (!result.isEmpty())
			{
				return result;
			}
			
			return findDrawable(element.getBackground(), "mipmap", true);
		}
		else if (element.getSrc() != null)
		{
			var result = findDrawable(element.getSrc(), "drawable", true);
			
			if (!result.isEmpty())
			{
				return result;
			}
			
			return findDrawable(element.getSrc(), "mipmap", true);
		}
		
		return Collections.emptyList();
	}
	
	public synchronized List<Drawable> findDrawable(String id, String type, boolean useSelectors)
	{
		if (!id.startsWith("@" + type + "/"))
		{
			return Collections.emptyList();
		}
		
		id = id.substring(("@" + type + "/").length());
		
		var result = this.drawables.getOrDefault(id, null);
		
		if (result == null)
		{
			result = new ArrayList<>();

			var drawableFolders = new File(Settings.unpackedAppsFolder + this.app.apkFile() + "/res/").listFiles(p -> p.getName().startsWith(type) && p.isDirectory());

			for (var folder : drawableFolders)
			{
				var file1 = new File(folder, id + ".png");
				var file2 = new File(folder, id + ".jpg");
				var file3 = new File(folder, id + ".webp");
				var file4 = new File(folder, id + ".9.png");
				
				if (file1.exists())
				{
					result.add(new Drawable(file1));
				}
				
				if (file2.exists())
				{
					result.add(new Drawable(file2));
				}				
				
				if (file3.exists())
				{
					result.add(new Drawable(file3));
				}				

				if (file4.exists())
				{
					result.add(new Drawable(file4));
				}				
			}
			
			if (result.isEmpty() && useSelectors)
			{
				var drawableFile = new File(Settings.unpackedAppsFolder + this.app.apkFile() + "/res/drawable/" + id + ".xml");
				
				if (drawableFile.exists())
				{
					var drawable = new DrawableDocument(drawableFile);
					var rootElement = drawable.getRoot();
					
					if ("selector".equals(rootElement.getType()))
					{
						for (var item : rootElement.getChildren())
						{
							if (item.getDrawable() != null)
							{
								var drawables = findDrawable(item.getDrawable(), "drawable", false);
								
								result.addAll(drawables);
							}
						}
					}
				}
			}
			
			this.drawables.put(id, result);
		}
		
		return result;
	}
	
	private List<Drawable> getAllImages()
	{
		var result = new ArrayList<Drawable>();
		var folders = new String[] { "drawable", "mipmap" };

		for (var imageFolder : folders)
		{
			var drawableFolders = new File(Settings.unpackedAppsFolder + this.app.apkFile() + "/res/").listFiles(p -> p.getName().startsWith(imageFolder) && p.isDirectory());

			for (var folder : drawableFolders)
			{
				var images = folder.listFiles(p -> p.getName().endsWith(".png") || p.getName().endsWith(".jpg") || p.getName().endsWith(".webp"));

				if (null != images)
				{
					for (var image : images)
					{
						result.add(new Drawable(image));
					}
				}
			}
		}

		return result;
	}

	public Drawable getSmallestIcon()
	{
		return this.smallestIcon.instance();
	}
	
	public synchronized List<Layout> getLayouts()
	{
		if (null == this.layouts)
		{
			var layoutFiles = new File(Settings.unpackedAppsFolder + this.app.apkFile() + "/res/layout/").listFiles(p -> p.getAbsolutePath().endsWith(".xml"));

			if (layoutFiles != null)
			{
				this.layouts = Arrays.stream(layoutFiles).map(Layout::new).toList();
			}
			else
			{
				this.layouts = new ArrayList<>();
			}
		}
		
		return this.layouts;
	}
	
	public Layout getSplashLayout()
	{
		for (var layout : getLayouts())
		{
			if (layout.getLayoutFile().getName().contains("splash_screen") || layout.getLayoutFile().getName().contains("activity_splash"))
			{
				return layout;
			}
		}
		
		return null;
	}
	
	public List<UIElement> findElement(Control control)
	{
		var result = new ArrayList<UIElement>();
		var resourceId = control.getResourceId();
		var matcher = Pattern.compile(".*:id/(.*)$").matcher(resourceId);
		
		if (matcher.find())
		{
			var id = "@id/" + matcher.group(1);		
			
			getLayouts().forEach(p -> result.addAll(p.findElement(id)));
		}
		
		return result;
	}
	
	public SystemContext getSystemContext()
	{
		return this.systemContext;
	}	
	
	public List<State> getStates()
	{
		return this.states;
	}
	
	public synchronized LocalizedMessages getMessages()
	{
/*		if (null == this.messages)
		{
			var tempFolder = new File(Settings.unpackedAppsTempFolder + this.app.apkFile());

			try
			{
				if (!tempFolder.exists())
				{
					SystemUtils.executeCommand("java -jar ./tools/apktool_2.3.4.jar d " + this.apkFile.getAbsolutePath() + " -o " + tempFolder.getAbsolutePath());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			this.messages = new LocalizedMessages(tempFolder, this);
		}
*/
		return this.messages;
	}
	
	public record ResourceText(String key, String value, String file)
	{
	}
	
	public Map<String, List<ResourceText>> getResources()
	{
		return this.resources;
	}
	
	public File getDataFolder()
	{
		return this.dataFolder;
	}	
	
	public String getPackage()
	{
		return this.app.packageName();
	}
	
	public String getName()
	{
		return this.app.name();
	}

	private LocalizedMessages messages = null;
	private List<Layout> layouts = null;
	private List<Drawable> allImages = null;
	private final File dataFolder;	
	private final Map<String, List<ResourceText>> resources = new HashMap<>();
	private final SystemContext systemContext;
	private final HashMap<String, List<Drawable>> drawables = new HashMap<>();
	private final List<TestDevice> testDevices;
	private final Application app;
	private final List<State> states;
	private LazyObject<Drawable> smallestIcon = new LazyObject<>(() -> getAllImages().stream().filter(p -> p.isIcon() && p.getWidth() > 0).sorted((a, b) -> a.getWidth() - b.getWidth()).findFirst().orElse(null));	
	private static final String STATE_FILE_EXTENSION = "json";
}
