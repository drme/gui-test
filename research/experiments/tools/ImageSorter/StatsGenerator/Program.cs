using StatsGenerator.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace StatsGenerator
{






	public struct DefectTypes
	{
		public const string TextPlacement = "TP1";
		public const string FontSizes = "TS1";
		public const string UnreadableText = "TS2";
		public const string ClashingBackground = "TB1";
		public const string PartialText = "TC1";
		public const string ClippedText = "TC2";
		public const string WrongEncoding = "TE1";
		public const string MissingText = "TM1";

		public const string NotEnoughSpace = "NS1";

		public const String UntranslatedText = "SL2";
		public const String WastedSpace = "WS1";
		public const String BadColors = "BC1";
		public const String BadScaling = "BS1";
		public const String InvisibleControl = "IC1";
		public const String MisalignedControl = "MC1";
		public const String BadSpelling = "BadSpelling";
		public const String TechnicalJargon = "TechnicalJargon";
		public const String LowResImage = "LowResImage";
		public const String ClippedControl = "ClippedControl";
		public const String NoMargins = "NoMargins";
		public const String Uncentered = "Uncentered";
		public const String UnfilledPlaceholder = "UnfilledPlaceholder";
		public const String BadMargins = "BadMargins";
		public const String ObscuredControl = "ObscuredControl";
		public const String NoAntiAliasing = "NoAntiAliasing";
		public const String EmptyView = "EmptyView";
		public const String UnalignedControlls = "UnalignedControls";
		public const String CrowdedControlls = "CrowdedControlls";
		public const String ObscuredText = "ObscuredText";
		public const String UnlabeledEntry = "UnlabeledEntryField";


		//Misaligned controls
		public const String Unknown = "???";

	}

	























		class Program
	{













		static void Main(string[] args)
		{
            using (var dataBase = new defectsdbContext())
            {



                String rootFolder = "D:/_r/";


                String badFile;
                String okFile;
                String invalidFile;
                List<String> sortedImages = new List<String>();
                String laterFile;
                Random r = new Random();







                DirectoryInfo root = new DirectoryInfo(rootFolder);

                badFile = Path.Combine(rootFolder, "bad.txt");
                okFile = Path.Combine(rootFolder, "ok.txt");
                invalidFile = Path.Combine(rootFolder, "invalid.txt");
                laterFile = Path.Combine(rootFolder, "later.txt");


                var devices = new Dictionary<String, TestDevice>();

                SaveOkImages(dataBase, okFile, devices);
                SaveInvalidImages(dataBase, invalidFile, devices);
                SaveDefectImages(dataBase, badFile, devices);


                long totalImages = 0;


                var defects = new Dictionary<String, long>();


                if (File.Exists(badFile))
                {
                    using (var file = new StreamReader(badFile))
                    {
                        String fileName;

                        while ((fileName = file.ReadLine()) != null)
                        {
                            string[] img = fileName.Split('|');

                            String source = img[0];

                            for (int i = 1; i < img.Length; i++)
                            {
                                var d = img[i].Split('@')[0];

                                if (false == defects.ContainsKey(d))
                                {
                                    defects.Add(d, 1);
                                }
                                else
                                {
                                    defects[d] += 1;
                                }

                            }


                            totalImages += 1;
                        }
                    }
                }

                System.Console.WriteLine("defects: " + totalImages);


                foreach (var dd in defects.Keys)
                {
                    System.Console.WriteLine(dd + ": " + defects[dd]);
                }


                if (File.Exists(okFile))
                {
                    using (var file = new StreamReader(okFile))
                    {
                        String fileName;

                        while ((fileName = file.ReadLine()) != null)
                        {
                            totalImages += 1;
                        }
                    }
                }


                if (File.Exists(okFile))
                {
                    using (var file = new StreamReader(okFile))
                    {
                        String fileName;

                        while ((fileName = file.ReadLine()) != null)
                        {
                            totalImages += 1;
                        }
                    }
                }


                System.Console.WriteLine("totall: " + totalImages);



            }
        }

        private static void SaveOkImages(defectsdbContext dataBase, string okFile, Dictionary<string, TestDevice> devices)
        {
            if (File.Exists(okFile))
            {
                using (var file = new StreamReader(okFile))
                {
                    String fileName;

                    while ((fileName = file.ReadLine()) != null)
                    {
                        if (fileName.Length > 0)
                        {
                            var deviceName = fileName.Split('\\')[0];

                            TestDevice device;

                            if (false == devices.TryGetValue(deviceName, out device))
                            {
                                device = dataBase.TestDevice.FirstOrDefault(p => p.Name == deviceName);

                                if (null == device)
                                {
                                    device = new TestDevice();
                                    device.Name = deviceName;

                                    dataBase.TestDevice.Add(device);

                                    dataBase.SaveChanges();
                                }
                                else
                                {
                                    devices[deviceName] = device;
                                }
                            }

//                            var screenShot = dataBase.ScreenShot.FirstOrDefault(p => p.FileName == fileName);

                            //                                if (null != screenShot)
                            //                              {
                            //                                System.Diagnostics.Debug.WriteLine("Exists :" + fileName);
                            //                          }
                            //                        else
                            {
                                var screenShot = new ScreenShot();
                                screenShot.FileName = fileName;
                                screenShot.TestDevice = device;

                                dataBase.ScreenShot.Add(screenShot);

                            }
                        }


                    }
                }

                dataBase.SaveChanges();
            }
        }


        private static void SaveInvalidImages(defectsdbContext dataBase, string invalidFile, Dictionary<string, TestDevice> devices)
        {
            if (File.Exists(invalidFile))
            {
                using (var file = new StreamReader(invalidFile))
                {
                    String fileName;

                    while ((fileName = file.ReadLine()) != null)
                    {
                        if (fileName.Length > 0)
                        {
                            var deviceName = fileName.Split('\\')[0];

                            TestDevice device;

                            if (false == devices.TryGetValue(deviceName, out device))
                            {
                                device = dataBase.TestDevice.FirstOrDefault(p => p.Name == deviceName);

                                if (null == device)
                                {
                                    device = new TestDevice();
                                    device.Name = deviceName;

                                    dataBase.TestDevice.Add(device);

                                    dataBase.SaveChanges();
                                }
                                else
                                {
                                    devices[deviceName] = device;
                                }
                            }

                           // var screenShot = dataBase.ScreenShot.FirstOrDefault(p => p.FileName == fileName);

                            //                                if (null != screenShot)
                            //                              {
                            //                                System.Diagnostics.Debug.WriteLine("Exists :" + fileName);
                            //                          }
                            //                        else
                            {
                               var screenShot = new ScreenShot();
                                screenShot.FileName = fileName;
                                screenShot.TestDevice = device;
                                screenShot.Invalid = true;

                                dataBase.ScreenShot.Add(screenShot);

                            }
                        }


                    }
                }

                dataBase.SaveChanges();
            }
        }



        private static void SaveDefectImages(defectsdbContext dataBase, string badFile, Dictionary<string, TestDevice> devices)
        {
            var defectTypes = new Dictionary<String, DefectType>();



            if (File.Exists(badFile))
            {
                using (var file = new StreamReader(badFile))
                {
                    String fileName;

                    while ((fileName = file.ReadLine()) != null)
                    {
                        if (fileName.Length > 0)
                        {
                            var deviceName = fileName.Split('\\')[0];

                            TestDevice device;

                            if (false == devices.TryGetValue(deviceName, out device))
                            {
                                device = dataBase.TestDevice.FirstOrDefault(p => p.Name == deviceName);

                                if (null == device)
                                {
                                    device = new TestDevice();
                                    device.Name = deviceName;

                                    dataBase.TestDevice.Add(device);

                                    dataBase.SaveChanges();
                                }
                                else
                                {
                                    devices[deviceName] = device;
                                }
                            }





                            string[] img = fileName.Split('|');

                            String source = img[0];


                            var screenShot = new ScreenShot();
                            screenShot.FileName = source;
                            screenShot.TestDevice = device;
                            screenShot.Invalid = false;


                            dataBase.ScreenShot.Add(screenShot);


                            Console.WriteLine(source);


                            for (int i = 1; i < img.Length; i++)
                            {
                                var d = img[i].Split('@')[0];

                                //     if (false == defects.ContainsKey(d))
                                //    {
                                //       defects.Add(d, 1);
                                //  }
                                // else
                                //{
                                //   defects[d] += 1;
                                // }



                                DefectType defectType;

                                if (false == defectTypes.TryGetValue(d, out defectType))
                                {
                                    defectType = dataBase.DefectType.FirstOrDefault(p => p.Code == d);

                                    if (null == defectType)
                                    {
                                        defectType = new DefectType();
                                        defectType.Code = d;
                                        defectType.Description = d;

                                        dataBase.DefectType.Add(defectType);

                                        dataBase.SaveChanges();
                                    }
                                    else
                                    {
                                        defectTypes[d] = defectType;
                                    }
                                }


                                var defect = new Defect();
                                defect.DefectType = defectType;
                                defect.ScreenShot = screenShot;

                                dataBase.Defect.Add(defect);



                             //   System.Diagnostics.Debug.WriteLine("Defect: " + d);
                            }










                            // var screenShot = dataBase.ScreenShot.FirstOrDefault(p => p.FileName == fileName);

                            //                                if (null != screenShot)
                            //                              {
                            //                                System.Diagnostics.Debug.WriteLine("Exists :" + fileName);
                            //                          }
                            //                        else
                            {
                                //var screenShot = new ScreenShot();
                               // screenShot.FileName = fileName;
                             //   screenShot.TestDevice = device;
                           //     screenShot.Invalid = false;



                            //    dataBase.ScreenShot.Add(screenShot);

                            }
                        }


                    }
                }

                dataBase.SaveChanges();
            }
        }







    }
}
