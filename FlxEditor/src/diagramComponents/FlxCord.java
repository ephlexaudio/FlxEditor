package diagramComponents;

//import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface FlxCord {

	public JsonValue getEffectConnectionData();
	public String getEffectConnectionString();
	public String getName();
	public int getIndex();
	public String getSrcObject();
	public String getSrcPort();
	public String getDestObject();
	public String getDestPort();
	public boolean containsEffectName(String effectName);
	public void changeSrcEffectName(String effectName);
	public void changeDestEffectName(String effectName);
}
