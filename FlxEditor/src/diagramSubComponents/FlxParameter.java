package diagramSubComponents;

import java.util.List;

import javax.json.Json;
import javax.json.JsonValue;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import main.DataAccess;
import main.DataAccess_HostImpl;
import main.DataAccess_PedalImpl;
import main.SystemUtility;

public class FlxParameter
{
	boolean debugStatements = false;
	String name;
	String parentName;
	Group parentObjectSymbolGroup;
	String parentType;
	int index;
	String parameterEditorLabel;
	int parameterUnitType;
	LookUpTable parameterValueLut = new LookUpTable();
	SystemUtility sysUtil = SystemUtility.getInstance();
	DataAccess dataAccess;
	double value;
	int valueIndex;
	String orientation = null;
	String parameterControllerType = null;
	Label parameterLabel = new Label();
	Slider parameterValue = new Slider(0, 1, 0.5);
	TextField parameterDisplay = new TextField();
	FlowPane parameterPane = new FlowPane();

	public FlxParameter()
	{
		try
		{
			if(sysUtil.dataAccessMode().compareTo("host") == 0)
			{
				dataAccess = new DataAccess_HostImpl();
			}
			else
			{
				dataAccess = new DataAccess_PedalImpl();
			}


			this.parameterLabel.getStyleClass().add("parameterLabel");
			this.parameterValue.valueProperty().addListener(new ChangeListener<Number>(){
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					double doubleValue = (double)newValue;
					valueChanged(doubleValue);
				}
			});





			this.parameterDisplay.getStyleClass().add("parameterDisplay");
			this.parameterPane.getChildren().addAll(parameterLabel,parameterValue,parameterDisplay);

			this.parameterLabel.setText("Label");

		}
		catch(Exception e)
		{
			System.out.println("FlxParameter::constructor error: " + e);
		}

	}

	public FlxParameter(String paramName, Group parentObjectSymbolGroup, int paramIndex, String paramLabel, int paramType, String paramControllerType, String orientation, double currentValue)
	{
		try
		{
			if(sysUtil.dataAccessMode().compareTo("host") == 0)
			{
				dataAccess = new DataAccess_HostImpl();
			}
			else
			{
				dataAccess = new DataAccess_PedalImpl();
			}
			this.parameterLabel.getStyleClass().add("parameterLabel");
			this.parentObjectSymbolGroup = parentObjectSymbolGroup;
			this.name = paramName;
			this.parentName = parentObjectSymbolGroup.getId();
			if(this.parentName.contains("control"))
			{
				this.parentType = "control";
			}
			else
			{
				this.parentType = "process";
			}
			this.index = paramIndex;
			this.parameterEditorLabel = paramLabel;
			this.parameterUnitType = paramType;
			this.orientation = orientation;
			if(this.parameterControllerType == null) this.parameterControllerType = "none";
			else this.parameterControllerType = paramControllerType;


			this.initialize(currentValue/99.0);
			this.value = Math.round(currentValue);
			this.parameterDisplay.setText(this.parameterValueLut.getParameterValueString(this.parameterUnitType, this.valueIndex));
			this.parameterValue.setValue(currentValue/99.0);
			this.parameterPane.getChildren().addAll(parameterLabel,parameterValue,parameterDisplay);
			this.parameterLabel.setText(paramLabel);
			this.parameterLabel.setWrapText(true);
			this.parameterValue.valueProperty().addListener((observableValue, oldValue, newValue) -> {
				double doubleValue = (double)newValue;
				valueChanged(doubleValue);
			});


			if(this.orientation.compareTo("v") == 0)
			{
				this.parameterValue.setOrientation(Orientation.VERTICAL);
				this.parameterPane.setOrientation(Orientation.VERTICAL);
				this.parameterLabel.setMaxSize(80, 30);
				this.parameterLabel.setMinSize(80, 30);
				this.parameterValue.setMaxSize(30, 120);
				this.parameterValue.setMinSize(30, 120);
				this.parameterDisplay.setMaxSize(80, 30);
				this.parameterDisplay.setMinSize(80, 30);
				this.parameterPane.setMaxSize(80, 200);
				this.parameterPane.setMinSize(80, 200);
			}
			else
			{
				this.parameterValue.setOrientation(Orientation.HORIZONTAL);
				this.parameterPane.setOrientation(Orientation.HORIZONTAL);
				this.parameterLabel.setMaxSize(150, 30);
				this.parameterLabel.setMinSize(150, 30);
				this.parameterValue.setMaxSize(120, 30);
				this.parameterValue.setMinSize(120, 30);
				this.parameterDisplay.setMaxSize(90, 30);
				this.parameterDisplay.setMinSize(90, 30);
				this.parameterPane.setMaxSize(380, 50);
				this.parameterPane.setMinSize(380, 50);
			}

		}
		catch(Exception e)
		{
			System.out.println("FlxParameter::constructor error: " + e);
		}
	}

	public FlowPane getParameter()
	{
		return this.parameterPane;
	}

	public String getName()
	{
		return this.name;
	}
	public void setName(String parameterName)
	{
		this.name = parameterName;
	}

	public String getParameterEditorLabel()
	{
		return this.parameterEditorLabel;
	}

	public int getParameterUnitType()
	{

		return this.parameterUnitType;
	}

	public int getParamValueIndex()
	{
		return this.valueIndex;
	}

	public int getParamIndex()
	{
		return this.index;
	}

	public String getParamControllerType()
	{
		return this.parameterControllerType;
	}

	public void setParamControllerType(String paramContType)
	{
		this.parameterControllerType = paramContType;
	}


	private void initialize(double value)
	{
		this.value = Math.round(99.0*value);
		this.valueIndex = (int)(Math.round(99.0*value));
		this.parameterDisplay.setText(this.parameterValueLut.getParameterValueString(this.parameterUnitType, this.valueIndex));
	}

	protected void valueChanged(double value)
	{
		this.value = Math.round(99.0*value);
		this.valueIndex = (int)(Math.round(99.0*value));
		String paramString = this.parameterValueLut.getParameterValueString(this.parameterUnitType, this.valueIndex);
		this.parameterDisplay.setText(paramString);
		List<Node> nodes = this.parentObjectSymbolGroup.getChildren();
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
		System.out.println("parameter");
		this.dataAccess.changeValue(this.parentName, this.parentType, this.name, this.valueIndex);
	}

	public void setOrientation(String orientation)
	{
		this.orientation = orientation;
		if(this.orientation.compareTo("v") == 0)
		{
			this.parameterValue.setOrientation(Orientation.VERTICAL);
			this.parameterPane.setOrientation(Orientation.VERTICAL);
			this.parameterLabel.setMaxSize(80, 30);
			this.parameterLabel.setMinSize(80, 30);
			this.parameterValue.setMaxSize(120, 30);
			this.parameterValue.setMinSize(120, 30);
		}
		else
		{
			this.parameterValue.setOrientation(Orientation.HORIZONTAL);
			this.parameterPane.setOrientation(Orientation.HORIZONTAL);
			this.parameterLabel.setMaxSize(80, 30);
			this.parameterLabel.setMinSize(80, 30);
			this.parameterValue.setMaxSize(30, 120);
			this.parameterValue.setMinSize(30, 120);
		}
	}

	public String getOrientation()
	{
		return this.orientation;
	}

	public void setValue(double paramValue) {
		this.value = paramValue;

	}

	public JsonValue getParameterData()
	{
		JsonValue parameterData = (JsonValue)Json.createObjectBuilder()
				.add("name", this.name)
				.add("index", this.index)
				.add("label", this.parameterEditorLabel)
				.add("paramType", this.parameterUnitType)
				.add("value", this.value)
				.add("paramContType", this.parameterControllerType)
				.add("orientation", this.orientation)
				.build();

		return parameterData;

	}

}
