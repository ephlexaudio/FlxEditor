package diagramComponents;

import java.util.List;
//import java.util.Map;

import javax.json.JsonValue;

import diagramSubComponents.Coord;
import diagramSubComponents.FlxControlParameter;
//import diagramSubComponents.FlxParameter;
import javafx.scene.Group;

public interface FlxControl extends FlxBlock {


	public String getName();
	public int getIndex();
	Group getControl();
	public void setIndex(int controlIndex);
	public List<String> getControlOutputConnectorList();
	public Coord getControlOutputConnectorCoord(String connName);
	public Coord getControlCoords();
	
	// These were originally in the Block class
	JsonValue getData();
	String getDataString();
	//public Map<String, FlxControlParameter> getParamMap();
	public List<FlxControlParameter> getParamList();
	//public void setParam(String paramName, double paramValue);
	public JsonValue getParamArrayData();
	//public void setControlParameterLcdLabels(int controlIndex, )
}
