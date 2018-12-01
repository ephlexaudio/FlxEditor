package diagramComponents;

import javax.json.JsonValue;

public interface FlxControlWire extends FlxWire {

	JsonValue getControlConnectionData();
	public String getControlConnectionString();
	public void deleteWireSymbol();
}
