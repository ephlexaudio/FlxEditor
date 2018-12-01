package diagramSubComponents;

import java.util.List;

import javax.json.Json;
import javax.json.JsonValue;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import main.DataAccess;

public class FlxControlParameter extends FlxParameter
{
	String lcdAlias;
	String lcdAbbr;
	String parentControllerType;
	boolean controlVoltageEnabled;
	FlowPane controlParameterPane = new FlowPane();
	Pane leftCvEnableSpacer = new Pane();
	Pane rightCvEnableSpacer = new Pane();
	TextField controlParameterNameText = new TextField();
	TextField controlParameterAbbrText = new TextField();
	CheckBox controlParameterControlVoltageEnable = new CheckBox();
	int controlledProcessParameterType;
	DataAccess controlDataAccess;
	boolean inheritControlledParamType;

	Pane innerParameter = new Pane();
	public FlxControlParameter(String paramName, Group parentObjectSymbolGroup, int paramIndex, String paramLabel, int paramType, int contParamType, boolean inheritControlParamType, String orientation, double currentValue, String controlParameterLcdAlias, String controlParameterLcdAbbr, boolean controlVoltageEnabled)
	{

		super(paramName, parentObjectSymbolGroup, paramIndex, paramLabel, paramType, null,  orientation, currentValue);
		try
		{
			this.controlParameterNameText.setMinSize(80, 30);
			this.controlParameterNameText.setMaxSize(80, 30);

			this.controlParameterAbbrText.setMinSize(50, 30);
			this.controlParameterAbbrText.setMaxSize(50, 30);

			this.leftCvEnableSpacer.setMinSize(1, 30);
			this.leftCvEnableSpacer.setMaxSize(1, 30);
			this.rightCvEnableSpacer.setMinSize(1, 30);
			this.rightCvEnableSpacer.setMaxSize(1, 30);

			this.controlledProcessParameterType = contParamType;
			this.inheritControlledParamType = inheritControlParamType;
			this.controlParameterNameText.setAlignment(Pos.CENTER);
			this.controlParameterAbbrText.setAlignment(Pos.CENTER);
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
		catch(Exception e)
		{
			System.out.println("FlxControlParameter::constructor error: " + e);
		}
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

	public void setParentControllerType(String contType)
	{
		this.parentControllerType = contType;
	}

	public boolean getControlVoltageStatus()
	{
		return this.controlVoltageEnabled;
	}
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
				.add("controlledParamType", this.controlledProcessParameterType)
				.add("inheritControlledParamType", this.inheritControlledParamType)
				.add("controlVoltageEnabled", this.controlVoltageEnabled)
				.build();

		return parameterData;
	}

	public int getControlledProcessParameterType()
	{
		return this.controlledProcessParameterType;
	}

	public void setControlledProcessParameterType(int paramType)
	{
		this.controlledProcessParameterType = paramType;
	}

	public boolean getInheritControlParameterType()
	{
		return this.inheritControlledParamType;
	}

	protected void valueChanged(double value)
	{
		try
		{
			int paramType = 0;
				this.value = Math.round(99.0*value);
				this.valueIndex = (int)(Math.round(99.0*value));

				if(this.inheritControlledParamType == true)
				{
					paramType = this.controlledProcessParameterType; // inherit value type for controlled parameter
				}
				else
				{
					paramType = this.parameterUnitType; // retain original value type (i.e. envTime for Envelope Generator)
				}


			List<Node> nodes = null;
			String paramString = "";
				 paramString = this.parameterValueLut.getParameterValueString(paramType, this.valueIndex);
				this.parameterDisplay.setText(paramString);
				 nodes = this.parentObjectSymbolGroup.getChildren();
				if(this.debugStatements) System.out.println(nodes.toString());


				for(int i = 0; i < nodes.size(); i++)
				{
					if(nodes.get(i).getId().compareTo(this.parentName+"_info") == 0)
					{

						String labelString = ((Label)(nodes.get(i))).getText();
						String updatedLabelString = new String();

						if(labelString.contains(","))
						{
							String[] values = labelString.split(",");
							values[this.index] = paramString;
							for(int j = 0; j < values.length-1; j++)
							{
								updatedLabelString += values[j].toString() + ",";
							}
							updatedLabelString += values[values.length-1];
						}
						else
						{
							updatedLabelString = paramString;
						}
						if(this.debugStatements) System.out.println(updatedLabelString);
						((Label)(nodes.get(i))).setText(updatedLabelString);
						break;
					}
				}


			System.out.println("controlParameter: " + this.parentName + ":" + this.parentType + ":" + this.lcdAlias
					 + ":" + this.valueIndex);

			super.dataAccess.changeValue(this.parentName, this.parentType, this.lcdAlias, this.valueIndex);
		}
		catch(Exception e)
		{
			System.out.println("FlxControlParameter::valueChanged error: "+ e);
		}

	}

}
