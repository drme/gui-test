using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Brushes = System.Windows.Media.Brushes;


namespace DefectsViewer
{
    /// <summary>
    /// Logique d'interaction pour MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private static string baseFolder = "D:/_r/";
            //@"E:\darbai\sh\OneDrive_1_2-8-2019\app-screenshots\_results_\";
        //  @"E:\darbai\sh\OneDrive_1_2-8-2019\app-screenshots\_results_\";

        private ImagesManager sorter = new ImagesSorter(baseFolder);

        Dictionary<string, List<Rect>> defects = new Dictionary<string, List<Rect>>();
        List<Rect> rects = new List<Rect>();


		private List<String> previousDefects = new List<String>();


        public MainWindow()
        {
            InitializeComponent();

            ShowActiveImage();
        }

        private void NextImageClick(object sender, RoutedEventArgs e)
        {
            this.sorter.NextImage();
            Reset();
            ShowActiveImage();
        }

        private void MarkOkClick(object sender, RoutedEventArgs e)
        {
            this.sorter.MarkOk();
            Reset();
            ShowActiveImage();

			this.previousDefects.Clear();
        }

        private void MarkLaterClick(object sender, RoutedEventArgs e)
        {
            this.sorter.MarkLater();
            Reset();
            ShowActiveImage();

			this.previousDefects.Clear();
		}

		private void Reset()
        {
            X0 = -1;
            X1 = -1;
            Y0 = 1;
            Y1 = -1;
            this.rects.Clear();
            this.defects.Clear();
            this.updateStatus();
        }

		private void ShowActiveImage()
		{
			var image = this.sorter.ActiveImage.Length > 0 ? new BitmapImage(new Uri(this.sorter.ActiveImage.FullName)) : null;

			ShowImage(image, this.sorter.ActiveImage.FullName);
		}

        double actualW = -1;
        double actualH = -1;
        private void ShowImage(BitmapImage src, String fileName)
        {
			if (null == src)
			{
				this.ImageView.Source = new RenderTargetBitmap(100, 100, 96, 96, PixelFormats.Pbgra32);

				this.Title = this.sorter.Status + " " + fileName;
			}
			else
			{
				ImageSource s = src;
				actualH = src.PixelHeight;
				actualW = src.PixelWidth;



				RenderTargetBitmap rtb = new RenderTargetBitmap(src.PixelWidth, src.PixelHeight, 96, 96, PixelFormats.Pbgra32);

				DrawingVisual dv = new DrawingVisual();
				using (DrawingContext dc = dv.RenderOpen())
				{
					dc.DrawImage(src, new Rect(0, 0, src.PixelWidth, src.PixelHeight));
					foreach (Rect area in rects)
					{
						dc.DrawRectangle(null, new Pen(new SolidColorBrush(Color.FromRgb(255, 0, 0)), 1.5), area);
					}
				}


				rtb.Render(dv);

				s = rtb;


				RenderOptions.SetBitmapScalingMode(s, BitmapScalingMode.NearestNeighbor);



				this.ImageView.Source = src;//s;
				var p = PresentationSource.FromVisual(this);

				if (null != p)
				{
					Matrix m = p.CompositionTarget.TransformToDevice;
					var dx = m.M11;
					var dy = m.M22;

					double hh = src.PixelHeight;
					double ww = src.PixelHeight;


				}

				this.Title = this.sorter.Status + " " + src.PixelWidth + "x" + src.PixelHeight + " " + fileName;
			}
		}


		private void MarkDefect(String defect)
		{
			if (this.defects.ContainsKey(defect))
			{
				defects[defect].AddRange(rects);
			}
			else
			{
				defects.Add(defect, new List<Rect>(rects));
			}

			updateStatus();
			rects.Clear();
			ShowActiveImage();

			if (false == this.previousDefects.Contains(defect))
			{
				this.previousDefects.Add(defect);
			}
		}

        private void MarkInvalid(object sender, RoutedEventArgs e)
        {
            this.sorter.MarkInvalid();
            ShowActiveImage();
        }

        private void UnreadableClick(object sender, RoutedEventArgs e)
        {
            MarkDefect(DefectTypes.UnreadableText);
        }

       
        private void Save(object sender, RoutedEventArgs e)
        {
            if (defects.Count > 0)
            {
                this.sorter.MarkBad(this.defects.Select(x => x.Key + "@[" + string.Join(",", x.Value.Select(p => $"({p.X}x{p.Y},{p.X+p.Width}x{p.Y + p.Height})")) + "]"
                ).ToArray());
            } else
            {
                this.sorter.MarkOk();
            }
            Reset();

            ShowActiveImage();

        }
		private void updateStatus()
		{
			RichTextBoxStatus.Document.Blocks.Clear();

			foreach (var d in defects)
			{
				RichTextBoxStatus.Document.Blocks.Add(new Paragraph(new Run(d.Key + " " + "@[" + string.Join(",", d.Value.Select(p => $"({p.X}x{p.Y},{p.X + p.Width}x{p.Y + p.Height})")) + "]")));
			}
		}

