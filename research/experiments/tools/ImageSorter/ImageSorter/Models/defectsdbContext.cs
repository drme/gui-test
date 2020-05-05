using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace ImageSorter.Models
{
    public partial class defectsdbContext : DbContext
    {
        public defectsdbContext()
        {
        }

        public defectsdbContext(DbContextOptions<defectsdbContext> options)
            : base(options)
        {
        }

        public virtual DbSet<DefectType> DefectType { get; set; }
        public virtual DbSet<Image> Image { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<DefectType>(entity =>
            {
                entity.Property(e => e.Code).IsRequired();

                entity.Property(e => e.Description).IsRequired();

                entity.Property(e => e.Name).IsRequired();
            });

            modelBuilder.Entity<Image>(entity =>
            {
                entity.Property(e => e.FileName).IsRequired();

                entity.Property(e => e.Hash).IsRequired();

                entity.HasOne(d => d.DefectType)
                    .WithMany(p => p.Image)
                    .HasForeignKey(d => d.DefectTypeId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Image__DefectTyp__4BAC3F29");
            });
        }
    }
}
