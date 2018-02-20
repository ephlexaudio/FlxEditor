package main;

//import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import diagramComponents.FlxCombo;
//import diagramComponents.FlxComponent;
//import diagramComponents.FlxComponent_Impl;
import diagramComponents.FlxControl;
import diagramComponents.FlxControlWire;
import diagramComponents.FlxControl_Impl;
import diagramComponents.FlxCord;
import diagramComponents.FlxEffect;
//import diagramComponents.FlxEffect_Impl;
import diagramComponents.FlxProcess;
import diagramComponents.FlxControlWire_Impl;
import diagramComponents.FlxProcess_Impl;
import diagramComponents.FlxWire;
import diagramComponents.FlxWire_Impl;
import diagramSubComponents.BoundCoord;
import diagramSubComponents.Coord;
import diagramSubComponents.FlxConnector;
import diagramSubComponents.FlxControlParameter;
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
//import diagramSubComponents.FlxSymbol;
import diagramSubComponents.LookUpTable;
import diagramSubComponents.NamedBoundCoord;
//import diagramSubComponents.NamedCoord;
import javafx.event.EventHandler;
//import javafx.event.EventTarget;
//import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
//import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
/*import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;*/


class Effect 
{
	String name;
	String abbr;
	int index;
	Map<String,FlxConnector> effectIO = new HashMap<String,FlxConnector>();
	Map<String,FlxProcess> processMap = new HashMap<String,FlxProcess>();
	Map<String,FlxControl> controlMap = new HashMap<String,FlxControl>();
	Map<String,FlxWire> wireMap = new HashMap<String,FlxWire>();
	Map<String,FlxControlWire> controlWireMap = new HashMap<String,FlxControlWire>();
}


public class FlxDrawingArea_Impl implements FlxDrawingArea {
	boolean debugStatements = true;
	boolean errorStatements = true;
	String selectedBlockId;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    double cursorX, cursorY;
    boolean dragged;
    
    TabPane drawingAreaTabPane = new TabPane();
    Tab comboTab = new Tab();
    ComboEditArea comboEditArea = new ComboEditArea();
	Pane[] drawingAreaCanvas = new Pane[2];
	ScrollPane[] drawingAreaScrollPane = new ScrollPane[2];
	Tab[] drawingAreaTab = new Tab[2];
	//Pane drawingAreaCanvas1 = new Pane();
	//ScrollPane drawingAreaScrollPane1 = new ScrollPane(drawingAreaCanvas1);
	
	
	//Map<String, Integer> processCountMap = new HashMap<String, Integer>();
	int[] controlCount = new int[2];
	FlxMenuBar menuBarReference;
	FlxSidebar sideBarReference;
	FlxParameterEditor parameterEditorReference;
	FlxCombo combo;
	String comboName;
	int activeTab;

	Effect[] effectArray = new Effect[2];
	Map<String,JsonValue> effectConnectionJsonDataMap = new HashMap<String,JsonValue>();
	Map<String,FlxCord> effectConnectionMap = new HashMap<String,FlxCord>();
	FlxCord[] effectConnectionList = new FlxCord[6];
	LookUpTable lut = new LookUpTable();
	NamedBoundCoord prelimProcessWireStartConnection = null;
	NamedBoundCoord prelimProcessWireEndConnection = null;
	NamedBoundCoord prelimControlWireStartConnection = null;
	NamedBoundCoord prelimControlWireEndConnection = null;
	boolean legacyCombo = false;
	
