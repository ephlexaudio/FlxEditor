package diagramComponents;

import java.util.List;

import javax.json.JsonValue;

import diagramSubComponents.Coord;
import diagramSubComponents.FlxControlParameter;
import javafx.scene.Group;

public interface FlxControl extends FlxBlock {


	public String getName();
	public String getControlType();
	public int getIndex();
	Group getControl();
	public void setIndex(int controlIndex);
	public List<String> getControlOutputConnectorList();
	public Coord getControlOutputConnectorCoord(String connName);
	public Coord getControlCoords();


	JsonValue getData();
	String getDataString();
	public List<FlxControlParameter> getParamList();
	public JsonValue getParamArrayData();
	public void setProcessParameterType(int contType);
	public int getProcessParameterType();

}
