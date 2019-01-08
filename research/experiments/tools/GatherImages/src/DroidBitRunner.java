import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.xml.ws.Holder;

public class DroidBitRunner
{
	public static void main(String[] ar)
	{
//		String root = "D:/jjj/Raccoon/content/apps";
		String root = "/home/me/apps";

		for (String folderName : new File(root).list())
		{
			File folder = new File(root + "/" + folderName);

			if (folder.isDirectory())
			{
				runTests(folder);
			}
		}

	}

	private static void runTests(File folder)
	{
		FileFilter filter = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.getName().endsWith(".apk");
			}
		};

		for (File fileName : folder.listFiles(filter))
		{
			try
			{
				runDroidBot(folder, fileName);
			}
			catch (InterruptedException | ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void uninstall(String name)
	{
		String command = "adb uninstall " + name;

		System.out.println("---------- unnstalling [" + command + "]");
		
		try
		{
			Process p = Runtime.getRuntime().exec(command);

			try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream())))
			{
				String line;

				while ((line = input.readLine()) != null)
				{
					System.out.println(line);
				}
			}

		}
		catch (Throwable e1)
		{
			e1.printStackTrace();

			return;
		}
	}

	private static void runDroidBot(File folder, File apkFile) throws InterruptedException, ExecutionException
	{
		File resultsFolder = new File(folder.getAbsolutePath() + "/_results_/jolla/");

		if (false == resultsFolder.exists())
		{
			resultsFolder.mkdirs();
		}
		else if (new File(resultsFolder.getAbsolutePath() + "/states/").exists())
		{
			if (new File(resultsFolder.getAbsolutePath() + "/states/").list(new FilenameFilter()
			{
				
				@Override
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".jpg") || name.endsWith(".png");
				}
			}).length > 0)
			{
				System.out.println("skip: " + apkFile.getName());
				
				return;
			}
		}

		try
{
			
			String command = "droidbot -a " + apkFile.getAbsolutePath() + " -o " + resultsFolder.getAbsolutePath() + " -keep_env -ignore_ad -timeout 300";
			
			 Process p = Runtime.getRuntime().exec(command);

			System.out.println(command);
			
			
			try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream())))
			{
				String line;

				while ((line = input.readLine()) != null)
				{
					System.out.println(line);
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}

			//uninstall(folder.getName());			
			
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		 
			

			// enter code here
			
			

		


		/*
		
		ExecutorService service = Executors.newSingleThreadExecutor();


		

		Future<?> future = service.submit(runnable);
		try {
		    future.get(5, TimeUnit.MINUTES);
		    // completed within timeout
		    System.out.println("==================================================================");
		} catch (TimeoutException e) {
		    // do something about the timeout
			
			System.out.println("----------------------------------------------------------------terminate");
			
			p.destroy();

	//		uninstall(folder.getName());
			
		} finally {
		    service.shutdown();
		    
		    
		    Thread.sleep(60000);
		    
		}
		
		*/
	}
}
