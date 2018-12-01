package main;

import javafx.scene.control.MenuBar;

public interface FlxMenuBar {


	public MenuBar getMenuBar();
	public boolean getData();
	public void setDrawingArea(FlxDrawingArea drawingArea);
	public void updatePedalSoftware();
	public void saveComboHandler();

}
