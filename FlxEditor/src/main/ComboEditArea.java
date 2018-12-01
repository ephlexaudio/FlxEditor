package main;

import java.util.ArrayList;
import java.util.List;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ComboEditArea {
	boolean debugStatements = false;
	boolean errorStatements = true;

	String effect0Name = new String();
	String effect0Abbr = new String();
	String effect1Name = new String();
	String effect1Abbr = new String();

	Label comboNameLabel = new Label();
	TextField comboNameText = new TextField();
	Label effect0NameLabel = new Label();
	TextField effect0NameText = new TextField();
	Label effect0AbbrLabel = new Label();
	TextField effect0AbbrText = new TextField();
	Label effect1NameLabel = new Label();
	TextField effect1NameText = new TextField();
	Label effect1AbbrLabel = new Label();
	TextField effect1AbbrText = new TextField();
	Button updateButton = new Button();
	HBox buttonBox = new HBox();
	GridPane comboEditGridPane = new GridPane();
	AnchorPane comboEditAnchorPane = new AnchorPane();
	AnchorPane comboEditPane = new AnchorPane();
	FlxDrawingArea drawingAreaReference;

	ComboEditArea()
	{
		comboEditGridPane.setVgap(20.0);
		comboEditGridPane.setHgap(20.0);

		comboNameLabel.setText("Combo Name");
		effect0NameLabel.setText("Effect 0 Name");
		effect0AbbrLabel.setText("Effect 0 Abbr");
		effect1NameLabel.setText("Effect 1 Name");
		effect1AbbrLabel.setText("Effect 1 Abbr");


		comboEditGridPane.add(comboNameLabel, 0, 0);
		comboEditGridPane.add(effect0NameLabel, 0, 1);
		comboEditGridPane.add(effect0AbbrLabel, 0, 2);
		comboEditGridPane.add(effect1NameLabel, 0, 3);
		comboEditGridPane.add(effect1AbbrLabel, 0, 4);
		comboEditGridPane.add(comboNameText, 1, 0);
		comboEditGridPane.add(effect0NameText, 1, 1);
		comboEditGridPane.add(effect0AbbrText, 1, 2);
		comboEditGridPane.add(effect1NameText, 1, 3);
		comboEditGridPane.add(effect1AbbrText, 1, 4);

		buttonBox.setSpacing(20.0);
		updateButton.setText("Update");
		updateButton.setOnMouseClicked(event->{
			List<String> names = this.getNames();
			boolean allNamesValid = true;
			for(int i = 0; i < names.size(); i++)
			{
				if(names.get(i).isEmpty()) allNamesValid = false;
			}

			if(allNamesValid == true)
			{
				this.setNames(names.get(0), names.get(1), names.get(2), names.get(3), names.get(4));
			}
			else
			{
				if(this.errorStatements) System.out.println("one of the names is invalid");
			}
		});
		buttonBox.getChildren().addAll(updateButton);

		comboEditGridPane.add(buttonBox,1,5);
		AnchorPane.setTopAnchor(comboEditGridPane, 50.0);
		AnchorPane.setLeftAnchor(comboEditGridPane, 50.0);
		comboEditAnchorPane.getChildren().add(comboEditGridPane);
		comboEditPane.getChildren().add(comboEditAnchorPane);
	}

	public AnchorPane getComboEditPane()
	{
		return this.comboEditPane;
	}

	public void setDrawingAreaParent(FlxDrawingArea drawingAreaParent)
	{
		this.drawingAreaReference = drawingAreaParent;
	}

	public void clearNames()
	{
		this.effect0Name = new String();
		this.effect0Abbr = new String();
		this.effect1Name = new String();
		this.effect1Abbr = new String();
		this.comboNameText.clear();
		this.effect0NameText.clear();
		this.effect0AbbrText.clear();
		this.effect1NameText.clear();
		this.effect1AbbrText.clear();
	}

	public List<String> getNames()
	{
		List<String> names = new ArrayList<String>();

		names.add(comboNameText.getText());
		names.add(effect0NameText.getText());
		names.add(effect0AbbrText.getText());
		names.add(effect1NameText.getText());
		names.add(effect1AbbrText.getText());

		return names;
	}

	public void setNames(String comboName, String effect0Name, String effect0Abbr, String effect1Name, String effect1Abbr)
	{
		this.comboNameText.setText(comboName);
		this.drawingAreaReference.updateComboName(comboName);

		if(this.effect0Name.isEmpty())
		{
			this.effect0Name = effect0Name;
			this.effect0NameText.setText(this.effect0Name);
			this.effect0Abbr = effect0Abbr;
			this.effect0AbbrText.setText(this.effect0Abbr);
		}
		else
		{
			this.drawingAreaReference.updateEffectNameAbbr(this.effect0Name, effect0Name, effect0Abbr);
		}

		if(this.effect1Name.isEmpty())
		{
			this.effect1Name = effect1Name;
			this.effect1NameText.setText(this.effect1Name);
			this.effect1Abbr = effect1Abbr;
			this.effect1AbbrText.setText(this.effect1Abbr);
		}
		else
		{
			this.drawingAreaReference.updateEffectNameAbbr(this.effect1Name, effect1Name, effect1Abbr);
		}
	}


}
