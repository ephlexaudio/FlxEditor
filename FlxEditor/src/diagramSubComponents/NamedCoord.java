package diagramSubComponents;

public class NamedCoord extends Coord {
	String coordName;
	
	public NamedCoord(String name)
	{
		super(0,0);
		this.coordName = name;
		
	}
	
	public NamedCoord(String name, double x, double y)
	{
		super(x,y);
		this.coordName = name;
	}

	public String getName()
	{
		return this.coordName;
	}
	
	public void setCoordX(double x)
	{
		super.x = x;
	}
	
	public void setCoordY(double y)
	{
		super.y = y;
	}
	 
	public void setCoord(double x, double y)
	{
		super.x = x;
		super.y = y;		
	}
	
	public void setCoord(Coord coord)
	{
		super.x = coord.x;
		super.y = coord.y;		
	}
	
	public double getCoordX()
	{
		return super.x;
	}
	
	public double getCoordY()
	{
		return super.y;
	}
	
	public Coord getCoord()
	{
		Coord coord = new Coord();
		coord.x = super.x;
		coord.y = super.y;
		
		return coord;
	}
	
}
