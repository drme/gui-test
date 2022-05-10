package edu.ktu.screenshotanalyser.utils;

public class Tuple<T, K, L>
{
  public final T first;
  public final K second;
  public final L third;
  
  public Tuple(T first, K second, L third)
  {
  	this.first = first;
  	this.second = second;
  	this.third = third;
  }
}
