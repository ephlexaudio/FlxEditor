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
/*import diagramSubComponents.FlxParameter;
import javafx.event.EventHandler;*/
import javafx.scene.Group;
/*import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import main.FlxSidebar;*/

public class FlxControl_Impl extends FlxBlock_Impl implements FlxControl {

	String type;
	int index;
	//String parentEffectName;
	boolean debugStatements = false;
	boolean errorStatements = true;
	
	JsonValue jsonControlData;
	String controlType;
	
	Map<String,FlxControlParameter> paramValueMap = new HashMap<String,FlxControlParameter>(); 
	List<FlxControlParameter> paramValueList = new ArrayList<FlxControlParameter>(); 

	public FlxControl_Impl(String controlName, JsonValue jsonControlData, String parentEffectName) throws IOException
	{
		super(controlName, (JsonObject)jsonControlData/*, parentEffectName*/);
		try
		{
			//this.name = ((JsonObject)jsonControlData).get("name").toString().replace("\"", "").toLowerCase();
			if(this.name.contains("_")) super.symbolGroup.setId(this.name);  //jsonControlData is from Combo file.
			
			//index is only used in pedal and is set when control is written to JSON combo file.
			this.index = 0;//((JsonObject)jsonControlData).getInt("index");
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
				this.type = ((JsonObject)jsonControlData).get("abbr").toString().replace("\"", "");
			}
			else if(((JsonObject)jsonControlData).containsKey("type"))
			{
				this.controlType = ((JsonObject)jsonControlData).get("type").toString().replace("\"", "").toLowerCase();				
				this.type = ((JsonObject)jsonControlData).get("type").toString().replace("\"", "");
			}
			
			if(this.name.contains("_"))
			{
				super.symbolObj.setLocation(super.x, super.y);
				//System.out.println("super.symbolObj.setLocation: " + this.name + ": " + super.x + "," + super.y);
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
				int paramType = paramJsonElement.getInt("type");
				double paramValue = Double.parseDouble(paramJsonElement.get("value").toString().replace("\"",""));
				//boolean conVoltEnabled = Boolean.parseBoolean(paramJsonElement.get("controlVoltageEnabled").toString().replace("\"",""));
				boolean conVoltEnabled = paramJsonElement.getBoolean("controlVoltageEnabled");
				
				//this.paramValueMap.put(paramName, new FlxControlParameter(paramName, super.symbolGroup, paramIndex, paramEditorLabel, paramType, "h", paramValue, paramLcdAlias, paramLcdAbbr));
				this.paramValueList.add(new FlxControlParameter(paramName, super.symbolGroup, paramIndex, paramEditorLabel, paramType, "h", paramValue, paramLcdAlias, paramLcdAbbr, conVoltEnabled));
			}
			//printFlxParameterList("constructor", this.paramValueList);
			
			/**************************** MODIFY LABELING ***********************************/
			
			//super.blockInfo.setId(this.name + "_info");
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

	public String getType()
	{
		return this.controlType;
	}
	public void setIndex(int controlIndex)
	{
		this.index = controlIndex;
	}
	public List<String> getControlOutputConnectorList() {
		// TODO Auto-generated method stub
		//List<String> controlOutputConnectorNames = new ArrayList<String>();
		
		
		return super.getOutputConnectorList();
	}
	
	public Coord getControlOutputConnectorCoord(String connName) {
		// TODO Auto-generated method stub
		return super.getOutputConnectorCoord(connName);
	}
	
	/*public Map<String, FlxControlParameter> getParamMap() {
		
		return this.paramValueMap;
	}*/
	
	public List<FlxControlParameter> getParamList() 
	{
		//printFlxParameterList("getParamList", this.paramValueList);
		return this.paramValueList;
	}
	
	/*public void setParam(String paramName, double paramValue)
	{
		this.paramValueMap.get(paramName).setValue(paramValue);
	}*/
	
	
	
	public Group getControl()
	{
				
		return super.getBlock(this.x, this.y);
	}
		
    public JsonValue getData()
    {
    	if(this.debugStatements) System.out.println("getting control data: " + super.name);
    	JsonValue controlData;
    	
    	controlData = this.jsonControlData;
		JsonObjectBuilder processData  = Json.createObjectBuilder();
		JsonObject processDataBuilt = null;
		try
		{
			
			processData.add("name", this.getName());
			processData.add("index", this.index);
			processData.add("type", this.type);
			processData.add("symbol", super.symbolObj.getSymbolData());
			processData.add("parentEffect", super.parentEffectName);
			System.out.println("putting outputArray into processData JsonObjectBuilder");
			processData.add("outputArray", super.getOutputArrayData());
			System.out.println("putting paramArray into processData JsonObjectBuilder");
			processData.add("conParamArray", this.getParamArrayData());
			processDataBuilt = processData.build();			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getControlData error: " + e);
			//processDataBuilt = (JsonObject) Json.createObjectBuilder().add("name","error").build();
		}
		//System.out.println("FlxControl::getControlData for " + this.getName() + ": " + this.jsonControlData);
		if(this.debugStatements) System.out.println("control data retrieved: " + super.name);
		return (JsonValue)processDataBuilt;
    }

    public String getDataString()
    {
    	if(this.debugStatements) System.out.println("getting control data: " + super.name);

		String controlDataString  = "";//Json.createObjectBuilder();
		//JsonObject processDataBuilt = null;
		try
		{
			controlDataString += "{\"name\":\"" + this.getName() + "\",";
			controlDataString += "\"index\":" + this.getIndex() + ",";
			controlDataString += "\"type\":\"" + this.getType() + "\",";
			controlDataString += "\"symbol\":" + super.symbolObj.getSymbolData().toString() + ",";
			controlDataString += "\"parentEffect\":\"" + super.getParentEffect() + "\",";
			controlDataString += "\"outputArray\":" + super.getOutputArrayString() + ",";
			controlDataString += "\"conParamArray\":" + this.getParamArrayString() + "}";
			/*processData.add("name", this.getName());
			processData.add("index", this.index);
			processData.add("type", this.type);
			processData.add("symbol", super.symbolObj.getSymbolData());
			processData.add("parentEffect", super.parentEffectName);
			//System.out.println("putting outputArray into processData JsonObjectBuilder");
			processData.add("outputArray", super.getOutputArrayData());
			//System.out.println("putting paramArray into processData JsonObjectBuilder");
			//processData.add("conParamArray", this.getParamArrayData());
			processDataBuilt = processData.build();			*/
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxProcess::getControlData error: " + e);
			//processDataBuilt = (JsonObject) Json.createObjectBuilder().add("name","error").build();
		}
		//System.out.println("FlxControl::getControlData for " + this.getName() + ": " + this.jsonControlData);
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
				JsonObject paramBaseData = (JsonObject)this.paramValueList.get(i).getParameterData();
				JsonObject controlParamData = (JsonObject)this.paramValueList.get(i).getControlParameterData();
				String name = this.paramValueList.get(i).getName();
				int index = paramBaseData.getInt("index");
				double value = Double.parseDouble((paramBaseData).get("value").toString());
				String label = paramBaseData.getString("label");
				String lcdAlias = controlParamData.getString("alias");
				String lcdAbbr = controlParamData.getString("abbr");
				int type = paramBaseData.getInt("type");
				String orientation = ((JsonObject)paramBaseData).get("orientation").toString().replace("\"","");
				boolean controlVoltageEnabled = controlParamData.getBoolean("controlVoltageEnabled");
				 
				JsonObject paramData = Json.createObjectBuilder()
						.add("index", index)
						.add("name", name)
						.add("label", label)
						.add("alias", lcdAlias)
						.add("abbr", lcdAbbr)
						.add("type", type)
						.add("value", value)
						.add("orientation", orientation)
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
		String paramArrayString = "[";//Json.createArrayBuilder();
		//JsonArray paramArrayDataBuilt = null;
		try
		{
			
			for(int i = 0; i < this.paramValueList.size(); i++)
			{
				String paramString = "";
				FlxControlParameter controlParamData = this.paramValueList.get(i);
				/*String name = controlParamData.getName();
				int index = controlParamData.getParamIndex();
				int valueIndex = controlParamData.getParamValueIndex();
				String label = controlParamData.getParameterEditorLabel();
				String lcdAlias = controlParamData.getLcdAlias();
				String lcdAbbr = controlParamData.getLcdAbbr();
				int type = controlParamData.getParameterUnitType();
				String orientation = controlParamData.getOrientation();
				boolean controlVoltageEnabled = controlParamData.getControlVoltageStatus();*/
				 
				paramString += "{\"name\":\"" + controlParamData.getName() + "\",";
				paramString += "\"index\":" + controlParamData.getParamIndex() + ",";
				paramString += "\"label\":\"" + controlParamData.getParameterEditorLabel() + "\",";
				paramString += "\"alias\":\"" + controlParamData.getLcdAlias() + "\",";
				paramString += "\"abbr\":\"" + controlParamData.getLcdAbbr() + "\",";
				paramString += "\"type\":" + controlParamData.getParameterUnitType() + ",";
				paramString += "\"value\":" + controlParamData.getParamValueIndex() + ",";
				paramString += "\"orientation\":\"" + controlParamData.getOrientation() + "\",";
				paramString += "\"controlVoltageEnabled\":" + controlParamData.getControlVoltageStatus() + "}";
				/*JsonObject paramData = Json.createObjectBuilder()
						//.add("index", index)
						.add("name", name)
						.add("label", label)
						.add("alias", lcdAlias)
						.add("abbr", lcdAbbr)
						.add("type", type)
						.add("value", value)
						.add("orientation", orientation)
						.add("controlVoltageEnabled", controlVoltageEnabled)
						.build();
				
				paramArrayData.add(paramData);*/
				if(i < this.paramValueList.size()-1)
				{
					paramString += ",";
				}	
				paramArrayString += paramString;
			}
			
			//paramArrayDataBuilt = paramArrayData.build();
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxControl::getParamArrayData error: " + e);
		}
		paramArrayString += "]";
		if(this.debugStatements) System.out.println("paramArrayString: " + paramArrayString);
		
		return paramArrayString;
		
	}

}
