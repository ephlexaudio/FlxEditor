package main;

//import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/*import diagramComponents.FlxCombo_Impl;
import diagramComponents.FlxComponent;
import diagramComponents.FlxComponent_Impl;*/

public class DataAccess_HostImpl implements DataAccess {
	boolean debugStatements = true;
	boolean errorStatements = true;

	//@Override
	
	public boolean confirmConnection()
	{
		return true;
	}
	
	public List<String> getComboList() {
		List<String> comboNameList = new ArrayList<String>();
		String jsonString = "";
		
    	SystemUtility sysUtil = SystemUtility.getInstance();

    	try
    	{
        	File comboFolder = new File(sysUtil.getComboDirectoryPath());
        	File[] comboList = comboFolder.listFiles();
        	String[] fileName = new String[comboList.length];
    		for(int i = 0; i < comboList.length; i++)
    		{
    			fileName[i] = comboList[i].getName();
    			comboNameList.add(fileName[i].replace(sysUtil.getComboDirectoryPath(),"").replace(".txt",""));
    		}
    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("error getting combo folder: " + e);
    		if(this.debugStatements) System.out.println("jsonString: " + jsonString);
    	}
    	return comboNameList;
	}

	//@Override
	public JsonValue getCombo(String comboName) {
		
		JsonValue comboData = null;
		SystemUtility sysUtil = SystemUtility.getInstance();
    	try
    	{
    		/********************************************************************
    		File comboFile = new File(comboPath+comboName+".txt");
    		RandomAccessFile jsonFile = new RandomAccessFile(comboFile,"r");
            FileChannel channel = jsonFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            
            channel.read(buffer);
            
            buffer.flip();//Restore buffer to position 0 to read it
            String jsonString = new String(buffer.array());
            JsonReader reader = Json.createReader(new StringReader(jsonString));
            comboData = reader.readObject();
            //combo = new FlxCombo_Impl(jsonComboData);
            //combo.draw();
            reader.close();
            channel.close();
            jsonFile.close();  
            ********************************************************************/
            
    		
    		String comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
    		if(this.debugStatements) System.out.println(comboPath+comboName+".txt");
    		String comboPathString = comboPath+comboName+".txt";
    		FileReader file = new FileReader(comboPathString);    		 		
    		char[] buffer = new char[(int)(new File(comboPathString).length())];    	
    		file.read(buffer);      
            String jsonString = new String(buffer);
    		if(this.debugStatements) System.out.println("jsonString: " + jsonString);
            JsonReader reader = Json.createReader(new StringReader(jsonString));
            comboData = reader.readObject();
            file.close();

    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("FlxMenuBar::loadComboHandler: " + e);
    	}
    	
    	return comboData;
	}

	//@Override
	public List<JsonValue> getProcessComponents() {
		
		List<JsonValue> procComponentList = new ArrayList<JsonValue>();
		String jsonString = "";
    	SystemUtility sysUtil = SystemUtility.getInstance();

    	try
    	{
        	File componentFolder = new File(sysUtil.getComponentDirectoryPath());
        	File[] componentList = componentFolder.listFiles();
       	
        	for(int componentIndex = 0; componentIndex < componentList.length; componentIndex++)
        	{
        		RandomAccessFile jsonFile = new RandomAccessFile(componentList[componentIndex],"r");
                FileChannel channel = jsonFile.getChannel();
               
                ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
                
                channel.read(buffer);
                
                buffer.flip();//Restore buffer to position 0 to read it
                jsonString = new String(buffer.array());
                
                JsonReader reader = Json.createReader(new StringReader(jsonString));
                JsonObject jsonComponentData = reader.readObject();
                procComponentList.add(jsonComponentData);
                
                reader.close();
                channel.close();
                jsonFile.close();      		
        	}    		
    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("error getting component folder: " + e);
    		if(this.debugStatements) System.out.println("jsonString: " + jsonString);
    	}
    	
    	return procComponentList;
	}

