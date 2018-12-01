package diagramComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;


import diagramSubComponents.BoundCoord;
import diagramSubComponents.Coord;
import diagramSubComponents.FlxConnector;
import diagramSubComponents.FlxSymbol;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Polygon;
import main.FlxSidebar;

public class FlxBlock_Impl implements FlxBlock {

	boolean debugStatements = false;
	boolean errorStatements = true;

	String name;
    boolean selected;
    String info;
    String componentType;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
	ScrollPane parentEditor;
	JsonValue blockJsonData;
	String parentEffectName;
	Map<String,FlxConnector> inputMap;
	Map<String,FlxConnector> outputMap;
	Map<String,FlxConnector> paramMap;
	Map<String,JsonValue> symbolDataMap = new HashMap<String,JsonValue>();
	FlxSymbol symbolObj;
	Group symbol;
	Group symbolGroup; //contains symbol and labeling
	Polygon body;
	double x;
	double y;
    Label blockLabel;
    Label blockInfo;
    FlxSidebar sideBarReference;


	FlxBlock_Impl (String blockName, JsonValue blockJsonData)
	{
		try
		{

			this.blockJsonData = blockJsonData;

			JsonObject blockJsonObject = (JsonObject)this.blockJsonData;

			this.selected = false;
			if(blockName == null)
			{
				this.name = blockJsonObject.get("name").toString().replace("\"", "").toLowerCase();
			}
			else
			{
				this.name = blockName;
			}
			JsonValue symbol = blockJsonObject.get("symbol");
			JsonValue location = ((JsonObject)symbol).get("location");
			this.x = 0;
			this.y = 0;
			if(location != null)
			{
				if(((JsonObject)location).containsKey("x") == true)
				{
					this.x = Double.parseDouble(((JsonObject)location).get("x").toString());
				}
				if(((JsonObject)location).containsKey("y") == true)
				{
					this.y = Double.parseDouble(((JsonObject)location).get("y").toString());
				}

			}

			/****************** CREATE SYMBOL AND SYMBOL GROUP ****************************/

			JsonValue bodyJsonValue = null;
			JsonValue graphicJsonValue = null;
			String colorString = null;

			bodyJsonValue = ((JsonObject)symbol).get("body");
			graphicJsonValue = ((JsonObject)symbol).get("graphic");
			colorString = ((JsonObject)symbol).get("color").toString().replace("\"", "");


			this.symbolObj = new FlxSymbol(this.name, bodyJsonValue, graphicJsonValue, colorString ,this.x,this.y);

			if(((JsonObject)blockJsonData).containsKey("processDirection"))
			{
				if(((JsonObject)blockJsonData).getString("processDirection").replace("\"", "").compareTo("feedback") == 0)
				{
					this.symbolObj.flipProcess(false, false);
				}
			}
			this.symbol = this.symbolObj.getSymbol();
			this.symbol.setId(this.name);


			this.symbolGroup = new Group();
			this.symbolGroup.setId(this.name);
			Coord symCoord = this.symbolObj.getSymbolCoords();
			this.x = symCoord.getX();
			this.y = symCoord.getY();
			this.symbolGroup.getChildren().add(this.symbol);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxBlock_Impl constructor error: " + this.name + ": " + e);
		}



		/*************************** PROCESS CONNECTOR DATA ******************************/

		if(((JsonObject)this.blockJsonData).containsKey("outputArray"))
		{
			try
			{
				JsonArray outputArray = (JsonArray)(((JsonObject)this.blockJsonData).get("outputArray"));
				this.outputMap = new HashMap<String,FlxConnector>();
				for(int outputArrayIndex = 0; outputArrayIndex < outputArray.size(); outputArrayIndex++)
				{
					JsonObject outputConnector = (JsonObject)(outputArray.get(outputArrayIndex));
					String name = new String(outputConnector.get("name").toString().replace("\"", ""));
					int index = (outputConnector.getInt("index"));
					double x = Double.parseDouble(outputConnector.get("x").toString());
					double y = Double.parseDouble(outputConnector.get("y").toString());
					FlxConnector output = null;
					{
						output = new FlxConnector(name, "output", index, this.symbolGroup, x, y);
					}

					this.outputMap.put(name, output);
					this.symbolGroup.getChildren().add(output.getConnector());
				}
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("FlxBlock_Impl outputMap create error: " + e);
			}

		}
		if(((JsonObject)this.blockJsonData).containsKey("inputArray"))
		{
			componentType = "process";

			try
			{
				this.inputMap = new HashMap<String,FlxConnector>();
				JsonArray inputArray = (JsonArray)(((JsonObject)this.blockJsonData).get("inputArray"));

				for(int inputArrayIndex = 0; inputArrayIndex < inputArray.size(); inputArrayIndex++)
				{
					JsonObject inputConnector = (JsonObject)(inputArray.get(inputArrayIndex));
					String name = new String(inputConnector.get("name").toString().replace("\"", ""));
					int index = (inputConnector.getInt("index"));
					double x = Double.parseDouble(inputConnector.get("x").toString());
					double y = Double.parseDouble(inputConnector.get("y").toString());

					FlxConnector input = null;
					{
						input = new FlxConnector(name, "input", index, this.symbolGroup, x, y);
					}

					this.inputMap.put(name, input);
					this.symbolGroup.getChildren().add(input.getConnector());
				}
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("FlxBlock_Impl inputMap create error: " + e);
			}
		}
		else
		{
			componentType = "control";
		}

		if(((JsonObject)this.blockJsonData).containsKey("paramArray")  )
		{
			try
			{
				JsonArray paramArray = null;
				if(((JsonObject)this.blockJsonData).containsKey("paramArray"))
				{
					paramArray = (JsonArray)(((JsonObject)this.blockJsonData).get("paramArray"));
				}
				else if(((JsonObject)this.blockJsonData).containsKey("conParamArray"))
				{
					paramArray = (JsonArray)(((JsonObject)this.blockJsonData).get("conParamArray"));
				}

				this.paramMap = new HashMap<String,FlxConnector>();
				for(int paramArrayIndex = 0; paramArrayIndex < paramArray.size(); paramArrayIndex++)
				{
					JsonObject paramConnector = (JsonObject)(paramArray.get(paramArrayIndex));
					String name = new String(paramConnector.get("name").toString().replace("\"", ""));
					int index = (paramConnector.getInt("index"));
					double x = Double.parseDouble(paramConnector.get("x").toString());
					double y = Double.parseDouble(paramConnector.get("y").toString());

					FlxConnector param = null;
					{
						param = new FlxConnector(name, "param", index, this.symbolGroup, x, y);
					}

					this.paramMap.put(name, param);
					this.symbolGroup.getChildren().add(param.getConnector());
				}
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("FlxBlock_Impl paramMap create error: " + e);
			}
		}


		/**************************** CREATE LABELING ***********************************/
		this.blockLabel = new Label();
		this.blockInfo = new Label();
		this.blockLabel.setId(this.name + "_label");
		this.blockInfo.setId(this.name + "_info");
		this.blockLabel.setLayoutX(0.0);
		this.blockLabel.setLayoutY(0.0);

		this.blockLabel.setText(this.name);

		this.symbolGroup.getChildren().add(this.blockLabel);
	}

