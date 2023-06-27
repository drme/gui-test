package edu.ktu.screenshotanalyser.utils;

import java.util.HashSet;

public class HashSetFloat extends HashSet<Float>
{
	public HashSetFloat(float delta)
	{
		super();
		
		this.delta = delta;
	}
	
	@Override
	public boolean add(Float e)
	{
		if (this.stream().noneMatch(p -> (Math.abs(p - e) > this.delta)))
		{
			return super.add(e);
		}
		
		return false;
	}
	
	public Float first()
	{
    if (this.isEmpty())
    {
      return null;
    }
    
    return this.iterator().next();
	}

	private final float delta;
}
