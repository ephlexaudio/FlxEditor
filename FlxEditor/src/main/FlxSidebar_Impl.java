package main;

/*import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;*/

//import javax.json.Json;
import javax.json.JsonObject;
//import javax.json.JsonReader;
import javax.json.JsonValue;

import diagramComponents.FlxComponent;
import diagramComponents.FlxComponent_Impl;
/*import diagramComponents.FlxControl;
import diagramComponents.FlxControl_Impl;
import diagramComponents.FlxProcess_Impl;*/
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import javafx.collections.ObservableListBase;
import javafx.event.EventHandler;
/*import javafx.geometry.Insets;
import javafx.scene.Cursor;*/
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListView;
//import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
/*import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;*/



public class FlxSidebar_Impl implements FlxSidebar {
	boolean debugStatements = true;
	boolean errorStatements = true;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

	Map<String, FlxComponent> processComponentMap = new HashMap<String,FlxComponent>();
	Map<String, FlxComponent> controlComponentMap = new HashMap<String,FlxComponent>();
	Map<String, JsonValue> symbolMap = new HashMap<String,JsonValue>();
	List<String> processNameArray = new ArrayList<String>();
	SystemUtility sysUtil = SystemUtility.getInstance();
	List<String> controlNameArray = new ArrayList<String>();
	//Group sidebarCanvas = new Group();
	List<Group> processComponentSideBarList = new ArrayList<Group>();
	GridPane processComponentSideBar;// = new GridPane();
	ListView<Group> processComponentSideBarListView = new ListView<Group>();
	
	List<Group> controlComponentSideBarList = new ArrayList<Group>();
	GridPane controlComponentSideBar;//  = new GridPane();
	ListView<Group> controlComponentSideBarListView = new ListView<Group>();
	DataAccess dataAccess;// = new DataAccess_HostImpl();
	//DataAccess dataAccess = new DataAccess_PedalImpl();
	FlxDrawingArea drawingAreaReference;
	boolean processComponentMapFinished = false;

	FlxSidebar_Impl()
	{
		
		/*if(sysUtil.dataAccessMode().compareTo("host") == 0)
		{
			dataAccess = new DataAccess_HostImpl();
		}
		else
		{
			dataAccess = new DataAccess_PedalImpl();
		}*/
		//updateTimer.scheduleAtFixedRate(updateComponentSideBar(), 0, 1000);
		
		/****************************** GET COMPONENTS *****************************/
		//this.processComponentSideBarListView.setPadding(new Insets(5));
		this.processComponentSideBarListView.setMaxWidth(130);
		this.processComponentSideBarListView.setMinWidth(130);
		//this.processComponentSideBarListView.set
		/****************************** GET CONTROLS ********************************/
		//this.controlComponentSideBarListView.setPadding(new Insets(5));
		this.controlComponentSideBarListView.setMaxWidth(130);
		this.controlComponentSideBarListView.setMinWidth(130);
		
		/******************************************************************************/
		
	}
	
	public void initSidebarProcessComponents()
	{
		//GridPane processComponentSideBar = new GridPane();
		
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
			this.getProcessComponents();
			this.processComponentSideBarListView.setMinHeight(this.processComponentMap.size()*86);
			this.processComponentSideBarListView.setMaxHeight(this.processComponentMap.size()*86);
			if(this.debugStatements) System.out.println("components: " + this.processComponentMap.size());
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Sidebar::getProcessComponents error" + e);
		}
		
		//int i = 0;
		ObservableList<Group>  processGroupList = FXCollections.observableList(this.processComponentSideBarList);
		for(String componentKey:this.processComponentMap.keySet())
		{
			Group tempGroup = this.processComponentMap.get(componentKey).getComponent();
			tempGroup.setOnMouseClicked(event -> {processComponentClicked(event);});
			//this.processComponentSideBarList.add(tempGroup);
			processGroupList.add(tempGroup);
		}
		this.processComponentSideBarListView.setItems(processGroupList);
		
		/*************************************************************************/		
		
		try
		{
			this.getControlComponents();
			this.controlComponentSideBarListView.setMinHeight(this.controlComponentMap.size()*120);
			this.controlComponentSideBarListView.setMaxHeight(this.controlComponentMap.size()*120);
			if(this.debugStatements) System.out.println("controls: " + this.controlComponentMap.size());
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Sidebar::getControls error" + e);
		}
		
