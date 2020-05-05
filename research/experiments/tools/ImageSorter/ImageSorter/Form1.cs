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
		private void button1_Click(object sender, EventArgs e)
		{
			active++;
			active %= images.Count;

			this.Text = "" + active + "/" + images.Count;
		}


		private void Form1_Load(object sender, EventArgs e)
		{

			Random rnd = new Random();

			images = images.OrderBy(x => rnd.Next()).ToList();
		}


		private void button2_Click(object sender, EventArgs e)
		{
			active--;
			if (active < 0)
			{
				active = 0;
			}

			this.Text = "" + active + "/" + images.Count;
		}

		private void button4_Click(object sender, EventArgs e)
		{
			File.AppendAllText("d:/_results_/bad.txt", images[active].FullName + "\n");
			button1_Click(sender, e);
		}

		private void CopyToClipboardClick(object sender, EventArgs e)
		{
			Clipboard.SetImage(this.imageView.Image);
		}
	}
}
