package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonValue;

import diagramComponents.FlxComponent;
import diagramComponents.FlxComponent_Impl;
import diagramComponents.FlxControl;
import diagramComponents.FlxProcess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;



public class FlxSidebar_Impl implements FlxSidebar {
	boolean debugStatements = true;

	boolean errorStatements = true;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    FlxProcess processCompXferToDrawingArea = null;
    FlxControl controlCompXferToDrawingArea = null;
	Map<String, FlxComponent> processComponentMap = new HashMap<String,FlxComponent>();
	Map<String, FlxComponent> controlComponentMap = new HashMap<String,FlxComponent>();
	Map<String, JsonValue> symbolMap = new HashMap<String,JsonValue>();
	List<String> processNameArray = new ArrayList<String>();
	SystemUtility sysUtil = SystemUtility.getInstance();
	List<String> controlNameArray = new ArrayList<String>();
	List<Group> processComponentSideBarList = new ArrayList<Group>();
	GridPane processComponentSideBar;
	ListView<Group> processComponentSideBarListView = new ListView<Group>();

	List<Group> controlComponentSideBarList = new ArrayList<Group>();
	GridPane controlComponentSideBar;
	ListView<Group> controlComponentSideBarListView = new ListView<Group>();
	DataAccess dataAccess;
	FlxDrawingArea drawingAreaReference;
	Pane drawingAreaPane;
	boolean processComponentMapFinished = false;

	FlxSidebar_Impl()
	{

		/****************************** GET COMPONENTS *****************************/
		this.processComponentSideBarListView.setMaxWidth(130);
		this.processComponentSideBarListView.setMinWidth(130);
		/****************************** GET CONTROLS ********************************/
		this.controlComponentSideBarListView.setMaxWidth(130);
		this.controlComponentSideBarListView.setMinWidth(130);

		/******************************************************************************/

	}

	public void initSidebarProcessComponents()
	{

		if(sysUtil.dataAccessMode().compareTo("host") == 0)
		{
			this.dataAccess = new DataAccess_HostImpl();
		}
		else
		{
			this.dataAccess = new DataAccess_PedalImpl();
		}

		/*************************************************************************/

		try
		{
			this.setProcessComponents();
			this.processComponentSideBarListView.setMinHeight(this.processComponentMap.size()*86);
			this.processComponentSideBarListView.setMaxHeight(this.processComponentMap.size()*86);
			if(this.debugStatements) System.out.println("components: " + this.processComponentMap.size());
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Sidebar::getProcessComponents error" + e);
		}

		ObservableList<Group>  processGroupObservableList = FXCollections.observableList(this.processComponentSideBarList);
		for(String componentKey:this.processComponentMap.keySet())
		{
			Group tempGroup = this.processComponentMap.get(componentKey).getComponent();
			this.processComponentSideBarList.add(tempGroup);
		}
		this.processComponentSideBarListView.setItems(processGroupObservableList);

		/*************************************************************************/

		try
		{
			this.setControlComponents();
			this.controlComponentSideBarListView.setMinHeight(this.controlComponentMap.size()*120);
			this.controlComponentSideBarListView.setMaxHeight(this.controlComponentMap.size()*120);
			if(this.debugStatements) System.out.println("controls: " + this.controlComponentMap.size());
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Sidebar::getControls error" + e);
		}

		ObservableList<Group>  controlGroupList = FXCollections.observableList(this.controlComponentSideBarList);
		for(String controlKey:this.controlComponentMap.keySet())
		{
			Group tempGroup = this.controlComponentMap.get(controlKey).getComponent();
			controlGroupList.add(tempGroup);
		}
		this.controlComponentSideBarListView.setItems(controlGroupList);

	}

	public boolean isProcessComponentSideBarFinished()
	{
		return this.processComponentMapFinished;
	}


	/*private void processComponentClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		try
		{
			if(event.getButton() == MouseButton.PRIMARY )
			{
		        orgSceneX = event.getSceneX();
		        orgSceneY = event.getSceneY();
		        Node n = (Node)(event.getSource());
		        String id = n.getId();
		        FlxComponent comp = this.processComponentMap.get(id);
		        JsonValue compData = comp.getComponentData();
		        try
		        {
		        	this.drawingAreaReference.addProcess(compData,null,-1);
		        }
		        catch(Exception e)
		        {
		        	if(this.errorStatements) System.out.println(e);
		        }
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("processComponentClicked error: " + e);
		}
	}

	private void controlComponentClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		if(event.getButton() == MouseButton.PRIMARY )
		{
	        orgSceneX = event.getSceneX();
	        orgSceneY = event.getSceneY();
	        Node n = (Node)(event.getSource());
	        System.out.println(n.getId().toString());
	        String id = n.getId();
	        FlxComponent control = this.controlComponentMap.get(id);
	        JsonValue controlData = control.getComponentData();
	        try
	        {
	        	this.drawingAreaReference.addControl(controlData, null,-1);
	        }
	        catch(Exception e)
	        {
	        	if(this.errorStatements) System.out.println("controlComponentClicked error: " + e);
	        }
		}

	}*/

