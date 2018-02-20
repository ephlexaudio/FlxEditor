package diagramSubComponents;

import javafx.beans.property.DoubleProperty;

public class NamedBoundCoord extends BoundCoord{
	String coordName;
	
	public NamedBoundCoord(String name)
	{
		super(0,0);
		this.coordName = name;
		
	}
	
	public NamedBoundCoord(String name, double x, double y)
	{
		super(x,y);
		this.coordName = name;
	}

	public String getName()
	{
		return this.coordName;
	}
	
	public void setName(String name)
	{
		this.coordName = name;
	}
	public void setCoordX(double x)
	{
		super.x.set(x);
	}
	
	public void setCoordY(double y)
	{
		super.y.set(y);
	}
	 
	public void setCoord(double x, double y)
	{
		super.x.set(x);
		super.y.set(y);	
	}
	
	public void setCoord(Coord coord)
	{
		super.x.set(coord.x);
		super.y.set(coord.y);
	}
	
	public DoubleProperty getCoordX()
	{
		return super.x;
	}
	
	public DoubleProperty getCoordY()
	{
		return super.y;
	}
	
	public BoundCoord getCoord()
	{
		BoundCoord coord = new BoundCoord();
		coord.x = super.x;
		coord.y = super.y;
		
		return coord;
	}

	public void offset(double x, double y)
	{
		super.x.set(super.x.doubleValue() + x);
		super.y.set(super.y.doubleValue() + y);

	}
}
