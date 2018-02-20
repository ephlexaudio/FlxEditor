package main;

//import java.io.IOException;
import java.util.List;
//import java.util.Map;

import javax.json.JsonValue;

/*import diagramComponents.FlxComponent;
import diagramComponents.FlxComponent_Impl;*/
import javafx.scene.Group;
import javafx.scene.control.ListView;
//import javafx.scene.layout.GridPane;

public interface FlxSidebar {

	public void setDrawingArea(FlxDrawingArea drawingArea);
	public ListView<Group> getProcessComponentSideBar();
	public ListView<Group> getControlComponentSideBar();
	//public Map<String, FlxComponent> getProcessComponents();
	//public Map<String, FlxComponent> getControlComponents();
	public List<String> getProcessList();
	public List<String> getControlList();
	public JsonValue getSymbol(String objectName);
	public void initSidebarProcessComponents();
	//public void updateComponentSideBar();
	public boolean isProcessComponentSideBarFinished();
	//public void addProcess(String componentName);
}
