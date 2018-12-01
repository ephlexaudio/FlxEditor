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
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
import diagramSubComponents.FlxSymbol;
import javafx.scene.Group;

public class FlxProcess_Impl extends FlxBlock_Impl implements FlxProcess {

	boolean debugStatements = false;
	boolean errorStatements = true;
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
	String procType;
	String controlledParameterControllerType;
    boolean flipped;

	public FlxProcess_Impl(String procName, JsonValue jsonProcessData, String parentEffectName) throws IOException
	{
		super(procName, (JsonObject)jsonProcessData);
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
			}
			else
			{
				super.symbolObj.setLocation(0, 0);
			}


			this.procType = jsonProcessDataObject.get("procType").toString().replace("\"", "");


			this.footswitchType = jsonProcessDataObject.get("footswitchType").toString().replace("\"", "");
			this.footswitchNumber = jsonProcessDataObject.get("footswitchNumber").toString().replace("\"", "");
			this.footswitch.setFootswitchNumber(this.footswitchNumber);
			this.processDirection = jsonProcessDataObject.get("processDirection").toString().replace("\"", "");
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
				int paramType = paramJsonElement.getInt("paramType");
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
				String paramContType = paramJsonElement.getString("paramContType");
				this.paramValueList.add(new FlxParameter(paramName, super.symbolGroup, paramIndex, paramEditorLabel, paramType, paramContType, paramOrientation, paramValue));
			}

			/**************************** MODIFY LABELING ***********************************/

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
		return this.procType;
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


	public Group getSymbolGroup()
	{
		return super.getBlock(this.x, this.y);
	}


	public void flipConnectorCoord(boolean flip)
	{

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

			if(this.debugStatements) System.out.println("process direction: " + this.processDirection);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::flipProcess error: " + e);
		}

	}




	public JsonValue getData()
	{
		if(this.debugStatements) System.out.println("getting process data: " + this.getName());
		JsonObjectBuilder processData  = Json.createObjectBuilder();
		JsonObject processDataBuilt = null;
		try
		{
			this.footswitchNumber = this.footswitch.getFootswitchNumber();
			processData.add("processDirection", this.processDirection);
			processData.add("name", this.getName());
			processData.add("procType", this.procType);
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
			processDataString += "\"procType\":\"" + this.getProcessType() + "\",";
			processDataString += "\"cpuPower\":" + this.getProcessCpuPower() + ",";
			processDataString += "\"footswitchType\":\"" + this.getFootswitchType() + "\",";
			processDataString += "\"footswitchNumber\":\"" + this.getFootswitchNumber() + "\",";
			processDataString += "\"symbol\":" + super.symbolObj.getSymbolData().toString() + ",";
			processDataString += "\"parentEffect\":\"" + super.parentEffectName + "\",";
			processDataString += "\"inputArray\":" + super.getInputArrayString() + ",";
			processDataString += "\"outputArray\":" + super.getOutputArrayString() + ",";
			processDataString += "\"paramArray\":" + this.getParamArrayString() + "}";


		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getData error: " + e);
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
		return this.procType;
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
				int paramType = paramValue.getInt("paramType");
				int index = paramValue.getInt("index");
				JsonObject paramData = Json.createObjectBuilder()
						.add("name", name)
						.add("label", label)
						.add("paramType", paramType)
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
		try
		{
			for(int i = 0; i < this.paramValueList.size(); i++)
			{
				String paramString = "";
				FlxParameter paramValue = this.paramValueList.get(i);
				String name = paramValue.getName();
				Coord paramCoord = this.paramMap.get(name).getConnectorLocalLocation();

				paramString += "{\"name\":\"" + paramValue.getName() + "\",";
				paramString += "\"label\":\"" + paramValue.getParameterEditorLabel() + "\",";
				paramString += "\"paramType\":" + paramValue.getParameterUnitType() + ",";
				paramString += "\"index\":" + paramValue.getParamIndex() + ",";
				paramString += "\"x\":" + paramCoord.getX() + ",";
				paramString += "\"y\":" + paramCoord.getY() + ",";
				paramString += "\"value\":" + paramValue.getParamValueIndex() + ",";
				paramString += "\"paramContType\":\"" + paramValue.getParamControllerType() + "\",";
				paramString += "\"orientation\":\"" + paramValue.getOrientation() + "\"}";
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
		return this.paramValueList;
	}

	public FlxParameter getParamListItem(int index)
	{
		return this.paramValueList.get(index);
	}

	public FlxParameter getParamMapItem(String key)
	{
		FlxParameter tempParam = null;
		for(FlxParameter param:this.paramValueList)
		{
			if(param.getName().compareTo(key) == 0)
			{
				tempParam = param;
				break;
			}
		}
		return tempParam;
	}

	public void setParameterControllerType(String paramContType)
	{
		this.controlledParameterControllerType = paramContType;
	}

	public String getParameterControllerType()
	{
		return this.controlledParameterControllerType;
	}
}
