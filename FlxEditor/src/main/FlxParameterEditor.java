package main;

import java.util.List;
import java.util.Map;

import diagramSubComponents.FlxControlParameter;
import diagramSubComponents.FlxFootswitch;
import diagramSubComponents.FlxParameter;
import javafx.scene.Node;

public interface FlxParameterEditor {

	public Node getParamEditor();
	public void setProcessParameters(Map<String,FlxParameter> jsonProcessParamMap);
	public void setProcessParameters(List<FlxParameter> jsonProcessParamList);
	public void setParameterControllerParameters(Map<String,FlxControlParameter> jsonControlParamMap);
	public void setParameterControllerParameters(List<FlxControlParameter> jsonControlParamList);
	public void clearEditor();
	public void setFootswitchSelector(FlxFootswitch footswitch);
	public void setLcdLabelDescriptionLabels();
	public void setLcdLabelUpdateButton();
}
