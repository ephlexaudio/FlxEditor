package main;

//import java.io.CharArrayWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;
/*import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;*/
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
/*import java.util.Map;
import java.util.concurrent.TimeUnit;*/

import javax.json.Json;
//import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
//import javax.json.JsonReader;
import javax.json.JsonValue;

import diagramComponents.FlxCombo;
import diagramComponents.FlxCombo_Impl;
//import diagramComponents.FlxComponent_Impl;
import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
//import javafx.scene.Group;
import javafx.scene.Scene;
/*import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;*/
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
/*import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;*/
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main._FTP;


public class FlxMenuBar_Impl implements FlxMenuBar {
	boolean debugStatements = true;
	boolean errorStatements = true;
	
	List<String> comboNameList = new ArrayList<String>();
	List<MenuItem> comboList;
    Menu loadCombo; //= new Menu("Load Combo");
    Menu menuFile;
    Menu menuEdit;
    
	MenuBar menuBar;
	FlxDrawingArea drawingAreaReference;
	SystemUtility sysUtil = SystemUtility.getInstance();
	//SFTP sftp = new SFTP("root", "192.168.10.33");
	_FTP ftp = new _FTP("root", "root", "192.168.10.33");
	DataAccess dataAccess;/* = new DataAccess_HostImpl();
	//DataAccess dataAccess = new DataAccess_PedalImpl();*/
	
	FlxMenuBar_Impl()
	{
		this.menuBar = new MenuBar();
		this.menuBar.setId("menuBar");
		this.comboList = new ArrayList<MenuItem>();
		this.menuFile = new Menu("File");
	    MenuItem newCombo = new MenuItem("New Combo");
	    newCombo.setOnAction(actionEvent -> this.newComboHandler());
	    this.loadCombo = new Menu("Load Combo");
	    MenuItem saveCombo = new MenuItem("Save Combo");
	    saveCombo.setOnAction(actionEvent -> this.saveComboHandler());
	    MenuItem deleteCombo = new MenuItem("Delete Combo");
	    deleteCombo.setOnAction(actionEvent -> this.deleteComboHandler(this.drawingAreaReference.getCurrentCombo()));
	    
	    
		if(sysUtil.dataAccessStatus() == true)
		{
			//this.getData();
		}
		else
		{
			//System.out.println("data access not acquired.");
		}

	    //loadCombo.setOnAction(actionEvent -> menuBarHandler.loadComboHandler());
	    menuFile.getItems().addAll(newCombo,loadCombo,saveCombo,deleteCombo);
	    
		this.menuEdit = new Menu("Edit");
		MenuItem updatePedal = new MenuItem("Update Pedal Software");
		updatePedal.setOnAction(actionEvent -> this.updatePedalSoftware());
		this.menuEdit.getItems().add(updatePedal);
	    this.menuBar.getMenus().addAll(this.menuFile, this.menuEdit);
	}
	
	public MenuBar getMenuBar()
	{
		return this.menuBar;
	}
	
	public void getData()
	{
		comboList.clear();
		String dataAccessMode = sysUtil.dataAccessMode();
		if(/*sysUtil.dataAccessMode()*/dataAccessMode.compareTo("host") == 0)
		{
			dataAccess = new DataAccess_HostImpl();
			try
			{
				comboNameList = this.getComboNames();
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("_MenuBar::getComboNames error: " + e);
			}
			
		    //menuBar.setFill(Color.OLDLACE);
		    
	        //double[] polygonCoords = new double[10];
		    // --- Menu File
		    //_MenuBar menuBarHandler = new _MenuBar();
		    
		    for(String comboName:comboNameList)
		    {
		    	MenuItem combo = new MenuItem();
		    	combo.setText(comboName);
		    	combo.setOnAction(actionEvent -> this.loadComboHandler(combo.getText()));
		    	comboList.add(combo);
		    }
		    
		    loadCombo.getItems().addAll(comboList);
			
		}
		else
		{
			dataAccess = new DataAccess_PedalImpl();
			while(sysUtil.getCommPortStatus())
			{
				if(dataAccess.confirmConnection()) break;
			}
			
			if(sysUtil.getCommPortStatus() == true)
			{
				try
				{
					comboNameList = this.getComboNames();
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("_MenuBar::getComboNames error: " + e);
				}
				
			    //menuBar.setFill(Color.OLDLACE);
			    
		        //double[] polygonCoords = new double[10];
			    // --- Menu File
			    //_MenuBar menuBarHandler = new _MenuBar();
			    
			    for(String comboName:comboNameList)
			    {
			    	MenuItem combo = new MenuItem();
			    	combo.setText(comboName);
			    	combo.setOnAction(actionEvent -> this.loadComboHandler(combo.getText()));
			    	comboList.add(combo);
			    }
			    
			    loadCombo.getItems().addAll(comboList);
			}			
		}

	}
	
	
	public void setDrawingArea(FlxDrawingArea drawingArea)
	{
		this.drawingAreaReference = drawingArea;
	}
	
		
    private List<String> getComboNames() throws IOException
    {
    	List<String> tempComboList = null;
    	if(this.dataAccess.checkCommPortStatus() == true)
    	{
        	tempComboList = this.dataAccess.getComboList();//new ArrayList<String>();
    	}
    	else
    	{
    		tempComboList = this.dataAccess.getComboList();//new ArrayList<String>();
    	}
    	    	
       	return tempComboList;
    }

