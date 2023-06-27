package edu.ktu.screenshotanalyser.utils;

import javax.annotation.Nonnull;

public class LazyObject<T>
{
	public interface ISupplier<T>
	{
		T get() throws Throwable;
	}
	
	public LazyObject(@Nonnull ISupplier<T> supplier)
	{
		this.supplier = supplier;
	}
	
	public synchronized T instance()
	{
		if (!this.loaded)
		{
			try
			{
				this.instance = this.supplier.get();
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
				
				this.instance = null;
			}
			
			this.loaded = true;
		}
		
		return this.instance;
	}
	
	public synchronized void unload()
	{
		this.instance = null;
		this.loaded = false;
	}
	
	private boolean loaded = false;
	private final ISupplier<T> supplier;
	private T instance = null;
}
