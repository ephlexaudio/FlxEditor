package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import diagramComponents.FlxCombo;
import diagramComponents.FlxCombo_Impl;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main._FTP;
import main.App;

public class FlxMenuBar_Impl implements FlxMenuBar {
	boolean debugStatements = true;
	boolean errorStatements = true;
	HBox menu_applyButton = new HBox();
	List<String> comboNameList = new ArrayList<String>();
	List<MenuItem> comboList;
    Menu loadCombo;
    Menu menuFile;
    Menu menuEdit;
    Button applyChanges = new Button("Apply Changes");
	MenuBar menuBar;
	FlxDrawingArea drawingAreaReference;
	App appReference;
	SystemUtility sysUtil = SystemUtility.getInstance();
	_FTP ftp = new _FTP("root", "root", "192.168.10.33");
	DataAccess dataAccess;

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
	    SeparatorMenuItem divider = new SeparatorMenuItem();
	    MenuItem importCombo = new MenuItem("Import Combo");
	    importCombo.setOnAction(actionEvent -> this.importComboHandler());
	    MenuItem exportCombo = new MenuItem("Export Combo");
	    exportCombo.setOnAction(actionEvent -> this.exportComboHandler());

	    menuFile.getItems().addAll(newCombo,loadCombo,saveCombo,deleteCombo, divider, importCombo, exportCombo);

		this.menuEdit = new Menu("Updates");
		MenuItem updatePedal = new MenuItem("Update Pedal Software");
		updatePedal.setOnAction(actionEvent -> this.updatePedalSoftware());
		this.menuEdit.getItems().add(updatePedal);
	    this.menuBar.getMenus().addAll(this.menuFile, this.menuEdit);
	}

	public MenuBar getMenuBar()
	{
		return this.menuBar;
	}

	public boolean getData()
	{
		boolean success = false;
		comboList.clear();
		String dataAccessMode = sysUtil.dataAccessMode();
		if(dataAccessMode.compareTo("host") == 0)
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

		    for(String comboName:comboNameList)
		    {
		    	MenuItem combo = new MenuItem();
		    	combo.setText(comboName);
		    	combo.setOnAction(actionEvent -> this.loadComboHandler(combo.getText()));
		    	comboList.add(combo);
		    }

		    loadCombo.getItems().addAll(comboList);
			if(comboList.size() > 0) success = true;
		}
		else
		{
			dataAccess = new DataAccess_PedalImpl();

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


				if(comboNameList.isEmpty() == false)
				{
				    for(String comboName:comboNameList)
				    {
				    	MenuItem combo = new MenuItem();
				    	combo.setText(comboName);
				    	combo.setOnAction(actionEvent -> this.loadComboHandler(combo.getText()));
				    	comboList.add(combo);
				    }

				    loadCombo.getItems().addAll(comboList);
				    success = true;
				}
				else success = false;

			}
		}
		return success;
	}


	public void setDrawingArea(FlxDrawingArea drawingArea)
	{
		this.drawingAreaReference = drawingArea;
	}


    private List<String> getComboNames() throws IOException
    {
    	List<String> tempComboList = this.dataAccess.getComboList();
    	java.util.Collections.sort(tempComboList);

       	return tempComboList;
    }

	private void newComboHandler()
	{
		if(this.debugStatements) System.out.println("newComboHandler");

		createNewCombo();

	}

	public void saveComboHandler()
	{
		String comboName = this.drawingAreaReference.getCurrentCombo();
		String comboString = this.drawingAreaReference.getComboString();
		List<String> comboList = this.dataAccess.sendComboString(comboName, comboString);
		updateComboList(comboList);
	}

	private void deleteComboHandler(String name)
	{
		if(this.debugStatements) System.out.println("deleteComboHandler");
		List<String> comboList = this.dataAccess.deleteCombo(name);
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

	public void importComboHandler()
	{
		Stage fileDialog = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Combo File to Import");
		File comboImport = fileChooser.showOpenDialog(fileDialog);


  		try
  		{
         	String importComboString = new String(Files.readAllBytes(comboImport.toPath()));

      		List<String> comboList = dataAccess.sendComboString(comboImport.getName(), importComboString);
      		JsonReader importReader = Json.createReader(new StringReader(importComboString));

      		FlxCombo combo = new FlxCombo_Impl(importReader.readObject());
					if(debugStatements) System.out.println("combo imported");
					drawingAreaReference.setCombo(combo);
					updateComboList(comboList);

  		}
  		catch(Exception e)
  		{
  			System.out.println("Error reading import combo: " + e);
  		}

	}

	public void exportComboHandler()
	{
		Stage exportDialog = new Stage();
		FileChooser fileExporter = new FileChooser();
		fileExporter.setTitle("Open Combo File to Export");
		String exportString = drawingAreaReference.getComboString();
		String comboName = drawingAreaReference.getComboName() + ".txt";
		System.out.println("Exported combo file: " + comboName);
		fileExporter.setInitialFileName(comboName);
		File comboExport = fileExporter.showSaveDialog(exportDialog);


        Task<Void> exportCombo = new Task<Void>()
        {
        	public Void call() throws InterruptedException
        	{
						try
						{


							if(exportString.isEmpty() == false)
							{
					            FileWriter fileWriter = null;

					            fileWriter = new FileWriter(comboExport);
					            fileWriter.write(exportString);
					            fileWriter.close();

							}
						}
						catch(Exception e)
						{

						}
        		return null;
        	}
        };

        Thread exportThread = new Thread(exportCombo);

        exportCombo.setOnSucceeded(event -> {
        	exportDialog.close();
        });
        exportThread.start();
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

	public class PedalUpdateProgressForm {
	    private Stage dialogStage;
	    private final ProgressBar pb = new ProgressBar();
	    private final Label progInfo = new Label();

	    public PedalUpdateProgressForm()
	    {
	        dialogStage = new Stage();
	        dialogStage.initStyle(StageStyle.UTILITY);
	        dialogStage.setResizable(false);
	        dialogStage.initModality(Modality.APPLICATION_MODAL);

	        // PROGRESS BAR
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

	    public void closePedalUpdateProgressForm()
	    {
	    	this.dialogStage.close();
	    }
	}

	public void updatePedalSoftware()
	{
		try
		{
			Stage fileDialog = new Stage();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Update File");
			File ofxMainFile = fileChooser.showOpenDialog(fileDialog);
			if(ofxMainFile == null) return;
			if(this.debugStatements) System.out.println("file: " + ofxMainFile.getName() + "\tpath: " + ofxMainFile.getPath());
			PedalUpdateProgressForm updateProg = new PedalUpdateProgressForm();

			updateProg.activateProgressBar(ftp.getProgressProperty());

	        Task<Void> update = new Task<Void>()
	        {
	        	public Void call() throws InterruptedException
	        	{
							if(ftp.sendFile(ofxMainFile,"Updates") == false)
		      		{
		      			if(errorStatements) System.out.println("FTP session connect failed.");
		      		}
		  				return null;
	        	}
	        };

	        Thread updateThread = new Thread(update);
	        updateThread.start();
	        update.setOnSucceeded(event -> {
	        	updateProg.closePedalUpdateProgressForm();
	        });

		}
		catch(Exception e)
		{
			if(debugStatements) System.out.println("updatePedalSoftware error: " + e);
		}
	}


}
