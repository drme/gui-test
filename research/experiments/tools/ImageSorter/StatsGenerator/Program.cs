using StatsGenerator.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace StatsGenerator
{
    class Program
    {
        static void Main(string[] args)
        {
            using (var dataBase = new defectsdbContext())
            {
                //ValidateImages(dataBase, "E:/gui/_r/");
                //ImportResults(dataBase, "E:/gui/_r/1080x1920-he/", "1080x1920-he");
            }
        }

        private static void ValidateImages(defectsdbContext dataBase, String rootFolder)
        {
            var images = dataBase.ScreenShot;

            foreach (var image in images)
            {
                var fileName = Path.Combine(rootFolder, image.FileName);

                if (false == File.Exists(fileName))
                {
                    Console.WriteLine("Missing: " + image.Id + " " + fileName);
                }
            }
        }

        private static void ImportResults(defectsdbContext dataBase, String rootFolder, String deviceName)
        {
            var devices = new Dictionary<String, TestDevice>();

            SaveOkImages(dataBase, Path.Combine(rootFolder, "ok.txt"), devices, deviceName);
            SaveInvalidImages(dataBase, Path.Combine(rootFolder, "invalid.txt"), devices, deviceName);
            SaveDefectImages(dataBase, Path.Combine(rootFolder, "bad.txt"), devices, deviceName);
        }

        private static void SaveOkImages(defectsdbContext dataBase, String okFile, Dictionary<String, TestDevice> devices, String deviceName)
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
                            if (null == deviceName)
                            {
                                deviceName = fileName.Split('\\')[0];
                            }

                            var device = GetTestDevice(dataBase, deviceName, devices);

                            var screenShot = new ScreenShot();
                            screenShot.FileName = deviceName + "\\" + fileName;
                            screenShot.TestDevice = device;

                            dataBase.ScreenShot.Add(screenShot);
                        }
                    }
                }

                dataBase.SaveChanges();
            }
        }

        private static TestDevice GetTestDevice(defectsdbContext dataBase, String deviceName, Dictionary<String, TestDevice> devices)
        {
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

            return device;
        }

        private static void SaveInvalidImages(defectsdbContext dataBase, String invalidFile, Dictionary<String, TestDevice> devices, String deviceName)
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
                            if (null == deviceName)
                            {
                                deviceName = fileName.Split('\\')[0];
                            }

                            var device = GetTestDevice(dataBase, deviceName, devices);

                            var screenShot = new ScreenShot();
                            screenShot.FileName = deviceName + "\\" + fileName;
                            screenShot.TestDevice = device;
                            screenShot.Invalid = true;

                            dataBase.ScreenShot.Add(screenShot);
                        }
                    }
                }

                dataBase.SaveChanges();
            }
        }

        private static void SaveDefectImages(defectsdbContext dataBase, string badFile, Dictionary<string, TestDevice> devices, String deviceName)
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
                            if (null == deviceName)
                            {
                                deviceName = fileName.Split('\\')[0];
                            }

                            var device = GetTestDevice(dataBase, deviceName, devices);

                            String[] img = fileName.Split('|');
                            String source = img[0];

                            var screenShot = new ScreenShot();
                            screenShot.FileName = deviceName + "\\" + source;
                            screenShot.TestDevice = device;
                            screenShot.Invalid = false;

                            dataBase.ScreenShot.Add(screenShot);

                            Console.WriteLine(source);

                            for (int i = 1; i < img.Length; i++)
                            {
                                var d = img[i].Split('@')[0];

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
                            }
                        }
                    }
                }

                dataBase.SaveChanges();
            }
        }
    }
}
