package main;

import javax.json.JsonValue;

import diagramComponents.FlxCombo;
import diagramComponents.FlxControl;
import diagramComponents.FlxProcess;
/*import diagramComponents.FlxProcess_Impl;
import diagramComponents.FlxWire_Impl;
import diagramSubComponents.BoundCoord;
import javafx.scene.control.ScrollPane;*/
import javafx.scene.control.TabPane;
//import javafx.scene.layout.Pane;

public interface FlxDrawingArea 
{

	public void setEditor(FlxParameterEditor paramEditor);
	public void setSidebar(FlxSidebar sideBar);
	public void setNames(String comboName, String effect0Name, String effect0Abbr, String effect1Name, String effect1Abbr);
	public String getCurrentCombo();
	public void updateComboName(String comboName);
	public void updateEffectNameAbbr(String oldEffectName,String newEffectName, String effectAbbr);
	public void setCombo(FlxCombo combo);
	public JsonValue getCombo();
	public String getComboString();
	public /*ScrollPane*/TabPane getDrawingArea();
	public FlxProcess addProcess(JsonValue componentData, String parentEffectName, int index);
	public FlxProcess addProcess(FlxProcess process, String parentEffectName, int index);
	public void createProcessCountMap();
	public FlxControl addControl(JsonValue compData, String parentEffectName, int index);
	public void editProcess(FlxProcess process);
	public void editControl(FlxControl control);
	public void addEffectIO(int index);
}
