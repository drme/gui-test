using System;
using System.Collections.Generic;

namespace ImageSorter.Models
{
    public partial class Image
    {
        public long Id { get; set; }
        public string FileName { get; set; }
        public string Hash { get; set; }
        public long DefectTypeId { get; set; }
        public string Description { get; set; }

        public DefectType DefectType { get; set; }
    }
}
