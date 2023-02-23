package com.faisaldeveloper.office.fc.doc;

import android.graphics.Path;

public class PathWithArrow 
{
	public PathWithArrow(Path[] polygon, Path startArrow, Path endArrow)
	{
		this.polygon = polygon;
		this.startArrow = startArrow;
		this.endArrow = endArrow;
	}
	
	public Path getStartArrow()
	{
		return startArrow;
	}
	
	public Path getEndArrow()
	{
		return endArrow;
	}
	
	public Path[] getPolygonPath()
	{
		return polygon;
	}
	
	private final Path startArrow;
	private final Path endArrow;
	private final Path[] polygon;
}
