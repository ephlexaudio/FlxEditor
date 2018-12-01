package diagramSubComponents;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class FlxFootswitch
{

	String footswitchNumber = new String();
	String[] footSwitches = new String[]{"None","1","2"};
	Label dropDownBoxLabel = new Label("Footswitch");

    ComboBox<String> dropDownBox = new ComboBox<String>();
    FlowPane footswitchSelectorPane = new FlowPane();

	public FlxFootswitch()
	{
		this.dropDownBoxLabel.getStyleClass().add("editorLabel");
		this.dropDownBoxLabel.setMaxSize(80, 30);
		this.dropDownBoxLabel.setMinSize(80, 30);
		this.dropDownBox.getItems().addAll(footSwitches);

		this.dropDownBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			setFootswitchNumber(newValue);
		});

		this.footswitchSelectorPane.getChildren().add(this.dropDownBoxLabel);
		this.footswitchSelectorPane.getChildren().add(this.dropDownBox);
	}

	public FlxFootswitch(String currentValue)
	{
		this.footswitchNumber = currentValue;

		this.dropDownBox.getItems().addAll(footSwitches);

		this.dropDownBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			setFootswitchNumber(newValue);
		});

		this.footswitchSelectorPane.getChildren().add(this.dropDownBox);
	}


	public void setFootswitchNumber(String footswitchNumber)
	{
		this.footswitchNumber = footswitchNumber;
		this.dropDownBox.setValue(this.footswitchNumber);
	}

	public String getFootswitchNumber()
	{
		return this.footswitchNumber;
	}

	public FlowPane getFootswitchSelector()
	{
		return this.footswitchSelectorPane;
	}
}
