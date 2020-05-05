using System;
using System.Collections.Generic;

namespace ImageSorter.Models
{
    public partial class DefectType
    {
        public DefectType()
        {
            Image = new HashSet<Image>();
        }

        public long Id { get; set; }
        public string Code { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }

        public ICollection<Image> Image { get; set; }
    }
}
