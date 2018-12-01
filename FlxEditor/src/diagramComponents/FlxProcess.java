package diagramComponents;

import java.util.List;

import javax.json.JsonValue;

import diagramSubComponents.Coord;
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
import diagramSubComponents.FlxSymbol;
import javafx.scene.Group;

public interface FlxProcess extends FlxBlock {

	public String getType();
	public Group getSymbolGroup();
	public Coord getProcessCoords();
	public void flipConnectorCoord(boolean flip);
	public void flipProcess();
	public FlxFootswitch getFootswitch();
	public void setFootswitchNumber(String footswitchNumber);

	// These were originally in the Block class
	public JsonValue getData();
	public String getDataString();
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
	public FlxParameter getParamMapItem(String key);
	public void setParameterControllerType(String paramContType);
	public String getParameterControllerType();
}
