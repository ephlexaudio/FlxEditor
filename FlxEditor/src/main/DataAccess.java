package main;

import java.util.List;

import javax.json.JsonValue;

public interface DataAccess {

	public boolean confirmConnection();
	public List<String> getComboList();
	public JsonValue getCombo(String comboName);
	public List<JsonValue> getProcessComponents();
	public List<JsonValue> getControlComponents();
	public List<String> sendCombo(JsonValue comboData);
	public List<String> sendComboString(String comboName, String comboString);
	public List<String> deleteCombo(String comboName);
	public int changeValue(String parent, String parentType, String paramName, int valueIndex); 
	
	public boolean checkCommPortStatus();
	public String getPedalStatus();
}
