package diagramComponents;

//import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import diagramSubComponents.BoundCoord;
import diagramSubComponents.Coord;
import javafx.scene.Group;
//import javafx.scene.shape.Line;

public interface FlxWire {

	public void setWireName(String wireName);
	public String getWireName();
	public void setParentEffect(String parentEffectName);
	public void setSourceConnection(Coord srcConn);
	public void setSourceConnection(BoundCoord srcConn);
	public void setSourceConnection(double x, double y);
	public void setDestConnection(Coord destConn);
	public void setDestConnection(BoundCoord destConn);
	public void setDestConnection(double x, double y);
	public BoundCoord getSourceConnection();
	public BoundCoord getDestConnection();
	public Group getWire();
	public void deleteWireSymbol();
	public JsonValue getProcessConnectionDataLegacy();
	public JsonValue getProcessConnectionData();
	public String getProcessConnectionString();
	public boolean isWireLegacy();
}
