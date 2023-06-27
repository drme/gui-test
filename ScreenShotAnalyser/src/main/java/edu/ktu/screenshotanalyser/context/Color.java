package edu.ktu.screenshotanalyser.context;

public class Color
{
	public Color(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 255;
	}
	
	Color(int single)
	{
		this.r = single;
		this.g = single;
		this.b = single;
		this.a = 255;
	}	
	
	Color(String code)
	{
		if ("#fff".equals(code))
		{
			this.a = this.r = this.g = this.b = 255;
		}
		else if ("#ccc".equals(code))
		{
			this.a = this.r = this.g = 204;
			this.b = 255;
		}
		else if ("#aaa".equals(code))
		{
			this.a = this.r = this.g = 170;
			this.b = 255;
		}
		else if ("#000".equals(code))
		{
			this.a = this.r = this.g = 0;
			this.b = 255;
		}
		else if ("#777".equals(code))
		{
			this.a = this.r = this.g = 119;
			this.b = 255;
		}
		else if ("#333".equals(code))
		{
			this.a = this.r = this.g = 51;
			this.b = 255;
		}
		else if ("#bfff".equals(code))
		{
			this.a = this.r = this.g = 255;
			this.b = 187;
		}
		else if (null != code && code.length() > 0 && code.startsWith("#")) 
		{
			code = code.replace("#", "");

			if (code.length() == 8)
			{
				this.a = Integer.valueOf(code.substring(0, 2), 16);
				this.r = Integer.valueOf(code.substring(2, 4), 16);
				this.g = Integer.valueOf(code.substring(4, 6), 16);
				this.b = Integer.valueOf(code.substring(6, 8), 16);
			}
			else if (code.length() == 6)
			{
				this.a = 255;
				this.r = Integer.valueOf(code.substring(0, 2), 16);
				this.g = Integer.valueOf(code.substring(2, 4), 16);
				this.b = Integer.valueOf(code.substring(4, 6), 16);
			}
			else
			{
				System.out.println("" + code);
			
				this.r = -1;
				this.g = -1;
				this.b = -1;
				this.a = -1;			
			}
		}
		else
		{
			this.r = -1;
			this.g = -1;
			this.b = -1;
			this.a = -1;
		}
	}
	
	public String toString()
	{
		return this.r + " "+ this.g + " " + this.b;
	}

	public final int r;
	public final int g;
	public final int b;
	public final int a;
}
