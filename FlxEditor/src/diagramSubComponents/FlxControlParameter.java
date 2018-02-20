package diagramSubComponents;

import javax.json.Json;
import javax.json.JsonValue;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
/*import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;*/
import javafx.scene.layout.Pane;

public class FlxControlParameter extends FlxParameter
{
	String lcdAlias;
	String lcdAbbr;
	boolean controlVoltageEnabled;
	//GridPane controlParameterPane = new GridPane();
	FlowPane controlParameterPane = new FlowPane();
	//HBox controlParameterBox = new HBox();
	Pane leftCvEnableSpacer = new Pane();
	Pane rightCvEnableSpacer = new Pane();
	TextField controlParameterNameText = new TextField();
	TextField controlParameterAbbrText = new TextField();
	CheckBox controlParameterControlVoltageEnable = new CheckBox();
	Pane innerParameter = new Pane();
	public FlxControlParameter(String paramName, Group parentObjectSymbolGroup, int paramIndex, String paramLabel, int paramType, String orientation, double currentValue, String controlParameterLcdAlias, String controlParameterLcdAbbr, boolean controlVoltageEnabled)
	{
		//FlxParameter(String paramName, String paramLabel, int paramType, String orientation, double currentValue)
		super(paramName, parentObjectSymbolGroup, paramIndex, paramLabel, paramType, orientation, currentValue);
		
		this.controlParameterNameText.setMinSize(80, 30);
		this.controlParameterNameText.setMaxSize(80, 30);

		this.controlParameterAbbrText.setMinSize(50, 30);
		this.controlParameterAbbrText.setMaxSize(50, 30);

		this.leftCvEnableSpacer.setMinSize(1, 30);
		this.leftCvEnableSpacer.setMaxSize(1, 30);
		this.rightCvEnableSpacer.setMinSize(1, 30);
		this.rightCvEnableSpacer.setMaxSize(1, 30);
		
		
		this.controlParameterNameText.setAlignment(Pos.CENTER);
		this.controlParameterAbbrText.setAlignment(Pos.CENTER);
		//this.parameterLabel.setMinSize(80, 30);
		this.lcdAlias = controlParameterLcdAlias;
		this.lcdAbbr = controlParameterLcdAbbr;
		this.controlVoltageEnabled = controlVoltageEnabled;
		if(controlParameterLcdAlias.isEmpty())
		{
			this.controlParameterNameText.setText("Name");
		}
		else
		{
			this.controlParameterNameText.setText(controlParameterLcdAlias);
		}
		
		if(controlParameterLcdAbbr.isEmpty())
		{
			this.controlParameterAbbrText.setText("Abbr");
		}
		else
		{
			this.controlParameterAbbrText.setText(controlParameterLcdAbbr);
		}
		
		this.innerParameter.getChildren().add(super.getParameter());
		this.innerParameter.setMinSize(330, 30);
		this.innerParameter.setMaxSize(330, 30);
		this.controlParameterControlVoltageEnable.setSelected(this.controlVoltageEnabled);
		
		this.controlParameterControlVoltageEnable.selectedProperty().addListener(new ChangeListener<Boolean>() {
	           public void changed(ObservableValue<? extends Boolean> ov,
	                   Boolean old_val, Boolean new_val) {
	        	   		cvEnableClicked(controlParameterControlVoltageEnable.isSelected());
	                }
	              });

		this.controlParameterPane.getChildren().addAll(this.innerParameter, this.leftCvEnableSpacer,
				this.controlParameterControlVoltageEnable, this.rightCvEnableSpacer,
				this.controlParameterNameText,this.controlParameterAbbrText);
		this.controlParameterPane.setMaxSize(750, 50);
		this.controlParameterPane.setMinSize(750, 50);

		this.controlParameterPane.setHgap(20.0);
		this.innerParameter.setDisable(this.controlVoltageEnabled);
	}
	
	private void cvEnableClicked(boolean status)
	{
		
		if(this.debugStatements) System.out.println("control voltage enable: " + status);
		this.controlVoltageEnabled = status;
		this.innerParameter.setDisable(status);
	}
	
	public String getLcdAlias()
	{
		return this.lcdAlias;
	}
	
	public String getLcdAbbr()
	{
		return this.lcdAbbr;
	}
	
	public boolean getControlVoltageStatus()
	{
		return this.controlVoltageEnabled;
	}
	//@Override
	public Pane getControlParameter()
	{
		return this.controlParameterPane;
	}
	
	public JsonValue getControlParameterData()
	{
		
		this.lcdAlias = this.controlParameterNameText.getText();
		this.lcdAbbr = this.controlParameterAbbrText.getText();
		this.controlVoltageEnabled = this.controlParameterControlVoltageEnable.isSelected();
		JsonValue parameterData = (JsonValue)Json.createObjectBuilder()
				.add("alias", this.lcdAlias)
				.add("abbr", this.lcdAbbr)
				.add("controlVoltageEnabled", this.controlVoltageEnabled)
				.build();
		
		return parameterData;
	}


}
