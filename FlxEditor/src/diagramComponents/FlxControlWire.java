package diagramComponents;

//import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface FlxControlWire extends FlxWire {

	JsonValue getControlConnectionData();
	public String getControlConnectionString();
}
