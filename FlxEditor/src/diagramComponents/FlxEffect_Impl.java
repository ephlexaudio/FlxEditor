package diagramComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import diagramSubComponents.FlxConnector;

public class FlxEffect_Impl implements FlxEffect {
	String name;
	String abbr;
	int index;
	boolean debugStatements = false;
	boolean errorStatements = true;
	JsonValue effectJsonData;


	List<JsonValue> processDataArray;
	List<JsonValue> processConnectionDataArray;
	List<JsonValue> controlDataArray;
	List<JsonValue> controlConnectionDataArray;
	Map<String,FlxConnector> effectIO;
	Map<String,FlxProcess> processMap;
	Map<String,FlxWire> processConnectionMap;
	Map<String,FlxControl> controlMap;
	Map<String,FlxControlWire> controlConnectionMap;

	public FlxEffect_Impl(int index, JsonValue effectData)
	{

		this.effectJsonData = effectData;
		JsonObject effectJsonObject = ((JsonObject)this.effectJsonData);
		this.name = effectJsonObject.get("name").toString().replace("\"", "");
		this.abbr = effectJsonObject.get("abbr").toString().replace("\"", "");
		this.index = Integer.parseInt(effectJsonObject.get("index").toString().replace("\"", ""));

		/******************************** CREATE PROCESS MAP ******************************/
		this.processMap = new HashMap<String,FlxProcess>();
		JsonArray processJsonArray = null;
		boolean success = false;
		try
		{
			processJsonArray = effectJsonObject.getJsonArray("processArray");
			success = true;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("error getting processArray from effectJsonObject: " + e);
		}

		if(success == true)
		{
			for(int processDataArrayIndex = 0; processDataArrayIndex < processJsonArray.size(); processDataArrayIndex++)
			{
				String processName = null;
				JsonObject processJsonObject = null;
				try
				{
					processJsonObject = (JsonObject)processJsonArray.get(processDataArrayIndex);
					FlxProcess process = new FlxProcess_Impl(null,processJsonObject, this.name);
					processName = process.getName();
					if(this.debugStatements) System.out.println("creating processMap in constructor for: " + this.name + ":" + processName );

					this.processMap.put(processName, process);
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("Process constructor error: " + this.name + ":" + e);
				}
			}
		}
		/********************** CREATE PROCESS CONNECTION MAP ****************************/
		this.processConnectionMap = new HashMap<String,FlxWire>();
		success = false;
		JsonArray processConnectionArray = null;
		try
		{
			processConnectionArray = effectJsonObject.getJsonArray("connectionArray");
			success = true;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("error getting connectionArray from effectJsonObject: " + e);
		}
		if(success == true)
		{
			for(int processConnectionArrayIndex = 0; processConnectionArrayIndex < processConnectionArray.size(); processConnectionArrayIndex++)
			{
				String processConnectionName = null;
				JsonObject processConnectionJsonObject = null;
				try
				{
					processConnectionJsonObject = (JsonObject)processConnectionArray.get(processConnectionArrayIndex);
					FlxWire processConnection = new FlxWire_Impl(processConnectionJsonObject,this.name);
					processConnectionName = processConnection.getWireName().replace("\"", "");
					this.processConnectionMap.put(processConnectionName, processConnection);
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("Process connection constructor error: " + this.name + ":" + e);
				}
			}
		}


		/********************** CREATE CONTROL MAP **************************************/
		this.controlMap = new HashMap<String,FlxControl>();
		JsonArray processControlArray = null;
		success = false;
		try
		{
			processControlArray = effectJsonObject.getJsonArray("controlArray");
			success = true;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("error getting controlArray from effectJsonObject: " + e);
		}

		if(success == true)
		{
			for(int processControlArrayIndex = 0; processControlArrayIndex < processControlArray.size(); processControlArrayIndex++)
			{
				String processControlName = null;
				JsonObject processControlJsonObject = null;
				try
				{
					processControlJsonObject = (JsonObject)processControlArray.get(processControlArrayIndex);
					FlxControl processControl = new FlxControl_Impl(null, processControlJsonObject, this.name);
					processControlName = processControlJsonObject.get("name").toString().replace("\"", "");
					if(this.debugStatements) System.out.println("creating processControlMap in constructor for: " + this.name + ":" + processControlName );
					this.controlMap.put(processControlName, processControl);
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("Control constructor error: " + this.name + ":" + e);
				}
			}
		}

		/*********************** CREATE CONTROL CONNECTION MAP **************************/
		this.controlConnectionMap = new HashMap<String,FlxControlWire>();
		JsonArray controlConnectionArray = null;
		success = false;
		try
		{
			controlConnectionArray = effectJsonObject.getJsonArray("controlConnectionArray");
			success = true;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("error getting controlConnectionArray from effectJsonObject: " + e);
		}

		if(success == true)
		{
			for(int controlConnectionArrayIndex = 0; controlConnectionArrayIndex < controlConnectionArray.size(); controlConnectionArrayIndex++)
			{
				String controlWireName = null;
				JsonObject controlConnectionJsonObject = null;
				try
				{
					controlConnectionJsonObject = (JsonObject)controlConnectionArray.get(controlConnectionArrayIndex);
					FlxControlWire controlWire = new FlxControlWire_Impl(controlConnectionJsonObject, this.name);
					controlWireName = controlWire.getWireName().replace("\"", "");
					if(this.debugStatements) System.out.println("creating controlWireMap in constructor for: " + this.name + ":" + controlWireName );
					this.controlConnectionMap.put(controlWireName, controlWire);
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("Control wire constructor error: " + this.name + ":" + e);
				}
			}
		}

	}

