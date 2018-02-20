package main;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;
/*import java.io.RandomAccessFile;
import java.io.StringReader;*/
import java.net.URLDecoder;
/*import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;*/
import javax.json.JsonValue;
//import javax.swing.event.DocumentEvent.EventType;


import diagramComponents.FlxCombo_Impl;
//import diagramComponents.FlxComponent;
//import diagramComponents.Component;
/*import diagramComponents.FlxComponent_Impl;
import diagramComponents.FlxControl;
import diagramComponents.FlxProcess_Impl;*/
import javafx.application.Application;
//import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
/*import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;*/
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
/*import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;*/
import javafx.scene.Scene;
/*import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;*/
import javafx.scene.control.*;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
/*import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;*/
import javafx.scene.layout.BorderPane;
/*import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;*/
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
/*import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;*/
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
//import javafx.stage.WindowEvent;



public class App extends Application {
	boolean debugStatements = false;
	boolean errorStatements = true;

	FlxCombo_Impl combo;
	static public SystemUtility sysUtil = SystemUtility.getInstance();
	String comboDirectoryPath;
	String componentDirectoryPath;
	String controlDirectoryPath;
	//Map<String,FlxComponent> componentMap = new HashMap<String,FlxComponent>();
	//Map<String,FlxControl> controlMap = new HashMap<String,FlxControl>();
	Map<String,JsonValue> parameterControlTypeMap = new HashMap<String, JsonValue>();
	//final Canvas sidebarCanvas = new Canvas(100,1000);
	//Group sidebarCanvas = new /*Canvas*/Group();
	//final Canvas drawingAreaCanvas = new Canvas(500,500);
	FlxMenuBar menuBar;
	FlxSidebar sidebar;
	FlxDrawingArea drawingArea;
	//Thread initThread = new Thread();
	FlxParameterEditor_Impl parameterEditor;
	ScrollPane parameterEditorAreaScroll = new ScrollPane();
	static String absoluteFilePath;
	public void start(Stage stage) throws IOException {
	    //comboNameList = getComboNames();
        //parameterControlTypeMap = getParameterControlTypes();
    	
    	/*FlxMenuBar _menuBar = null;
    	FlxSidebar sidebar = null;
    	FlxDrawingArea drawingArea = null;*/
		
	    /******************************** Create Menu Bar ******************************************/

    	//this.menuBar = new FlxMenuBar_Impl();
    	this.menuBar = new FlxMenuBar_Impl();

        VBox menuBarBox = new VBox();
        //Scene menuBarScene = new Scene(menuBarBox);
        ((VBox) menuBarBox).getChildren().add(this.menuBar.getMenuBar());
       
	    /******************************* Create Side Bar ***********************************/
       
        this.sidebar = new FlxSidebar_Impl();
	    
		ScrollPane processComponentSideBarPane = new ScrollPane(sidebar.getProcessComponentSideBar());
		processComponentSideBarPane.setId("sideBar");
		processComponentSideBarPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		//processComponentSideBarPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		ScrollPane controlComponentSideBarPane = new ScrollPane(sidebar.getControlComponentSideBar());
		controlComponentSideBarPane.setId("sideBar");
		controlComponentSideBarPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		
     	SplitPane sideBarPane = new SplitPane();
      	sideBarPane.getItems().addAll(processComponentSideBarPane, controlComponentSideBarPane);
      	sideBarPane.setDividerPositions(0.7f, 0.3f);
      	sideBarPane.setOrientation(Orientation.VERTICAL);
      	
      	sideBarPane.setMinWidth(170.0);
      	sideBarPane.setMaxWidth(170.0);

	    /****************************** Create Drawing/Editor Area ******************************/
	    
      	this.drawingArea = new FlxDrawingArea_Impl();
      	//this.drawingArea.createProcessCountMap();
	    final SplitPane drawingAreaEditor = new SplitPane();
	    /****************************** Create Drawing Area  ***********************************/
	    
	    
	    //Group drawingAreaGroup = new Group(drawingArea);
	    final Pane drawingAreaPane = new Pane(drawingArea.getDrawingArea());
	    drawingAreaPane.setId("drawingArea");
	    
	    /************************* Create "Add Parameter Control" Button **********************
	    
	    Button addParameterController = new Button();
	    addParameterController.setText("Add Parameter Controller");
	    final Pane buttonPane = new Pane(addParameterController);
	    buttonPane.setMinHeight(30.0);
	    buttonPane.setMaxHeight(40.0);
	    /******************* Create Process/Control Parameter Editor Area  ***********************/
	    this.parameterEditor = new FlxParameterEditor_Impl();
	    this.drawingArea.setEditor(this.parameterEditor);
	    final GridPane parameterEditorAreaPane = new GridPane();
	    
	    parameterEditorAreaPane.setId("parameterEditor");
	    final ScrollPane parameterEditorAreaScroll = new ScrollPane(parameterEditor.getParamEditor());
	    
	    /****************************************************************************************/
	    drawingAreaEditor.getItems().addAll(drawingAreaPane,parameterEditorAreaScroll);
	    drawingAreaEditor.setDividerPositions(0.7f, 0.25f);
	    drawingAreaEditor.setOrientation(Orientation.VERTICAL);

	    SplitPane sidebarDrawingAreaEditor = new SplitPane();
	    sidebarDrawingAreaEditor.getItems().addAll(sideBarPane,drawingAreaEditor);
	    sidebarDrawingAreaEditor.setDividerPositions(0.15f, 0.85f);
	    sidebarDrawingAreaEditor.setOrientation(Orientation.HORIZONTAL);
	    
	    this.menuBar.setDrawingArea(this.drawingArea);
      	this.sidebar.setDrawingArea(this.drawingArea);
      	this.drawingArea.setSidebar(this.sidebar);
	    try
	    {
		    stage.setTitle("FLX Editor V0.2");

		    BorderPane mainBox = new BorderPane();
		    Scene mainScene = new Scene(mainBox);//, 1000, 600);
		    File f = new File("application.css");
		    absoluteFilePath = f.getAbsolutePath().replace("\\", "/");
		    String cssPath = "file:///" +  absoluteFilePath;//f.getAbsolutePath().replace("\\", "/");
		    System.out.println("cssPath: " + cssPath);
		    mainScene.getStylesheets().add(cssPath);
		    
		    mainBox.setTop(menuBarBox);
		    mainBox.setCenter(sidebarDrawingAreaEditor);

		    stage.setScene(mainScene);
		    ProgressForm initStatus = new ProgressForm(this.menuBar, this.sidebar);
		    
		    //if(sysUtil.dataAccessMode().contains("pedal"))
		    {
			    stage.setOnShown(event->{
			    	
			    	//initStatus = new ProgressForm(this.menuBar, this.sidebar);
			    	
		            /*Task<Void> initTask = new Task<Void>() {
		                @Override
		                public Void call() throws InterruptedException {
		                	
		                    updateProgress(0, 3);
		                	
		                    updateMessage("Getting USB port");
			    			sysUtil.getDataAccess();
			    			if(sysUtil.getCommPortStatus())
			    			{
			    				
			                    updateProgress(1, 3);
			                    updateMessage("Getting combo names");
				    			menuBar.getData();		    		    				
			    			}
			    			if(sysUtil.getCommPortStatus())
			    			{
			                    updateProgress(2, 3);
			                    updateMessage("Getting components for side bar");
				    			sidebar.initSidebarProcessComponents();
			                    updateProgress(3, 3);		    				
			    			}
		                    return null ;
		                }
		            };

		            // binds progress of progress bars to progress of task:	            

		            initTask.setOnSucceeded(event2 -> {
		            	initStatus.getDialogStage().close();
		            });
			    	Thread initThread = new Thread(initTask);*/
			    	initStatus.activateProgressBar();
		            initStatus.dialogStage.setOnCloseRequest(event3->{
				    	sysUtil.shutdownPort();
				    });
		            initStatus.getDialogStage().show();
			    	
		    		//initThread.start();
			    	
			    });
		    }
		    /*else
		    {
    			menuBar.getData();		    		    				
    			sidebar.initSidebarProcessComponents();
		    	
		    }*/
		    stage.setOnCloseRequest(event -> {
		    	
		    	System.exit(0);
		    });
		    stage.show();
							    	    	
	    }
	    catch(Exception e)
	    {
	    	if(this.errorStatements) System.out.println(e);
	    }
	    
	}
	
