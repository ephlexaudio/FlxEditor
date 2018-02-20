package diagramSubComponents;

public class Coord {
	double x;
	double y;
	
	public Coord(){}
	
	public Coord(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getX()
	{
		return this.x;
	}
	
	public double getY()
	{
		return this.y;
	}
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public void offset(double x, double y)
	{
		this.x += x;
		this.y += y;
	}

}
