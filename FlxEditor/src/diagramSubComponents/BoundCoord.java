package diagramSubComponents;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BoundCoord {
	DoubleProperty x = new SimpleDoubleProperty();
	DoubleProperty y = new SimpleDoubleProperty();
	
	public BoundCoord(){}
	
	public BoundCoord(double x, double y)
	{
		this.x.set(x);
		this.y.set(y);
	}
	
	public DoubleProperty getX()
	{
		return this.x;
	}
	
	public DoubleProperty getY()
	{
		return this.y;
	}
	
	public void setX(double x)
	{
		this.x.set(x);
	}
	
	public void setY(double y)
	{
		this.y.set(y);
	}
}
