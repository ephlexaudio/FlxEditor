package diagramComponents;

import java.util.Map;

import javax.json.JsonValue;


public interface FlxEffect {

	public JsonValue getEffectData();
	public int getIndex();
	public String getName();
	public String getAbbr();
	public void setName(String effectName);
	public void setAbbr(String effectAbbr);
	public Map<String,FlxProcess> getProcessMap();
	public Map<String,FlxWire> getWireMap();
	public Map<String,FlxControl> getControlMap();
	public Map<String,FlxControlWire> getControlWireMap();
	public void addToProcessMap(String processName, FlxProcess process);

}
