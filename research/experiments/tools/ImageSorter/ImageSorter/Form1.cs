using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ImageSorter
{
	public partial class Form1 : Form
	{
		List<FileInfo> images = new List<FileInfo>();
		int active = 0;

		public Form1()
		{
			InitializeComponent();
		}

		private void button1_Click(object sender, EventArgs e)
		{
			active++;
			active %= images.Count;

			try { 
			this.pictureBox1.Image = new Bitmap(images[active].FullName);
		}
			catch (Exception)
			{

			}

			this.Text = "" + active + "/" + images.Count;
		}

		private void RemoveImageFromList(String fileName)
		{
			foreach(var f in this.images)
			{
				if (f.FullName == fileName)
				{
					Debug.WriteLine("Rem " + fileName);
					this.images.Remove(f);
					break;
				}
			}
		}

		private void Form1_Load(object sender, EventArgs e)
		{
			ScanFolder(new DirectoryInfo("d:/_results_"));

			using (var file = new StreamReader("d:/_results_/ok.txt"))
			{
				String fileName;

				while ((fileName = file.ReadLine()) != null)
				{
					RemoveImageFromList(fileName);
				}
			}

			using (var file = new StreamReader("d:/_results_/bad.txt"))
			{
				String fileName;

				while ((fileName = file.ReadLine()) != null)
				{
					RemoveImageFromList(fileName);
				}
			}






			Random rnd = new Random();

			images = images.OrderBy(x => rnd.Next()).ToList();

			this.pictureBox1.Image = new Bitmap(images[0].FullName);
		}

		private void ScanFolder(DirectoryInfo folder)
		{
			if (folder.Name == "views" || folder.Name == "temp")
			{
				return;
			}

			foreach (var file in folder.GetFiles())
			{
				if ((file.Extension == ".jpg") || (file.Extension == ".png"))
				{
					images.Add(file);
				}
			}

			foreach (var directory in folder.GetDirectories())
			{
				ScanFolder(directory);
			}
		}

		private void button2_Click(object sender, EventArgs e)
		{
			active--;
			if (active < 0)
			{
				active = 0;
			}

			try { 
			this.pictureBox1.Image = new Bitmap(images[active].FullName);
			}
			catch (Exception)
			{

			}


			this.Text = "" + active + "/" + images.Count;
		}

		private void button3_Click(object sender, EventArgs e)
		{
			File.AppendAllText("d:/_results_/ok.txt", images[active].FullName + "\n");
			button1_Click(sender, e);
		}

		private void button4_Click(object sender, EventArgs e)
		{
			File.AppendAllText("d:/_results_/bad.txt", images[active].FullName + "\n");
			button1_Click(sender, e);
		}
	}
}
