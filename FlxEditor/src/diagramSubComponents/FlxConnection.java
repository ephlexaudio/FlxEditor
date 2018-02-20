package diagramSubComponents;

import javax.json.Json;
import javax.json.JsonObject;
//import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class FlxConnection {
	
	String parentObjectName;
	String portName;
	DoubleProperty x = new SimpleDoubleProperty();
	DoubleProperty y = new SimpleDoubleProperty();
	
	public FlxConnection(String parentObjectName, String portName)
	{
		this.parentObjectName = parentObjectName;
		this.portName = portName;
	}
	
	public FlxConnection(String parentObjectName, String portName, double x, double y)
	{
		this.parentObjectName = parentObjectName;
		this.portName = portName;
		this.x.set(x);
		this.y.set(y);
	}

	public String getName()
	{
		return this.parentObjectName + ":" + this.portName;
	}
	
	public String getPort()
	{
		return this.portName;
	}
		
	public String getParentObject()
	{
		return this.parentObjectName;
	}
	
	public void setParentObjectName(String parentObjectName)
	{
		this.parentObjectName = parentObjectName;
	}
	
	public void setCoordX(double x)
	{
		this.x.set(x);
	}
	
	public void setBoundCoordX(DoubleProperty x)
	{
		this.x = x;
	}
	
	public void setCoordY(double y)
	{
		this.y.set(y);
	}
	
	public void setBoundCoordY(DoubleProperty y)
	{
		this.y = y;
	}
	 
	public void setDoubleCoord(double x, double y)
	{
		this.x.set(x);
		this.y.set(y);
	}
	
	public void setCoord(Coord coord)
	{
		this.x.set(coord.x);
		this.y.set(coord.y);
	}
	
	public double getCoordX()
	{
		return this.x.doubleValue();
	}
	
	public DoubleProperty getBoundCoordX()
	{
		return this.x;
	}
	
	public double getCoordY()
	{
		return this.y.doubleValue();
	}
	
	public DoubleProperty getBoundCoordY()
	{
		return this.y;
	}
	
	public Coord getCoord()
	{
		Coord coord = new Coord();
		coord.x = this.x.doubleValue();
		coord.y = this.y.doubleValue();
		
		return coord;
	}
	public BoundCoord getBoundCoord()
	{
		BoundCoord coord = new BoundCoord();
		coord.y = this.x;
		coord.y = this.y;
		
		return coord;
	}
	
	public JsonValue getConnectionData()
	{
		JsonObject connection = Json.createObjectBuilder()
			.add("object", this.parentObjectName)
			.add("port", this.portName)
			.add("x", this.x.doubleValue())
			.add("y", this.y.doubleValue())
			.build();
		
		return (JsonValue)connection;
	}
	
	public String getConnectionString()
	{
		String connectionString = "";//Json.createObjectBuilder()
		connectionString += "{\"object\":\"" + this.getParentObject() + "\",";
		connectionString += "\"port\":\"" + this.getPort() + "\",";
		connectionString += "\"x\":" + this.getCoordX() + ",";
		connectionString += "\"y\":" + this.getCoordY() + "}";
			/*.add("object", this.parentObjectName)
			.add("port", this.portName)
			.add("x", this.x.doubleValue())
			.add("y", this.y.doubleValue())
			.build();*/
		
		return connectionString;
	}

}
