package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;


import diagramComponents.FlxCombo;
import diagramComponents.FlxComponent;
import diagramComponents.FlxControl;
import diagramComponents.FlxControlWire;
import diagramComponents.FlxControl_Impl;
import diagramComponents.FlxCord;
import diagramComponents.FlxEffect;
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
import diagramSubComponents.LookUpTable;
import diagramSubComponents.NamedBoundCoord;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


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

class Symbol_Data
{
	List<Group> processComponentSymbol;
	JsonValue processComponentData;
}

public class FlxDrawingArea_Impl implements FlxDrawingArea {
	boolean debugStatements = false;
	boolean errorStatements = true;
	String selectedBlockId;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    double cursorX, cursorY;
    boolean dragged;
    String selectedComponent = "";
    String selectedProcess = "";
    String selectedController = "";

    TabPane drawingAreaTabPane = new TabPane();
    SplitPane applyButton_DrawingArea = new SplitPane();
    Tab comboTab = new Tab();
    Tab applyChanges = new Tab();
    ComboEditArea comboEditArea = new ComboEditArea();
	Pane[] drawingAreaCanvas = new Pane[2];
	ScrollPane[] drawingAreaScrollPane = new ScrollPane[2];
	Tab[] drawingAreaTab = new Tab[2];
	FlxWire tempWire = null;
	Line tempLine = new Line();


	Map<String, Integer> processCountMap = new HashMap<String, Integer>();
	int[] controlCount = new int[2];
	FlxMenuBar menuBarReference;
	FlxSidebar sideBarReference;
	FlxParameterEditor parameterEditorReference;
	FlxCombo combo;
	String comboName;
	int activeTab;
	JsonValue clickedComponentData = null;
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
	List<Group> processComponentSideBarList = new ArrayList<Group>();
	BoundCoord dest;
	BoundCoord src;
	ListView<Group> processComponentSideBarListView = new ListView<Group>();
	String SelectedId = "";
	String SelectedType = "";
	String procCompId = "";
	String contCompId = "";
	String procId = "";
	String contId = "";
	int canvasIndex = 0;

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

				this.drawingAreaTab[i].setContent(drawingAreaScrollPane[i]);
				this.drawingAreaTab[i].setClosable(false);
				this.drawingAreaTabPane.getTabs().add(drawingAreaTab[i]);
				this.drawingAreaCanvas[i].setMinSize(1249, 549);
				this.drawingAreaCanvas[i].setId("drawingArea");
				this.drawingAreaScrollPane[i].setMinSize(1149, 449);
				this.drawingAreaScrollPane[i].setHbarPolicy(ScrollBarPolicy.NEVER);
				this.drawingAreaScrollPane[i].setVbarPolicy(ScrollBarPolicy.NEVER);