		private void UntranslatedClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.UntranslatedText);
		}

        private void DefectsClick(object sender, RoutedEventArgs e)
        {
            Reset();
            this.sorter = new ImagesManager(baseFolder);
            ShowActiveImage();
        }

		private void BadScaling_Click(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.BadScaling);
		}
        
        private void WindowPreviewKeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Space)
            {
                NextImageClick(sender, null);
            }
            else if (e.Key == Key.Enter || e.Key == Key.A)
            {
                MarkOkClick(sender, null);
            }
            else if (e.Key == Key.Escape)
            {
                MarkLaterClick(sender, null);
            }
            else if (e.Key == Key.S)
            {
                Save(sender, null);
            }
			else if (e.Key == Key.C)
			{
				MarkDefect(DefectTypes.ClippedControl);
			}
			else if (e.Key == Key.T)
			{
				MarkDefect(DefectTypes.ClippedText);
			}
			else if (e.Key == Key.E)
			{
				MarkDefect(DefectTypes.EmptyView);
			}
			else if (e.Key == Key.I)
            {
                MarkInvalid(sender, null);
            }
			else if (e.Key == Key.U)
			{
				MarkDefect(DefectTypes.UntranslatedText);
			}
			else if (e.Key == Key.W)
			{
				MarkDefect(DefectTypes.WastedSpace);
			}
			e.Handled = true;
        }

		private void WindowSizeChanged(object sender, SizeChangedEventArgs e)
		{
			ShowActiveImage();
		}

		private void WastedSpaceClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.WastedSpace);
		}

		private void ClippedTextClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.ClippedText);
		}

		private void BadColorsClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.BadColors);
		}




        private void ImageView_MouseRightButtonDown(object sender, MouseButtonEventArgs e)
        {
            System.Windows.Point p = e.GetPosition(this.ImageView);
            this.X1 = p.X;
            this.Y1 = p.Y;
            var ah = this.ImageView.ActualHeight;
            var aw = this.ImageView.ActualWidth;
            var h = Math.Abs(Y1 - Y0);
            var w = Math.Abs(X1 - X0);
            if (X0 > 0 && Y0 > 0 && h > 0 && w > 0 && h > 0 && w > 0)
            {
                var dh = this.actualH / ah;
                var dw = this.actualW / aw;
                var area = new Rect((int)(X0 * dw), (int)(Y0 * dh), (int)(w * dw), (int)(h * dh));
                this.rects.Add(area);
            }
            ShowActiveImage();

        }

        private void ImageView_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            System.Windows.Point p = e.GetPosition(this.ImageView);
            this.X0 = p.X;
            this.Y0 = p.Y;

        }
        private double X0, X1, Y0, Y1 = -1;

        private void TooSmallText_Click(object sender, RoutedEventArgs e)
        {
       }

        private void Misaligned_Click(object sender, RoutedEventArgs e)
        {
            MarkDefect(DefectTypes.MisalignedControl);
        }

		private void BadSpellingClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.BadSpelling);
		}

		private void TechnicalJargonClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.TechnicalJargon);
		}

		private void LowResImageClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.LowResImage);
		}

		private void ClippedControlClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.ClippedControl);
		}

		private void NoMarginsClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.NoMargins);
		}

		private void UncenteredClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.Uncentered);
		}

		private void UnfilledPlaceholderClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.UnfilledPlaceholder);
		}

		private void BadMarginsClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.BadMargins);
		}

		private void DefectButton_Click(object sender, RoutedEventArgs e)
		{
			MarkDefect(((DefectButton)sender).Defect);
		}

		private void SameAsPreviousClick(object sender, RoutedEventArgs e)
		{
			foreach (var defect in this.previousDefects)
			{
				MarkDefect(defect);
			}
		}

		private void NotEnoughSpace_Click(object sender, RoutedEventArgs e)
        {
            MarkDefect(DefectTypes.NotEnoughSpace);
        }
        private void Clear_Click(object sender, RoutedEventArgs e)
        {
			this.previousDefects.Clear();

            Reset();
      
            ShowActiveImage();

        }

        private void RichTextBox_TextChanged(object sender, TextChangedEventArgs e)
        {

        }
    }
}
