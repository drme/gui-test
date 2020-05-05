using System;
using System.Collections.Generic;

namespace StatsGenerator.Models
{
    public partial class DefectType
    {
        public DefectType()
        {
            Defect = new HashSet<Defect>();
        }

        public long Id { get; set; }
        public string Code { get; set; }
        public string Description { get; set; }

        public virtual ICollection<Defect> Defect { get; set; }
    }
}
