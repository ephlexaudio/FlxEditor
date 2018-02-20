package diagramComponents;

import javax.json.JsonValue;

import javafx.scene.Group;

public interface FlxComponent extends FlxBlock {

	public String getName();
	public Group getComponent();
	public void setLocation(double x, double y);
	public JsonValue getComponentData();
	//public JsonValue getControlComponentData();
	
}
