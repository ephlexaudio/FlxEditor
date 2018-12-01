package diagramComponents;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import diagramSubComponents.BoundCoord;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class FlxControlWire_Impl extends FlxWire_Impl implements FlxControlWire {

	boolean debugStatements = false;
	boolean errorStatements = true;
	DoubleProperty x1 = new SimpleDoubleProperty();
	DoubleProperty y1 = new SimpleDoubleProperty();
	DoubleProperty x2 = new SimpleDoubleProperty();
	DoubleProperty y2 = new SimpleDoubleProperty();

	public FlxControlWire_Impl(String name)
	{
		super(name);
	}

	public FlxControlWire_Impl(String name, BoundCoord src, BoundCoord dest, String parentEffectName)
	{
		super(name, src, dest, parentEffectName);
	}

	public FlxControlWire_Impl(String name, double srcX, double srcY, double destX, double destY, String parentEffectName)
	{
		super(name, srcX, srcY, destX, destY, parentEffectName);
	}

	public FlxControlWire_Impl(JsonValue processConnectionJson, String parentEffectName)
	{
		super(processConnectionJson, parentEffectName);
	}

	public void deleteWireSymbol()
	{
		super.deleteWireSymbol();
	}

	public JsonValue getControlConnectionData() {
		if(this.debugStatements) System.out.println("getting control connection data: " + super.wireName);
		JsonObjectBuilder controlConnection = Json.createObjectBuilder();
		JsonObject controlConnectionBuilt = null;

		try
		{
			controlConnection.add("src", super.src.getConnectionData());
			controlConnection.add("dest", super.dest.getConnectionData());
			controlConnection.add("x1", this.x1.doubleValue());
			controlConnection.add("y1", this.y1.doubleValue());
			controlConnection.add("x2", this.x2.doubleValue());
			controlConnection.add("y2", this.y2.doubleValue());
			controlConnectionBuilt = controlConnection.build();

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxControlWire::getControlConnectionData error: " + e);
		}
		if(this.debugStatements) System.out.println("control connection data retrieved: " + super.wireName);
		return (JsonValue)controlConnectionBuilt;
	}

	public String getControlConnectionString() {
		if(this.debugStatements) System.out.println("getting control connection data: " + super.wireName);
		String controlConnection = "";

		try
		{
			controlConnection += "{\"src\":" + super.src.getConnectionString() + ",";
			controlConnection += "\"dest\":" + super.dest.getConnectionString() + ",";
			controlConnection += "\"x1\":" + super.src.getCoordX() + ",";
			controlConnection += "\"y1\":" + super.src.getCoordY() + ",";
			controlConnection += "\"x2\":" + super.dest.getCoordX() + ",";
			controlConnection += "\"y2\":" + super.dest.getCoordY() + "}";
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxControlWire::getControlConnectionData error: " + e);
		}

		if(this.debugStatements) System.out.println("control connection data: " + controlConnection);
		return controlConnection;
	}


}
