using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;

namespace DefectsViewer
{
	public class DefectButton : Button
	{
		private String defect;

		public String Defect
		{
			get
			{
				return this.defect;
			}
			set
			{
				this.Content = value;
				this.defect = value;
			}
		}
	}
}
