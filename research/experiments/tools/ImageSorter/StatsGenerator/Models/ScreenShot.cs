using System;
using System.Collections.Generic;

namespace StatsGenerator.Models
{
    public partial class ScreenShot
    {
        public ScreenShot()
        {
            Defect = new HashSet<Defect>();
        }

        public long Id { get; set; }
        public string FileName { get; set; }
        public long TestDeviceId { get; set; }
        public bool Invalid { get; set; }

        public virtual TestDevice TestDevice { get; set; }
        public virtual ICollection<Defect> Defect { get; set; }
    }
}