				this.drawingAreaCanvas[i].setOnMouseClicked(canvasClickEvent ->
				{

					if(canvasClickEvent.getButton() == MouseButton.PRIMARY && canvasClickEvent.getTarget().toString().indexOf("Pane") >= 0)
					{
						if(this.debugStatements) System.out.println("clicked on canvas");

						clearAllSelectIndicators();
						parameterEditorReference.clearEditor();
					}
					else if(canvasClickEvent.getButton() == MouseButton.SECONDARY)
					{

					}
				});
				this.drawingAreaTabPane.setOnMouseClicked(mouseEvent -> {
				    	System.out.println("drawingAreaTabPane clicked");
				    	EventTarget eventTarget = mouseEvent.getTarget();
				       	System.out.println("target: " + mouseEvent.getTarget().toString());
				       	String eventTargetString = eventTarget.toString();
				        if(eventTargetString.indexOf("Polygon") < 0 && eventTargetString.indexOf("SVGPath") < 0)
				        {
							clearAllSelectIndicators();
							parameterEditorReference.clearEditor();
				        }

				        String id = drawingAreaTabPane.getSelectionModel().getSelectedItem().getId();
				        System.out.println("drawingAreaTabPane ID: " + id);

			    		if(id != null)
			    		{
					        if(id.indexOf("_") >= 0)
					        {
						        this.activeTab = Integer.parseInt(id.split("_")[1]);
						        if(debugStatements) System.out.println("clicked on effect: " + activeTab);
					        }
					        else
					        {
					        	if(debugStatements) System.out.println("clicked on combo");
					        }
			    		}
				});
			}

			this.dragged = false;
			this.controlCount[0] = 0;
			this.controlCount[1] = 0;

		}
		catch (Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea error: " + e);
		}
	}

	/********************************************************************************************
	 ********************************************************************************************
	 ********************************************************************************************/

	public void initializeSideBarEvents()
	{
		try
		{
			/************************* Initialize process mouse events *******************************/
			Map<String,FlxComponent> procCompMap = this.sideBarReference.getProcessComponentMap();

			System.out.println("keyset: " + procCompMap.keySet());
			List<String> procCompList = new ArrayList<String>();
			for(String key : procCompMap.keySet())
			{
				procCompList.add(key);
				System.out.print(key + ",");
			}
			System.out.println();

			for(String procCompKey:procCompMap.keySet())
			{

				Group tempGroupProc = this.sideBarReference.getProcessComponentMap().get(procCompKey).getComponent();

				tempGroupProc.setOnMouseClicked(tempGroupProcSelected ->{
								System.out.println(tempGroupProcSelected.toString());

								String compId = ((Group)tempGroupProcSelected.getSource()).getId();
								clickedComponentData = sideBarReference.getProcessData(compId);
								System.out.println("compId: " + compId);
								selectedComponent = compId;
							});

			}

			Map<String,FlxComponent> contCompMap = this.sideBarReference.getControlComponentMap();



			System.out.println("Control keys:");
			List<String> contCompList = new ArrayList<String>();
			for(String key : contCompMap.keySet())
			{
				contCompList.add(key);
				System.out.print(key + ",");
			}
			System.out.println();

			System.out.println("keyset: " + contCompMap.keySet());


			for(String contCompKey : contCompMap.keySet())
			{
				Group tempGroupCont = this.sideBarReference.getControlComponentMap().get(contCompKey).getComponent();
				tempGroupCont.setOnMouseClicked(tempGroupContSelected -> {

								{
										System.out.println(tempGroupContSelected.toString());

										String compId = ((Group)tempGroupContSelected.getSource()).getId();
										clickedComponentData = sideBarReference.getControlData(compId);
										System.out.println("compId: " + compId);
										selectedComponent = compId;
								}
						});
			}
			for(this.canvasIndex = 0; this.canvasIndex < 2; this.canvasIndex++)
			{

				drawingAreaCanvas[this.canvasIndex].addEventHandler(MouseEvent.MOUSE_ENTERED,
						new EventHandler<MouseEvent>()
						{
							public void handle(MouseEvent enteredCanvas)
							{
								try
								{
									if(selectedComponent.isEmpty() == false)
									{
										double centerOffsetX = -30.0;
										double centerOffsetY = -30.0;
										if(procCompList.contains(selectedComponent))
										{
											SelectedId = addProcess(clickedComponentData,effectArray[activeTab].name,activeTab).getName();

									        effectArray[activeTab].processMap.get(SelectedId).getBlock().setTranslateX(enteredCanvas.getX()+centerOffsetX);
									        effectArray[activeTab].processMap.get(SelectedId).getBlock().setTranslateY(enteredCanvas.getY()+centerOffsetY);
									        effectArray[activeTab].processMap.get(SelectedId).setLocation(enteredCanvas.getX()+centerOffsetX, enteredCanvas.getY()+centerOffsetY);
									        selectedComponent = "";
									        SelectedType = "process";
										}
								        if(contCompList.contains(selectedComponent))
								        {
								        	SelectedId = addControl(clickedComponentData,effectArray[activeTab].name,activeTab).getName();

									        effectArray[activeTab].controlMap.get(SelectedId).getBlock().setTranslateX(enteredCanvas.getX()+centerOffsetX);
									        effectArray[activeTab].controlMap.get(SelectedId).getBlock().setTranslateY(enteredCanvas.getY()+centerOffsetY);
									        effectArray[activeTab].controlMap.get(SelectedId).setLocation(enteredCanvas.getX()+centerOffsetX, enteredCanvas.getY()+centerOffsetY);
									        selectedComponent = "";
									        SelectedType = "control";
								        }
									}

								}
								catch(Exception e)
								{
									System.out.println("enteredCanvas error: " + e);
								}
							}
						});


				drawingAreaCanvas[this.canvasIndex].addEventHandler(MouseEvent.MOUSE_MOVED,
						new EventHandler<MouseEvent>()
					 	{
						 	public void handle(MouseEvent dragOnCanvas)
						 	{



						 		if(SelectedId.isEmpty() == false )
						 		{
									double centerOffsetX = -30.0;
									double centerOffsetY = -30.0;
									if(SelectedType.compareTo("process") == 0)
									{
								        effectArray[activeTab].processMap.get(SelectedId).getBlock().setTranslateX(dragOnCanvas.getX()+centerOffsetX);
								        effectArray[activeTab].processMap.get(SelectedId).getBlock().setTranslateY(dragOnCanvas.getY()+centerOffsetY);
								        effectArray[activeTab].processMap.get(SelectedId).setLocation(dragOnCanvas.getX()+centerOffsetX, dragOnCanvas.getY()+centerOffsetY);
									}

									if(SelectedType.compareTo("control") == 0)
									{
								        effectArray[activeTab].controlMap.get(SelectedId).getBlock().setTranslateX(dragOnCanvas.getX()+centerOffsetX);
								        effectArray[activeTab].controlMap.get(SelectedId).getBlock().setTranslateY(dragOnCanvas.getY()+centerOffsetY);
								        effectArray[activeTab].controlMap.get(SelectedId).setLocation(dragOnCanvas.getX()+centerOffsetX, dragOnCanvas.getY()+centerOffsetY);
									}
						 		}
						 	}
						 });

				drawingAreaCanvas[this.canvasIndex].setOnMouseClicked(releaseProc->{
						 		System.out.println(releaseProc.toString());
						 		if(SelectedId.isEmpty() == false)
						 		{
						 			SelectedId = "";
						 			System.out.println("component released");
						 			selectedComponent = "";
						 		}

					 	});

			}


		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea::initializeSideBarEvents error: " + e);
		}

	}


	public String getCurrentCombo()
	{
		return this.comboName;
	}
	public void setTabNames(String comboName, String effect0Name, String effect0Abbr, String effect1Name, String effect1Abbr)
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





	public TabPane getDrawingArea()
	{
		return this.drawingAreaTabPane;
	}

	public Pane getDrawingPane()
	{
		return this.drawingAreaCanvas[0];
	}
	/******************************* ADD/DELETE DRAWING AREA ITEMS ********************************/

	public void addEffectIO(int index)
	{

		this.effectArray[index].effectIO = createEffectIO(index);

		for(String effectIoKey : this.effectArray[index].effectIO.keySet())
		{
			this.effectArray[index].effectIO.get(effectIoKey).getConnector().setOnMouseClicked(event -> {

				if(event.getButton() == MouseButton.PRIMARY)
				{
					this.connectorClicked(event);
				}
			});
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
			String compName = ((JsonObject)componentData).getString("name").toLowerCase();
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


			newProcess.setName(procName);
			JsonArray paramArray = (JsonArray)newProcess.getParamArrayData();
			String infoString = new String();

			for(int i = 0; i < paramArray.size()-1; i++)
			{

				infoString += lut.getParameterValueString(
						((JsonObject)paramArray.get(i)).getInt("paramType"),
						((JsonObject)paramArray.get(i)).getInt("value")
						) + ",";
			}
			infoString += lut.getParameterValueString(
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("paramType"),
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("value")
					);
			newProcess.setInfo(infoString);

			this.effectArray[index].processMap.put(procName, newProcess);
			newProcess.getBlock().setOnMouseClicked(event ->
			{
				this.processClicked(event);
			});
			newProcess.getBlock().setOnMousePressed(event ->
			{
				this.processSelected(event);
			});
			newProcess.getBlock().setOnMouseDragged(event ->
			{
				this.processDragged(event);
			});

			this.drawingAreaCanvas[index].getChildren().add(newProcess.getBlock());

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

		if(parentEffectName == null)
		{
			parentEffectName = "effect0";
		}

		try
		{
			String compName = process.getName();
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

			List<FlxParameter> paramArray = process.getParamList();
			int paramArraySize = paramArray.size();
			String infoString = new String();

			for(int i = 0; i < paramArraySize-1; i++)
			{

				infoString += lut.getParameterValueString(
						paramArray.get(i).getParameterUnitType(),
						paramArray.get(i).getParamValueIndex()
						) + ",";
			}
			infoString += lut.getParameterValueString(
					paramArray.get(paramArraySize-1).getParameterUnitType(),
					paramArray.get(paramArraySize-1).getParamValueIndex()
					);
			process.setInfo(infoString);

			this.effectArray[index].processMap.put(procName, process);
			process.getBlock().setOnMouseClicked(event -> this.processClicked(event));
			process.getBlock().setOnMousePressed(event -> this.processSelected(event));
			process.getBlock().setOnMouseDragged(event -> this.processDragged(event));

			this.drawingAreaCanvas[index].getChildren().add(process.getBlock());

		}
		catch (Exception e)
		{

			if(this.errorStatements) System.out.println("FlxDrawingArea addProcess error2: " + e);
		}

		return process;
	}

	private void deleteProcess(String processName, int index) {

		try
		{

			// Delete all attached wires, first

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
			this.drawingAreaCanvas[index].getChildren().remove(this.tempWire.getWire());

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addWire error: " + e);
		}

		return wire;

	}


	private FlxWire addWire(FlxWire wire, String parentEffectName, int index)
	{

		try
		{
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
			Group wireGroup = wire.getWire();
			wireGroup.setId(wireName);
			this.drawingAreaCanvas[index].getChildren().add(wireGroup);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addWire error2: " + e);
		}

		return wire;
	}







	private void deleteWire(String wireName, int index)
	{
		try
		{
			if(this.debugStatements) System.out.println("delete wire: " + wireName);
			{
				System.out.println(this.effectArray[index].wireMap.toString());
				FlxWire temp = this.effectArray[index].wireMap.get(wireName);
				temp.deleteWireSymbol();
				this.effectArray[index].wireMap.remove(wireName);
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea deleteWire error: " + e);
		}
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
			String controlName = ((JsonObject)compData).getString("name").toLowerCase();

			if(controlName.contains("_"))
			{
			}
			else
			{
				for(int conNameIndex = 0; conNameIndex < 50; conNameIndex++)
				{
					controlName = "control_" + conNameIndex;
					if((this.effectArray[0].controlMap.containsKey(controlName) == false) &&
							(this.effectArray[1].controlMap.containsKey(controlName) == false))
					{
						break;
					}
				}
			}
			newControl = new FlxControl_Impl(controlName, compData, parentEffectName);

			this.controlCount[index]++;
			JsonArray paramArray = (JsonArray)newControl.getParamArrayData();
			String infoString = new String();
			for(int i = 0; i < paramArray.size()-1; i++)
			{

				infoString += lut.getParameterValueString(
						((JsonObject)paramArray.get(i)).getInt("paramType"),
						((JsonObject)paramArray.get(i)).getInt("value")
						) + ",";
			}
			infoString += lut.getParameterValueString(
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("paramType"),
					((JsonObject)paramArray.get(paramArray.size()-1)).getInt("value")
					);
			newControl.setInfo(infoString);




			this.effectArray[index].controlMap.put(controlName, newControl);
			newControl.getBlock().setOnMouseClicked(event -> this.controlClicked(event));
			newControl.getBlock().setOnMousePressed(event -> this.controlSelected(event));
			newControl.getBlock().setOnMouseDragged(event -> this.controlDragged(event));

			this.drawingAreaCanvas[index].getChildren().add(newControl.getBlock());

		} catch (Exception e) {

			if(this.errorStatements) System.out.println("FlxDrawingArea addControl error: " + e);
		}
		return newControl;
	}

	public FlxControl addControl(FlxControl control, String parentEffectName, int index)
	{
		if(index < 0) index = this.activeTab;
		if(parentEffectName == null)
		{
			parentEffectName = "effect0";
		}
		try
		{
			String controlName = control.getName();

			if(controlName.contains("_"))
			{
			}
			else
			{
				for(int conNameIndex = 0; conNameIndex < 50; conNameIndex++)
				{
					controlName = "control_" + conNameIndex;
					if((this.effectArray[0].controlMap.containsKey(controlName) == false) &&
							(this.effectArray[1].controlMap.containsKey(controlName) == false))
					{
						break;
					}
				}
			}

			this.controlCount[index]++;
			List<FlxControlParameter> conParamArray = control.getParamList();
			int conParamArraySize = conParamArray.size();
			String infoString = new String();
			for(int i = 0; i < conParamArraySize-1; i++)
			{

				infoString += lut.getParameterValueString(
						conParamArray.get(i).getParameterUnitType(),
						conParamArray.get(i).getParamValueIndex()
						) + ",";
			}
			infoString += lut.getParameterValueString(
					conParamArray.get(conParamArraySize-1).getParameterUnitType(),
					conParamArray.get(conParamArraySize-1).getParamValueIndex()
					);
			control.setInfo(infoString);




			this.effectArray[index].controlMap.put(controlName, control);
			control.getBlock().setOnMouseClicked(event -> this.controlClicked(event));
			control.getBlock().setOnMousePressed(event -> this.controlSelected(event));
			control.getBlock().setOnMouseDragged(event -> this.controlDragged(event));

			this.drawingAreaCanvas[index].getChildren().add(control.getBlock());

		} catch (Exception e) {

			if(this.errorStatements) System.out.println("FlxDrawingArea addControl error2: " + e);
		}
		return control;
	}

	private void deleteControl(String controlName, int index) {
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
					clearControlledParameterTypeFromController(controlWireName,index);
					deleteControlWire(controlWireName,index);
				}
			});

			inheritControlledParameterTypeIntoController(controlWireName,index);
			this.effectArray[index].controlWireMap.put(controlWireName, controlWire);
			this.drawingAreaCanvas[index].getChildren().add(controlWire.getWire());
			if(this.tempWire != null)
			{
				this.drawingAreaCanvas[index].getChildren().remove(this.tempWire.getWire());
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addControlWire error: " + e);
		}

		return controlWire;


	}

	private void inheritControlledParameterTypeIntoController(String controlWireName, int index)
	{
		try
		{

			String conWireName = controlWireName;
			String[] conWireNameParsed = conWireName.split(">");
			String controller = conWireNameParsed[0].split(":")[0];
			String conPort = conWireNameParsed[0].split(":")[1];
			String process = conWireNameParsed[1].split(":")[0];
			String procParam = conWireNameParsed[1].split(":")[1];

			// *************** Setting process parameter type in controller ***********************
			// get process parameter type
			{
				FlxProcess tempProc = this.effectArray[index].processMap.get(process);
				FlxParameter tempParam = tempProc.getParamMapItem(procParam);
				int procParamType = tempParam.getParameterUnitType();

				// set process parameter type in controller
				this.effectArray[index].controlMap.get(controller).setProcessParameterType(procParamType);

				// *************** Setting controller type in process parameter ***********************
				// get controller type
				String controlType = this.effectArray[index].controlMap.get(controller).getControlType();
				// set controller type in process parameter
				this.effectArray[index].processMap.get(process).getParamMapItem(procParam).setParamControllerType(controlType);
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea inheritControlledParameterTypeIntoController error: " + e);
		}
	}


	private void clearControlledParameterTypeFromController(String controlWireName,int index)
	{
		try
		{

			String conWireName = controlWireName;
			String[] conWireNameParsed = conWireName.split(">");
			String controller = conWireNameParsed[0].split(":")[0];
			String conPort = conWireNameParsed[0].split(":")[1];
			String process = conWireNameParsed[1].split(":")[0];
			String procParam = conWireNameParsed[1].split(":")[1];
			System.out.println(controller+":"+conPort+">"+process+":"+procParam);

			// *************** clearing process parameter type from controller ***********************
			// set process parameter type in controller
			this.effectArray[index].controlMap.get(controller).setProcessParameterType(0);

			// get process parameter type from controller to verify
			int paramType = this.effectArray[index].controlMap.get(controller).getProcessParameterType();
			System.out.println(controller+"->"+paramType);

			// *************** clearing controller type from process parameter ***********************
			// set controller type in process parameter
			this.effectArray[index].processMap.get(process).getParamMapItem(procParam).setParamControllerType("none");

			// get controller type from process parameter to verify
			String contType = this.effectArray[index].processMap.get(process).getParamMapItem(procParam).getParamControllerType();
			System.out.println(process+":"+procParam+"->"+contType);
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea clearControlledParameterTypeFromController error: " + e);
		}

	}

	private FlxControlWire addControlWire(FlxControlWire controlWire, String parentEffectName, int index)
	{
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

			inheritControlledParameterTypeIntoController(controlWireName, index);
			this.effectArray[index].controlWireMap.put(controlWireName, controlWire);
			this.drawingAreaCanvas[index].getChildren().add(controlWire.getWire());
			if(this.tempWire != null)
			{
				this.drawingAreaCanvas[index].getChildren().remove(this.tempWire.getWire());
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea addControlWire error2: " + e);
		}

		return controlWire;


	}


	private void deleteControlWire(String controlWireName, int index)
	{
		if(this.debugStatements) System.out.println("delete control wire: " + controlWireName);
		{
			this.effectArray[index].controlWireMap.get(controlWireName).deleteWireSymbol();
			this.effectArray[index].controlWireMap.remove(controlWireName);
		}
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




	private void connectorClicked(MouseEvent event)
	{
		int index = this.activeTab;
		String targetNodeId = null;
        this.orgSceneX = event.getSceneX();
        this.orgSceneY = event.getSceneY();
		this.orgTranslateX = ((Circle)(event.getSource())).getTranslateX();
		this.orgTranslateY = ((Circle)(event.getSource())).getTranslateY();

		try
		{
			System.out.println("Connection event: " + event.toString());
			Node targetNode = (Node)event.getTarget();
			if(targetNode.getId() != null)
			{
				targetNodeId = targetNode.getId().toString();
				System.out.println("targetNodeId: " + targetNodeId);
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("connectorClicked target node error: " + e);
		}

		String connName = targetNodeId.split(":")[1];
		String connectorType = this.effectArray[index].effectIO.get(connName).getType();
		Coord connectorCoord = null;

		try
		{
			connectorCoord = this.effectArray[index].effectIO.get(connName).getConnectorLocalLocation();
			if(connectorType.compareTo("input") == 0 && this.prelimProcessWireEndConnection == null)
			{
				System.out.println("input connector clicked1");
				this.prelimProcessWireEndConnection = new NamedBoundCoord(targetNodeId);
				this.prelimProcessWireEndConnection.setCoord(connectorCoord);
				setConnectionTempWire("input", this.prelimProcessWireEndConnection);

			}
			else if(connectorType.compareTo("output") == 0 && this.prelimProcessWireStartConnection == null)
			{
				System.out.println("output connector clicked1");
				this.prelimProcessWireStartConnection = new NamedBoundCoord(targetNodeId);
				this.prelimProcessWireStartConnection.setCoord(connectorCoord);
				setConnectionTempWire("output", this.prelimProcessWireStartConnection);

			}

			if(this.prelimProcessWireStartConnection != null && this.prelimProcessWireEndConnection != null)
			{

				String wireName = this.prelimProcessWireStartConnection.getName() + ">" +
						this.prelimProcessWireEndConnection.getName();


				this.addWire(wireName, this.prelimProcessWireStartConnection.getCoord(),
						this.prelimProcessWireEndConnection.getCoord(),"effect0",index);


				this.prelimProcessWireStartConnection = null;
				this.prelimProcessWireEndConnection = null;
				this.tempWire = null;


				System.out.println(wireName + " is now in map.");
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("connecting processes error: " + e);
		}
	}

	private void setConnectionTempWire(String connectorType, NamedBoundCoord connection)
	{
		try
		{
			int index = this.activeTab;
			if(connectorType == "output")
			{

				BoundCoord dest = new BoundCoord();

				if(this.tempWire == null)
				{
					if(this.prelimProcessWireStartConnection != null)
					{
						this.tempWire = new FlxWire_Impl("tempWire",this.prelimProcessWireStartConnection.getCoord(), this.prelimProcessWireStartConnection.getCoord() ,"effect0");
					}
					else if(this.prelimControlWireStartConnection != null)
					{
						this.tempWire = new FlxControlWire_Impl("tempWire",this.prelimControlWireStartConnection.getCoord(), this.prelimControlWireStartConnection.getCoord() ,"effect0");
					}

					this.drawingAreaCanvas[index].getChildren().add(this.tempWire.getWire());
					this.drawingAreaCanvas[index].setOnMouseMoved(eventAddWireDest -> {
						dest.setX(eventAddWireDest.getX());
						dest.setY(eventAddWireDest.getY());

						if(this.tempWire != null)
						{
							this.tempWire.setDestConnection(dest);
						}

					});

					this.drawingAreaCanvas[index].setOnMouseClicked(eventDelWire -> {
						if(eventDelWire.getButton() == MouseButton.SECONDARY)
						{
							if(this.tempWire != null)
							{
								if(this.drawingAreaCanvas[index].getChildren().contains(this.tempWire.getWire()))
								{
									System.out.println("deleting tempwire");
									this.drawingAreaCanvas[index].getChildren().remove(this.tempWire.getWire());
									this.tempWire = null;
									this.prelimProcessWireStartConnection = null;
									this.prelimProcessWireEndConnection = null;
									this.prelimControlWireStartConnection = null;
									this.prelimControlWireEndConnection = null;
								}
								System.out.println(eventDelWire.toString());
								System.out.println("Deleted temp output wire");
							}


						}
						else
						{

						}
					});

				}

			}
			else if(connectorType == "input") //dest
			{
				BoundCoord src = new BoundCoord();
				if(this.tempWire == null)
				{
					if(this.prelimProcessWireEndConnection != null)
					{
						this.tempWire = new FlxWire_Impl("tempWire",this.prelimProcessWireEndConnection.getCoord(), this.prelimProcessWireEndConnection.getCoord() ,"effect0");
					}

					this.drawingAreaCanvas[index].getChildren().add(this.tempWire.getWire());
					this.drawingAreaCanvas[index].setOnMouseMoved(eventAddWireSrc -> {
						src.setX(eventAddWireSrc.getX());
						src.setY(eventAddWireSrc.getY());

						if(this.tempWire != null)
						{
							this.tempWire.setSourceConnection(src);
						}
					});

					this.drawingAreaCanvas[index].setOnMouseClicked(eventDelWire -> {
						if(eventDelWire.getButton() == MouseButton.SECONDARY)
						{
							if(this.drawingAreaCanvas[index].getChildren().contains("tempWire"))
							{

								this.drawingAreaCanvas[index].getChildren().remove(this.tempWire.getWire());
								this.tempWire = null;
								this.prelimProcessWireEndConnection = null;
								this.prelimProcessWireStartConnection = null;
							}
							System.out.println(eventDelWire.toString());
							System.out.println("Deleted temp input wire");
						}
					});
				}
			}
			else if(connectorType == "param")
			{

				BoundCoord dest = new BoundCoord();

				if(this.tempWire == null)
				{
					if(this.prelimControlWireEndConnection != null)
					{
						this.tempWire = new FlxControlWire_Impl("tempWire",this.prelimControlWireEndConnection.getCoord(), this.prelimControlWireEndConnection.getCoord() ,"effect0");
					}
					this.drawingAreaCanvas[index].getChildren().add(this.tempWire.getWire());
					this.drawingAreaCanvas[index].setOnMouseMoved(eventAddWireParam -> {
						dest.setX(eventAddWireParam.getX());
						dest.setY(eventAddWireParam.getY());

						if(this.tempWire != null)
						{
							this.tempWire.setDestConnection(dest);
						}

					});

					this.drawingAreaCanvas[index].setOnMouseClicked(eventDelWire -> {
						if(eventDelWire.getButton() == MouseButton.SECONDARY)
						{
							if(this.drawingAreaCanvas[index].getChildren().contains("tempWire"))
							{
								this.drawingAreaCanvas[index].getChildren().remove(this.tempWire.getWire());
								this.tempWire = null;
								this.prelimProcessWireStartConnection = null;
								this.prelimProcessWireEndConnection = null;
								this.prelimControlWireStartConnection = null;
								this.prelimControlWireEndConnection = null;
							}

							System.out.println(eventDelWire.toString());
							System.out.println("Deleted temp param wire");
						}
					});

				}

			}


		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxDrawingArea setConnectionTempWire error: " + e);
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

	public void processDragged(MouseEvent event)
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
	       List<String> inputList = this.effectArray[index].processMap.get(id).getInputConnectorList();

	        for(int inputConnIndex = 0; inputConnIndex < inputList.size(); inputConnIndex++)
	        {
	        	String inputConnName = inputList.get(inputConnIndex);
	        	String procInputName = this.effectArray[index].processMap.get(id).getName() + ":" + inputConnName;
	           	BoundCoord inputCoord = this.effectArray[index].processMap.get(id).getInputConnectorBoundCoord(inputConnName);

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
	        	BoundCoord outputCoord = this.effectArray[index].processMap.get(id).getOutputConnectorBoundCoord(outputConnName);
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
	        	BoundCoord paramCoord = this.effectArray[index].processMap.get(id).getParamConnectorBoundCoord(paramConnName);
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





	private void processClicked(MouseEvent processClickedEvent) {

		System.out.println("selectedComponent: " + selectedComponent);
		int index = this.activeTab;
		Node processNode = (Node)processClickedEvent.getSource();
		String targetNodeId = null;
		System.out.println("event: " + processClickedEvent.toString());
		try
		{
			Node targetNode = (Node)processClickedEvent.getTarget();
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
		{
			this.selectedBlockId = id;
			if(processClickedEvent.getButton() == MouseButton.PRIMARY)
			{
				String clickedShape = processClickedEvent.getTarget().getClass().getSimpleName().toString();
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
								setConnectionTempWire("input", this.prelimProcessWireEndConnection);

							}
							if(this.effectArray[index].processMap.get(id).getOutputConnectorList().contains(connName))
							{
								this.prelimProcessWireStartConnection = new NamedBoundCoord(targetNodeId);
								connectorCoord = this.effectArray[index].processMap.get(id).getOutputConnectorCoord(connName);
								this.prelimProcessWireStartConnection.setCoord(connectorCoord);
								setConnectionTempWire("output", this.prelimProcessWireStartConnection);
							}
							if(this.effectArray[index].processMap.get(id).getParamConnectorList().contains(connName))
							{
								this.prelimControlWireEndConnection = new NamedBoundCoord(targetNodeId);
								connectorCoord = this.effectArray[index].processMap.get(id).getParamConnectorCoord(connName);
								this.prelimControlWireEndConnection.setCoord(connectorCoord);
								setConnectionTempWire("param", this.prelimControlWireEndConnection);



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
								this.tempWire = null;

							}
							else if(this.prelimControlWireStartConnection != null && this.prelimControlWireEndConnection != null)
							{
								String controlWireName = this.prelimControlWireStartConnection.getName() + ">" +
										this.prelimControlWireEndConnection.getName();
								this.addControlWire(controlWireName, this.prelimControlWireStartConnection.getCoord(),
										this.prelimControlWireEndConnection.getCoord(),"effect0",index);
								this.prelimControlWireStartConnection = null;
								this.prelimControlWireEndConnection = null;
								this.tempWire = null;

							}
						}

					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("connecting processes error: " + e);
					}
				}
				else if(this.dragged == false && SelectedId.isEmpty() && this.tempWire == null)
				{
					clearAllSelectIndicators();
					parameterEditorReference.clearEditor();
					this.effectArray[index].processMap.get(id).setSelectIndicator(true);
					this.editProcess(this.effectArray[index].processMap.get(id));
					this.prelimProcessWireStartConnection = null;
					this.prelimProcessWireEndConnection = null;
					this.prelimControlWireStartConnection = null;
					this.prelimControlWireEndConnection = null;
				}
			}
			else if(processClickedEvent.getButton() == MouseButton.SECONDARY)
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
				deleteProcessItem.setOnAction(evt->{
					System.out.println(this.selectedBlockId + "\t" + index);
					this.deleteProcess(this.selectedBlockId,index);
				});

				processContextMenu.getItems().add(flipProcessItem);
				processContextMenu.getItems().add(deleteProcessItem);
				processContextMenu.show(processNode, processClickedEvent.getScreenX(), processClickedEvent.getScreenY());
			}
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
		System.out.println("event: " + event.toString());
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
		if(event.getButton() == MouseButton.PRIMARY)
		{
			String clickedShape = event.getTarget().getClass().getSimpleName().toString();
			if(clickedShape.indexOf("Circle")>=0)
			{
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
							setConnectionTempWire("output", this.prelimControlWireStartConnection);
						}
					}

					if((this.prelimControlWireStartConnection != null && this.prelimControlWireEndConnection != null))
					{
						{
							String controlWireName = this.prelimControlWireStartConnection.getName() + ">" +
									this.prelimControlWireEndConnection.getName();
							this.addControlWire(controlWireName, this.prelimControlWireStartConnection.getCoord(),
									this.prelimControlWireEndConnection.getCoord(),"effect0",index);
							this.prelimControlWireStartConnection = null;
							this.prelimControlWireEndConnection = null;
							deleteControlWire("tempWire",0);
							this.tempWire = null;

						}
					}

				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("connecting controls error: " + e);
				}
			}
			else if(this.dragged == false)
			{
				clearAllSelectIndicators();
				parameterEditorReference.clearEditor();
				this.effectArray[index].controlMap.get(id).setSelectIndicator(true);
				this.editControl(this.effectArray[index].controlMap.get(id));
				this.prelimProcessWireStartConnection = null;
				this.prelimProcessWireStartConnection = null;
				this.prelimControlWireStartConnection = null;
				this.prelimControlWireStartConnection = null;
			}
		}
		else if(event.getButton() == MouseButton.SECONDARY)
		{
			ContextMenu controlContextMenu = new ContextMenu();
			System.out.println("Right-click control: " + event.toString());
			MenuItem deleteControlItem = new MenuItem("Delete");
			deleteControlItem.setOnAction(evt->{
				this.deleteControl(this.selectedBlockId,index);
			});
			controlContextMenu.getItems().add(deleteControlItem);
			controlContextMenu.show(controlNode, event.getScreenX(), event.getScreenY());
		}

	}



	/***************************************************************************************************/


	public void setCombo(FlxCombo combo)
	{

		this.combo = combo;
		clearAllSelectIndicators();
		parameterEditorReference.clearEditor();
		this.comboEditArea.clearNames();
		for(int i = 0; i < 2; i++)
		{
			this.drawingAreaCanvas[i].getChildren().clear();
			this.effectArray[i].processMap.clear();
			this.effectArray[i].controlMap.clear();

			this.effectArray[i].wireMap.clear();
			this.effectArray[i].controlWireMap.clear();
		}


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
						this.addProcess(process, this.effectArray[effectIndex].name, effectIndex);
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
						this.addWire(wire,this.effectArray[effectIndex].name,effectIndex);
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
						this.addControl(control,this.effectArray[effectIndex].name,effectIndex);
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
						this.addControlWire(controlWire,this.effectArray[effectIndex].name,effectIndex);
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

		List<String> names = this.getNames();
		this.comboName = names.get(0);
		this.effectArray[0].name = names.get(1);
		this.effectArray[0].abbr = names.get(2);
		this.effectArray[1].name = names.get(3);
		this.effectArray[1].abbr = names.get(4);

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

		List<String> names = this.getNames();
		this.comboName = names.get(0);
		this.effectArray[0].name = names.get(1);
		this.effectArray[0].abbr = names.get(2);
		this.effectArray[1].name = names.get(3);
		this.effectArray[1].abbr = names.get(4);

		int[] controlIndex = new int[2];
		controlIndex[0] = 0;
		controlIndex[1] = 0;
		String comboString = "{";
		try
		{
			comboString += "\"name\":\"" + this.comboName + "\",";
			comboString += "\"effectArray\":";
			String effectArrayString = "[";
			for(int effectIndex = 0; effectIndex < 2; effectIndex++)
			{
				String effectString = "{";

				effectString += "\"name\":\"" + this.effectArray[effectIndex].name + "\",";
				effectString += "\"abbr\":\"" + this.effectArray[effectIndex].abbr + "\",";
				effectString += "\"index\":" + effectIndex + ",";
				/******************************************************************************/
				String processArrayString = "\"processArray\":[";
				int processMapCount = this.effectArray[effectIndex].processMap.size();
				int processMapIndex = 0;
				for(String processKey : this.effectArray[effectIndex].processMap.keySet())
				{

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
					this.effectArray[effectIndex].wireMap.get(wireKey).setParentEffect(this.effectArray[effectIndex].name);
					processConnectionArrayString += this.effectArray[effectIndex].wireMap.get(wireKey).getProcessConnectionString();
					if(wireMapIndex++ < wireMapCount-1)
					{
						processConnectionArrayString += ",";
					}
					if(this.debugStatements) System.out.println(wireKey);
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
			String effectConnectionArrayString = "";
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
		System.out.println(comboString);
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
		List<FlxParameter> processDataList = process.getParamList();
		FlxFootswitch footswitch = process.getFootswitch();
		this.parameterEditorReference.setFootswitchSelector(footswitch);
		this.parameterEditorReference.setProcessParameters(processDataList);
	}


	public void editControl(FlxControl control)
	{
		List<FlxControlParameter> controlDataList = control.getParamList();
		this.parameterEditorReference.setLcdLabelDescriptionLabels();
		this.parameterEditorReference.setParameterControllerParameters(controlDataList);
		this.parameterEditorReference.setLcdLabelUpdateButton();
	}

	private Map<String,FlxConnector> createEffectIO(int index)
	{
		Map<String,FlxConnector> effectIO = new HashMap<String,FlxConnector>();



		FlxConnector input1 = new FlxConnector("input1", "output", 0, "(effect"+index+")", 20, 160);
		FlxConnector input2 = new FlxConnector("input2", "output", 1, "(effect"+index+")", 20, 400);
		FlxConnector output1 = new FlxConnector("output1", "input", 0, "(effect"+index+")", 970, 160);
		FlxConnector output2 = new FlxConnector("output2", "input", 1, "(effect"+index+")", 970, 400);

		effectIO.put("input1", input1);
		effectIO.put("input2", input2);
		effectIO.put("output1", output1);
		effectIO.put("output2", output2);

		return effectIO;
	}
	public String getCurrentEffectName()
	{
		return effectArray[activeTab].name;
	}
	public int getCurrentEffectIndex()
	{
		return activeTab;
	}

	public Effect[] getCurrentEffectArray()
	{
		return effectArray;
	}
	public Effect getCurrentEffectArray(int index)
	{
		return effectArray[index];
	}


	public List<String> getNames()
	{
		List<String> names = this.comboEditArea.getNames();
		boolean allNamesValid = true;
		for(int i = 0; i < names.size(); i++)
		{
			if(names.get(i).isEmpty()) allNamesValid = false;
		}

		if(allNamesValid == false)
		{
			if(errorStatements) System.out.println("one of the names is invalid");
		}

		return names;
	}

	public String getComboName()
	{
		return this.comboName;
	}

}
