package diagramComponents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import diagramSubComponents.Coord;
/*import diagramSubComponents.FlxConnector;
import diagramSubComponents.FlxControlParameter;*/
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
import diagramSubComponents.FlxSymbol;
//import javafx.event.EventHandler;
import javafx.scene.Group;
/*import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import main.FlxSidebar;*/

public class FlxProcess_Impl extends FlxBlock_Impl implements FlxProcess {
	
	boolean debugStatements = false;
	boolean errorStatements = true;
	class FlxProcessData {
		
	}
	JsonValue jsonProcessData;
	JsonValue parentEffectData;
	JsonObject jsonProcessDataObject;
	JsonObject parentEffectDataObject;
	Map<String,FlxParameter> paramValueMap = new HashMap<String,FlxParameter>(); 
	List<FlxParameter> paramValueList = new ArrayList<FlxParameter>(); 
	FlxBlock parentReference;
	FlxFootswitch footswitch = new FlxFootswitch();
	String footswitchType;
	String footswitchNumber;
	int cpuPower;
	String processDirection;
	String processName;
	String type;
    boolean flipped;
	//String parentEffectName;
    
	public FlxProcess_Impl(String procName, JsonValue jsonProcessData, String parentEffectName) throws IOException 
	{
		super(procName, (JsonObject)jsonProcessData/*, parentEffectName*/);
		try
		{
			this.processName = super.name;
			this.jsonProcessData = jsonProcessData;
			if(parentEffectName.isEmpty())
			{
				System.out.println(procName + " parentEffect name is empty");
			}
			else
			{
				super.parentEffectName = parentEffectName;
			}
			
			this.jsonProcessDataObject = (JsonObject)jsonProcessData;
			
			if(super.name.contains("_"))
			{
				super.symbolObj.setLocation(super.x, super.y);
				//System.out.println("super.symbolObj.setLocation: " + this.name + ": " + super.x + "," + super.y);
			}
			else
			{
				super.symbolObj.setLocation(0, 0);
			}
			

			this.type = jsonProcessDataObject.get("type").toString().replace("\"", "");

			
			this.footswitchType = /*((JsonObject)jsonProcessData)*/jsonProcessDataObject.get("footswitchType").toString().replace("\"", "");
			this.footswitchNumber = /*((JsonObject)jsonProcessData)*/jsonProcessDataObject.get("footswitchNumber").toString().replace("\"", "");
			this.footswitch.setFootswitchNumber(this.footswitchNumber);
			this.processDirection = /*((JsonObject)jsonProcessData)*/jsonProcessDataObject.get("processDirection").toString().replace("\"", "");
			if(this.processDirection.compareTo("feedback") == 0)
			{
				this.flipped = true;
			}
			else 
			{
				this.flipped = false;
			}
			
			super.setName(this.processName);
			super.symbolGroup.setId(this.processName);
			/****************************  GET PARAMETERS ************************************/
			JsonObject blockJsonObject = (JsonObject)this.blockJsonData;
			JsonArray paramJsonArray = null;
			paramJsonArray = blockJsonObject.getJsonArray("paramArray");
			
			for(int paramJsonArrayIndex = 0; paramJsonArrayIndex < paramJsonArray.size(); paramJsonArrayIndex++)
			{
				JsonObject paramJsonElement = (JsonObject)paramJsonArray.get(paramJsonArrayIndex);
				String paramName = paramJsonElement.get("name").toString().replace("\"","");
				int paramIndex = paramJsonElement.getInt("index");
				String paramEditorLabel = paramJsonElement.get("label").toString().replace("\"","");
				int paramType = paramJsonElement.getInt("type");
				double paramValue = Double.parseDouble(paramJsonElement.get("value").toString().replace("\"",""));
				String paramOrientation;
				if(paramJsonElement.containsKey("orientation"))
				{
					paramOrientation = paramJsonElement.get("orientation").toString().replace("\"","");
				}
				else
				{
					paramOrientation = "h";
				}
				//this.paramValueMap.put(paramName, new FlxParameter(paramName, super.symbolGroup, paramIndex, paramEditorLabel, paramType, paramOrientation, paramValue));
				this.paramValueList.add(new FlxParameter(paramName, super.symbolGroup, paramIndex, paramEditorLabel, paramType, paramOrientation, paramValue));
			}
			//printFlxParameterList("constructor", this.paramValueList);

			/**************************** MODIFY LABELING ***********************************/
			
			/*super.blockInfo.setLayoutX(super.x);
			super.blockInfo.setLayoutY(super.y);*/
			super.blockInfo.setMaxSize(80, 40);
			super.blockInfo.setWrapText(true);
			super.blockInfo.setStyle("-fx-font-size:10;");
			super.blockLabel.setStyle("-fx-font-size:10;");
			super.blockLabel.setTranslateY(-50.0);
			super.blockInfo.setTranslateY(-35.0);
			
			super.symbolGroup.getChildren().add(this.blockInfo);	
			
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess constructor error: " + e);
		}

	}

/********************************************************************************************
 ******************************************************************************************** 
 ********************************************************************************************/
	public void print()
	{
		super.printData();
	}
	
