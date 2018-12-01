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
import diagramSubComponents.FlxControlParameter;
import javafx.scene.Group;

public class FlxControl_Impl extends FlxBlock_Impl implements FlxControl {

	int controlledParameterType;
	int index;
	boolean debugStatements = false;
	boolean errorStatements = true;

	JsonValue jsonControlData;
	String controlType;

	Map<String,FlxControlParameter> paramValueMap = new HashMap<String,FlxControlParameter>();
	List<FlxControlParameter> paramValueList = new ArrayList<FlxControlParameter>();

	public FlxControl_Impl(String controlName, JsonValue jsonControlData, String parentEffectName) throws IOException
	{
		super(controlName, (JsonObject)jsonControlData);
		try
		{
			if(this.name.contains("_")) super.symbolGroup.setId(this.name);

			//index is only used in pedal and is set when control is written to JSON combo file.
			this.index = 0;
			this.jsonControlData = jsonControlData;
			if(parentEffectName.isEmpty())
			{
				System.out.println(controlName + " parentEffect name is empty");
			}
			else
			{
				super.parentEffectName = parentEffectName;
			}

			if(((JsonObject)jsonControlData).containsKey("abbr"))
			{
				this.controlType = ((JsonObject)jsonControlData).get("abbr").toString().replace("\"", "").toLowerCase();
			}
			else if(((JsonObject)jsonControlData).containsKey("conType"))
			{
				this.controlType = ((JsonObject)jsonControlData).get("conType").toString().replace("\"", "").toLowerCase();
			}

			if(this.name.contains("_"))
			{
				super.symbolObj.setLocation(super.x, super.y);
			}
			else
			{
				super.symbolObj.setLocation(0, 0);
			}

			/****************************  GET PARAMETERS ************************************/
			JsonObject blockJsonObject = (JsonObject)this.blockJsonData;
			JsonArray paramJsonArray = null;

			paramJsonArray = blockJsonObject.getJsonArray("conParamArray");

			for(int paramJsonArrayIndex = 0; paramJsonArrayIndex < paramJsonArray.size(); paramJsonArrayIndex++)
			{
				JsonObject paramJsonElement = (JsonObject)paramJsonArray.get(paramJsonArrayIndex);
				String paramName = paramJsonElement.get("name").toString().replace("\"","");
				int paramIndex = paramJsonElement.getInt("index");
				String paramEditorLabel = paramJsonElement.get("label").toString().replace("\"","");
				String paramLcdAlias = paramJsonElement.get("alias").toString().replace("\"","");
				String paramLcdAbbr = paramJsonElement.get("abbr").toString().replace("\"","");
				int paramType = paramJsonElement.getInt("paramType");
				double paramValue = Double.parseDouble(paramJsonElement.get("value").toString().replace("\"",""));
				boolean conVoltEnabled = paramJsonElement.getBoolean("controlVoltageEnabled");
				int controlledParamType = paramJsonElement.getInt("controlledParamType");
				boolean inheritControlledParamType = paramJsonElement.getBoolean("inheritControlledParamType");
				this.paramValueList.add(new FlxControlParameter(paramName, super.symbolGroup, paramIndex, paramEditorLabel, paramType, controlledParamType, inheritControlledParamType,  "h", paramValue, paramLcdAlias, paramLcdAbbr, conVoltEnabled));
			}

			/**************************** MODIFY LABELING ***********************************/

			super.blockInfo.setStyle("-fx-font-size:10;");
			super.blockLabel.setStyle("-fx-font-size:10;");
			super.blockLabel.setTranslateY(50.0);
			super.blockInfo.setTranslateY(62.0);

			super.symbolGroup.getChildren().add(this.blockInfo);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxControl constructor error: " + e);
		}
	}

	public String getName()
	{
		return super.getName();
	}
	public int getIndex()
	{
		return this.index;
	}

	public String getControlType()
	{
		return this.controlType;
	}
	public void setIndex(int controlIndex)
	{
		this.index = controlIndex;
	}
	public List<String> getControlOutputConnectorList() {

		return super.getOutputConnectorList();
	}

	public Coord getControlOutputConnectorCoord(String connName) {
		// TODO Auto-generated method stub
		return super.getOutputConnectorCoord(connName);
	}

	public List<FlxControlParameter> getParamList()
	{
		return this.paramValueList;
	}


	public Group getControl()
	{

		return super.getBlock(this.x, this.y);
	}

    public JsonValue getData()
    {
    	if(this.debugStatements) System.out.println("getting control data: " + super.name);
		JsonObjectBuilder controlData  = Json.createObjectBuilder();

		JsonObject controlDataBuilt = null;
		try
		{

			controlData.add("name", this.getName());
			controlData.add("index", this.index);
			controlData.add("conType", this.getControlType());
			controlData.add("symbol", super.symbolObj.getSymbolData());
			controlData.add("parentEffect", super.parentEffectName);
			System.out.println("putting outputArray into processData JsonObjectBuilder");
			controlData.add("outputArray", super.getOutputArrayData());
			System.out.println("putting paramArray into processData JsonObjectBuilder");
			controlData.add("conParamArray", this.getParamArrayData());
			controlDataBuilt = controlData.build();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getControlData error: " + e);
		}
		if(this.debugStatements) System.out.println("control data retrieved: " + super.name);
		return (JsonValue)controlDataBuilt;
    }

