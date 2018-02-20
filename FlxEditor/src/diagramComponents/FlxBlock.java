package diagramComponents;

import java.util.List;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
/*import java.util.Vector;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;*/
import javax.json.JsonValue;

import diagramSubComponents.BoundCoord;
import diagramSubComponents.Coord;
import diagramSubComponents.FlxConnector;
/*import diagramSubComponents.FlxParameter;
import diagramSubComponents.FlxSymbol;*/
import javafx.scene.Group;
//import main.FlxSidebar;

public interface FlxBlock {
	public String getName();
	public void setName(String blockName);
	public void setParentEffect(String parentEffectName);
	public String getParentEffect();
	public void setInfo(String blockInfoString);
	public Group getBlock(double x, double y);
	public Group getBlock();
	public void setLocation(double x, double y);
	public Coord getLocation();
	public void setSymbol(JsonValue symbolData);
	public List<String> getInputConnectorList();
	public List<String> getOutputConnectorList();
	public List<String> getParamConnectorList();
	public Coord getInputConnectorCoord(String inputName);
	public BoundCoord getInputConnectorBoundCoord(String inputName);
	public Coord getOutputConnectorCoord(String outputName);
	public Coord getParamConnectorCoord(String paramName);
	public void setInputConnectorCoord(String inputName, double x, double y);
	public Coord setOutputConnectorCoord(String outputName, double x, double y);
	public void deleteSymbol();  // will symbol disappear on its own if it's Group isn't deleted explicity???
	public void setSelectIndicator(boolean selected);
	//public Map<String, FlxParameter> getParamMap();
	//public List<FlxParameter> getParamList();
	//public void setParam(String paramName, double paramValue);
	
	public JsonValue getInputArrayData();
	public String getInputArrayString();
	public int getInputMapSize();
	public Map<String,FlxConnector> getInputMap();
	public List<String> getInputMapKeys();
	public FlxConnector getInputMapItem(String key);
	
	public JsonValue getOutputArrayData();
	public String getOutputArrayString();
	public JsonArrayBuilder getOutputArrayBuilder();
	public int getOutputMapSize();
	public Map<String,FlxConnector> getOutputMap();
	public List<String> getOutputMapKeys();
	public FlxConnector getOutputMapItem(String key);
	//abstract public JsonValue getParamArrayData();
	//public JsonValue getProcessParamArrayData();
	//public JsonValue getControlParamArrayData();
	//public Map<String, JsonValue> getComponentSymbols();
	public void printConnectionCoords();
}
