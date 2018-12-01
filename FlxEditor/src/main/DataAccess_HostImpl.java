package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
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

public class DataAccess_HostImpl implements DataAccess {
	boolean debugStatements = true;
	boolean errorStatements = true;

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

	public JsonValue getCombo(String comboName) {

		JsonValue comboData = null;
		SystemUtility sysUtil = SystemUtility.getInstance();
    	try
    	{


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

	public List<String> sendCombo(JsonValue comboData) {

		SystemUtility sysUtil = SystemUtility.getInstance();
    	try
    	{

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
    	}

		return this.getComboList();
	}

	public List<String> deleteCombo(String comboName)
	{

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

		}


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

	@Override
	public String getPedalStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}