	//@Override
	public List<JsonValue> getControlComponents() {
		List<JsonValue> procComponentList = new ArrayList<JsonValue>();
		String jsonString = "";
    	SystemUtility sysUtil = SystemUtility.getInstance();

    	try
    	{
        	File controlFolder = new File(sysUtil.getControlDirectoryPath());
 
        	File[] controlList = controlFolder.listFiles();
        	
        	for(int controlIndex = 0; controlIndex < controlList.length; controlIndex++)
        	{
        		RandomAccessFile jsonFile = new RandomAccessFile(controlList[controlIndex],"r");
                FileChannel channel = jsonFile.getChannel();
               
                ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
                
                channel.read(buffer);
                
                buffer.flip();//Restore buffer to position 0 to read it
                jsonString = new String(buffer.array());
                
                JsonReader reader = Json.createReader(new StringReader(jsonString));
                JsonObject jsonControlData = reader.readObject();
                procComponentList.add(jsonControlData);
                //FlxComponent control = new FlxComponent_Impl(jsonControlData);
                
                //System.out.println("component name: " + control.getName());
                
                //tempControlMap.put(control.getName().toLowerCase(), control/*jsonComponentData*/);
                
                reader.close();
                channel.close();
                jsonFile.close();      		
        	}    		
    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("error getting control folder: " + e);
    		if(this.debugStatements) System.out.println("jsonString: " + jsonString);
    	}

    	return procComponentList;
	}

	//@Override
	public List<String> sendCombo(JsonValue comboData) {
		//int status = 0;
		
		SystemUtility sysUtil = SystemUtility.getInstance();
    	try
    	{
    		/*String comboPath = null;
    		String comboFileString = null;
    		if(sysUtil.getParentOS().indexOf("Linux") >= 0)
    		{
    			comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
    		}
    		else if(sysUtil.getParentOS().indexOf("Windows") >= 0)
    		{
    			comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
    			if(comboPath.startsWith("/"))
    			{
    				comboPath = comboPath.replaceFirst("/", "");
    			}
    			comboPath = comboPath.replace( "/","\\");
    			
    		}
    		
    		String fileName = ((JsonObject)comboData).getString("name");
    		comboFileString = comboData.toString();
    		
    		
    		String comboPathString = comboPath+fileName+".txt";
    		if(this.debugStatements) System.out.println(comboPathString);
        	Path file = Paths.get(comboPathString);
        	Files.write(file, comboFileString.getBytes());*/
    		
    		String comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
    		String fileName = ((JsonObject)comboData).getString("name");
    		String comboPathString = comboPath+fileName+".txt";
    		FileWriter file = new FileWriter(comboPathString);
    		file.write(comboData.toString());
    		file.close();
    		
     
    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("FlxMenuBar::saveComboHandler: " + e);
    		//status = -1;
    	}
    	
    	return this.getComboList();
	}

	
	public List<String> sendComboString(String comboName, String comboString)
	{
		SystemUtility sysUtil = SystemUtility.getInstance();
		
    	try
    	{
    		String comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
    		
    		String comboPathString = comboPath+comboName+".txt";
    		FileWriter file = new FileWriter(comboPathString);
    		file.write(comboString);
    		file.close();
    		
     
    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("FlxMenuBar::saveComboHandler: " + e);
    		//status = -1;
    	}
		
		return this.getComboList();
	}
	
	public List<String> deleteCombo(String comboName)
	{
		//int status = 0;

		try
		{
			SystemUtility sysUtil = SystemUtility.getInstance();
			String comboPath = null;
			if(sysUtil.getParentOS().indexOf("Linux") >= 0)
			{
				comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
			}
			else if(sysUtil.getParentOS().indexOf("Windows") >= 0)
			{
				comboPath = new String(sysUtil.getComboDirectoryPath()+"/");
    			if(comboPath.startsWith("/"))
    			{
    				comboPath = comboPath.replaceFirst("/", "");
    			}
				comboPath = comboPath.replace( "/","\\");
			}
			
			Path file = Paths.get(comboPath+comboName+".txt");
			Files.delete(file);
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxMenuBar::deleteCombo error: " + e);
    		//status = -1;
			
		}
    	
        /*if parsed_command.find("getCombo") >= 0:
            if temp.find("ComboData") >= 0 and temp.find(search_string) >= 0:
                temp_response_string = temp.split(':',1)[1]
                print "getCombo done"
                done = True;
                break
            else:
                check_rx_messages_count += 1
                if check_rx_messages_count > 8:
                    resend_command = True
                #time.sleep(4)*/
		
		return this.getComboList();
	}

	public int changeValue(String parent, String parentType, String paramName, int valueIndex) 
	{
		int status = 0;
		

		
		return status;
	}

	@Override
	public boolean checkCommPortStatus() {
		// TODO Auto-generated method stub
		return true;
	}
}