	public int getIndex()
	{
		return this.index;
	}

	public String getName()
	{
		return this.name;
	}

	public String getAbbr()
	{
		return this.abbr;
	}

	public void setName(String effectName)
	{
		this.name = effectName;
	}

	public void setAbbr(String effectAbbr)
	{
		this.abbr = effectAbbr;
	}

	private JsonValue getProcessDataArray()
	{
		JsonArrayBuilder processDataArray = Json.createArrayBuilder();
		JsonArray processDataArrayBuilt = null;

		try
		{
			for(String processKey : this.processMap.keySet())
			{
				JsonValue processData = this.processMap.get(processKey).getData();
				processDataArray.add(processData);
			}
			processDataArrayBuilt = processDataArray.build();

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getProcessDataArray error: " + e);
		}

		return (JsonValue)processDataArrayBuilt;
	}

	private JsonValue getProcessConnectionDataArray()
	{
		JsonArrayBuilder processConnectionDataArray = Json.createArrayBuilder();
		JsonArray processConnectionDataArrayBuilt = null;

		try
		{
			for(String processConnectionKey : this.processConnectionMap.keySet())
			{
				JsonValue processConnectionData = this.processConnectionMap.get(processConnectionKey).getProcessConnectionData();
				processConnectionDataArray.add(processConnectionData);
			}
			processConnectionDataArrayBuilt = processConnectionDataArray.build();

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getProcessConnectionDataArray error: " + e);
		}

		return (JsonValue)processConnectionDataArrayBuilt;
	}

	private JsonValue getControlDataArray()
	{
		JsonArrayBuilder controlDataArray = Json.createArrayBuilder();
		JsonArray controlDataArrayBuilt = null;

		try
		{
			for(String controlKey : this.controlMap.keySet())
			{
				JsonValue controlData = this.controlMap.get(controlKey).getData();
				controlDataArray.add(controlData);
			}
			controlDataArrayBuilt = controlDataArray.build();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getControlDataArray error: " + e);
		}

		return (JsonValue)controlDataArrayBuilt;
	}

	private JsonValue getControlConnectionDataArray()
	{
		JsonArrayBuilder controlConnectionDataArray = Json.createArrayBuilder();
		JsonArray controlConnectionDataArrayBuilt = null;

		try
		{
			for(String controlConnectionKey : this.controlConnectionMap.keySet())
			{
				JsonValue controlConnectionData = this.controlConnectionMap.get(controlConnectionKey).getControlConnectionData();
				controlConnectionDataArray.add(controlConnectionData);
			}
			controlConnectionDataArrayBuilt = controlConnectionDataArray.build();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getControlConnectionDataArray error: " + e);
		}

		return (JsonValue)controlConnectionDataArrayBuilt;
	}

	public JsonValue getEffectData()
	{
		JsonObjectBuilder effectData = Json.createObjectBuilder();
		JsonObject effectDataBuilt = null;

		try
		{
			effectDataBuilt = effectData.add("name", this.name)
					.add("abbr", this.abbr)
					.add("index", this.index)
					.add("processArray", this.getProcessDataArray())
					.add("connectionArray", this.getProcessConnectionDataArray())
					.add("controlArray", this.getControlDataArray())
					.add("controlConnectionArray", this.getControlConnectionDataArray())
					.build();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getEffectData error: " + e);
		}

		return (JsonValue)effectDataBuilt;
	}

	public Map<String,FlxProcess> getProcessMap()
	{
		Map<String,FlxProcess> tempProcMap = null;

		try
		{
			tempProcMap = this.processMap;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getProcessMap error: " + e);
		}
		return tempProcMap;
	}

	public Map<String,FlxWire> getWireMap()
	{
		Map<String,FlxWire> tempWireMap = null;
		try
		{
			tempWireMap = this.processConnectionMap;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getWireMap error: " + e);
		}
		return tempWireMap;
	}

	public Map<String,FlxControl> getControlMap()
	{
		Map<String,FlxControl> tempControlMap = null;
		try
		{
			tempControlMap = this.controlMap;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getControlMap error: " + e);
		}
		return tempControlMap;
	}

	public Map<String,FlxControlWire> getControlWireMap()
	{
		Map<String,FlxControlWire> tempControlWireMap = null;
		try
		{
			tempControlWireMap = this.controlConnectionMap;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::getControlWireMap error: " + e);
		}
		return tempControlWireMap;
	}


	public void addToProcessMap(String processName, FlxProcess process)
	{
		try
		{
			this.processMap.put(processName, process);
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxEffect::addToProcessMap error: " + e);
		}

	}
}
