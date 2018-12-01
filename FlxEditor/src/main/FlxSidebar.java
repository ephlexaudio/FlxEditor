package main;

import java.util.List;
import java.util.Map;

import javax.json.JsonValue;

import diagramComponents.FlxComponent;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public interface FlxSidebar {

	public void setDrawingArea(FlxDrawingArea drawingArea);
	public void setDrawingPane(Pane pane);
	public ListView<Group> getProcessComponentSideBar();
	public ListView<Group> getControlComponentSideBar();
	public List<Group> getProcessComponentList();
	public List<Group> getControlComponentList();
	public Map<String, FlxComponent> getProcessComponentMap();
	public Map<String, FlxComponent> getControlComponentMap();
	public FlxSidebar getSideBar();
	public List<String> getProcessList();
	public List<String> getControlList();
	public JsonValue getSymbol(String objectName);
	public void initSidebarProcessComponents();
	public boolean isProcessComponentSideBarFinished();
	public JsonValue getProcessData(String compId);
	public JsonValue getControlData(String compId);
}
