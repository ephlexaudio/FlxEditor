package diagramSubComponents;

import javafx.scene.Group;
import javafx.scene.Node;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
//import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

/*import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;*/

//import diagramComponents.FlxBlock;

public class FlxConnector {
	//Group parentDrawingArea;
	boolean debugStatements = false;
	String tempInfoString;
	Group parentObjectSymbolGroup; 
	Circle connectorDot;
	String parentProcess;
	String name;
	String type;
	int index;
	double x; 
	double y;
	
	public FlxConnector(String name,String type, int index, Group parentObjectSymbolGroup, double x, double y)
	{
		this.parentObjectSymbolGroup = parentObjectSymbolGroup;
		this.parentProcess = this.parentObjectSymbolGroup.getId();
		this.name = name;
		this.type = type;
		this.index = index;
		this.x = x;
		this.y = y;
		this.connectorDot = new Circle(this.x, this.y,4);
		this.connectorDot.getStyleClass().add("connector");
		this.connectorDot.setId(this.parentProcess + ":" + this.name);
		
		this.connectorDot.setOnMouseEntered(event->{
			displayConnectorName();
		});
		
		this.connectorDot.setOnMouseExited(event->{
			recoverOldInfo();
		});
	}
	
	public FlxConnector(String name,String type, int index, String parentProcess, double x, double y)
	{
		this.parentProcess = parentProcess;
		this.name = name;
		this.type = type;
		this.index = index;
		this.x = x;
		this.y = y;
		this.connectorDot = new Circle(this.x, this.y,4);
		this.connectorDot.getStyleClass().add("connector");
		this.connectorDot.setId(this.parentProcess + ":" + this.name);
	}
	
	public Circle getConnector()
	{
		return this.connectorDot;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	public void setName(String connName)
	{
		this.name = connName;
	}
	
	public void setParentProcessName(String procName)
	{
		this.parentProcess = procName;
		this.connectorDot.setId(this.parentProcess + ":" + this.name);
	}
	
	public String getCompositeName()
	{
		return this.parentProcess + ":" + this.name;
	}
	
	public Coord getConnectorLocalLocation()
	{
		Coord connectorData = new Coord(this.x, this.y);
		
		/*connectorData.setX(this.x);
		connectorData.setY(this.y);*/
		return connectorData;
	}
	
	public void setConnectorLocalLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
		this.connectorDot.setCenterX(this.x);
		this.connectorDot.setCenterY(this.y);
	}

	private void displayConnectorName()
	{
		List<Node> nodes = this.parentObjectSymbolGroup.getChildren();
		
		
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodes.get(i).getId().compareTo(this.parentProcess+"_info") == 0)
			{
				this.tempInfoString = ((Label)(nodes.get(i))).getText();
				String updatedLabelString = this.name;
				if(this.debugStatements) System.out.println(updatedLabelString);
				((Label)(nodes.get(i))).setText(updatedLabelString);
				break;
			}
		}

	}
	
	private void recoverOldInfo()
	{
		List<Node> nodes = this.parentObjectSymbolGroup.getChildren();
		
		
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodes.get(i).getId().compareTo(this.parentProcess+"_info") == 0)
			{
				((Label)(nodes.get(i))).setText(this.tempInfoString);
				break;
			}
		}
		
	}
}
