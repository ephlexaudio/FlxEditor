package main;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.JsonValue;

import diagramComponents.FlxCombo_Impl;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class App extends Application {
	boolean debugStatements = false;
	boolean errorStatements = true;

	FlxCombo_Impl combo;
	static public SystemUtility sysUtil = SystemUtility.getInstance();
	String comboDirectoryPath;
	String componentDirectoryPath;
	String controlDirectoryPath;
	Map<String,JsonValue> parameterControlTypeMap = new HashMap<String, JsonValue>();
	FlxMenuBar menuBar;
	FlxSidebar sidebar;
	FlxDrawingArea drawingArea;
	ComboEditArea comboEditArea;
	TabPane tabbedDrawingArea;
	TabPane drawingAreaPane;
	FlxParameterEditor_Impl parameterEditor;
	ScrollPane parameterEditorAreaScroll = new ScrollPane();
	static String absoluteFilePath;
	TabPane sideBarTabPane = new TabPane();
	Tab[] sideBarTabs = new Tab[2];
	BorderPane mainBox = null;
	Scene mainScene = null;


	public void start(Stage stage) throws IOException {
		/******************************** Create Menu Bar ******************************************/

		this.menuBar = new FlxMenuBar_Impl();

		VBox menuBarBox = new VBox();
		((VBox) menuBarBox).getChildren().add(this.menuBar.getMenuBar());


		/******************************* Create Side Bar ***********************************/

		this.sidebar = new FlxSidebar_Impl();

		ScrollPane processComponentSideBarPane = new ScrollPane(sidebar.getProcessComponentSideBar());
		processComponentSideBarPane.setId("sideBar");
		processComponentSideBarPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		ScrollPane controlComponentSideBarPane = new ScrollPane(sidebar.getControlComponentSideBar());
		controlComponentSideBarPane.setId("sideBar");
		controlComponentSideBarPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		this.sideBarTabs[0] = new Tab();
		this.sideBarTabs[0].setText("Process");
		this.sideBarTabs[0].setId("proc");
		this.sideBarTabs[0].setClosable(false);
		this.sideBarTabs[0].setContent(processComponentSideBarPane);
		this.sideBarTabs[1] = new Tab();
		this.sideBarTabs[1].setText("Control");
		this.sideBarTabs[1].setId("cont");
		this.sideBarTabs[1].setClosable(false);
		this.sideBarTabs[1].setContent(controlComponentSideBarPane);
		this.sideBarTabPane.getTabs().add(this.sideBarTabs[0]);
		this.sideBarTabPane.getTabs().add(this.sideBarTabs[1]);
		sideBarTabPane.setMinWidth(160.0);
		sideBarTabPane.setMaxWidth(160.0);

		/****************************** Create Drawing/Editor Area ******************************/

		this.drawingArea = new FlxDrawingArea_Impl();
		this.drawingArea.setSidebar(this.sidebar.getSideBar());

		final SplitPane drawingAreaEditor = new SplitPane();
		/****************************** Create Drawing Area  ***********************************/
		TabPane drawingAreaPane = drawingArea.getDrawingArea();
		drawingAreaPane.setId("drawingArea");

		/******************* Create Process/Control Parameter Editor Area  ***********************/
		this.parameterEditor = new FlxParameterEditor_Impl();
		this.drawingArea.setEditor(this.parameterEditor);
		this.sidebar.setDrawingPane(this.drawingArea.getDrawingPane());
		final GridPane parameterEditorAreaPane = new GridPane();

		parameterEditorAreaPane.setId("parameterEditor");
		final ScrollPane parameterEditorAreaScroll = new ScrollPane(parameterEditor.getParamEditor());

		/****************************************************************************************/

		drawingAreaEditor.getItems().addAll(drawingAreaPane,parameterEditorAreaScroll);
		drawingAreaEditor.setDividerPositions(0.7f, 0.3f);
		drawingAreaEditor.setOrientation(Orientation.VERTICAL);

		SplitPane sidebarDrawingAreaEditor = new SplitPane();
		sidebarDrawingAreaEditor.getItems().addAll(sideBarTabPane,drawingAreaEditor);
		sidebarDrawingAreaEditor.setDividerPositions(0.75f, 0.25f);
		sidebarDrawingAreaEditor.setOrientation(Orientation.HORIZONTAL);

		this.menuBar.setDrawingArea(this.drawingArea);
		this.sidebar.setDrawingArea(this.drawingArea);

		this.drawingArea.setSidebar(this.sidebar);
		try
		{
			stage.setTitle("FLX Editor");

			mainBox = new BorderPane();
			mainBox.setMaxSize(500, 300);
			mainBox.setMinSize(500, 300);
			mainScene = new Scene(mainBox, 1175, 700);
			File f = new File("application.css");
			absoluteFilePath = f.getAbsolutePath().replace("\\", "/");
			String cssPath = "file:///" +  absoluteFilePath;
			System.out.println("cssPath: " + cssPath);
			mainScene.getStylesheets().add(cssPath);
			HBox menu = new HBox();
			menuBarBox.setMinWidth(1200.0);

			menu.getChildren().addAll(menuBarBox);
			mainBox.setTop(menu);
			mainBox.setCenter(sidebarDrawingAreaEditor);

			stage.setScene(mainScene);
			DataCollectProgressForm initStatus = new DataCollectProgressForm(this.mainScene, this.mainBox, this.menuBar, this.sidebar, this.drawingArea);


			stage.setOnShown(event->{

				initStatus.activateProgressBar();
				initStatus.dialogStage.setOnCloseRequest(event3->{
					sysUtil.shutdownPort();
				});
				initStatus.getDialogStage().show();
			});

			stage.setOnCloseRequest(event -> {
				sysUtil.shutdownPort();
				System.exit(0);
			});

			stage.show();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println(e);
		}

	}

	public static class DataCollectProgressForm  // is dialog box contains the progress bar indicating status
	// of getting the Combo names for the dropdown Load Combo menu,
	// and the Process/Control component data for the sidebar
	{
		private Stage dialogStage;
		private final ProgressBar pb = new ProgressBar();
		private final Label progInfo = new Label();
		private final Button cancelUsbConnectButton = new Button();

		private final ComboBox<String> ports = new ComboBox<String>();
		List<String> optionList = new ArrayList<String>();
		private FlxMenuBar menubarReference;
		private FlxSidebar sidebarReference;
		private FlxDrawingArea drawingAreaReference;
		boolean startUpdate = false;
		DataAccess dataAccess = new DataAccess_PedalImpl();
		HBox buttons = new HBox();
		HBox radButtonProgress = new HBox();

		public DataCollectProgressForm(Scene mainScene, BorderPane mainBoxReference, FlxMenuBar menubarReference, FlxSidebar sidebarReference, FlxDrawingArea drawingAreaReference)
		{
			this.menubarReference = menubarReference;
			this.sidebarReference = sidebarReference;
			this.drawingAreaReference = drawingAreaReference;
			cancelUsbConnectButton.setText("Cancel USB Connection");
			cancelUsbConnectButton.setOnMouseClicked(event->{
				sysUtil.setDataAccessMode("host");
				closingDataCollectProgressForm();
			});
			dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.APPLICATION_MODAL);

			// PROGRESS BAR
			progInfo.setMaxWidth(250);
			progInfo.setMinWidth(250);

			pb.setMinHeight(40.0);
			pb.setMinWidth(150.0);
			pb.setProgress(-1F);


			buttons.getChildren().addAll(ports, cancelUsbConnectButton);
			buttons.setSpacing(5);
			final VBox hb = new VBox();
			hb.setSpacing(5);
			hb.setAlignment(Pos.CENTER);
			hb.getChildren().addAll(pb, progInfo, buttons);


			Scene scene = new Scene(hb);
			dialogStage.setMinWidth(350);
			dialogStage.setMinHeight(100);
			dialogStage.setScene(scene);
		}

		private void closingDataCollectProgressForm()
		{
			String dataAccessMode = sysUtil.getDataAccessMode();
			if(dataAccessMode.compareTo("host") == 0)
			{
				System.out.println("closing Progress Form");
				sysUtil.initializeSystemUtility(System.getProperty("os.name"),null);
				this.menubarReference.getData();
				this.sidebarReference.initSidebarProcessComponents();
				this.drawingAreaReference.initializeSideBarEvents();

				this.getDialogStage().close();
			}
		}

		public void activateProgressBar()
		{
			/********************** 2 *****************************/
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
						if(dataAccess.getPedalStatus().compareTo("ready for update") == 0)
						{
							startUpdate = true;
							updateMessage("Open menu to update pedal");
							return null;
						}
						else
						{
							updateProgress(1, 3);
							updateMessage("Getting combo names");
							menubarReference.getData();

							{
								updateProgress(2, 3);
								updateMessage("Getting components for side bar");
								sidebarReference.initSidebarProcessComponents();
								drawingAreaReference.initializeSideBarEvents();
								updateProgress(3, 3);
							}
							return null ;
						}
					}
					return null ;
				}
			};

			Thread initThread = new Thread(initTask);
			initTask.setOnSucceeded(event2 ->
			{
				this.getDialogStage().close();
				sysUtil.setDataAccessMode("pedal");
				sysUtil.initializeSystemUtility(System.getProperty("os.name"), null);

				if(startUpdate == true)
				{
					menubarReference.updatePedalSoftware();
				}
			});
			dialogStage.show();

			/*********************** 1 ************************/
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
						}
						catch (InterruptedException e)
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
			String path = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			System.out.println("path: " + path);
			String jarPath = URLDecoder.decode(path, "UTF-8");
			jarPath = jarPath.replaceAll("target/classes/", "");
			System.out.println("jarPath: " + jarPath);


			//************** Get the Component, Control, and Combo directories using the jarPath variable *****
			sysUtil.setComponentDirectoryPath(jarPath + "Components");
			sysUtil.setControlDirectoryPath(jarPath + "Controls");
			sysUtil.setComboDirectoryPath(jarPath + "Combos");
		}
		catch(Exception e)
		{
			System.out.println("sysUtil error: " + e);
		}

		launch(args);
	}
}
