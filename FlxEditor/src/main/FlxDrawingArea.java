package main;

import java.util.List;

import javax.json.JsonValue;

import diagramComponents.FlxCombo;
import diagramComponents.FlxControl;
import diagramComponents.FlxProcess;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface FlxDrawingArea
{

	public void setEditor(FlxParameterEditor paramEditor);
	public void setSidebar(FlxSidebar sideBar);
	public void setTabNames(String comboName, String effect0Name, String effect0Abbr, String effect1Name, String effect1Abbr);
	public List<String> getNames();
	public String getCurrentCombo();
	public void updateComboName(String comboName);
	public void updateEffectNameAbbr(String oldEffectName,String newEffectName, String effectAbbr);
	public void setCombo(FlxCombo combo);
	public JsonValue getCombo();
	public String getComboString();
	public Pane getDrawingPane();
	public TabPane getDrawingArea();
	public FlxProcess addProcess(JsonValue componentData, String parentEffectName, int index);
	public FlxProcess addProcess(FlxProcess process, String parentEffectName, int index);
	public FlxControl addControl(JsonValue compData, String parentEffectName, int index);
	public void editProcess(FlxProcess process);
	public void editControl(FlxControl control);
	public void addEffectIO(int index);
	public String getCurrentEffectName();
	public int getCurrentEffectIndex();
	public Effect[] getCurrentEffectArray();
	public Effect getCurrentEffectArray(int index);
	public void processDragged(MouseEvent event);
	public void initializeSideBarEvents();
	public String getComboName();
}