	private void newComboHandler()
	{
		if(this.debugStatements) System.out.println("newComboHandler");
		
		createNewCombo();
		
	}
	
	private void saveComboHandler()
	{
		String comboName = this.drawingAreaReference.getCurrentCombo();
		String comboString = this.drawingAreaReference.getComboString();
		List<String> comboList = this.dataAccess.sendComboString(comboName, comboString);
		//List<String> comboList = this.dataAccess.sendCombo(comboName, comboString);
		//List<String> comboList = this.dataAccess.getComboList();
		updateComboList(comboList);
	}
	
	private void deleteComboHandler(String name)
	{
		if(this.debugStatements) System.out.println("deleteComboHandler");
		List<String> comboList = this.dataAccess.deleteCombo(name);
		//List<String> comboList = this.dataAccess.getComboList();
		updateComboList(comboList);
	}
	
	private void loadComboHandler(String name)
	{
		if(this.debugStatements) System.out.println("getting combo");
		if(this.debugStatements) System.out.println("loadComboHandler: " + name);
		boolean success = false;
		
		while(success == false)
		{
			try
			{
				
				FlxCombo combo = new FlxCombo_Impl(this.dataAccess.getCombo(name));
				if(this.debugStatements) System.out.println("combo retrieved");
				this.drawingAreaReference.setCombo(combo);
				success = true;
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("FlxMenuBar::loadComboHandler: " + e);
			}		
			
		}
		
		if(this.debugStatements) System.out.println("combo loaded");
	}
	

