package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import diagramComponents.FlxControl;
import diagramComponents.FlxControl_Impl;
import diagramComponents.FlxProcess;
import diagramComponents.FlxProcess_Impl;*/
import diagramSubComponents.FlxControlParameter;
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
//import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
/*import javafx.scene.control.Slider;
import javafx.scene.layout.Border;*/
import javafx.scene.layout.BorderPane;
/*import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;*/
import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;

public class FlxParameterEditor_Impl implements FlxParameterEditor {
	
    VBox horizontalParamSliders = new VBox();
    HBox verticalParamSliders = new HBox();
    Pane topPane = new Pane();
    Pane bottomPane = new Pane();
    BorderPane editorPane = new BorderPane();
    
    FlxFootswitch footswitchSelector = new FlxFootswitch();
    List<FlxParameter> vertSliderArray;
    List<FlxParameter> horzSliderArray;
    List<FlxControlParameter> vertControlSliderArray;
    List<FlxControlParameter> horzControlSliderArray;

	FlxParameterEditor_Impl()
	{
		editorPane.setTop(this.topPane);
		this.topPane.setMinSize(500, 30);
		editorPane.setLeft(this.horizontalParamSliders);
		editorPane.setRight(this.verticalParamSliders);
		editorPane.setBottom(this.bottomPane);
	}

	
	public Node getParamEditor() 
	{
		return this.editorPane;
	}
	
	
	public void setProcessParameters(Map<String,FlxParameter> jsonProcessParamMap) // to be called by processClicked in FlxDrawingArea_Impl
	{
		this.vertSliderArray = new ArrayList<FlxParameter>();
		this.horzSliderArray = new ArrayList<FlxParameter>();

		for(String jsonProcessParamKey : jsonProcessParamMap.keySet())
		{
			FlxParameter tempParam = jsonProcessParamMap.get(jsonProcessParamKey);
			
			if(tempParam.getOrientation().compareTo("v") == 0)
			{
				this.vertSliderArray.add(tempParam);
			}
			else // slider is horizontal
			{
				this.horzSliderArray.add(tempParam);
			}
		}
		
		this.verticalParamSliders.getChildren().clear();
		this.horizontalParamSliders.getChildren().clear();
		
		for(int i = 0; i < this.vertSliderArray.size(); i++)
		{
			this.verticalParamSliders.getChildren().add(this.vertSliderArray.get(i).getParameter());
		}
		
		for(int i = 0; i < this.horzSliderArray.size(); i++)
		{
			this.horizontalParamSliders.getChildren().add(this.horzSliderArray.get(i).getParameter());
		}
	}

	
	public void setProcessParameters(List<FlxParameter> jsonProcessParamList) // to be called by processClicked in FlxDrawingArea_Impl
	{
		this.vertSliderArray = new ArrayList<FlxParameter>();
		this.horzSliderArray = new ArrayList<FlxParameter>();

		for(int i = 0 ; i < jsonProcessParamList.size(); i++)
		{
			FlxParameter tempParam = jsonProcessParamList.get(i);
			
			if(tempParam.getOrientation().compareTo("v") == 0)
			{
				this.vertSliderArray.add(tempParam);
			}
			else // slider is horizontal
			{
				this.horzSliderArray.add(tempParam);
			}
		}
		
		this.verticalParamSliders.getChildren().clear();
		this.horizontalParamSliders.getChildren().clear();
		
		for(int i = 0; i < this.vertSliderArray.size(); i++)
		{
			this.verticalParamSliders.getChildren().add(this.vertSliderArray.get(i).getParameter());
		}
		
		for(int i = 0; i < this.horzSliderArray.size(); i++)
		{
			this.horizontalParamSliders.getChildren().add(this.horzSliderArray.get(i).getParameter());
		}
	}

	
	public void setParameterControllerParameters(Map<String,FlxControlParameter> jsonControlParamMap) 
	{
		this.vertControlSliderArray = new ArrayList<FlxControlParameter>();
		this.horzControlSliderArray = new ArrayList<FlxControlParameter>();

		for(String jsonControlParamKey : jsonControlParamMap.keySet())
		{
			FlxControlParameter tempParam = jsonControlParamMap.get(jsonControlParamKey);
			
			if(tempParam.getOrientation().compareTo("v") == 0)
			{
				this.vertControlSliderArray.add(tempParam);
			}
			else // slider is horizontal
			{
				this.horzControlSliderArray.add(tempParam);
			}
		}
		
		this.verticalParamSliders.getChildren().clear();
		this.horizontalParamSliders.getChildren().clear();
		
		for(int i = 0; i < this.vertSliderArray.size(); i++)
		{
			this.verticalParamSliders.getChildren().add(this.vertControlSliderArray.get(i).getControlParameter());
		}
		
		for(int i = 0; i < this.horzSliderArray.size(); i++)
		{
			this.horizontalParamSliders.getChildren().add(this.horzControlSliderArray.get(i).getControlParameter());
		}
	}
	
	
	public void setParameterControllerParameters(List<FlxControlParameter> jsonControlParamList) // to be called by processClicked in FlxDrawingArea_Impl
	{
		this.vertControlSliderArray = new ArrayList<FlxControlParameter>();
		this.horzControlSliderArray = new ArrayList<FlxControlParameter>();

		for(int i = 0 ; i < jsonControlParamList.size(); i++)
		{
			FlxControlParameter tempParam = jsonControlParamList.get(i);
			
			if(tempParam.getOrientation().compareTo("v") == 0)
			{
				this.vertControlSliderArray.add(tempParam);
			}
			else // slider is horizontal
			{
				this.horzControlSliderArray.add(tempParam);
			}
		}
		
		this.verticalParamSliders.getChildren().clear();
		this.horizontalParamSliders.getChildren().clear();
		
		for(int i = 0; i < this.vertControlSliderArray.size(); i++)
		{
			this.verticalParamSliders.getChildren().add(this.vertControlSliderArray.get(i).getControlParameter());
		}
		
		for(int i = 0; i < this.horzControlSliderArray.size(); i++)
		{
			this.horizontalParamSliders.getChildren().add(this.horzControlSliderArray.get(i).getControlParameter());
		}
	}
	
	
	public void setFootswitchSelector(FlxFootswitch footswitch)
	{
		this.topPane.getChildren().add(footswitch.getFootswitchSelector());
	}