    public String getDataString()
    {
    	if(this.debugStatements) System.out.println("getting control data: " + super.name);

		String controlDataString  = "";
		try
		{
			controlDataString += "{\"name\":\"" + this.getName() + "\",";
			controlDataString += "\"index\":" + this.getIndex() + ",";
			controlDataString += "\"conType\":\"" + this.getControlType() + "\",";
			controlDataString += "\"symbol\":" + super.symbolObj.getSymbolData().toString() + ",";
			controlDataString += "\"parentEffect\":\"" + super.getParentEffect() + "\",";
			controlDataString += "\"outputArray\":" + super.getOutputArrayString() + ",";
			controlDataString += "\"conParamArray\":" + this.getParamArrayString() + "}";
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getControlData error: " + e);
		}
		if(this.debugStatements) System.out.println("control data: " + controlDataString);
		return controlDataString;
    }


    public Coord getControlCoords()
	{
		Coord controlCoord = new Coord(super.symbolGroup.getLayoutX(),super.symbolGroup.getLayoutY());
		return controlCoord;
	}

	public JsonValue getParamArrayData()
	{
		JsonArrayBuilder paramArrayData = Json.createArrayBuilder();
		JsonArray paramArrayDataBuilt = null;
		try
		{
			for(int i = 0; i < this.paramValueList.size(); i++)
			{
				FlxControlParameter param = this.paramValueList.get(i);
				JsonObject paramBaseData = (JsonObject)param.getParameterData();
				JsonObject controlParamData = (JsonObject)param.getControlParameterData();
				String name = this.paramValueList.get(i).getName();
				int index = paramBaseData.getInt("index");
				double value = Double.parseDouble((paramBaseData).get("value").toString());
				String label = paramBaseData.getString("label");
				String lcdAlias = controlParamData.getString("alias");
				String lcdAbbr = controlParamData.getString("abbr");
				int paramType = paramBaseData.getInt("paramType");
				String orientation = ((JsonObject)paramBaseData).get("orientation").toString().replace("\"","");
				int controlledParamType = controlParamData.getInt("controlledParamType");
				boolean inheritControlledParameterType = controlParamData.getBoolean("inheritControlledParamType");
				boolean controlVoltageEnabled = controlParamData.getBoolean("controlVoltageEnabled");

				JsonObject paramData = Json.createObjectBuilder()
						.add("index", index)
						.add("name", name)
						.add("label", label)
						.add("alias", lcdAlias)
						.add("abbr", lcdAbbr)
						.add("paramType", paramType)
						.add("value", value)
						.add("inheritControlledParamType", inheritControlledParameterType)
						.add("orientation", orientation)
						.add("controlledParamType", controlledParamType)
						.add("controlVoltageEnabled", controlVoltageEnabled)
						.build();

				paramArrayData.add(paramData);
			}

			paramArrayDataBuilt = paramArrayData.build();

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxControl::getParamArrayData error: " + e);
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
				FlxControlParameter controlParamData = this.paramValueList.get(i);

				paramString += "{\"name\":\"" + controlParamData.getName() + "\",";
				paramString += "\"index\":" + controlParamData.getParamIndex() + ",";
				paramString += "\"label\":\"" + controlParamData.getParameterEditorLabel() + "\",";
				paramString += "\"alias\":\"" + controlParamData.getLcdAlias() + "\",";
				paramString += "\"abbr\":\"" + controlParamData.getLcdAbbr() + "\",";
				paramString += "\"paramType\":" + controlParamData.getParameterUnitType() + ",";
				paramString += "\"value\":" + controlParamData.getParamValueIndex() + ",";
				paramString += "\"controlledParamType\":" + controlParamData.getControlledProcessParameterType() + ",";
				paramString += "\"inheritControlledParamType\":" + controlParamData.getInheritControlParameterType() + ",";
				paramString += "\"orientation\":\"" + controlParamData.getOrientation() + "\",";
				paramString += "\"controlVoltageEnabled\":" + controlParamData.getControlVoltageStatus() + "}";

				if(i < this.paramValueList.size()-1)
				{
					paramString += ",";
				}
				paramArrayString += paramString;
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxControl::getParamArrayString error: " + e);
		}

		paramArrayString += "]";
		if(this.debugStatements) System.out.println("paramArrayString: " + paramArrayString);

		return paramArrayString;

	}

	public void setProcessParameterType(int contType)
	{
		this.controlledParameterType = contType;
		for(int i = 0; i < this.paramValueList.size(); i++)
		{
			this.paramValueList.get(i).setControlledProcessParameterType(contType);
		}
	}

	public int getProcessParameterType()
	{
		return this.controlledParameterType;
	}

}
