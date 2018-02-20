package main;

//import diagramComponents.FlxCombo_Impl;
import javafx.scene.control.MenuBar;

public interface FlxMenuBar {
	
	
	public MenuBar getMenuBar();
	public void getData();
	public void setDrawingArea(FlxDrawingArea drawingArea);
	public void updatePedalSoftware();
	/*
	 * getter/setter methods for this class are private because actions involving drawing area 
	 * (Create combo (newCombo),Read combo (loadCombo), Update combo (saveCombo), Delete combo (deleteCombo) 
	 * will use public methods in FlxDrawingArea class,  hence the getDrawingArea method.
	 */
}