		//i = 0;
		ObservableList<Group>  controlGroupList = FXCollections.observableList(this.controlComponentSideBarList);
		for(String controlKey:this.controlComponentMap.keySet())
		{
			Group tempGroup = this.controlComponentMap.get(controlKey).getComponent();
			//tempGroup.setTranslateX(30);
			tempGroup.setOnMouseClicked(event -> {controlComponentClicked(event);});
			//this.controlComponentSideBar.add(tempGroup, 0, i++);
			controlGroupList.add(tempGroup);
		}
		this.controlComponentSideBarListView.setItems(controlGroupList);
		
	}
	
	public boolean isProcessComponentSideBarFinished()
	{
		return this.processComponentMapFinished;
	}
	
	public void updateComponentSideBar()
	{
		int i = 0;
		for(String componentKey:this.processComponentMap.keySet())
		{
			Group tempGroup = this.processComponentMap.get(componentKey).getComponent();
			tempGroup.setOnMouseClicked(event -> {processComponentClicked(event);});
			this.processComponentSideBar.add(tempGroup, 0, i++);
		}
		
		i = 0;
		for(String controlKey:this.controlComponentMap.keySet())
		{
			Group tempGroup = this.controlComponentMap.get(controlKey).getComponent();
			//tempGroup.setTranslateX(30);
			tempGroup.setOnMouseClicked(event -> {controlComponentClicked(event);});
			this.controlComponentSideBar.add(tempGroup, 0, i++);
		}
	}
	
	private void controlComponentClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		if(event.getButton() == MouseButton.PRIMARY )
		{
	        orgSceneX = event.getSceneX();
	        orgSceneY = event.getSceneY();
	        Node n = (Node)(event.getSource());
	        //System.out.println(n.getId().toString());
	        String id = n.getId();
	        FlxComponent control = this.controlComponentMap.get(id);
	        JsonValue controlData = control.getComponentData();
	        try
	        {
	        	this.drawingAreaReference.addControl(controlData, null,-1);//.addProcess(compData);
	        }
	        catch(Exception e)
	        {
	        	if(this.errorStatements) System.out.println("controlComponentClicked error: " + e);
	        }			
		}
		
	}

	public void setDrawingArea(FlxDrawingArea drawingArea)
	{
		this.drawingAreaReference = drawingArea;
	}
	
	private void processComponentClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		try
		{
			if(event.getButton() == MouseButton.PRIMARY )
			{
		        orgSceneX = event.getSceneX();
		        orgSceneY = event.getSceneY();
		        Node n = (Node)(event.getSource());
		        //System.out.println(n.getId().toString());
		        String id = n.getId();
		        FlxComponent comp = this.processComponentMap.get(id);
		        JsonValue compData = comp.getComponentData();
		        try
		        {
		        	this.drawingAreaReference.addProcess(compData,null,-1);//.addProcess(compData);
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

	/*public GridPane getProcessComponentSideBar()
	{
		return this.processComponentSideBar;
	}*/
	
	public ListView<Group> getProcessComponentSideBar()
	{
		return this.processComponentSideBarListView;
	}
	
	/*public GridPane getControlComponentSideBar()
	{
		return this.controlComponentSideBar;
	}*/

	public ListView<Group> getControlComponentSideBar()
	{
		return this.controlComponentSideBarListView;
	}

	
	private Map<String, FlxComponent> getProcessComponents()// throws IOException
    {
    	Map<String,FlxComponent> tempComponentMap = new HashMap<String,FlxComponent>();
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
            		String processType = processJsonData.get("type").toString().replace("\"", "").toLowerCase();
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
    	
    	return tempComponentMap;
    }

	private Map<String, FlxComponent> getControlComponents()// throws IOException
    {
    	Map<String,FlxComponent> tempControlMap = new HashMap<String,FlxComponent>();
    	    	
    	if(dataAccess.checkCommPortStatus() == true)
    	{
        	try
        	{
            	List<JsonValue> controlComponentArray = dataAccess.getControlComponents();
            	
            	for(int controlComponentIndex = 0; controlComponentIndex < controlComponentArray.size(); controlComponentIndex++)
            	{
            		JsonObject controlJsonData = (JsonObject)(controlComponentArray.get(controlComponentIndex));
            		JsonValue controlSymbolData = controlJsonData.get("symbol");
            		String controlName = controlJsonData.get("name").toString().replace("\"", "").toLowerCase();
            		String controlType = controlJsonData.get("abbr").toString().replace("\"", "").toLowerCase();
            		FlxComponent control = new FlxComponent_Impl(controlJsonData);
            		this.controlComponentMap.put(controlName, control);
            		this.symbolMap.put(controlType, controlSymbolData);
            		this.controlNameArray.add(controlName);
            	}
        	}
        	catch(Exception e)
        	{
        		if(this.errorStatements) System.out.println("error getting control components: " + e);
        	}
    		
    	}
    		
    	return tempControlMap;
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
	
	

}