    public static class ProgressForm 
    {
        private Stage dialogStage;
        private final ProgressBar pb = new ProgressBar();
        private final Label progInfo = new Label();
        private final Button cancelUsbConnectButton = new Button();//();
        private final ComboBox<String> ports = new ComboBox<String>();
    	List<String> optionList = new ArrayList<String>();
        private FlxMenuBar menubarReference;
        private FlxSidebar sidebarReference;
        //private final ProgressIndicator pin = new ProgressIndicator();

        public ProgressForm(FlxMenuBar menubarReference, FlxSidebar sidebarReference) 
        {
        	this.menubarReference = menubarReference;
        	this.sidebarReference = sidebarReference;
        	cancelUsbConnectButton.setText("Cancel USB Connection");
        	cancelUsbConnectButton.setOnMouseClicked(event->{
        		sysUtil.setDataAccessMode("host");
        		closingProgressForm();
        	});
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // PROGRESS BAR
            //final Label label = new Label();
            //label.setText("alerto");
            progInfo.setMaxWidth(250);
            progInfo.setMinWidth(250);
            pb.setProgress(-1F);
            //pin.setProgress(-1F);
            
            final HBox buttons = new HBox();
            buttons.getChildren().addAll(ports, cancelUsbConnectButton);
            buttons.setSpacing(5);
            final VBox hb = new VBox();
            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(pb, progInfo, buttons/*, pin*/);
            
            
            Scene scene = new Scene(hb);
            dialogStage.setMinWidth(350);
            dialogStage.setScene(scene);
        }

