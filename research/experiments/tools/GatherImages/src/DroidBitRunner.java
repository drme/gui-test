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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DroidBitRunner
{
	private static boolean skipRestart = false;

	public static void main(String[] ar)
	{
//		String root = "./apps";
		String root = "/media/me/ddsdg/mt/apps";
//		String device = "n5-480x800";
		String device = "nokia31";
boolean emulator = false;



//		File resultsFolder = new File(folder.getAbsolutePath() + "/_results_/nx5-240x320-de/");

		String[] ddd = sa(new File(root).list());
		
		int i = 0;
		
		for (String folderName : ddd)
		{
			System.out.println("----------------- " + (i++) + "/" + ddd.length + " ---------------------");
			
			File folder = new File(root + "/" + folderName);

			if (folder.isDirectory())
			{
				runTests(folder, device, emulator);
			}
		}

	}

	private static void runTests(File folder, String device, boolean emulator)
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
				runDroidBot(folder, fileName, device, emulator);
			}
			catch (InterruptedException | ExecutionException ex)
			{
				ex.printStackTrace();
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

	private static void runDroidBot(File folder, File apkFile, String device, boolean emulator) throws InterruptedException, ExecutionException
	{
		File noEmulatorFile = new File(folder.getAbsolutePath() + "/_results_/no_emulator");

		if (noEmulatorFile.exists())
{

//                              System.out.println("---- no emulator ------------skip: " + apkFile.getName() + "----------- no emulator");
  
if (emulator)
{
return;
}
}


		File resultsFolder = new File(folder.getAbsolutePath() + "/_results_/" + device + "/");

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
//				System.out.println("----- has data ---------------skip: " + apkFile.getName());
				
				return;
			}



			
			}

System.out.println("===== running " + apkFile.getName());

		try
		{


			if (false == skipRestart)
			{
				runCommand("adb reboot bootloader");
/*



//				runCommand("adb reboot bootloader");
//				Thread.sleep(60000);
			
if (emulator)
				{
				runCommand("adb reboot bootloader");
				Thread.sleep(60000);
					}
				}).start();



*/
				Thread.sleep(60000);
			
				runCommand("adb kill-server");
				Thread.sleep(2000);

				runCommand("adb start-server");
				Thread.sleep(3000);
			}
			
			skipRestart = false;
			
			String command = "droidbot -a " + apkFile.getAbsolutePath().replaceAll("[ ]", "\\ ") + " -o " + resultsFolder.getAbsolutePath().replaceAll("[ ]", "\\ ") + " -keep_env -ignore_ad -timeout 300 -is_emulator";
			
			Process p = Runtime.getRuntime().exec(command);

			System.out.println(command);
			
			try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream())))
			{
				String line;

				while ((line = input.readLine()) != null)
				{
					System.out.println(line);

					if (line.contains("INSTALL_FAILED_NO_MATCHING_ABIS") || line.contains("DroidBot Stopped") || line.contains("Failed to disconnect DroidBotIME!"))
					{
						if (line.contains("INSTALL_FAILED_NO_MATCHING_ABIS"))
						{
							skipRestart = true;
	
							noEmulatorFile.createNewFile();
						}

						System.out.println("------ terminate ------");

						new Thread(new Runnable()
						{
							public void run()
							{
								try
								{
									Thread.sleep(10000);

									p.destroy();
								}
								catch (Throwable ex)
								{
									ex.printStackTrace();
								}
							}
						}
						).start();
					}
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


  // Implementing Fisher–Yates shuffle
  static <T> T[] sa(T[] ar)
  {
    // If running on Java 6 or older, use `new Random()` on RHS here
    Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--)
    {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      T a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }

return ar;
  }
  
  
	private static void runCommand(String command)
	{
		try
{
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
}
catch (IOException ex)
{
ex.printStackTrace();
}
	}
}