	public void setDrawingArea(FlxDrawingArea drawingArea)
	{
		this.drawingAreaReference = drawingArea;
	}

	public void setDrawingPane(Pane pane)
	{
		this.drawingAreaPane = pane;
	}

	public FlxSidebar getSideBar()
	{
		return this;
	}

	public ListView<Group> getProcessComponentSideBar()
	{
		return this.processComponentSideBarListView;
	}

	public ListView<Group> getControlComponentSideBar()
	{
		return this.controlComponentSideBarListView;
	}
	public Map<String, FlxComponent> getProcessComponentMap()
	{
		return this.processComponentMap;
	}
	public Map<String, FlxComponent> getControlComponentMap()
	{
		return this.controlComponentMap;
	}
	public List<Group> getProcessComponentList()
	{
		return this.processComponentSideBarList;
	}

	public List<Group> getControlComponentList()
	{
		return this.controlComponentSideBarList;
	}


	private void  setProcessComponents()
    {
    	if(dataAccess.checkCommPortStatus() == true)
    	{
        	try
        	{
            	List<JsonValue> processComponentArray = dataAccess.getProcessComponents();

            	for(int processComponentIndex = 0; processComponentIndex < processComponentArray.size(); processComponentIndex++)
            	{
            		JsonObject processJsonData = (JsonObject)(processComponentArray.get(processComponentIndex));
            		JsonValue processSymbolData = processJsonData.get("symbol");
            		String processName = processJsonData.get("name").toString().replace("\"", "").toLowerCase();
            		String processType = processJsonData.get("procType").toString().replace("\"", "").toLowerCase();
            		FlxComponent process = new FlxComponent_Impl(processJsonData);
            		this.processComponentMap.put(processName, process);
            		this.symbolMap.put(processType, processSymbolData);
            		this.processNameArray.add(processName);
            	}
        	}
        	catch(Exception e)
        	{
        		if(this.errorStatements) System.out.println("error getting process components: " + e);
        	}

    	}


    }

	private void setControlComponents()
    {
    	if(dataAccess.checkCommPortStatus() == true)
    	{
        	try
        	{
            	List<JsonValue> controlComponentArray = dataAccess.getControlComponents();
            	System.out.println("controlComponentArray size: " + controlComponentArray.size());
            	for(int controlComponentIndex = 0; controlComponentIndex < controlComponentArray.size(); controlComponentIndex++)
            	{
            		JsonObject controlJsonData = (JsonObject)(controlComponentArray.get(controlComponentIndex));
            		JsonValue controlSymbolData = controlJsonData.get("symbol");
            		String controlName = controlJsonData.get("name").toString().replace("\"", "").toLowerCase();
            		System.out.println("this.controlComponentMap size: " + this.controlComponentMap.size());
            		String controlType = controlJsonData.get("abbr").toString().replace("\"", "").toLowerCase();
            		System.out.println("this.controlComponentMap size: " + this.controlComponentMap.size());
            		FlxComponent control = new FlxComponent_Impl(controlJsonData);
            		this.controlComponentMap.put(controlName, control);
            		this.symbolMap.put(controlType, controlSymbolData);
            		this.controlNameArray.add(controlName);
            	}
            	System.out.println("this.controlComponentMap size: " + this.controlComponentMap.size());
        	}
        	catch(Exception e)
        	{
        		if(this.errorStatements) System.out.println("error getting control components: " + e);
        	}
    	}
    }

	public List<String> getProcessList()
	{
		return this.processNameArray;
	}

	public List<String> getControlList()
	{
		return this.controlNameArray;
	}

	EventHandler<MouseEvent> circleOnMousePressedEventHandler =
    new EventHandler<MouseEvent>() {

	    @Override
	    public void handle(MouseEvent t)
	    {
	        orgSceneX = t.getSceneX();
	        orgSceneY = t.getSceneY();
	        Node n = (Node)(t.getSource());
	        if(debugStatements) System.out.println(n.getId().toString());

	    }
	};

	public JsonValue getSymbol(String objectName)
	{
		JsonValue tempSymbolData = this.symbolMap.get(objectName);
	   	return tempSymbolData;
	}

	public JsonValue getProcessData(String compId)
	{
		JsonValue procJsonData = null;

		try
		{
			procJsonData = this.getProcessComponentMap().get(compId).getComponentData();
		}
		catch(Exception e)
		{
			System.out.println("FlxSidebar_Impl::getProcessData: " + e);
		}
		return procJsonData;
	}

	public JsonValue getControlData(String compId)
	{
		JsonValue contJsonData = null;

		try
		{
			contJsonData = this.getControlComponentMap().get(compId).getComponentData();
		}
		catch(Exception e)
		{
			System.out.println("FlxSidebar_Impl::getControlData: " + e);
		}
		return contJsonData;
	}


}
