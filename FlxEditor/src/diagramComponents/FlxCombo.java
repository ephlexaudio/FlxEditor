package diagramComponents;

import java.util.List;
import java.util.Map;

import javax.json.JsonValue;

public interface FlxCombo {

	public JsonValue getComboData();
	public String getComboName();
	public Map<String,FlxEffect> getEffectMap();
	public Map<String,FlxCord> getCordMap();
	public List<FlxCord> getCordList();
	public FlxProcess getProcess(String effectName, String processName);
}
