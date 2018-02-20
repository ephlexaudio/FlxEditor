package diagramComponents;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

class CordConnector{
	String object;
	String port;
}

public class FlxCord_Impl implements FlxCord {

	boolean errorStatements = true;
	int index;
	CordConnector src = new CordConnector();
	CordConnector dest = new CordConnector();
	
	FlxCord_Impl()
	{
		
	}
	
	FlxCord_Impl(JsonValue cordData)
	{
		try
		{
			JsonObject cordJsonObject = (JsonObject)cordData;
			this.index = cordJsonObject.getInt("index");
			JsonObject src = cordJsonObject.getJsonObject("src"); 
			JsonObject dest = cordJsonObject.getJsonObject("dest"); 
			this.src.object = src.getString("object").replace("\"", "");
			this.src.port = src.getString("port").replace("\"", "");
			this.dest.object = dest.getString("object").replace("\"", "");
			this.dest.port = dest.getString("port").replace("\"", "");
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxCord constructor error: " + e);
		}
	}
	
	FlxCord_Impl(String srcEffect, String srcPort, String destEffect, String destPort)
	{
		this.src.object = srcEffect;
		this.src.port = srcPort;
		this.dest.object = destEffect;
		this.dest.port = destPort;
	}
	
	public JsonValue getEffectConnectionData()
	{
		JsonObjectBuilder cord = Json.createObjectBuilder();
		JsonObject cordBuilt = null;
		JsonObject cordSrc = Json.createObjectBuilder().add("object", this.src.object).add("port", this.src.port).build();
		JsonObject cordDest = Json.createObjectBuilder().add("object", this.dest.object).add("port", this.dest.port).build();
		cord.add("index", this.index);
		cord.add("src", (JsonValue) cordSrc);
		cord.add("dest", (JsonValue) cordDest);
		cordBuilt = cord.build();
		
		return (JsonValue)cordBuilt;
	}
	
	public String getEffectConnectionString()
	{
		String effectConnectionString = "";
		
		effectConnectionString += "{\"index\":" + this.getIndex() + ",";
		effectConnectionString += "\"src\":{\"object\":\"" + this.getSrcObject() + "\",\"port\":\"" + this.getSrcPort() + "\"},";
		effectConnectionString += "\"dest\":{\"object\":\"" + this.getDestObject() + "\",\"port\":\"" + this.getDestPort() + "\"}";
		effectConnectionString += "}";
		
		
		return effectConnectionString;
	}
	
	public String getName()
	{
		return this.src.object + ":" + this.src.port + ">" +this.dest.object + ":" + this.dest.port;
	}
	
	public String getSrcObject()
	{
		return this.src.object;
	}
	
	public String getSrcPort()
	{
		return this.src.port;
	}
	
	public String getDestObject()
	{
		return this.dest.object;
	}
	
	public String getDestPort()
	{
		return this.dest.port;
	}

	
	public boolean containsEffectName(String effectName)
	{
		boolean status = false;
		
		if((this.src.object.compareTo(effectName) == 0) || (this.dest.object.compareTo(effectName) == 0))
		{
			status = true;
		}
		
		return status;
	}
	
	
	public void changeSrcEffectName(String effectName)
	{
		this.src.object = effectName;
	}
	
	public void changeDestEffectName(String effectName)
	{
		this.dest.object = effectName;
	}

	public int getIndex()
	{
		return this.index;
	}
}
