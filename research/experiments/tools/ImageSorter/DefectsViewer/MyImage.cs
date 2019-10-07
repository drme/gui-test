using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media;

namespace DefectsViewer
{
	class MyImage : System.Windows.Controls.Image
	{
		protected override void OnRender(DrawingContext dc)
		{
			this.VisualBitmapScalingMode = System.Windows.Media.BitmapScalingMode.NearestNeighbor;
			base.OnRender(dc);
		}
	}
}