	/*************************** BASIC GETTERS AND SETTERS ***************************/

	public String getName()
	{
		return this.name;
	}

	public void setParentEffect(String parentEffectName)
	{
		this.parentEffectName = parentEffectName;
	}
	public String getParentEffect()
	{
		return this.parentEffectName;
	}
	public void setInfo(String blockInfoString)
	{
		this.info = blockInfoString;
		this.blockInfo.setText(this.info);
	}


	public void printData()
	{
		System.out.println(((JsonObject)this.blockJsonData).get("name").toString());
	}


	public Group getBlock(double x, double y)
	{
		this.x = x;
		this.y = y;

		this.symbolGroup.setTranslateX(this.x);
		this.symbolGroup.setTranslateY(this.y);

		return this.symbolGroup;
	}



	public Group getBlock()
	{
		this.symbolGroup.setTranslateX(this.x);
		this.symbolGroup.setTranslateY(this.y);

		return this.symbolGroup;
	}

	public String getComponentType()
	{
		return this.componentType;
	}
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
		this.symbolObj.setLocation(x, y);
	}

	public void setSymbol(JsonValue symbolData)
	{

	}

	/********************************** CONNECTOR GET/SET METHODS *********************************/

	public void setName(String blockName)
	{

		this.name = blockName;
		this.blockLabel.setText(this.name);
		this.blockLabel.setId(this.name + "_label");
		this.blockInfo.setId(this.name + "_info");
		this.symbolGroup.setId(this.name);

		if(this.inputMap != null)
		{
			for(String inputKey : this.inputMap.keySet())
			{
				this.inputMap.get(inputKey).setParentProcessName(name);
			}
		}

		if(this.outputMap != null)
		{
			for(String outputKey : this.outputMap.keySet())
			{
				this.outputMap.get(outputKey).setParentProcessName(name);
			}
		}

		if(this.paramMap != null)
		{
			for(String paramKey : this.paramMap.keySet())
			{
				this.paramMap.get(paramKey).setParentProcessName(name);
			}
		}
	}

	public List<String> getInputConnectorList()
	{
		List<String> inputConnList = new ArrayList<String>();
		for(String connName : this.inputMap.keySet())
		{
			inputConnList.add(connName);
		}

		return inputConnList;
	}

	public Coord getLocation()
	{
		return this.symbolObj.getSymbolCoords();
	}

	public List<String> getOutputConnectorList()
	{
		List<String> outputConnList = new ArrayList<String>();
		for(String connName : this.outputMap.keySet())
		{
			outputConnList.add(connName);
		}

		return outputConnList;
	}

	public List<String> getParamConnectorList()
	{
		List<String> paramConnList = new ArrayList<String>();
		for(String connName : this.paramMap.keySet())
		{
			paramConnList.add(connName);
		}

		return paramConnList;
	}


	public Coord getInputConnectorCoord(String inputName)
	{
		Coord inputConnCoord = new Coord();
		try
		{
			if(this.inputMap.containsKey(inputName))
			{
				Coord localInputConnCoord = this.inputMap.get(inputName).getConnectorLocalLocation();

				inputConnCoord.setX(localInputConnCoord.getX() + this.x);
				inputConnCoord.setY(localInputConnCoord.getY() + this.y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getInputConnectorCoord error: " + e);
			inputConnCoord = new Coord(0,0);
		}

		return inputConnCoord;
	}

	public BoundCoord getInputConnectorBoundCoord(String inputName)
	{
		BoundCoord inputConnCoord = new BoundCoord();
		try
		{
			if(this.inputMap.containsKey(inputName))
			{
				Coord localInputConnCoord = this.inputMap.get(inputName).getConnectorLocalLocation();

				inputConnCoord.setX(localInputConnCoord.getX() + this.x);
				inputConnCoord.setY(localInputConnCoord.getY() + this.y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getInputConnectorCoord error: " + e);
			inputConnCoord = new BoundCoord(0,0);
		}

		return inputConnCoord;
	}


	public Coord getOutputConnectorCoord(String outputName)
	{
		Coord outputConnCoord = new Coord();
		try
		{
			if(this.outputMap.containsKey(outputName))
			{
				Coord localOutputConnCoord = this.outputMap.get(outputName).getConnectorLocalLocation();

				outputConnCoord.setX(localOutputConnCoord.getX() + this.x);
				outputConnCoord.setY(localOutputConnCoord.getY() + this.y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getOutputConnectorCoord error: " + e);
			outputConnCoord = new Coord(0,0);
		}

		return outputConnCoord;
	}

	public BoundCoord getOutputConnectorBoundCoord(String outputName)
	{
		BoundCoord outputConnCoord = new BoundCoord();
		try
		{
			if(this.outputMap.containsKey(outputName))
			{
				Coord localInputConnCoord = this.outputMap.get(outputName).getConnectorLocalLocation();

				outputConnCoord.setX(localInputConnCoord.getX() + this.x);
				outputConnCoord.setY(localInputConnCoord.getY() + this.y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getInputConnectorCoord error: " + e);
			outputConnCoord = new BoundCoord(0,0);
		}

		return outputConnCoord;
	}


	public Coord getParamConnectorCoord(String paramName)
	{
		Coord paramConnCoord = new Coord();
		try
		{
			if(this.paramMap.containsKey(paramName))
			{
				Coord localParamConnCoord = this.paramMap.get(paramName).getConnectorLocalLocation();

				paramConnCoord.setX(localParamConnCoord.getX() + this.x);
				paramConnCoord.setY(localParamConnCoord.getY() + this.y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getParamConnectorCoord error: " + e);
			paramConnCoord = new Coord(0,0);
		}

		return paramConnCoord;
	}

	public BoundCoord getParamConnectorBoundCoord(String paramName)
	{
		BoundCoord paramConnCoord = new BoundCoord();
		try
		{
			if(this.paramMap.containsKey(paramName))
			{
				Coord localParamConnCoord = this.paramMap.get(paramName).getConnectorLocalLocation();

				paramConnCoord.setX(localParamConnCoord.getX() + this.x);
				paramConnCoord.setY(localParamConnCoord.getY() + this.y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getInputConnectorCoord error: " + e);
			paramConnCoord = new BoundCoord(0,0);
		}

		return paramConnCoord;
	}


	public void setInputConnectorCoord(String inputName, double x, double y) // use for updating connector coords when flipping block
	{
		try
		{
			if(this.inputMap.containsKey(inputName))
			{
				this.inputMap.get(inputName).setConnectorLocalLocation(x, y);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getInputConnectorCoord error: " + e);
		}

	}


	/***************************** OTHER METHODS FOR SUBCLASSES (?) ****************************/

	public void deleteSymbol()
	{
		this.symbolGroup.getChildren().clear();
	}

	public void setSelectIndicator(boolean selected)
	{
		this.selected = selected;
		try
		{
			this.symbolObj.setSelectIndicator(this.selected);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.setSelectIndicator error: " + e);
		}
	}



	public JsonValue getInputArrayData()
	{
		JsonArrayBuilder inputArrayData = Json.createArrayBuilder();
		try
		{
			for(String inputKey : this.inputMap.keySet())
			{
				JsonObject inputData = Json.createObjectBuilder()
						.add("name", inputKey)
						.build();

				inputArrayData.add(inputData);
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxBlock::getInputArrayData error: " + e);
		}

		JsonArray inputArrayDataBuilt = inputArrayData.build();

		return (JsonValue)inputArrayDataBuilt;
	}

	public String getInputArrayString()
	{
		String inputArrayString = "[";
		try
		{
			int keyCount = this.inputMap.size();
			int keyIndex = 0;
			for(String inputKey : this.inputMap.keySet())
			{
				Coord inputCoord = this.inputMap.get(inputKey).getConnectorLocalLocation();
				String inputString = "";
				inputString += "{\"name\":\"" + inputKey +"\",";
				inputString += "\"index\":" + this.inputMap.get(inputKey).getIndex() +",";
				inputString += "\"x\":" + inputCoord.getX() +",";
				inputString += "\"y\":" + inputCoord.getY() +"}";
				if(keyIndex++ < keyCount-1)
				{
					inputString += ",";
				}
				inputArrayString += inputString;

			}
			inputArrayString += "]";
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxBlock::getInputArrayData error: " + e);
		}

		if(this.debugStatements) System.out.println("inputArrayString: " + inputArrayString);

		return inputArrayString;
	}

	public int getInputMapSize()
	{
		return this.inputMap.size();
	}

	public Map<String,FlxConnector> getInputMap()
	{
		return this.inputMap;
	}

	public List<String> getInputMapKeys()
	{
		return new ArrayList<String>(this.inputMap.keySet());
	}

	public FlxConnector getInputMapItem(String key)
	{
		return this.inputMap.get(key);
	}


	public JsonValue getOutputArrayData()
	{
		JsonArrayBuilder outputArrayData = Json.createArrayBuilder();

		try
		{
			for(String outputKey : this.outputMap.keySet())
			{
				JsonObject outputData = Json.createObjectBuilder()
						.add("name", outputKey)
						.build();
				outputArrayData.add(outputData);
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxBlock::getInputArrayData error: " + e);
		}
		JsonArray outputArrayDataBuilt = outputArrayData.build();

		return (JsonValue) outputArrayDataBuilt;
	}

	public String getOutputArrayString()
	{
		String outputArrayString = "[";
		try
		{
			int keyCount = this.outputMap.size();
			int keyIndex = 0;
			for(String outputKey : this.outputMap.keySet())
			{
				Coord outputCoord = this.outputMap.get(outputKey).getConnectorLocalLocation();
				String outputString = "";
				outputString += "{\"name\":\"" + outputKey +"\",";
				outputString += "\"index\":" + this.outputMap.get(outputKey).getIndex() +",";
				outputString += "\"x\":" + outputCoord.getX() +",";
				outputString += "\"y\":" + outputCoord.getY() +"}";
				if(keyIndex++ < keyCount-1)
				{
					outputString += ",";
				}
				outputArrayString += outputString;

			}
			outputArrayString += "]";
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxBlock::getOutputArrayData error: " + e);
		}
		if(this.debugStatements) System.out.println("outputArrayString: " + outputArrayString);

		return outputArrayString;
	}


	public JsonArrayBuilder getOutputArrayBuilder()
	{
		JsonArrayBuilder outputArrayData = Json.createArrayBuilder();


		return outputArrayData;
	}



	public int getOutputMapSize()
	{
		return this.outputMap.size();
	}

	public Map<String,FlxConnector> getOutputMap()
	{
		return this.outputMap;
	}

	public List<String> getOutputMapKeys()
	{
		return new ArrayList<String>(this.outputMap.keySet());
	}

	public FlxConnector getOutputMapItem(String key)
	{
		return this.outputMap.get(key);
	}


/*	public void printConnectionCoords()
	{

		for(String inputKey : this.getInputConnectorList())
		{
			Coord inputCoord = this.getInputConnectorCoord(inputKey);
			if(this.debugStatements) System.out.print(inputKey + ": " + inputCoord.getX() + "," + inputCoord.getY() + "\t");
		}
		if(this.debugStatements) System.out.println();
		for(String outputKey : this.getOutputConnectorList())
		{
			Coord outputCoord = this.getOutputConnectorCoord(outputKey);
			if(this.debugStatements) System.out.print(outputKey + ": " + outputCoord.getX() + "," + outputCoord.getY() + "\t");
		}
		if(this.debugStatements) System.out.println();

		for(String paramKey : this.getParamConnectorList())
		{
			Coord paramCoord = this.getParamConnectorCoord(paramKey);
			if(this.debugStatements) System.out.print(paramKey + ": " + paramCoord.getX() + "," + paramCoord.getY() + "\t");
		}
		if(this.debugStatements) System.out.println();


	}*/

}
