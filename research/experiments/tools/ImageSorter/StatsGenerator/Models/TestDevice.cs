using System;
using System.Collections.Generic;

namespace StatsGenerator.Models
{
    public partial class TestDevice
    {
        public TestDevice()
        {
            ScreenShot = new HashSet<ScreenShot>();
        }

        public long Id { get; set; }
        public string Name { get; set; }

        public virtual ICollection<ScreenShot> ScreenShot { get; set; }
    }
}