	private void createNewCombo()
	{
		if(this.debugStatements) System.out.println("creating new combo");
		FlxCombo newCombo;
		JsonObjectBuilder newComboJsonObject = Json.createObjectBuilder();
		JsonObject newComboJsonObjectBuilt = null;
		newComboJsonObject.add("name", "new");
		
		JsonArrayBuilder effectArray = Json.createArrayBuilder();
		
		JsonArrayBuilder processArray = Json.createArrayBuilder();
		
		
		JsonArrayBuilder connectionArray0 = Json.createArrayBuilder();
		connectionArray0.add(
				Json.createObjectBuilder()
				.add("parentEffect", "effect0")
				.add("src", Json.createObjectBuilder().add("object", "(effect0)").add("port", "input1")
						.add("x", 20).add("y", 160).build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect0)").add("port", "output1")
						.add("x", 970).add("y", 160).build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("parentEffect", "effect1")
				.add("src", Json.createObjectBuilder().add("object", "(effect0)").add("port", "input2")
						.add("x", 20).add("y", 400).build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect0)").add("port", "output2")
						.add("x", 970).add("y", 400).build())
				.build());
		JsonValue connectionArrayBuilt0 = connectionArray0.build();

		
		JsonArrayBuilder connectionArray1 = Json.createArrayBuilder();	
		connectionArray1.add(
				Json.createObjectBuilder() 
				.add("src", Json.createObjectBuilder().add("object", "(effect1)").add("port", "input1")
						.add("x", 20).add("y", 160).build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect1)").add("port", "output1")
						.add("x", 970).add("y", 160).build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("src", Json.createObjectBuilder().add("object", "(effect1)").add("port", "input2")
						.add("x", 20).add("y", 400).build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect1)").add("port", "output2")
						.add("x", 970).add("y", 400).build())
				.build());
		JsonValue connectionArrayBuilt1 = connectionArray1.build();

		JsonArrayBuilder controlArray = Json.createArrayBuilder();
		JsonArrayBuilder controlConnectionArray = Json.createArrayBuilder();
		
		JsonObjectBuilder effect0 = Json.createObjectBuilder();
		effect0.add("index",0);
		effect0.add("name","effect0");
		effect0.add("abbr","fx0");
		effect0.add("processArray",processArray);
		effect0.add("connectionArray",connectionArrayBuilt0);
		effect0.add("controlArray",controlArray);
		effect0.add("controlConnectionArray",controlConnectionArray);
		JsonObject effect0Built = effect0.build();
		
		
		JsonObjectBuilder effect1 = Json.createObjectBuilder();
		effect1.add("index",1);
		effect1.add("name","effect1");
		effect1.add("abbr","fx1");
		effect1.add("processArray",processArray);
		effect1.add("connectionArray",connectionArrayBuilt1);
		effect1.add("controlArray",controlArray);
		effect1.add("controlConnectionArray",controlConnectionArray);
		JsonObject effect1Built = effect1.build();
		
		
		JsonArrayBuilder effectConnectionArray = Json.createArrayBuilder();
		
		effectConnectionArray.add(
				Json.createObjectBuilder()
				.add("index", 0)
				.add("src", Json.createObjectBuilder().add("object", "system").add("port", "capture_1").build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect0)").add("port", "input1").build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("index", 1)
				.add("src", Json.createObjectBuilder().add("object", "system").add("port", "capture_2").build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect0)").add("port", "input2").build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("index", 2)
				.add("src", Json.createObjectBuilder().add("object", "(effect0)").add("port", "output1").build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect1)").add("port", "input1").build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("index", 3)
				.add("src", Json.createObjectBuilder().add("object", "(effect0)").add("port", "output2").build())
				.add("dest", Json.createObjectBuilder().add("object", "(effect1)").add("port", "input2").build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("index", 4)
				.add("src", Json.createObjectBuilder().add("object", "(effect1)").add("port", "output1").build())
				.add("dest", Json.createObjectBuilder().add("object", "system").add("port", "playback_1").build())
				.build())
		.add(
				Json.createObjectBuilder() 
				.add("index", 5)
				.add("src", Json.createObjectBuilder().add("object", "(effect1)").add("port", "output2").build())
				.add("dest", Json.createObjectBuilder().add("object", "system").add("port", "playback_2").build())
				.build());
		
		JsonValue effectConnectionArrayBuilt = effectConnectionArray.build();
		
		effectArray.add(effect0Built);
		effectArray.add(effect1Built);
		newComboJsonObject.add("effectArray", effectArray.build());
		newComboJsonObject.add("effectConnectionArray",effectConnectionArrayBuilt);
		newComboJsonObjectBuilt = newComboJsonObject.build();
		newCombo = new FlxCombo_Impl(newComboJsonObjectBuilt);
		if(this.debugStatements) System.out.println(newComboJsonObjectBuilt);
		if(this.debugStatements) System.out.println("new combo created");
        this.drawingAreaReference.setCombo(newCombo);
        if(this.debugStatements) System.out.println("new combo loaded");
	}
	
	public void updateComboList(List<String> comboNameList)
	{
		this.loadCombo.getItems().clear();
		this.comboList = new ArrayList<MenuItem>();
	    for(String comboName:comboNameList)
	    {
	    	MenuItem combo = new MenuItem();
	    	combo.setText(comboName);
	    	combo.setOnAction(actionEvent -> this.loadComboHandler(combo.getText()));
	    	this.comboList.add(combo);
	    }
	    this.loadCombo.getItems().addAll(this.comboList);
	}
		
	public class ProgressForm {
	    private Stage dialogStage;
	    private final ProgressBar pb = new ProgressBar();
	    private final Label progInfo = new Label();

	    public ProgressForm() 
	    {
	        dialogStage = new Stage();
	        dialogStage.initStyle(StageStyle.UTILITY);
	        dialogStage.setResizable(false);
	        dialogStage.initModality(Modality.APPLICATION_MODAL);

	        // PROGRESS BAR
	        //final Label label = new Label();
	        //label.setText("alerto");
	        progInfo.setMaxWidth(250);
	        progInfo.setMinWidth(250);
	        progInfo.setText("Updating pedal software...");
	        final VBox hb = new VBox();
	        hb.setSpacing(5);
	        hb.setAlignment(Pos.CENTER);
	        hb.getChildren().addAll(pb, progInfo);
	        
	        Scene scene = new Scene(hb);
	        
	        dialogStage.setScene(scene);
	    }
	    
	    public void activateProgressBar(DoubleProperty progress)  
	    {
	    	pb.progressProperty().bind(ftp.getProgressProperty());
	    	dialogStage.show();
	    }
	    
	    public void closeProgressForm()
	    {
	    	this.dialogStage.close();
	    }
	}
	
	public void updatePedalSoftware()
	{
		Stage fileDialog = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Update File");
		File ofxMainFile = fileChooser.showOpenDialog(fileDialog);
		
		if(this.debugStatements) System.out.println("file: " + ofxMainFile.getName() + "\tpath: " + ofxMainFile.getPath());
		ProgressForm updateProg = new ProgressForm();
		
		updateProg.activateProgressBar(ftp.getProgressProperty());
		
        Task<Void> update = new Task<Void>()
        {
        	public Void call() throws InterruptedException
        	{
        		      		
        		if(ftp.sendFile(ofxMainFile)/*ftp.openSession()*/)
        		{
        			//ftp.sendFile(ofxMainFile);	
        		}
        		else
        		{
        			if(errorStatements) System.out.println("SFTP session connect failed.");
        		}
  				return null;           		
        	}
        };        		
        
        Thread updateThread = new Thread(update);
        updateThread.start();
        update.setOnSucceeded(event -> {
        	updateProg.closeProgressForm();
        });
	}
	
}