        private void closingProgressForm()
        {
        	String dataAccessMode = sysUtil.getDataAccessMode();
        	if(dataAccessMode.compareTo("host") == 0)
        	{
            	System.out.println("closing Progress Form");
            	sysUtil.initializeSystemUtility(System.getProperty("os.name"),null);
            	this.menubarReference.getData();		    		    				
            	this.sidebarReference.initSidebarProcessComponents();
            	this.getDialogStage().close();
        	}
        }
        
        public void activateProgressBar(/*final Task<?> task*/)  
        {
        	//progInfo.setText("Select USB port");    	
            Task<Void> initTask = new Task<Void>() 
            {
                @Override
                public Void call() throws InterruptedException 
                {                	
                	System.out.println("initTask");                    
 					updateProgress(0, 3);
	    			sysUtil.getPedalDataAccess();
	    			if(sysUtil.getCommPortStatus())
	    			{
	                    updateProgress(1, 3);
	                    updateMessage("Getting combo names");
		    			menubarReference.getData();		    		    				
	    			}
	    			/*if(sysUtil.getCommPortStatus())
	    			{
	                    updateProgress(2, 3);
	                    updateMessage("Getting combo names2");
		    			menubarReference.getData();		    		    				
	    			}*/
	    			if(sysUtil.getCommPortStatus())
	    			{
	                    updateProgress(2, 3);
	                    updateMessage("Getting components for side bar");
		    			sidebarReference.initSidebarProcessComponents();
	                    updateProgress(3, 3);		    				
	    			} 			
	    			
                    return null ;
                }                     
            };      	        	
            
            Task<Void> getPorts = new Task<Void>()
            {
            	public Void call() throws InterruptedException
            	{
            		updateMessage("Plugin in USB cable");

            		while(optionList.isEmpty() && dialogStage.isShowing())
                	{
                		optionList = sysUtil.listPorts();
                		try 
                		{
        					TimeUnit.SECONDS.sleep(1);
        				} catch (InterruptedException e) 
                		{
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
                	}
                	
                	ObservableList<String> options = 
                		    FXCollections.observableArrayList(optionList);
                	ports.setItems(options);
                	updateMessage("Select USB port");
					return null;           		
            	}
            };
        	/*List<String> optionList = new ArrayList<String>();
        	
        	while(optionList.isEmpty())
        	{
        		optionList = sysUtil.listPorts();
        		try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	
        	ObservableList<String> options = 
        		    FXCollections.observableArrayList(optionList);
        	ports.setItems(options);*/
        	
            //progInfo.setText("Select USB port");
            
            
            Thread initThread = new Thread(initTask);
            initTask.setOnSucceeded(event2 -> 
            {
            	this.getDialogStage().close();   	
            	sysUtil.setDataAccessMode("pedal");
            	sysUtil.initializeSystemUtility(System.getProperty("os.name"), null/*args[0]*/);
            });
            //initThread.start();
            dialogStage.show();
            
            Thread getPortsThread = new Thread(getPorts);
            getPorts.setOnSucceeded(event3 -> 
            {
            	   	
            });
            getPortsThread.start();
       	    

            ports.valueProperty().addListener(new ChangeListener<String>() 
        	{
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue,
						String newValue) 
				{
		            pb.progressProperty().bind(initTask.progressProperty());
		            progInfo.textProperty().bind(initTask.messageProperty());
					System.out.println("observable: " + observable);
					System.out.println("oldValue: " + oldValue);
					System.out.println("newValue: " + newValue);
					sysUtil.setCommPort(newValue);
					System.out.println("Comm port set: " + newValue);
					initThread.start();
				}
        	});
        	
            progInfo.textProperty().bind(getPorts.messageProperty());
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
        
        
    }

    public static void main(String[] args) throws IOException {

	    try
	    {
	    	SystemUtility sysUtil = SystemUtility.getInstance();

	    	System.out.println("arg count: " + args.length);
	    	
	    	//************** Get the JAR file path **************************
		    File f = new File("application.css");
		    //absoluteFilePath = f.getAbsolutePath().replace("\\", "/").replace("application.css", "");
		    String path = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();// absoluteFilePath;//
	    	System.out.println("path: " + path);
	    	String jarPath = URLDecoder.decode(path, "UTF-8");  
	    	jarPath = jarPath.replaceAll("target/classes/", "");
	    	System.out.println("jarPath: " + jarPath);
	    	
	    	
	    	//************** Get the Component, Control, and Combo directories using the jarPath variable *****
	    	sysUtil.setComponentDirectoryPath(/*args[0]*/jarPath + "Components");
	    	sysUtil.setControlDirectoryPath(/*args[1]*/jarPath + "Controls");
	    	sysUtil.setComboDirectoryPath(/*args[2]*/jarPath + "Combos");
	    	/*if(args.length == 0) // get data from pedal
	    	{
		    	//sysUtil.initializeSystemUtility(System.getProperty("os.name"), null;	    		
	    	}
	    	else // get data from host PC
	    	{
		    	//sysUtil.initializeSystemUtility(System.getProperty("os.name"), args[0], args[1], args[2]);
	    	}*/
	    }
		catch(Exception e)
	    {
			System.out.println("sysUtil error: " + e);
	    }

        launch(args);
    }
}



