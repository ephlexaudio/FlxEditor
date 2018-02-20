package diagramComponents;

import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
/*import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;*/
import javax.json.JsonValue;

import diagramSubComponents.Coord;
//import diagramSubComponents.FlxConnector;
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
import diagramSubComponents.FlxSymbol;
import javafx.scene.Group;
//import javafx.scene.Node;

public interface FlxProcess extends FlxBlock {

	public String getType();
	public Group getSymbolGroup();
	//public void draw();
	public Coord getProcessCoords();
	public void flipConnectorCoord(boolean flip);
	public void flipProcess();
	/*public List getProcessInputConnectorList();
	public List getProcessOutputConnectorList();
	public List getProcessParamConnectorList();
	public Coord getProcessInputConnectorCoord(String connName);
	public Coord getProcessOutputConnectorCoord(String connName);
	public Coord getProcessParamConnectorCoord(String connName);*/
	public FlxFootswitch getFootswitch();
	public void setFootswitchNumber(String footswitchNumber);
	
	// These were originally in the Block class
	public JsonValue getData();
	public String getDataString();
	//public Map<String, FlxParameter> getParamMap();
	
	
	//public void setParam(String paramName, double paramValue);
	public String getFootswitchNumber();
	public String getProcessDirection();
	public String getProcessName();
	public String getProcessType();
	public int getProcessCpuPower();
	public String getFootswitchType();
	public FlxSymbol getSymbolData();
	public String getParentEffectName();
	public JsonValue getInputArrayData();
	public String getInputArrayString();
	public JsonValue getOutputArrayData();
	public String getOutputArrayString();
	public JsonValue getParamArrayData();
	public String getParamArrayString();
	public int getParamListSize();
	public List<FlxParameter> getParamList();
	public FlxParameter getParamListItem(int index);

}
