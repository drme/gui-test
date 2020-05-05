using System;
using System.Collections.Generic;

namespace StatsGenerator.Models
{
    public partial class Defect
    {
        public long Id { get; set; }
        public long ScreenShotId { get; set; }
        public long DefectTypeId { get; set; }

        public virtual DefectType DefectType { get; set; }
        public virtual ScreenShot ScreenShot { get; set; }
    }
}
