package diagramComponents;

import javafx.scene.Group;

import java.io.IOException;

import javax.json.*;


public class FlxComponent_Impl extends FlxBlock_Impl  implements FlxComponent {

	Group parentDrawingArea;
	JsonValue componentJsonData;// use for creating process
	String process;
	String type;
	String footswitchType;
	String processDirection;

	double x;
	double y;
	JsonObject labels;
	public FlxComponent_Impl(JsonValue componentJsonData) throws IOException
	{
		super(null, (JsonObject)componentJsonData);
		this.componentJsonData = componentJsonData;
		super.symbolGroup.setId(super.getName());
		this.x = 0;
		this.y = 0;

		super.symbolObj.setLocation(0, 0);

		/**************************** MODIFY LABELING ***********************************/
		super.blockLabel.setWrapText(true);
		super.blockLabel.setMaxWidth(100);
		this.blockLabel.setLayoutX(this.x);
		this.blockLabel.setLayoutY(this.y);

		if(super.componentType.compareTo("process") == 0)
		{
			super.blockLabel.setTranslateY(-15.0);
		}
		else if(super.componentType.compareTo("control") == 0)
		{
			super.blockLabel.setTranslateY(50.0);
		}

		super.blockLabel.setStyle("-fx-font-size:12; -fx-font-weight: bold;");
	}

	public String getName()
	{
		return super.getName();
	}

	public String getType()
	{
		return this.type;
	}

	public Group getComponent()
	{
		return super.getBlock(this.x, this.y);
	}

	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

    public JsonValue getComponentData()
    {
    	JsonValue componentData;

    	componentData = this.componentJsonData;

    	return componentData;
    }

}
