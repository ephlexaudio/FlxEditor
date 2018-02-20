package diagramComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/*import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import main.FlxDrawingArea;*/

public class FlxCombo_Impl implements FlxCombo {
 
	String name;// = currentCombo;
	String currentCombo;// = comboData.name;
	String currentEffect;// = this.effectArray[0].name;
	boolean debugStatements = false;
	boolean errorStatements = true;
	/*CanvasGroup parentDrawingArea;
	ScrollPane parentEditor;*/
	List<JsonValue> effectDataArray;// = comboData.effectArray;
	List<JsonValue> effectConnectionDataArray;// = comboData.effectConnectionArray;
	Map<String,FlxEffect> effectMap;// = new HashMap<String,Effect>();
	Map<String,FlxCord> effectConnectionMap;// = new HashMap<String,Cord>();
	List<FlxCord> effectConnectionList;
	
	public FlxCombo_Impl(JsonValue comboJsonData)
	{
		//if(this.debugStatements) System.out.println("creating processMap in constructor for: " + this.name + ":" + processName );
		try
		{
			JsonObject comboJsonObject = (JsonObject)comboJsonData;
			this.name = comboJsonObject.get("name").toString().replace("\"", "");
			this.currentCombo = comboJsonObject.getString("name").toString().replace("\"", "");
			
			
			/************************ CREATE EFFECT MAP *****************************/
			this.effectMap = new HashMap<String,FlxEffect>();
			boolean success = false;
			try
			{
				this.effectDataArray = comboJsonObject.getJsonArray("effectArray");//new ArrayList<Effect>();
				success = true;
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("Combo constructor effect map error: " + e);
			}
				
			
			
			
			if(success == true)
			{
				for(int effectDataArrayIndex = 0; effectDataArrayIndex < this.effectDataArray.size(); effectDataArrayIndex++)
				{
					JsonObject effectJsonObject = null;
					String name = "";
					try
					{
						effectJsonObject = ((JsonObject)this.effectDataArray.get(effectDataArrayIndex));
						int index = Integer.parseInt(effectJsonObject.get("index").toString().replace("\"", ""));
						name = effectJsonObject.get("name").toString().replace("\"", "");
						if(this.debugStatements) System.out.println("creating constructor in effectMap for: " + name );
						FlxEffect effect = new FlxEffect_Impl(index,this.effectDataArray.get(effectDataArrayIndex));
						this.effectMap.put(name, effect);
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("Combo constructor effect " + name + " error: " + e);
					}
				}				
			}

			
			/*************************** CREATE EFFECT CONNECTION MAP ***********************/
			try
			{
				this.effectConnectionDataArray = comboJsonObject.getJsonArray("effectConnectionArray");
				success = true;
			}
			catch(Exception e)
			{
				if(this.errorStatements) System.out.println("error getting effectConnectionArray from comboJsonObject: " + e);
			}

			this.effectConnectionList = new ArrayList<FlxCord>();
			if(success == true)
			{
				for(int effectConnectionDataArrayIndex = 0; effectConnectionDataArrayIndex < this.effectConnectionDataArray.size(); effectConnectionDataArrayIndex++)
				{
					JsonObject effectConnectionJsonObject = null;
					String name = "";
					try
					{
						effectConnectionJsonObject = ((JsonObject)this.effectConnectionDataArray.get(effectConnectionDataArrayIndex));
						JsonObject src = effectConnectionJsonObject.getJsonObject("src");
						JsonObject dest = effectConnectionJsonObject.getJsonObject("dest");
						
						name = src.getString("object") + ":" + src.getString("port") + ">" +  dest.getString("object") + ":" + dest.getString("port");
						if(this.debugStatements) System.out.println("creating constructor in effectConnectionList for: " + name );
						FlxCord effectConnection = new FlxCord_Impl(this.effectConnectionDataArray.get(effectConnectionDataArrayIndex));
						//String name = effectConnection.getName();
						this.effectConnectionList.add(effectConnection);
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("Combo constructor effect connection " + name + " error: " + e);
					}
				}				
			}
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Combo constructor error: " + e);
			//if(this.debugStatements) System.out.println(this.name + "\t" + this.currentEffect);
		}		
	}
	
	/*public void draw()
	{

		for(String effectKey : effectMap.keySet())
		{			
			//effectMap.get(effectKey).draw();
		}
	} */
	
	public JsonValue getComboData()
	{
		JsonObjectBuilder combo = Json.createObjectBuilder();
		JsonObject comboBuilt = null;
		
		JsonArrayBuilder effectArray = Json.createArrayBuilder();
		JsonArray effectArrayBuilt = null;
		JsonArrayBuilder effectConnectionArray = Json.createArrayBuilder();
		JsonArray effectConnectionArrayBuilt = null;
		try
		{
			combo.add("name", this.name);
			
			for(String effectKey : this.effectMap.keySet())
			{
				effectArray.add(this.effectMap.get(effectKey).getEffectData());
			}
			effectArrayBuilt = effectArray.build();
			
			for(String effectConnectionKey : this.effectConnectionMap.keySet())
			{
				effectConnectionArray.add(this.effectConnectionMap.get(effectConnectionKey).getEffectConnectionData());
			}
			effectConnectionArrayBuilt = effectConnectionArray.build();

			combo.add("effectArray", effectArrayBuilt);
			combo.add("effectConnectionArray", effectConnectionArrayBuilt);
			comboBuilt = combo.build();
			
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxCombo::getComboData error: " + e);
		}
		
		return (JsonValue)comboBuilt;
	}
	
	public Map<String,FlxEffect> getEffectMap()
	{
		Map<String,FlxEffect> tempEffectMap = null;
		try
		{
			tempEffectMap = this.effectMap;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxCombo::getEffectMap error: " + e);
		}
		return tempEffectMap;
	}
	
	public Map<String,FlxCord> getCordMap()
	{
		
		Map<String,FlxCord> tempCordMap = null;
		try
		{
			tempCordMap = this.effectConnectionMap;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxCombo::getCordMap error: " + e);
		}
		return tempCordMap;
	}
	
	public List<FlxCord> getCordList()
	{
		List<FlxCord> tempCordList = null;
		
		try
		{
			tempCordList = this.effectConnectionList;
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxCombo::getCordList error: " + e);
		}
		
		return tempCordList;
	}
	
	public String getComboName()
	{
		return this.name;
	}
	
	public List<String> getProcessList(String effectName)
	{
		List<String> processList = new ArrayList<String>();
		
		for(String processName : this.effectMap.get(effectName).getProcessMap().keySet())
		{
			processList.add(processName);
		}
		
		return processList;
	}
	
	public FlxProcess getProcess(String effectName, String processName)
	{
		return this.effectMap.get(effectName).getProcessMap().get(processName);
	}
	
	public void setProcess(String effectName, String processName, FlxProcess process)
	{
		this.effectMap.get(effectName).addToProcessMap(processName, process);
	}
}
