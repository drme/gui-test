using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace StatsGenerator.Models
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

        public virtual DbSet<Defect> Defect { get; set; }
        public virtual DbSet<DefectType> DefectType { get; set; }
        public virtual DbSet<ScreenShot> ScreenShot { get; set; }
        public virtual DbSet<TestDevice> TestDevice { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
                optionsBuilder.UseSqlServer("Server=localhost;Database=defects-db;Integrated Security=true;");
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.HasAnnotation("ProductVersion", "2.2.4-servicing-10062");

            modelBuilder.Entity<Defect>(entity =>
            {
                entity.HasOne(d => d.DefectType)
                    .WithMany(p => p.Defect)
                    .HasForeignKey(d => d.DefectTypeId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Defect__DefectTy__4222D4EF");

                entity.HasOne(d => d.ScreenShot)
                    .WithMany(p => p.Defect)
                    .HasForeignKey(d => d.ScreenShotId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Defect__ScreenSh__4316F928");
            });

            modelBuilder.Entity<DefectType>(entity =>
            {
                entity.Property(e => e.Code).IsRequired();

                entity.Property(e => e.Description).IsRequired();
            });

            modelBuilder.Entity<ScreenShot>(entity =>
            {
                entity.Property(e => e.FileName).IsRequired();

                entity.HasOne(d => d.TestDevice)
                    .WithMany(p => p.ScreenShot)
                    .HasForeignKey(d => d.TestDeviceId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__ScreenSho__TestD__3D5E1FD2");
            });

            modelBuilder.Entity<TestDevice>(entity =>
            {
                entity.Property(e => e.Name).IsRequired();
            });
        }
    }
}