	public void setLcdLabelDescriptionLabels()
	{
		FlowPane lcdLabelPane = new FlowPane();
		Pane spacerPane = new Pane();
		Pane spacerPane1 = new Pane();
		Pane spacerPane2 = new Pane();

		spacerPane.setMaxSize(340, 30);
		spacerPane.setMinSize(340, 30);
		spacerPane1.setMaxSize(1, 30);
		spacerPane1.setMinSize(1, 30);
		spacerPane2.setMaxSize(1, 30);
		spacerPane2.setMinSize(1, 30);
		Label cvEnableCheckBoxLabel = new Label("CV Enable");
		cvEnableCheckBoxLabel.getStyleClass().add("editorLabel");
		Label lcdAliasLabel = new Label("LCD Alias");
		lcdAliasLabel.getStyleClass().add("editorLabel");
		Label lcdAbbrLabel = new Label("LCD Abbr");
		lcdAbbrLabel.getStyleClass().add("editorLabel");
		lcdLabelPane.setMaxSize(650, 30);
		lcdLabelPane.setMinSize(650, 30);
		lcdLabelPane.setHgap(20.0);
		lcdLabelPane.getChildren().addAll(spacerPane, cvEnableCheckBoxLabel, /*spacerPane1,*/ lcdAliasLabel, spacerPane2,  lcdAbbrLabel);
		this.topPane.getChildren().add(lcdLabelPane);
		
	}
	
	public void setLcdLabelUpdateButton()
	{
		Button updateLcdLabelsButton = new Button();
		Pane spacerPane = new Pane();
		FlowPane lcdButtonPane = new FlowPane();
		
		spacerPane.setMaxSize(/*355*/430, 30);
		spacerPane.setMinSize(/*355*/430, 30);
		updateLcdLabelsButton.setText("Update LCD Labels");
		updateLcdLabelsButton.setOnMouseClicked(event->{
			updateLcdLabels();
		});
		updateLcdLabelsButton.setMaxSize(150, 30);
		updateLcdLabelsButton.setMinSize(150, 30);

		lcdButtonPane.setMaxSize(650, 30);
		lcdButtonPane.setMinSize(650, 30);
		lcdButtonPane.getChildren().addAll(spacerPane, updateLcdLabelsButton);
		this.bottomPane.getChildren().add(lcdButtonPane);
		
	}
	
	private void updateLcdLabels()
	{
	    for(int controlSliderIndex = 0; controlSliderIndex < this.vertControlSliderArray.size(); controlSliderIndex++)
	    {
	    	this.vertControlSliderArray.get(controlSliderIndex).getControlParameterData();
	    }
	    for(int controlSliderIndex = 0; controlSliderIndex < this.horzControlSliderArray.size(); controlSliderIndex++)
	    {
	    	this.horzControlSliderArray.get(controlSliderIndex).getControlParameterData();
	    }

	}
	
	public void clearEditor()
	{
		this.topPane.getChildren().clear();
		this.verticalParamSliders.getChildren().clear();
		this.horizontalParamSliders.getChildren().clear();
		this.bottomPane.getChildren().clear();
	}
}