	FlxDrawingArea_Impl()
	{
		try 
		{
			this.comboEditArea.setDrawingAreaParent(this);
			comboTab.setId("combo");
			comboTab.setText("new");
			comboTab.setContent(this.comboEditArea.getComboEditPane());
			comboTab.setClosable(false);
			drawingAreaTabPane.getTabs().add(comboTab);
			for(int i = 0; i < 2; i++)
			{
				this.drawingAreaTab[i] = new Tab();
				this.drawingAreaTab[i].setText("effect" + i);
				this.drawingAreaTab[i].setId("effect_" + i);
				this.effectArray[i] = new Effect();
				this.drawingAreaCanvas[i] = new Pane();
				this.drawingAreaScrollPane[i] = new ScrollPane(drawingAreaCanvas[i]);
				
				this.drawingAreaTab[i].setContent(drawingAreaScrollPane[i]/*new Rectangle(200,200, Color.LIGHTSTEELBLUE)*/);
				this.drawingAreaTab[i].setClosable(false);
				this.drawingAreaTabPane.getTabs().add(drawingAreaTab[i]);
				this.drawingAreaCanvas[i].setMaxSize(1250, 550);
				this.drawingAreaCanvas[i].setMinSize(1249, 549);
				this.drawingAreaCanvas[i].setId("drawingArea");
				this.drawingAreaScrollPane[i].setMaxSize(1150, 450);
				this.drawingAreaScrollPane[i].setMinSize(1149, 449);
				this.drawingAreaScrollPane[i].setHbarPolicy(ScrollBarPolicy.ALWAYS);
				this.drawingAreaScrollPane[i].setVbarPolicy(ScrollBarPolicy.ALWAYS);
				
				this.drawingAreaCanvas[i].setOnMouseClicked(event -> 
				{
					if(event.getTarget().toString().indexOf("Pane") >= 0)
					{
						if(this.debugStatements) System.out.println("clicked on canvas");
						
						clearAllSelectIndicators();
					}			
				});						
				this.drawingAreaTabPane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent> () {
				    @Override
				    public void handle(MouseEvent mouseEvent) {
				        //EventTarget eventTarget = mouseEvent.getTarget();
				        //Node eventTargetNode = (Node)mouseEvent.getSource();
				        //if(drawingAreaTabPane.getSelectionModel().getSelectedIndex())
				        {
					        String id = drawingAreaTabPane.getSelectionModel().getSelectedItem().getId();
					    	//if(mouseEvent.getClickCount() == 2)
					    	{
					    		if(id != null)
					    		{
							        if(id.indexOf("_") >= 0)
							        {
								        activeTab = Integer.parseInt(id.split("_")[1]);
								        if(debugStatements) System.out.println("clicked on effect: " + activeTab);
							        }				    		
							        else
							        {
							        	/*comboEditArea.setNames(comboName,effectArray[0].name,effectArray[0].abbr,
							        			effectArray[1].name,effectArray[1].abbr);*/
							        	if(debugStatements) System.out.println("clicked on combo");
							        }
							        parameterEditorReference.clearEditor();
					    		}
					    	}
				        }
				    }
				});
			}
			

			this.dragged = false;
			this.controlCount[0] = 0;
			this.controlCount[1] = 0;
			
		} catch (Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea error: " + e);
		}
	}
	
	/********************************************************************************************
	 ******************************************************************************************** 
	 ********************************************************************************************/

	public String getCurrentCombo()
	{
		return this.comboName;
	}
	public void setNames(String comboName, String effect0Name, String effect0Abbr, String effect1Name, String effect1Abbr)
	{
		this.comboName = comboName;
		this.comboTab.setText(this.comboName);
		
		this.effectArray[0].name = effect0Name;
		this.drawingAreaTab[0].setText(this.effectArray[0].name);
		this.effectArray[0].abbr = effect0Abbr;
		
		
		this.effectArray[1].name = effect1Name;		
		this.drawingAreaTab[1].setText(this.effectArray[1].name);
		this.effectArray[1].abbr = effect1Abbr;
		
	}
	
	public void updateComboName(String comboName)
	{
		this.comboName = comboName;
		this.comboTab.setText(this.comboName);		
	}
	
	public void updateEffectNameAbbr(String oldEffectName, String newEffectName, String effectAbbr)
	{
		/*for(String cordKey : this.effectConnectionMap.keySet())
		{
			if(cordKey.contains(oldEffectName))
			{
				this.effectConnectionMap.get(cordKey).updateEffectName(effectName);
			}
		}*/
		
		for(int i = 0; i < 2; i++)
		{
			if(this.effectArray[i].name.compareTo(oldEffectName) == 0)
			{
				this.effectArray[i].name = newEffectName;		
				this.drawingAreaTab[i].setText(newEffectName);
				this.effectArray[i].abbr = effectAbbr;				
			}
		}
	
	}

	public void setEditor(FlxParameterEditor_Impl paramEditor)
	{
		this.parameterEditorReference = paramEditor;
	}
	
	public void setSidebar(FlxSidebar sideBar)
	{
		this.sideBarReference = sideBar;
	}
	
	public void createProcessCountMap()
	{
		/*try
		{
			for(String component : this.sideBarReference.getProcessList())
			{
				//this.processCountMap.put(component.replace("\"", "").toLowerCase(), 0);
			}
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println(e);
		}*/
		
	}
	
	public TabPane getDrawingArea()
	{
		return this.drawingAreaTabPane;//this.drawingAreaScrollPane;
	}
		
	/******************************* ADD/DELETE DRAWING AREA ITEMS ********************************/

	public void addEffectIO(int index)
	{
		
		this.effectArray[index].effectIO = createEffectIO(index);			

		for(String effectIoKey : this.effectArray[index].effectIO.keySet())
		{
			this.effectArray[index].effectIO.get(effectIoKey).getConnector().setOnMouseClicked(event -> this.connectorClicked(event));
			//this.effectIO.get(effectIoKey).getConnector().setOnMousePressed(event -> this.connectorSelected(event));
			this.drawingAreaCanvas[index].getChildren().add(this.effectArray[index].effectIO.get(effectIoKey).getConnector());			
		}
	}
	
	public FlxProcess addProcess(JsonValue componentData, String parentEffectName, int index)
	{
		if(index < 0) index = this.activeTab;
		
		FlxProcess newProcess = null;
		if(parentEffectName == null)
		{
			parentEffectName = "effect0";
		}
		
		try
		{
			String compName = ((JsonObject)componentData).getString("name").toLowerCase();//newProcess.getName();
			String procName = null;
			
			if(compName.contains("_"))
			{
				procName = compName;
				compName = compName.split("_")[0];
			}
			else
			{
				for(int procNameIndex = 0; procNameIndex < 50; procNameIndex++)
				{
					procName = compName.toLowerCase() + "_" + procNameIndex;
					if((this.effectArray[0].processMap.containsKey(procName) == false) &&
							(this.effectArray[1].processMap.containsKey(procName) == false)) break;
				}
			}
			
			newProcess = new FlxProcess_Impl(procName, componentData, parentEffectName);
			
			
			////int compCount = this.processCountMap.get(compName);
			newProcess.setName(procName);
			JsonArray paramArray = (JsonArray)newProcess.getParamArrayData();
			String infoString = new String();
			
			for(int i = 0; i < paramArray.size()-1; i++)
			{
				
				infoString += lut.getParameterValueString(
						((JsonObject)paramArray.get(i)).getInt("type"), 
						((JsonObject)paramArray.get(i)).getInt("value")
						) + ",";
			}
			infoString += lut.getParameterValueString(
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("type"), 
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("value")
					);
			newProcess.setInfo(infoString);
			
			this.effectArray[index].processMap.put(procName, newProcess);
			newProcess.getBlock(/*0,0*/).setOnMouseClicked(event -> this.processClicked(event));
			newProcess.getBlock(/*0,0*/).setOnMousePressed(event -> this.processSelected(event));
			newProcess.getBlock(/*0,0*/).setOnMouseDragged(event -> this.processDragged(event));
			
			this.drawingAreaCanvas[index].getChildren().add(newProcess.getBlock(/*0,0*/));
			
			//this.processCountMap.put(compName, ++compCount);
//			for(String name : this.processCountMap.keySet())
//			{
//				System.out.println("process: " + name +"\tcount: " + this.processCountMap.get(name));
//			}
		} 
		catch (Exception e) 
		{
			
			if(this.errorStatements) System.out.println("FlxDrawingArea addProcess error: " + e);	
		}
		
		return newProcess;
	}

	public FlxProcess addProcess(FlxProcess process, String parentEffectName, int index)
	{
		if(index < 0) index = this.activeTab;
		
//		FlxProcess newProcess = null;
		if(parentEffectName == null)
		{
			parentEffectName = "effect0";
		}
		
		try
		{
			String compName = process.getName();//((JsonObject)componentData).getString("name").toLowerCase();//newProcess.getName();
			String procName = null;
			
			if(compName.contains("_"))
			{
				procName = compName;
				compName = compName.split("_")[0];
			}
			else
			{
				for(int procNameIndex = 0; procNameIndex < 50; procNameIndex++)
				{
					procName = compName.toLowerCase() + "_" + procNameIndex;
					if((this.effectArray[0].processMap.containsKey(procName) == false) &&
							(this.effectArray[1].processMap.containsKey(procName) == false)) break;
				}
			}
			
			//newProcess = new FlxProcess_Impl(procName, componentData, parentEffectName);
			
			
			////int compCount = this.processCountMap.get(compName);
			//newProcess.setName(procName);
			//JsonArray paramArray = (JsonArray)process.getParamArrayData();//(JsonArray)newProcess.getParamArrayData();
			List<FlxParameter> paramArray = process.getParamList();
			int paramArraySize = paramArray.size();
			String infoString = new String();
			
			for(int i = 0; i < paramArraySize-1; i++)
			{
				
				infoString += lut.getParameterValueString(
						paramArray.get(i).getParameterUnitType()/*((JsonObject)paramArray.get(i)).getInt("type")*/, 
						paramArray.get(i).getParamValueIndex()/*((JsonObject)paramArray.get(i)).getInt("value")*/
						) + ",";
			}
			infoString += lut.getParameterValueString(
					paramArray.get(paramArraySize-1).getParameterUnitType()/*((JsonObject)paramArray.get(paramArray.size()-1)).getInt("type")*/, 
					paramArray.get(paramArraySize-1).getParamValueIndex()/*((JsonObject)paramArray.get(paramArray.size()-1)).getInt("value")*/
					);
			process.setInfo(infoString);
			
			this.effectArray[index].processMap.put(procName, process);
			process.getBlock(/*0,0*/).setOnMouseClicked(event -> this.processClicked(event));
			process.getBlock(/*0,0*/).setOnMousePressed(event -> this.processSelected(event));
			process.getBlock(/*0,0*/).setOnMouseDragged(event -> this.processDragged(event));
			
			this.drawingAreaCanvas[index].getChildren().add(process.getBlock(/*0,0*/));
			
			//this.processCountMap.put(compName, ++compCount);
//			for(String name : this.processCountMap.keySet())
//			{
//				System.out.println("process: " + name +"\tcount: " + this.processCountMap.get(name));
//			}
		} 
		catch (Exception e) 
		{
			
			if(this.errorStatements) System.out.println("FlxDrawingArea addProcess error: " + e);	
		}
		
		return process;
	}

	private void deleteProcess(String processName, int index) {
		
		try
		{
//			String processType = this.effectArray[index].processMap.get(processName).getType();
			//int compCount = this.processCountMap.get(processType);
			//this.processCountMap.put(processType, --compCount);
			/*for(String name : this.processCountMap.keySet())
			{
				//System.out.println("process: " + name +"\tcount: " + this.processCountMap.get(name));
			}*/
			
			// Delete all attached wires

			for(Iterator<Map.Entry<String, FlxWire>> it = this.effectArray[index].wireMap.entrySet().iterator(); it.hasNext(); ) 
			{
				Map.Entry<String, FlxWire> entry = it.next();
				if(entry.getKey().contains(processName)) 
				{
					if(this.debugStatements) System.out.println("deleting: " + entry.getKey());
					this.effectArray[index].wireMap.get(entry.getKey()).deleteWireSymbol();
					it.remove();
				}
			}
			
			for(Iterator<Map.Entry<String, FlxControlWire>> it = this.effectArray[index].controlWireMap.entrySet().iterator(); it.hasNext(); ) 
			{
				Map.Entry<String, FlxControlWire> entry = it.next();
				if(entry.getKey().contains(processName)) 
				{
					if(this.debugStatements) System.out.println("deleting: " + entry.getKey());
					this.effectArray[index].controlWireMap.get(entry.getKey()).deleteWireSymbol();
					it.remove();
				}
			}
			if(this.debugStatements) 
			{
				System.out.println("Remaining wires...");
				for(String wireKey : this.effectArray[index].wireMap.keySet())
				{
					System.out.println(this.effectArray[index].wireMap.get(wireKey).getWireName().toString());
				}				
			}
			
			this.effectArray[index].processMap.get(processName).deleteSymbol();
			this.effectArray[index].processMap.remove(processName);
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea::deleteProcess error: " + e);
		}
	}

	
	private FlxWire addWire(String wireName, BoundCoord src, BoundCoord dest, String parentEffectName, int index)
	{
		FlxWire wire = new FlxWire_Impl(wireName, src, dest, parentEffectName);
		
		try 
		{
			wire.getWire().setId(wireName);
			wire.getWire().setOnMouseClicked(event -> {
				if((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
				{
					deleteWire(wireName,index);
				}
			});

			this.effectArray[index].wireMap.put(wireName, wire);
			Group wireGroup = wire.getWire();
			this.drawingAreaCanvas[index].getChildren().add(wireGroup);
			
		}
		catch(Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addWire error: " + e);	
		}

		return wire;
		
	}
	
	
	/*private FlxWire addWire(JsonValue wireData, String parentEffectName, int index)
	{
		FlxWire wire = null;//new FlxWire_Impl(wireName, src, dest);
		
		try 
		{
			JsonObject wireDataObject = (JsonObject)wireData;
			wire = new FlxWire_Impl(wireDataObject, parentEffectName);
			if(wire.isWireLegacy()) 
			{
				this.legacyCombo = true;
			}
			String wireName = wire.getWireName();
			
			wire.getWire().setOnMouseClicked(event -> {
				if((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
				{
					deleteWire(wireName,index);
				}
			});

			this.effectArray[index].wireMap.put(wireName, wire);
			this.drawingAreaCanvas[index].getChildren().add(wire.getWire());
			
		}
		catch(Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addWire error: " + e);	
		}

		return wire;
	}*/

	private FlxWire addWire(FlxWire wire, String parentEffectName, int index)
	{
		//FlxWire wire = null;//new FlxWire_Impl(wireName, src, dest);
		
		try 
		{
			//JsonObject wireDataObject = (JsonObject)wireData;
			//wire = new FlxWire_Impl(wireDataObject, parentEffectName);
			if(wire.isWireLegacy()) 
			{
				this.legacyCombo = true;
			}
			String wireName = wire.getWireName();
			
			wire.getWire().setOnMouseClicked(event -> {
				if((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
				{
					deleteWire(wireName,index);
				}
			});

			this.effectArray[index].wireMap.put(wireName, wire);
			this.drawingAreaCanvas[index].getChildren().add(wire.getWire());
			
		}
		catch(Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addWire error: " + e);	
		}

		return wire;
	}

	
	private void deleteWire(String wireName, int index)
	{
		if(this.debugStatements) System.out.println("delete wire: " + wireName);
		this.effectArray[index].wireMap.get(wireName).deleteWireSymbol();
		this.effectArray[index].wireMap.remove(wireName);		
	}
	
	
	public FlxControl addControl(JsonValue compData, String parentEffectName, int index) 
	{
		if(index < 0) index = this.activeTab;
		FlxControl newControl = null;
		if(parentEffectName == null)
		{
			parentEffectName = "effect0";
		}
		try
		{			
			String controlName = ((JsonObject)compData).getString("name").toLowerCase();//newControl.getName();
			//int controlIndex = 0;
			
			if(controlName.contains("_"))
			{
				//controlIndex = newControl.getIndex();
			}
			else
			{
				for(int conNameIndex = 0; conNameIndex < 50; conNameIndex++)
				{
					controlName = "control_" + conNameIndex;
					//controlIndex = conNameIndex;
					if((this.effectArray[0].controlMap.containsKey(controlName) == false) &&
							(this.effectArray[1].controlMap.containsKey(controlName) == false)) 
					{					
						//newControl.setName(controlName);
						//newControl.setIndex(controlIndex);
						break;
					}
				}
			}
			newControl = new FlxControl_Impl(controlName, compData, parentEffectName);
			
			this.controlCount[index]++;
			//System.out.println("control name: " + controlName);
			JsonArray paramArray = (JsonArray)newControl.getParamArrayData();
			String infoString = new String();
			for(int i = 0; i < paramArray.size()-1; i++)
			{
				
				infoString += lut.getParameterValueString(
						((JsonObject)paramArray.get(i)).getInt("type"), 
						((JsonObject)paramArray.get(i)).getInt("value")
						) + ",";
			}
			infoString += lut.getParameterValueString(
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("type"), 
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("value")
					);
			newControl.setInfo(infoString);
			
			
			
			
			this.effectArray[index].controlMap.put(controlName, newControl);
			newControl.getBlock(/*0, 0*/).setOnMouseClicked(event -> this.controlClicked(event));
			newControl.getBlock(/*0, 0*/).setOnMousePressed(event -> this.controlSelected(event));
			newControl.getBlock(/*0, 0*/).setOnMouseDragged(event -> this.controlDragged(event));
			
			this.drawingAreaCanvas[index].getChildren().add(newControl.getBlock(/*0, 0*/));
			
		} catch (Exception e) {
			
			if(this.errorStatements) System.out.println("FlxDrawingArea addControl error: " + e);
		}
		return newControl;
	}

	public FlxControl addControl(FlxControl control, String parentEffectName, int index) 
	{
		if(index < 0) index = this.activeTab;
		//FlxControl newControl = null;
		if(parentEffectName == null)
		{
			parentEffectName = "effect0";
		}
		try
		{			
			String controlName = control.getName();//((JsonObject)compData).getString("name").toLowerCase();//newControl.getName();
			//int controlIndex = 0;
			
			if(controlName.contains("_"))
			{
				//controlIndex = newControl.getIndex();
			}
			else
			{
				for(int conNameIndex = 0; conNameIndex < 50; conNameIndex++)
				{
					controlName = "control_" + conNameIndex;
					//controlIndex = conNameIndex;
					if((this.effectArray[0].controlMap.containsKey(controlName) == false) &&
							(this.effectArray[1].controlMap.containsKey(controlName) == false)) 
					{					
						//newControl.setName(controlName);
						//newControl.setIndex(controlIndex);
						break;
					}
				}
			}
			//newControl = new FlxControl_Impl(controlName, compData, parentEffectName);
			
			this.controlCount[index]++;
			//System.out.println("control name: " + controlName);
			//JsonArray paramArray = (JsonArray)newControl.getParamArrayData();
			List<FlxControlParameter> conParamArray = control.getParamList();
			int conParamArraySize = conParamArray.size();
			String infoString = new String();
			for(int i = 0; i < conParamArraySize-1; i++)
			{
				
				infoString += lut.getParameterValueString(
						conParamArray.get(i).getParameterUnitType()/*((JsonObject)paramArray.get(i)).getInt("type")*/, 
						conParamArray.get(i).getParamValueIndex()/*((JsonObject)paramArray.get(i)).getInt("value")*/
						) + ",";
			}
			infoString += lut.getParameterValueString(
					conParamArray.get(conParamArraySize-1).getParameterUnitType()/*((JsonObject)paramArray.get(paramArray.size()-1)).getInt("type")*/, 
					conParamArray.get(conParamArraySize-1).getParamValueIndex()/*((JsonObject)paramArray.get(paramArray.size()-1)).getInt("value")*/
					);
			control.setInfo(infoString);
			
			
			
			
			this.effectArray[index].controlMap.put(controlName, control);
			control.getBlock(/*0, 0*/).setOnMouseClicked(event -> this.controlClicked(event));
			control.getBlock(/*0, 0*/).setOnMousePressed(event -> this.controlSelected(event));
			control.getBlock(/*0, 0*/).setOnMouseDragged(event -> this.controlDragged(event));
			
			this.drawingAreaCanvas[index].getChildren().add(control.getBlock(/*0, 0*/));
			
		} catch (Exception e) {
			
			if(this.errorStatements) System.out.println("FlxDrawingArea addControl error: " + e);
		}
		return control;
	}

	private void deleteControl(String controlName, int index) {
		//System.out.println("Deleting process: " + controlName);
		try
		{
			this.controlCount[index]--;
			this.effectArray[index].controlMap.get(controlName).deleteSymbol();
			this.effectArray[index].controlMap.remove(controlName);
			
			// Delete control wire
			for(Iterator<Map.Entry<String, FlxControlWire>> it = this.effectArray[index].controlWireMap.entrySet().iterator(); it.hasNext(); ) 
			{
				Map.Entry<String, FlxControlWire> entry = it.next();
				if(entry.getKey().contains(controlName)) 
				{
					if(this.debugStatements) System.out.println("deleting: " + entry.getKey());
					this.effectArray[index].controlWireMap.get(entry.getKey()).deleteWireSymbol();
					it.remove();
				}
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea::deleteControl error: " + e);
		}
		
	}

	
	private FlxControlWire addControlWire(String controlWireName, BoundCoord src, BoundCoord dest,
			String parentEffectName, int index)
	{
		FlxControlWire controlWire = new FlxControlWire_Impl(controlWireName, src, dest, parentEffectName);
		
		try 
		{
			controlWire.getWire().setId(controlWireName);
			controlWire.getWire().setOnMouseClicked(event -> {
				if((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
				{
					deleteControlWire(controlWireName,index);
				}
			});

			this.effectArray[index].controlWireMap.put(controlWireName, controlWire);
			this.drawingAreaCanvas[index].getChildren().add(controlWire.getWire());
			
		}
		catch(Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addControlWire error: " + e);	
		}

		return controlWire;
		
		
	}

	/*private FlxControlWire addControlWire(JsonValue controlWireData, String parentEffectName, int index)
	{
		FlxControlWire controlWire = new FlxControlWire_Impl(controlWireData, parentEffectName);
		
		try 
		{
			String controlWireName = controlWire.getWireName();
			controlWire.getWire().setId(controlWireName);
			controlWire.getWire().setOnMouseClicked(event -> {
				if((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
				{
					deleteControlWire(controlWireName,index);
				}
			});

			this.effectArray[index].controlWireMap.put(controlWireName, controlWire);
			this.drawingAreaCanvas[index].getChildren().add(controlWire.getWire());
			
		}
		catch(Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addControlWire error: " + e);	
		}

		return controlWire;
		
		
	}*/
	
	private FlxControlWire addControlWire(FlxControlWire controlWire, String parentEffectName, int index)
	{
		//FlxControlWire controlWire = new FlxControlWire_Impl(controlWireData, parentEffectName);
		
		try 
		{
			String controlWireName = controlWire.getWireName();
			controlWire.getWire().setId(controlWireName);
			controlWire.getWire().setOnMouseClicked(event -> {
				if((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
				{
					deleteControlWire(controlWireName,index);
				}
			});

			this.effectArray[index].controlWireMap.put(controlWireName, controlWire);
			this.drawingAreaCanvas[index].getChildren().add(controlWire.getWire());
			
		}
		catch(Exception e) 
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addControlWire error: " + e);	
		}

		return controlWire;
		
		
	}


	private void deleteControlWire(String controlWireName, int index)
	{
		if(this.debugStatements) System.out.println("delete control wire: " + controlWireName);
		this.effectArray[index].controlWireMap.get(controlWireName).deleteWireSymbol();
		this.effectArray[index].controlWireMap.remove(controlWireName);				
	}
	

	/******************************* MANIPULATE DRAWING AREA ITEMS ********************************/


	private void flipProcess(String processName, int index) {
		
		try
		{
			FlxProcess proc = this.effectArray[index].processMap.get(processName);
			proc.flipProcess();
			
			//  Update wire connections
	        List<String> inputList = this.effectArray[index].processMap.get(processName).getInputConnectorList();
	        
	        for(int inputConnIndex = 0; inputConnIndex < inputList.size(); inputConnIndex++)
	        {
	        	String inputConnName = inputList.get(inputConnIndex);
	        	String procInputName = this.effectArray[index].processMap.get(processName).getName() + ":" + inputConnName;
	        	Coord inputCoord = this.effectArray[index].processMap.get(processName).getInputConnectorCoord(inputConnName);
	        	
		        if(this.effectArray[index].wireMap.isEmpty() == false)
		        {
		        	for(String wireKey : this.effectArray[index].wireMap.keySet())
		        	{
		        		String wireEndConnection = wireKey.split(">")[1];
		        		if(procInputName.compareTo(wireEndConnection) == 0)
		        		{
		        			this.effectArray[index].wireMap.get(wireKey).setDestConnection(inputCoord);	        			
		        		}
		        	}
		        }
	        }
	        
	        List<String> outputList = this.effectArray[index].processMap.get(processName).getOutputConnectorList();
		    for(int outputConnIndex = 0; outputConnIndex < outputList.size(); outputConnIndex++)
	        {
		        
	        	String outputConnName = outputList.get(outputConnIndex);	        	
	        	String procOutputName = this.effectArray[index].processMap.get(processName).getName() + ":" + outputConnName;
	        	Coord outputCoord = this.effectArray[index].processMap.get(processName).getOutputConnectorCoord(outputConnName);
	        	//System.out.println("outputCoord: " + outputCoord.getX() + "," + outputCoord.getY() );
		        if(this.effectArray[index].wireMap.isEmpty() == false)
		        {		        
		        	for(String wireKey : this.effectArray[index].wireMap.keySet())
		        	{			        
		        		String wireStartConnection = wireKey.split(">")[0];
		        		if(procOutputName.compareTo(wireStartConnection) == 0)
		        		{
		        			this.effectArray[index].wireMap.get(wireKey).setSourceConnection(outputCoord);
		        		}
		        	}
		        }
	        }

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea::flipProcess error: " + e);
		}
	}
	
	/*************************** EFFECTIO CONNECTOR MOUSE EVENTS **********************************/
	
	
	/*private void connectorSelected(MouseEvent event) 
	{
        this.orgSceneX = event.getSceneX();
        this.orgSceneY = event.getSceneY();
		this.orgTranslateX = ((Group)(event.getSource())).getTranslateX();
		this.orgTranslateY = ((Group)(event.getSource())).getTranslateY();
		this.dragged = false;
	}*/

	
	private void connectorClicked(MouseEvent event) 
	{
		int index = this.activeTab;
		//Node connectorNode = (Node)event.getSource();
		//System.out.println("connectorNode: " + connectorNode.toString());
		//System.out.println("connectorNodeId: " + connectorNode.getId().toString());
		String targetNodeId = null;
        this.orgSceneX = event.getSceneX();
        this.orgSceneY = event.getSceneY();
		this.orgTranslateX = ((Circle)(event.getSource())).getTranslateX();
		this.orgTranslateY = ((Circle)(event.getSource())).getTranslateY();

		try
		{
			Node targetNode = (Node)event.getTarget();
			if(targetNode.getId() != null)
			{
				targetNodeId = targetNode.getId().toString();
				//System.out.println("targetNode: " + targetNode.toString());
				//System.out.println("targetNodeId: " + targetNodeId);
			}		
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("connectorClicked target node error: " + e);
		}
		
		//String effectIoName = targetNodeId.split(":")[0];
		String connName = targetNodeId.split(":")[1];
		String connectorType = this.effectArray[index].effectIO.get(connName).getType();
		//System.out.println("connector clicked: " + effectIoName + ":" + connName + "\ttype: "+ connectorType);
		Coord connectorCoord = null; 
		
		try
		{
			connectorCoord = this.effectArray[index].effectIO.get(connName).getConnectorLocalLocation();
			if(connectorType.compareTo("input") == 0)
			{
				//System.out.println("input connector clicked1");
				this.prelimProcessWireStartConnection = new NamedBoundCoord(targetNodeId);
				this.prelimProcessWireStartConnection.setCoord(connectorCoord);
			}
			else if(connectorType.compareTo("output") == 0)
			{
				//System.out.println("output connector clicked1");
				this.prelimProcessWireEndConnection = new NamedBoundCoord(targetNodeId);
				this.prelimProcessWireEndConnection.setCoord(connectorCoord);
			}
			
			if(this.prelimProcessWireStartConnection != null && this.prelimProcessWireEndConnection != null)
			{
				String wireName = this.prelimProcessWireStartConnection.getName() + ">" +
						this.prelimProcessWireEndConnection.getName();
				if(this.debugStatements) System.out.println("wire entered into wireMap1: " + wireName);
				this.addWire(wireName, this.prelimProcessWireStartConnection.getCoord(), 
						this.prelimProcessWireEndConnection.getCoord(),"effect0",index);
				//this.addWire(wireName, srcX, srcY, destX, destY);
				if(this.debugStatements) System.out.println("wire entered into wireMap2: " + wireName);
				this.prelimProcessWireStartConnection = null;
				this.prelimProcessWireEndConnection = null;
				
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("connecting processes error: " + e);
		}							
	}


	/*************************** PROCESS MOUSE EVENTS **********************************/
	
	private void processSelected(MouseEvent event) {
		
        this.orgSceneX = event.getSceneX();
        this.orgSceneY = event.getSceneY();
		this.orgTranslateX = ((Group)(event.getSource())).getTranslateX();
		this.orgTranslateY = ((Group)(event.getSource())).getTranslateY();
		this.dragged = false;
	}
	
	private void processDragged(MouseEvent event)
	{
		int index = this.activeTab;
		try
		{
	        double offsetX = event.getSceneX() - this.orgSceneX;
	        double offsetY = event.getSceneY() - this.orgSceneY;
	        double newTranslateX = this.orgTranslateX + offsetX;
	        double newTranslateY = this.orgTranslateY + offsetY;
	        ((Group)(event.getSource())).setTranslateX(newTranslateX);
	        ((Group)(event.getSource())).setTranslateY(newTranslateY);
	        String id = ((Group)event.getSource()).getId();
	        this.effectArray[index].processMap.get(id).setLocation(newTranslateX, newTranslateY);
	        //this.processMap.get(id).printConnectionCoords();
	        if(this.debugStatements) System.out.println("processDragged setLocation: " + newTranslateX + "," + newTranslateY);
	        
	        List<String> inputList = this.effectArray[index].processMap.get(id).getInputConnectorList();
	        
	        for(int inputConnIndex = 0; inputConnIndex < inputList.size(); inputConnIndex++)
	        {
	        	String inputConnName = inputList.get(inputConnIndex);
	        	String procInputName = this.effectArray[index].processMap.get(id).getName() + ":" + inputConnName;
	        	Coord inputCoord = this.effectArray[index].processMap.get(id).getInputConnectorCoord(inputConnName);
	        	
		        if(this.effectArray[index].wireMap.isEmpty() == false)
		        {
		        	for(String wireKey : this.effectArray[index].wireMap.keySet())
		        	{
		        		String wireEndConnection = wireKey.split(">")[1];
		        		if(procInputName.compareTo(wireEndConnection) == 0)
		        		{
		        			this.effectArray[index].wireMap.get(wireKey).setDestConnection(inputCoord);	        			
		        		}
		        	}
		        }
	        }
	        
	        List<String> outputList = this.effectArray[index].processMap.get(id).getOutputConnectorList();
		    for(int outputConnIndex = 0; outputConnIndex < outputList.size(); outputConnIndex++)
	        {
		        
	        	String outputConnName = outputList.get(outputConnIndex);	        	
	        	String procOutputName = this.effectArray[index].processMap.get(id).getName() + ":" + outputConnName;
	        	Coord outputCoord = this.effectArray[index].processMap.get(id).getOutputConnectorCoord(outputConnName);
	        	if(this.debugStatements) System.out.println("outputCoord: " + outputCoord.getX() + "," + outputCoord.getY() );
		        if(this.effectArray[index].wireMap.isEmpty() == false)
		        {		        
		        	for(String wireKey : this.effectArray[index].wireMap.keySet())
		        	{			        
		        		String wireStartConnection = wireKey.split(">")[0];
		        		if(procOutputName.compareTo(wireStartConnection) == 0)
		        		{
		        			this.effectArray[index].wireMap.get(wireKey).setSourceConnection(outputCoord);
		        		}
		        	}
		        }
	        }

	        List<String> paramList = this.effectArray[index].processMap.get(id).getParamConnectorList();
		    for(int paramConnIndex = 0; paramConnIndex < paramList.size(); paramConnIndex++)
	        {
	        	String paramConnName = paramList.get(paramConnIndex);	        	
	        	String procParamName = this.effectArray[index].processMap.get(id).getName() + ":" + paramConnName;
	        	Coord paramCoord = this.effectArray[index].processMap.get(id).getParamConnectorCoord(paramConnName);
	        	this.effectArray[index].controlWireMap.toString();
		        if(this.effectArray[index].controlWireMap.isEmpty() == false)
		        {
		        	for(String controlWireKey : this.effectArray[index].controlWireMap.keySet())
		        	{
		        		String wireEndConnection = controlWireKey.split(">")[1];
		        		if(procParamName.compareTo(wireEndConnection) == 0)
		        		{
		        			this.effectArray[index].controlWireMap.get(controlWireKey).setDestConnection(paramCoord);
		        		}
		        	}
		        }
	        }
		}
		
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("processDragged error: " + e);
		}
		this.dragged = true;
	}

	private void processClicked(MouseEvent event) {
		
		int index = this.activeTab;
		Node processNode = (Node)event.getSource();
		String targetNodeId = null;

		try
		{
			Node targetNode = (Node)event.getTarget();
			if(targetNode.getId() != null)
			{
				targetNodeId = targetNode.getId().toString();
			}		
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("processClicked target node error: " + e);
		}
		
		String id = processNode.getId();
		
		this.selectedBlockId = id;
		//System.out.println("this.selectedBlockId: " + id);
		//int clickCount = event.getClickCount();
		if(event.getButton() == MouseButton.PRIMARY)
		{
			String clickedShape = event.getTarget().getClass().getSimpleName().toString();
			//String connectorType;
			if(clickedShape.indexOf("Circle")>=0)
			{
				String procName = targetNodeId.split(":")[0];
				String connName = targetNodeId.split(":")[1];
				if(this.debugStatements) System.out.println("connector clicked: " + procName + ":" + connName);
				Coord connectorCoord = null; 
				
				try
				{
					if(this.effectArray[index].processMap.containsKey(id))
					{
						if(this.effectArray[index].processMap.get(id).getInputConnectorList().contains(connName)) 
						{
							this.prelimProcessWireEndConnection = new NamedBoundCoord(targetNodeId);
							connectorCoord = this.effectArray[index].processMap.get(id).getInputConnectorCoord(connName);
							this.prelimProcessWireEndConnection.setCoord(connectorCoord);
							
						}
						if(this.effectArray[index].processMap.get(id).getOutputConnectorList().contains(connName))
						{
							this.prelimProcessWireStartConnection = new NamedBoundCoord(targetNodeId);
							connectorCoord = this.effectArray[index].processMap.get(id).getOutputConnectorCoord(connName);
							this.prelimProcessWireStartConnection.setCoord(connectorCoord);
						}
						if(this.effectArray[index].processMap.get(id).getParamConnectorList().contains(connName))
						{
							this.prelimControlWireEndConnection = new NamedBoundCoord(targetNodeId);
							connectorCoord = this.effectArray[index].processMap.get(id).getParamConnectorCoord(connName);
							this.prelimControlWireEndConnection.setCoord(connectorCoord);
						}			
					}
					
					if((this.prelimProcessWireStartConnection != null && this.prelimProcessWireEndConnection != null) ||
							(this.prelimControlWireStartConnection != null && this.prelimControlWireEndConnection != null))
					{					
						if(this.prelimProcessWireStartConnection != null && this.prelimProcessWireEndConnection != null)
						{
							String wireName = this.prelimProcessWireStartConnection.getName() + ">" +
									this.prelimProcessWireEndConnection.getName();
							this.addWire(wireName, this.prelimProcessWireStartConnection.getCoord(),
									this.prelimProcessWireEndConnection.getCoord(),"effect0",index);
							this.prelimProcessWireStartConnection = null;
							this.prelimProcessWireEndConnection = null;
							
						}
						else if(this.prelimControlWireStartConnection != null && this.prelimControlWireEndConnection != null)
						{
							String controlWireName = this.prelimControlWireStartConnection.getName() + ">" +
									this.prelimControlWireEndConnection.getName();
							this.addControlWire(controlWireName, this.prelimControlWireStartConnection.getCoord(), 
									this.prelimControlWireEndConnection.getCoord(),"effect0",index);
							this.prelimControlWireStartConnection = null;
							this.prelimControlWireEndConnection = null;
							
						}
					}
					
					//double x = connectorCoord.getX() + this.effectArray[index].processMap.get(id).getProcessCoords().getX();
					//double y = connectorCoord.getY() + this.effectArray[index].processMap.get(id).getProcessCoords().getY();
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("connecting processes error: " + e);
				}							
			}
			else if(this.dragged == false)
			{
				clearAllSelectIndicators();
				
				this.effectArray[index].processMap.get(id).setSelectIndicator(true);	
				this.editProcess(this.effectArray[index].processMap.get(id));
				this.prelimProcessWireStartConnection = null;
				this.prelimProcessWireEndConnection = null;
				this.prelimControlWireStartConnection = null;
				this.prelimControlWireEndConnection = null;
			}
		}
		else if(event.getButton() == MouseButton.SECONDARY)
		{
			ContextMenu processContextMenu = new ContextMenu();
			MenuItem flipProcessItem = new MenuItem("Flip");
			flipProcessItem.setOnAction(evt->
			{
				try
				{
					this.flipProcess(this.selectedBlockId, index);
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("FlxDrawingArea::processClicked error: " + e);
				}
				
			});
			MenuItem deleteProcessItem = new MenuItem("Delete");
			deleteProcessItem.setOnAction(evt->this.deleteProcess(this.selectedBlockId,index));
			processContextMenu.getItems().add(flipProcessItem);
			processContextMenu.getItems().add(deleteProcessItem);
			processContextMenu.show(processNode, event.getScreenX(), event.getScreenY());
		}
		
	}

	/*************************** CONTROL MOUSE EVENTS **********************************/
	
	private void controlSelected(MouseEvent event) {
		
        this.orgSceneX = event.getSceneX();
        this.orgSceneY = event.getSceneY();
		this.orgTranslateX = ((Group)(event.getSource())).getTranslateX();
		this.orgTranslateY = ((Group)(event.getSource())).getTranslateY();
		this.dragged = false;
	}

	
	private void controlDragged(MouseEvent event)
	{
		int index = this.activeTab;
		try
		{
	        double offsetX = event.getSceneX() - this.orgSceneX;
	        double offsetY = event.getSceneY() - this.orgSceneY;
	        double newTranslateX = this.orgTranslateX + offsetX;
	        double newTranslateY = this.orgTranslateY + offsetY;
	        ((Group)(event.getSource())).setTranslateX(newTranslateX);
	        ((Group)(event.getSource())).setTranslateY(newTranslateY);
	        String id = ((Group)event.getSource()).getId();
	        this.effectArray[index].controlMap.get(id).setLocation(newTranslateX, newTranslateY);
	        
	        List<String> outputList = this.effectArray[index].controlMap.get(id).getOutputConnectorList();
	        //for(String outputKey : this.controlMap.get(id).getOutputConnectorList())
		    for(int outputConnIndex = 0; outputConnIndex < outputList.size(); outputConnIndex++)
	        {
	        	String outputConnName = outputList.get(outputConnIndex);	        	
	        	String controlOutputName = this.effectArray[index].controlMap.get(id).getName() + ":" + outputConnName;
	        	Coord outputCoord = this.effectArray[index].controlMap.get(id).getOutputConnectorCoord(outputConnName);
	        	
		        if(this.effectArray[index].controlWireMap.isEmpty() == false)
		        {
		        	for(String controlWireKey : this.effectArray[index].controlWireMap.keySet())
		        	{
		        		String controlWireStartConnection = controlWireKey.split(">")[0];
		        		if(controlOutputName.compareTo(controlWireStartConnection) == 0)
		        		{
		        			this.effectArray[index].controlWireMap.get(controlWireKey).setSourceConnection(outputCoord);
		        		}
		        	}
		        }	        	
	        }
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("controlDragged error: " + e);
		}
		this.dragged = true;
	}

	private void controlClicked(MouseEvent event) {
		int index = this.activeTab;
		Node controlNode = (Node)event.getSource();
		if(this.debugStatements) System.out.println("controlNode: " + controlNode.toString());
		String targetNodeId = null;
		
		try
		{
			Node targetNode = (Node)event.getTarget();
			if(targetNode.getId() != null)
			{
				targetNodeId = targetNode.getId().toString();
			}		
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("controlClicked target node error: " + e);
		}
		
		String id = controlNode.getId();
		
		this.selectedBlockId = id;
		//int clickCount = event.getClickCount();
		if(event.getButton() == MouseButton.PRIMARY)
		{
			String clickedShape = event.getTarget().getClass().getSimpleName().toString();
			//String connectorType;
			if(clickedShape.indexOf("Circle")>=0)
			{
				//String controlName = targetNodeId.split(":")[0];
				String connName = targetNodeId.split(":")[1];
				Coord connectorCoord = null; 
				try
				{
					
					if(this.effectArray[index].controlMap.containsKey(id))
					{
						if(this.effectArray[index].controlMap.get(id).getControlOutputConnectorList().contains(connName))
						{
							this.prelimControlWireStartConnection = new NamedBoundCoord(targetNodeId);
							connectorCoord = this.effectArray[index].controlMap.get(id).getControlOutputConnectorCoord(connName);
							this.prelimControlWireStartConnection.setCoord(connectorCoord);
						}						
					}
					
					if((this.prelimControlWireStartConnection != null && this.prelimControlWireEndConnection != null))
					{					
						//if(this.prelimControlWireStartConnection != null && this.prelimControlWireEndConnection != null)
						{
							String controlWireName = this.prelimControlWireStartConnection.getName() + ">" +
									this.prelimControlWireEndConnection.getName();
							this.addControlWire(controlWireName, this.prelimControlWireStartConnection.getCoord(), 
									this.prelimControlWireEndConnection.getCoord(),"effect0",index);
							this.prelimControlWireStartConnection = null;
							this.prelimControlWireEndConnection = null;
							
						}
					}
					
					//double x = connectorCoord.getX() + this.effectArray[index].controlMap.get(id).getControlCoords().getX();
					//double y = connectorCoord.getY() + this.effectArray[index].controlMap.get(id).getControlCoords().getY();
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("connecting controls error: " + e);
				}							
			}
			else if(this.dragged == false)
			{
				clearAllSelectIndicators();
				/*for(String processKey : this.controlMap.keySet())
				{
					this.controlMap.get(processKey).setSelectIndicator(false);
				}*/
				this.effectArray[index].controlMap.get(id).setSelectIndicator(true);	
				this.editControl(this.effectArray[index].controlMap.get(id));
				/*this.prelimProcessWireStartConnection = null;
				this.prelimProcessWireStartConnection = null;*/
				this.prelimControlWireStartConnection = null;
				this.prelimControlWireStartConnection = null;
			}
		}
		else if(event.getButton() == MouseButton.SECONDARY)
		{
			ContextMenu controlContextMenu = new ContextMenu();
			MenuItem deleteControlItem = new MenuItem("Delete");
			deleteControlItem.setOnAction(evt->this.deleteControl(this.selectedBlockId,index));
			controlContextMenu.getItems().add(deleteControlItem);
			controlContextMenu.show(controlNode, event.getScreenX(), event.getScreenY());
		}
		
	}


	/***************************************************************************************************/
	
	
	public void setCombo(FlxCombo combo) 
	{
		
		this.combo = combo;
		
		this.comboEditArea.clearNames();
		for(int i = 0; i < 2; i++)
		{
			this.drawingAreaCanvas[i].getChildren().clear();
			this.effectArray[i].processMap.clear();// = new HashMap<String,FlxProcess>();
			this.effectArray[i].controlMap.clear();// = new HashMap<String,FlxControl>();

			this.effectArray[i].wireMap.clear();// = new HashMap<String,FlxWire>();
			this.effectArray[i].controlWireMap.clear();// = new HashMap<String,FlxControlWire>();
		}
		//this.effectConnectionJsonDataMap.clear();
		
		this.legacyCombo = false;
		try
		{
			if(this.debugStatements) System.out.println("setting combo: " + combo.getComboName());
			
			this.comboName = this.combo.getComboName();
			this.comboTab.setText(this.comboName);
			int index = 0;
			for(String effectKey : this.combo.getEffectMap().keySet())
			{
				FlxEffect effect = this.combo.getEffectMap().get(effectKey);
				
				int effectIndex = effect.getIndex();
				this.effectArray[effectIndex].name = effect.getName();
				this.drawingAreaTab[effectIndex].setText(this.effectArray[effectIndex].name);
				this.effectArray[effectIndex].abbr = effect.getAbbr();
				this.addEffectIO(effectIndex);
				if(this.debugStatements) System.out.println("Adding Processes");
				for(String processKey : effect.getProcessMap().keySet())
				{
					try
					{
						if(this.debugStatements) System.out.println("Getting Process " + processKey);
						FlxProcess process = effect.getProcessMap().get(processKey);
						if(this.debugStatements) System.out.println("Adding Process " + processKey);
						this.addProcess(process/*.getData()*/, this.effectArray[effectIndex].name, effectIndex);
						if(this.debugStatements) System.out.println(processKey + " added");
						
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("FlxDrawingArea::setCombo: error adding process " + e);
					}
				}
				
				for(String wireKey : effect.getWireMap().keySet())
				{
					try
					{
						if(this.debugStatements) System.out.println("Getting Wire " + wireKey);
						FlxWire wire = effect.getWireMap().get(wireKey);
						if(this.debugStatements) System.out.println("Adding Wire " + wireKey);
						this.addWire(wire/*.getProcessConnectionData()*/,this.effectArray[effectIndex].name,effectIndex);
						if(this.debugStatements) System.out.println(wireKey + " added");
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("FlxDrawingArea::setCombo: error adding wire: " + e);
						
					}
				}
				
				for(String controlKey : effect.getControlMap().keySet())
				{
					try
					{
						if(this.debugStatements) System.out.println("Getting Control " + controlKey);
						FlxControl control = effect.getControlMap().get(controlKey);
						if(this.debugStatements) System.out.println("Adding Control " + controlKey);
						this.addControl(control/*.getData()*/,this.effectArray[effectIndex].name,effectIndex);
						if(this.debugStatements) System.out.println(controlKey + " added");						
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("FlxDrawingArea::setCombo: error adding control: " + e);
					}
				}
				
				for(String controlWireKey : effect.getControlWireMap().keySet())
				{
					try
					{
						if(this.debugStatements) System.out.println("Getting ControlWire " + controlWireKey);
						FlxControlWire controlWire = effect.getControlWireMap().get(controlWireKey);
						if(this.debugStatements) System.out.println("Adding ControlWire " + controlWireKey);
						this.addControlWire(controlWire/*.getControlConnectionData()*/,this.effectArray[effectIndex].name,effectIndex);
						if(this.debugStatements) System.out.println(controlWireKey + " added");
						
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("FlxDrawingArea::setCombo: error adding control wire: " + e);
					}
				}
				
				if(this.debugStatements) System.out.println(this.drawingAreaCanvas[index].getChildren());
				index++;
			}
			
			/*for(String cordKey : this.combo.getCordMap().keySet())
			{
				FlxCord cord = this.combo.getCordMap().get(cordKey);
				this.effectConnectionMap.put(cordKey, cord);
				System.out.println(cord.getName());
			}*/
			for(int i = 0; i < this.combo.getCordList().size(); i++)
			{
				FlxCord cord = this.combo.getCordList().get(i);
				this.effectConnectionList[i] = cord;
			}
			
        	comboEditArea.setNames(comboName,effectArray[0].name,effectArray[0].abbr,
        			effectArray[1].name,effectArray[1].abbr);

			System.out.println("Combo maps complete.");
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea::setCombo: " + e);
		}	
	}

	public JsonValue getCombo() // for saving combo
	{
		if(this.debugStatements) System.out.println("getting combo");
		JsonObjectBuilder combo = Json.createObjectBuilder();
		JsonObject comboBuilt = null;
		int[] controlIndex = new int[2];
		controlIndex[0] = 0;
		controlIndex[1] = 0;
		
		try
		{
			combo.add("name", this.comboName);
			JsonArrayBuilder effectArray = Json.createArrayBuilder();
			for(int effectIndex = 0; effectIndex < 2; effectIndex++)
			{	
				JsonObjectBuilder effect = Json.createObjectBuilder();
				JsonArrayBuilder processArray = Json.createArrayBuilder();
				JsonArrayBuilder wireArray = Json.createArrayBuilder();
				JsonArrayBuilder controlArray = Json.createArrayBuilder();
				JsonArrayBuilder controlWireArray = Json.createArrayBuilder();
				effect.add("name",this.effectArray[effectIndex].name);
				effect.add("abbr",this.effectArray[effectIndex].abbr);
				effect.add("index",effectIndex);
				if(this.debugStatements) System.out.println("******************" + this.effectArray[effectIndex].name + "***************" );
				String processKeyExt = "";
				try
				{
					for(String processKey : this.effectArray[effectIndex].processMap.keySet())
					{
						processKeyExt = processKey;
						this.effectArray[effectIndex].processMap.get(processKey).setParentEffect(this.effectArray[effectIndex].name);
						this.effectArray[effectIndex].processMap.get(processKey).getDataString();
						JsonObject process = (JsonObject) this.effectArray[effectIndex].processMap.get(processKey).getData();
						if(this.debugStatements) System.out.println(processKey);
						processArray.add(process);
					}
					
				}
				catch(Exception e)
				{
					System.out.println("error creating process (" + processKeyExt + ") array: " + e);
				}


				effect.add("processArray", (JsonValue)processArray.build());
			
				String wireKeyExt = "";
				try
				{
					for(String wireKey : this.effectArray[effectIndex].wireMap.keySet())
					{
						wireKeyExt = wireKey;
						this.effectArray[effectIndex].wireMap.get(wireKey).setParentEffect(this.effectArray[effectIndex].name);
						JsonObject wire = (JsonObject)this.effectArray[effectIndex].wireMap.get(wireKey).getProcessConnectionData();
						if(this.debugStatements) System.out.println(wireKey);
						wireArray.add(wire);
					}
				}
				catch(Exception e)
				{
					System.out.println("error creating process connection (" + wireKeyExt + ") array: " + e);
				}

				effect.add("connectionArray", (JsonValue)wireArray.build());

				// first set indexing for controllers
				for(int i = 0; i < 100; i++)
				{
					for(String controlKey : this.effectArray[effectIndex].controlMap.keySet())
					{
						double xLoc = this.effectArray[effectIndex].controlMap.get(controlKey).getLocation().getX();
						if((double)(10.0*i) < xLoc && xLoc <= (double)(10.0*(i+1)))
						{
							this.effectArray[effectIndex].controlMap.get(controlKey).setIndex(controlIndex[effectIndex]);
							controlIndex[effectIndex]++;
						}
					}
				}

				String controlKeyExt = "";
				try
				{
					for(String controlKey : this.effectArray[effectIndex].controlMap.keySet())
					{
						controlKeyExt = controlKey;
						this.effectArray[effectIndex].controlMap.get(controlKey).setParentEffect(this.effectArray[effectIndex].name);
						this.effectArray[effectIndex].controlMap.get(controlKey).getDataString();
						JsonValue temp = this.effectArray[effectIndex].controlMap.get(controlKey).getData();
						if(this.debugStatements) System.out.println(controlKey);
						controlArray.add(temp);
					}
				}
				catch(Exception e)
				{
					System.out.println("error creating control (" + controlKeyExt + ") array: " + e);
				}


				effect.add("controlArray", (JsonValue)controlArray.build());
				String controlWireKeyExt = "";
				try
				{
					for(String controlWireKey : this.effectArray[effectIndex].controlWireMap.keySet())
					{
						controlWireKeyExt = controlWireKey;
						if(this.debugStatements) System.out.println(controlWireKey);
						controlWireArray.add(this.effectArray[effectIndex].controlWireMap.get(controlWireKey).getControlConnectionData());
					}
					
				}
				catch(Exception e)
				{
					System.out.println("error creating control connection (" + controlWireKeyExt + ") array: " + e);
				}
			

				effect.add("controlConnectionArray", (JsonValue)controlWireArray.build());
			
				effectArray.add(effect.build());
				
			}
			combo.add("effectArray", effectArray.build());
			
			JsonArrayBuilder effectConnectionArray = Json.createArrayBuilder();
			String[] effectNames = new String[6];
			effectNames[0] = "system";
			effectNames[1] = "(" + this.effectArray[0].name + ")";
			effectNames[2] = "(" + this.effectArray[1].name + ")";
			effectNames[3] = "system";
			int j = 0;
			for(int i = 0; i < 3; i++)
			{
				this.effectConnectionList[j].changeSrcEffectName(effectNames[i]);
				this.effectConnectionList[j].changeDestEffectName(effectNames[i+1]);
				effectConnectionArray.add(this.effectConnectionList[j].getEffectConnectionData());
				j++;
				this.effectConnectionList[j].changeSrcEffectName(effectNames[i]);
				this.effectConnectionList[j].changeDestEffectName(effectNames[i+1]);
				effectConnectionArray.add(this.effectConnectionList[j].getEffectConnectionData());
				j++;
			}
			
			combo.add("effectConnectionArray", effectConnectionArray.build());
			
			/*for(String cordKey : this.combo.getCordMap().keySet())
			{
				FlxCord cord = this.combo.getCordMap().get(cordKey);
				//this.effectConnectionJsonDataMap.put(cord.getName(), cord.getEffectConnectionData());
				System.out.println(cord.getName());
			}*/
			
			comboBuilt = combo.build();			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea::getCombo error: " + e);
		}
		return comboBuilt;
	}
	
	
	
	public String getComboString() // for saving combo
	{
		if(this.debugStatements) System.out.println("getting combo");
		
		
		int[] controlIndex = new int[2];
		controlIndex[0] = 0;
		controlIndex[1] = 0;
		String comboString = "{";
		try
		{
			//combo.add("name", this.comboName);
			comboString += "\"name\":\"" + this.comboName + "\",";
			comboString += "\"effectArray\":";
			String effectArrayString = "[";
			for(int effectIndex = 0; effectIndex < 2; effectIndex++)
			{	
				String effectString = "{";
				
				//effect.add("name",this.effectArray[effectIndex].name);
				effectString += "\"name\":\"" + this.effectArray[effectIndex].name + "\",";
				//effect.add("abbr",this.effectArray[effectIndex].abbr);
				effectString += "\"abbr\":\"" + this.effectArray[effectIndex].abbr + "\",";
				//effect.add("index",effectIndex);
				effectString += "\"index\":" + effectIndex + ",";
				/******************************************************************************/
				String processArrayString = "\"processArray\":[";
				int processMapCount = this.effectArray[effectIndex].processMap.size();
				int processMapIndex = 0;
				for(String processKey : this.effectArray[effectIndex].processMap.keySet())
				{
					//this.effectArray[effectIndex].processMap.get(processKey).setParentEffect(this.effectArray[effectIndex].name);
					
					processArrayString += this.effectArray[effectIndex].processMap.get(processKey).getDataString();
					if(processMapIndex++ < processMapCount-1)
					{
						processArrayString += ",";
					}
					if(this.debugStatements) System.out.println(processKey);
					
				}
				
				processArrayString += "],";
				effectString += processArrayString;
				/******************************************************************************/
				String processConnectionArrayString = "\"connectionArray\":[";
				int wireMapCount = this.effectArray[effectIndex].wireMap.size();
				int wireMapIndex = 0;
				for(String wireKey : this.effectArray[effectIndex].wireMap.keySet())
				{
					//this.effectArray[effectIndex].wireMap.get(wireKey).setParentEffect(this.effectArray[effectIndex].name);
					//JsonObject wire = (JsonObject)this.effectArray[effectIndex].wireMap.get(wireKey).getProcessConnectionData();
					processConnectionArrayString += this.effectArray[effectIndex].wireMap.get(wireKey).getProcessConnectionString();
					if(wireMapIndex++ < wireMapCount-1)
					{
						processConnectionArrayString += ",";
					}
					if(this.debugStatements) System.out.println(wireKey);
					//wireArray.add(wire);
				}
				processConnectionArrayString += "],";
				effectString += processConnectionArrayString;
				/******************************************************************************/
				// first set indexing for controllers
				for(int i = 0; i < 100; i++)
				{
					for(String controlKey : this.effectArray[effectIndex].controlMap.keySet())
					{
						double xLoc = this.effectArray[effectIndex].controlMap.get(controlKey).getLocation().getX();
						if((double)(10.0*i) < xLoc && xLoc <= (double)(10.0*(i+1)))
						{
							this.effectArray[effectIndex].controlMap.get(controlKey).setIndex(controlIndex[effectIndex]);
							controlIndex[effectIndex]++;
						}
					}
				}
				String controlArrayString = "\"controlArray\":[";				
				int controlMapCount = this.effectArray[effectIndex].controlMap.size();
				int controlMapIndex = 0;
				for(String controlKey : this.effectArray[effectIndex].controlMap.keySet())
				{
					//this.effectArray[effectIndex].controlMap.get(controlKey).setParentEffect(this.effectArray[effectIndex].name);
					//controlArray.add(this.effectArray[effectIndex].controlMap.get(controlKey).getData());
					controlArrayString += this.effectArray[effectIndex].controlMap.get(controlKey).getDataString();
					if(controlMapIndex++ < controlMapCount-1)
					{
						controlArrayString += ",";
					}
				}
				controlArrayString += "],";
				effectString += controlArrayString;
				/******************************************************************************/
				String controlConnectionArrayString = "\"controlConnectionArray\":[";
				int controlWireMapCount = this.effectArray[effectIndex].controlWireMap.size();
				int controlWireMapIndex = 0;
				for(String controlWireKey : this.effectArray[effectIndex].controlWireMap.keySet())
				{
					
					//controlWireArray.add(this.effectArray[effectIndex].controlWireMap.get(controlWireKey).getControlConnectionData());
					controlConnectionArrayString += this.effectArray[effectIndex].controlWireMap.get(controlWireKey).getControlConnectionString();
					if(controlWireMapIndex++ < controlWireMapCount-1)
					{
						controlConnectionArrayString += ",";
					}
				}
				controlConnectionArrayString += "]";
				effectString += controlConnectionArrayString + "}";
				effectArrayString += effectString;
				
				if(effectIndex == 0)
				{
					effectArrayString += ",";
				}
				else
				{
					effectArrayString += "],";
				}
			}
			comboString += effectArrayString;
			/******************************************************************************/
			String effectConnectionArrayString = "";//Json.createArrayBuilder();
			String[] effectNames = new String[6];
			effectNames[0] = "system";
			effectNames[1] = "(" + this.effectArray[0].name + ")";
			effectNames[2] = "(" + this.effectArray[1].name + ")";
			effectNames[3] = "system";
			int j = 0;
			effectConnectionArrayString += "\"effectConnectionArray\": [";
			for(int i = 0; i < 3; i++)
			{
				this.effectConnectionList[j].changeSrcEffectName(effectNames[i]);
				this.effectConnectionList[j].changeDestEffectName(effectNames[i+1]);
				effectConnectionArrayString += this.effectConnectionList[j].getEffectConnectionString();
				j++;
				effectConnectionArrayString += ",";
				this.effectConnectionList[j].changeSrcEffectName(effectNames[i]);
				this.effectConnectionList[j].changeDestEffectName(effectNames[i+1]);
				effectConnectionArrayString += this.effectConnectionList[j].getEffectConnectionString();
				j++;
				if(i < 2)
				{
					effectConnectionArrayString += ",";
				}
				else
				{
					effectConnectionArrayString += "]";
				}
			}
			
			comboString += effectConnectionArrayString;
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("getCombo error: " + e);
		}
		comboString += "}";
		System.out.println("comboString complete");
		return comboString;
	}

	/*********************************************************************************************/
	private void clearAllSelectIndicators()
	{
		for(int i = 0; i < 2; i++)
		{
			for(String processKey : this.effectArray[i].processMap.keySet())
			{
				this.effectArray[i].processMap.get(processKey).setSelectIndicator(false);
			}
			
			for(String controlKey : this.effectArray[i].controlMap.keySet())
			{
				this.effectArray[i].controlMap.get(controlKey).setSelectIndicator(false);
			}
		}
	}

	
	public void setEditor(FlxParameterEditor paramEditor) 
	{
		this.parameterEditorReference = paramEditor;
	}

	
	public void editProcess(FlxProcess process) 
	{
		//Map<String, FlxParameter> processDataMap = process.getParamMap();
		List<FlxParameter> processDataList = process.getParamList();
		FlxFootswitch footswitch = process.getFootswitch();
		this.parameterEditorReference.setFootswitchSelector(footswitch);
		//this.parameterEditorReference.setProcessParameters(processDataMap);
		this.parameterEditorReference.setProcessParameters(processDataList);
	}

	
	public void editControl(FlxControl control) 
	{
		//Map<String, FlxParameter> controlDataMap = control.getParamMap();
		List<FlxControlParameter> controlDataList = control.getParamList();
		this.parameterEditorReference.setLcdLabelDescriptionLabels();
		//this.parameterEditorReference.setParameterControllerParameters(controlDataMap);
		this.parameterEditorReference.setParameterControllerParameters(controlDataList);
		this.parameterEditorReference.setLcdLabelUpdateButton();
	}
	
	private Map<String,FlxConnector> createEffectIO(int index)
	{
		Map<String,FlxConnector> effectIO = new HashMap<String,FlxConnector>();
		
		/*FlxConnector input1 = new FlxConnector("input1", "input", 0, "(effect"+index+")", 20, 160);
		FlxConnector input2 = new FlxConnector("input2", "input", 1, "(effect"+index+")", 20, 400);
		FlxConnector output1 = new FlxConnector("output1", "output", 0, "(effect"+index+")", 970, 160);
		FlxConnector output2 = new FlxConnector("output2", "output", 1, "(effect"+index+")", 970, 400);*/
		FlxConnector input1 = new FlxConnector("input1", "input", 0, "(effect"+index+")", 20, 160);
		FlxConnector input2 = new FlxConnector("input2", "input", 1, "(effect"+index+")", 20, 400);
		FlxConnector output1 = new FlxConnector("output1", "output", 0, "(effect"+index+")", 970, 160);
		FlxConnector output2 = new FlxConnector("output2", "output", 1, "(effect"+index+")", 970, 400);

		effectIO.put("input1", input1);
		effectIO.put("input2", input2);
		effectIO.put("output1", output1);
		effectIO.put("output2", output2);
		
		return effectIO;
	}
}