	public String getType()
	{
		return this.type;
	}
		
	public void setInfo(String blockInfo)
	{
		super.setInfo(blockInfo);
	}
	
	
	 

	public Coord getProcessCoords()
	{
		Coord procCoord = new Coord(super.symbolGroup.getLayoutX(),super.symbolGroup.getLayoutY());
		return procCoord;
	}
	
	
	public Group getSymbolGroup(/*double x, double y*/)
	{
		/*super.x = x;
		super.y = y;*/
		
		return super.getBlock(this.x, this.y);
	}

	public void deleteProcess()
	{
		
	}
	
	public void flipConnectorCoord(boolean flip)
	{
		/*Coord inputConnCoord = new Coord();
		Coord outputConnCoord = new Coord();
		Coord paramConnCoord = new Coord();*/
		
		try
		{
			if(flip) // flip process 
			{
				for(String inputKey : super.inputMap.keySet()) // send inputs right
				{
					Coord localInputConnCoord = super.inputMap.get(inputKey).getConnectorLocalLocation();
					localInputConnCoord.setX(localInputConnCoord.getX() + 90);
					this.inputMap.get(inputKey).setConnectorLocalLocation(localInputConnCoord.getX(), localInputConnCoord.getY());
				}
				
				for(String outputKey : super.outputMap.keySet()) // send outputs left
				{
					Coord localOutputConnCoord = super.outputMap.get(outputKey).getConnectorLocalLocation();
					localOutputConnCoord.setX(localOutputConnCoord.getX() - 90);
					this.outputMap.get(outputKey).setConnectorLocalLocation(localOutputConnCoord.getX(), localOutputConnCoord.getY());
				}				
				
				/*for(String paramKey : super.paramMap.keySet()) // send param left
				{
					Coord localParamConnCoord = super.paramMap.get(paramKey).getConnectorLocalLocation();
					localParamConnCoord.setX(localParamConnCoord.getX() - 90);
					this.paramMap.get(paramKey).setConnectorLocalLocation(localParamConnCoord.getX(), localParamConnCoord.getY());
				}*/			
			}
			else // unflip process
			{
				for(String inputKey : super.inputMap.keySet()) // send inputs left
				{
					Coord localInputConnCoord = super.inputMap.get(inputKey).getConnectorLocalLocation();
					localInputConnCoord.setX(localInputConnCoord.getX() - 90);
					this.inputMap.get(inputKey).setConnectorLocalLocation(localInputConnCoord.getX(), localInputConnCoord.getY());
				}
				
				for(String outputKey : super.outputMap.keySet()) // send outputs right
				{
					Coord localOutputConnCoord = super.outputMap.get(outputKey).getConnectorLocalLocation();
					localOutputConnCoord.setX(localOutputConnCoord.getX() + 90);
					this.outputMap.get(outputKey).setConnectorLocalLocation(localOutputConnCoord.getX(), localOutputConnCoord.getY());
				}			
				
				/*for(String paramKey : super.paramMap.keySet()) // send param left
				{
					Coord localParamConnCoord = super.paramMap.get(paramKey).getConnectorLocalLocation();
					localParamConnCoord.setX(localParamConnCoord.getX() + 90);
					this.paramMap.get(paramKey).setConnectorLocalLocation(localParamConnCoord.getX(), localParamConnCoord.getY());
				}*/			
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("this.symbolObj.getInputConnectorCoord error: " + e);
		}
			
	}

	private void flipSymbol(boolean flipped)
	{
		if(flipped == true)
		{
			this.flipped = false;
			super.symbol.setScaleX(1.0);
			this.processDirection = "normal";
		}
		else
		{
			this.flipped = true;
			super.symbol.setScaleX(-1.0);
			this.processDirection = "feedback";			
		}			
	}
	
	public void flipProcess() {
		try
		{
			this.flipSymbol(this.flipped);
			this.flipConnectorCoord(this.flipped);
		
			/*for(String paramKey : super.paramMap.keySet()) // add unflipped parameter connectors to symbol group
			{
				super.symbolGroup.getChildren().add(super.paramMap.get(paramKey).getConnector());
			}*/
			if(this.debugStatements) System.out.println("process direction: " + this.processDirection);
						
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::flipProcess error: " + e);
		}

	}

	/*public void draw()
	{
		super.getBlock(0, 0);
	}*/
	


	/*public Map<String, FlxParameter> getParamMap() {
		
		return this.paramValueMap;
	}*/
	
		
	/*public void setParam(String paramName, double paramValue)
	{
		this.paramValueMap.get(paramName).setValue(paramValue);
	}*/

	
	
	public JsonValue getData()
	{
		if(this.debugStatements) System.out.println("getting process data: " + this.getName());
		JsonObjectBuilder processData  = Json.createObjectBuilder();
		JsonObject processDataBuilt = null;
		try
		{
			this.footswitchNumber = this.footswitch.getFootswitchNumber();
			//processData = Json.createObjectBuilder();
			processData.add("processDirection", this.processDirection);
			processData.add("name", this.getName());
			processData.add("type", this.type);
			processData.add("cpuPower", this.cpuPower);
			processData.add("footswitchType", this.footswitchType);
			processData.add("footswitchNumber", this.footswitchNumber);
			processData.add("symbol", super.symbolObj.getSymbolData());
			processData.add("parentEffect", super.parentEffectName);
			processData.add("inputArray", super.getInputArrayString());
			processData.add("outputArray", super.getOutputArrayData());
			processData.add("paramArray", this.getParamArrayData());
			processDataBuilt = processData.build();
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getData error: " + e);
			//processDataBuilt = (JsonObject) Json.createObjectBuilder().add("name","error").build();
		}
		if(this.debugStatements) System.out.println("process data retrieved: " + this.getName());
		if(this.debugStatements) System.out.println("process data: " + processDataBuilt.toString());
		return (JsonValue)processDataBuilt;
	}

	public String getDataString()
	{
		if(this.debugStatements) System.out.println("getting process data: " + this.getName());
		String processDataString = "";

		try
		{
			this.footswitchNumber = this.footswitch.getFootswitchNumber();
			processDataString += "{\"processDirection\":\"" + this.processDirection + "\",";
			processDataString += "\"name\":\"" + this.getName() + "\",";
			processDataString += "\"type\":\"" + this.getType() + "\",";
			processDataString += "\"cpuPower\":" + this.getProcessCpuPower() + ",";
			processDataString += "\"footswitchType\":\"" + this.getFootswitchType() + "\",";
			processDataString += "\"footswitchNumber\":\"" + this.getFootswitchNumber() + "\",";
			processDataString += "\"symbol\":" + super.symbolObj.getSymbolData().toString() + ",";
			processDataString += "\"parentEffect\":\"" + super.parentEffectName + "\",";
			processDataString += "\"inputArray\":" + super.getInputArrayString() + ",";
			processDataString += "\"outputArray\":" + super.getOutputArrayString() + ",";
			processDataString += "\"paramArray\":" + this.getParamArrayString() + "}";
			
			//processData = Json.createObjectBuilder();
			/*processData.add("processDirection", this.processDirection);
			processData.add("name", this.getName());
			processData.add("type", this.type);
			processData.add("cpuPower", this.cpuPower);
			processData.add("footswitchType", this.footswitchType);
			processData.add("footswitchNumber", this.footswitchNumber);
			processData.add("symbol", super.symbolObj.getSymbolData());
			processData.add("parentEffect", super.parentEffectName);
			processData.add("inputArray", super.getInputArrayString());
			processData.add("outputArray", super.getOutputArrayData());
			processData.add("paramArray", this.getParamArrayData());
			processDataBuilt = processData.build();*/
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getData error: " + e);
			//processDataBuilt = (JsonObject) Json.createObjectBuilder().add("name","error").build();
		}
		if(this.debugStatements) System.out.println("process data retrieved: " + this.getName());
		if(this.debugStatements) System.out.println("process data: " + processDataString);
		return processDataString;
	}

	public String getProcessDirection()
	{
		return this.processDirection;
	}
	
	public String getProcessName()
	{
		return this.processName;
	}
	
	public String getProcessType()
	{
		return this.type;
	}
	
	public int getProcessCpuPower()
	{
		return this.cpuPower;
	} 
	
	public String getFootswitchType()
	{
		return this.footswitchType;
	} 
	
	public String getFootswitchNumber()
	{
		return this.footswitchNumber;
	}
	
	public FlxSymbol getSymbolData()
	{
		return this.symbolObj;
	} 
	
	public String getParentEffectName()
	{
		return this.parentEffectName;
	} 

	public JsonValue getInputArrayData()
	{
		return super.getInputArrayData();
	} 
	
	public String getInputArrayString()
	{
		return super.getInputArrayString();
	} 
	
	public JsonValue getOutputArrayData()
	{
		return super.getOutputArrayData();
	} 

	
	public FlxFootswitch getFootswitch()
	{
		return this.footswitch;
	}
	
	public void setFootswitchNumber(String footswitchNumber)
	{
		this.footswitchNumber = footswitchNumber;
	}
	
	public JsonValue getParamArrayData()
	{
		
		JsonArrayBuilder paramArrayData = Json.createArrayBuilder();
		JsonArray paramArrayDataBuilt = null;
		try
		{
			for(int i = 0; i < this.paramValueList.size(); i++)
			{
				JsonObject paramValue = (JsonObject)(this.paramValueList.get(i).getParameterData());
				String name = this.paramValueList.get(i).getName();
				Coord paramCoord = this.paramMap.get(name).getConnectorLocalLocation();
				double value = Double.parseDouble((paramValue).get("value").toString());
				String label = paramValue.getString("label");
				int type = paramValue.getInt("type");
				int index = paramValue.getInt("index");
				JsonObject paramData = Json.createObjectBuilder()
						.add("name", name)					
						.add("label", label)
						.add("type", type)
						.add("index", index)
						.add("x", paramCoord.getX())
						.add("y", paramCoord.getY())
						.add("value", value)
						.add("orientation", ((JsonObject)paramValue).get("orientation").toString().replace("\"",""))
						.build();
				
				paramArrayData.add(paramData);
			}
			
			
			paramArrayDataBuilt = paramArrayData.build();
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getParamArrayData error: " + e);
		}
		
		return (JsonValue) paramArrayDataBuilt;
		
	}

	
	
	
	public String getParamArrayString()
	{
		
		String paramArrayString = "[";
		//JsonArray paramArrayDataBuilt = null;
		try
		{
			for(int i = 0; i < this.paramValueList.size(); i++)
			{
				String paramString = "";
				FlxParameter paramValue = this.paramValueList.get(i);
				String name = paramValue.getName();
				Coord paramCoord = this.paramMap.get(name).getConnectorLocalLocation();
				/*
				
				int valueIndex = paramValue.getParamValueIndex();
				String label = paramValue.getParameterEditorLabel();
				int type = paramValue.getParameterUnitType();
				int index = paramValue.getParamIndex();*/
				
				paramString += "{\"name\":\"" + paramValue.getName() + "\",";
				paramString += "\"label\":\"" + paramValue.getParameterEditorLabel() + "\",";
				paramString += "\"type\":" + paramValue.getParameterUnitType() + ",";
				paramString += "\"index\":" + paramValue.getParamIndex() + ",";
				paramString += "\"x\":" + paramCoord.getX() + ",";
				paramString += "\"y\":" + paramCoord.getY() + ",";
				paramString += "\"value\":" + paramValue.getParamValueIndex() + ",";
				paramString += "\"orientation\":\"" + paramValue.getOrientation() + "\"}";
				/*.JsonObject paramData = Json.createObjectBuilder()
						.add("name", name)					
						add("label", label)
						.add("type", type)
						.add("index", index)
						.add("x", paramCoord.getX())
						.add("y", paramCoord.getY())
						.add("value", value)
						.add("orientation", ((JsonObject)paramValue).get("orientation").toString().replace("\"",""))
						.build();*/
				if(i < this.paramValueList.size()-1)
				{
					paramString += ",";
				}	
				paramArrayString += paramString;
			}
			
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getParamArrayData error: " + e);
		}
		paramArrayString += "]";
		if(this.debugStatements) System.out.println("paramArrayString: " + paramArrayString);
		return paramArrayString;
		
	}

	public int getParamListSize()
	{
		return this.paramValueList.size();
	}
	

	public List<FlxParameter> getParamList() 
	{
		//printFlxParameterList("getParamList", this.paramValueList);
		return this.paramValueList;
	}

	public FlxParameter getParamListItem(int index)
	{
		return this.paramValueList.get(index);
	}


}
