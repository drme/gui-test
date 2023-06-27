package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.List;
import edu.ktu.screenshotanalyser.context.State;

public class StateCheckResults
{
	public StateCheckResults(State state)
	{
		this.state = state;
	}
	
	public State getState()
	{
		return this.state;
	}
	
	public List<DefectAnnotation> getAnnotations()
	{
		return this.annotations;
	}
	
	public void addAnnotation(DefectAnnotation annotation)
	{
		this.annotations.add(annotation);
	}
	
	private final State state;
	private final List<DefectAnnotation> annotations = new ArrayList<>();
}
