package main;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class DataAccess_PedalImpl implements DataAccess {

	SystemUtility sysUtil = SystemUtility.getInstance();
	boolean debugStatements = true;
	boolean errorStatements = true;
	String portName;

	public DataAccess_PedalImpl()
	{
		this.portName = sysUtil.getCommPort();
	}


	public String getPedalStatus()
	{
		String status = "";
		String currentStatus = "";
		while(currentStatus.isEmpty())
		{
			currentStatus = sysUtil.getData("getCurrentStatus","CurrentStatus",1000);

		}
		if(this.debugStatements) System.out.println("current status: " + currentStatus);
		if(currentStatus.length()>5) status = currentStatus;

		return status;
	}

	public boolean confirmConnection()
	{
		boolean status = false;

		if(this.getPedalStatus().length()>5) status = true;

		return status;
	}




	public List<String> getComboList()
	{
		List<String> comboList = new ArrayList<String>();

		if(sysUtil.dataAccessStatus() == true)
		{
			String listString = new String();
			for(int i = 0; listString.isEmpty() && i < 5; i++)
			{
				if(this.debugStatements) System.out.println("Getting combo list...");
				try
				{
					listString = sysUtil.getData("listCombos","ComboList",500);
					TimeUnit.MILLISECONDS.sleep(100);
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("error getting combo name string.");
				}
			}

			if(listString.length() > 3)
			{
				String[] listStringArray;
				if(this.debugStatements) System.out.println("listString: " + listString);
				listStringArray = listString.split(":");
				int listStringCount = listStringArray.length;
				String[] tempList = listStringArray[listStringCount-1].split(",");

				for(int i = 0; i < tempList.length; i++)
				{
					String tempItem = tempList[i];
					tempItem = tempItem.replace(" ", "").replace("\n", "").replace("\r", "");
					comboList.add(tempItem);
				}

				for(int i = 0; i < comboList.size(); i++)
				{
					if(this.debugStatements) System.out.println("comboList item: " + comboList.get(i));
				}
			}
		}
		else
		{
			System.out.println("data access not acquired.");
		}

		return comboList;
	}

	public List<JsonValue> getProcessComponents() {
		List<JsonValue> procComponents = new ArrayList<JsonValue>();
		try
		{
			if(sysUtil.dataAccessStatus() == true)
			{

				if(this.debugStatements) System.out.println("Getting process components...");

				String listString = null;
				do
				{
					try
					{
						listString = sysUtil.getData("getComponents","ComponentData",2000);
						TimeUnit.MILLISECONDS.sleep(200);
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("error getting process components string.");
					}
				}while(listString.isEmpty());

				/******************* Clean up string ***********************************/
				String cleanListString = new String();

      	for(int listStringIndex = 0; listStringIndex < listString.length(); listStringIndex++)
      	{
      		if(32 <= listString.charAt(listStringIndex) && listString.charAt(listStringIndex) < 127)
      		{
      			cleanListString += listString.charAt(listStringIndex);
      		}
      		else System.out.println("error at :" + listStringIndex + "\tchar: " + (int)listString.charAt(listStringIndex));
      	}
	      /***********************************************************************/
				if(this.debugStatements) System.out.println("process components listString: " + cleanListString);
				try
				{
					JsonReader reader = Json.createReader(new StringReader(listString));
					JsonArray procComponentJsonArray = reader.readArray();

					for(int i = 0; i < procComponentJsonArray.size(); i++)
					{
						JsonValue processComponent = procComponentJsonArray.get(i);
						procComponents.add(processComponent);
						if(this.debugStatements) System.out.println("procComponentJsonArray[" + i +"]: " + processComponent.toString());
					}

				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("DataAccess_Pedal::getProcessComponents error making process list:" + e);
				}
			}
			else
			{
				System.out.println("data access not acquired.");
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("DataAccess_PedalImpl::getProcessComponents error: " + e);
		}

		return procComponents;
	}

	public List<JsonValue> getControlComponents() {
		List<JsonValue> conComponents = new ArrayList<JsonValue>();
		try
		{
			if(sysUtil.dataAccessStatus() == true)
			{
				if(this.debugStatements) System.out.println("Getting control components...");
				String listString = null;
				do
				{
					try
					{
						listString = sysUtil.getData("getControlTypes","ControlTypeData",500);
						TimeUnit.MILLISECONDS.sleep(200);
					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("DataAccess_PedalImpl::getControlComponents error: " + e);
					}
				}while(listString.isEmpty());
				/******************* Clean up string ***********************************/
				String cleanListString = new String();

	        	for(int listStringIndex = 0; listStringIndex < listString.length() ; listStringIndex++)
	        	{
	        		if(32 <= listString.charAt(listStringIndex) && listString.charAt(listStringIndex) < 127)
	        		{
	        			cleanListString += listString.charAt(listStringIndex);
	        		}
	        		else System.out.println("error at :" + listStringIndex + "\tchar: " + (int)(listString.charAt(listStringIndex)));        	}
	        	/***********************************************************************/

				if(this.debugStatements) System.out.println("control components listString: " + cleanListString);

				try
				{
					JsonReader reader = Json.createReader(new StringReader(listString));
					JsonArray controlComponentJsonArray = reader.readArray();

					for(int i = 0; i < controlComponentJsonArray.size(); i++)
					{
						JsonValue controlComponent = controlComponentJsonArray.get(i);
						conComponents.add(controlComponent);
						if(this.debugStatements) System.out.println("controlComponentJsonArray[" + i +"]: " + controlComponent.toString());
					}

				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println("DataAccess_Pedal::getControlComponents error control making list:" + e);
				}
			}
			else
			{
				System.out.println("data access not acquired.");
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("DataAccess_PedalImpl::getControlComponents error: " + e);
		}



		return conComponents;
	}

	public JsonValue getCombo(String comboName) {
		JsonValue comboData = null;
		try
		{
			if(sysUtil.dataAccessStatus() == true)
			{
				if(this.debugStatements) System.out.println("Getting combo...");
				String comboString = new String();
				String requestString = "getCombo:" + comboName;
				comboString = sysUtil.getData(requestString,"ComboData",1500);
				if(this.debugStatements) System.out.println("comboString: " + comboString);
				JsonReader reader = Json.createReader(new StringReader(comboString));
				comboData = reader.readObject();
				System.out.println("*******************************************************************************************************");
				System.out.println(comboData.toString());

			}
			else
			{
				System.out.println("data access not acquired.");
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("DataAccess_Pedal::getCombo error:" + e);

		}
		return comboData;
	}

	public List<String> sendCombo(JsonValue comboData) {
		List<String> comboList = new ArrayList<String>();
		try
		{
			if(sysUtil.dataAccessStatus() == true)
			{
				if(this.debugStatements) System.out.println("Saving combo...");
				String dataString = "saveCombo:"+comboData.toString();
				if(this.debugStatements) System.out.println(dataString);
				String listString = sysUtil.sendData("saveCombo", comboData.toString(), "ComboList",2000);
				if(this.debugStatements) System.out.println("listString: " + listString);
				String[] tempList = listString.split(",");


				for(int i = 0; i < tempList.length; i++)
				{
					String tempItem = tempList[i];
					tempItem = tempItem.replace(" ", "").replace("\n", "").replace("\r", "");
					comboList.add(tempItem);
				}
			}
			else
			{
				System.out.println("data access not acquired.");
			}

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("DataAccess_Pedal::sendCombo error:" + e);
		}

		return this.getComboList();
	}


	public List<String> sendComboString(String comboName, String comboString)
	{
		List<String> comboList = new ArrayList<String>();
		if(sysUtil.dataAccessStatus() == true)
		{
			if(this.debugStatements) System.out.println("Saving combo...");
			String dataString = "saveCombo: "+comboString;
			if(this.debugStatements) System.out.println(dataString);
			String listString = sysUtil.sendData("saveCombo", comboString, "ComboList",500);
			if(this.debugStatements) System.out.println("listString: " + listString);
			String[] tempList = listString.split(",");


			for(int i = 0; i < tempList.length; i++)
			{
				String tempItem = tempList[i];
				tempItem = tempItem.replace(" ", "").replace("\n", "").replace("\r", "");
				comboList.add(tempItem);
			}
		}
		else
		{
			System.out.println("data access not acquired.");
		}

		return this.getComboList();
	}

	public List<String> deleteCombo(String comboName)
	{

		List<String> comboList = new ArrayList<String>();

		if(sysUtil.dataAccessStatus() == true)
		{
			if(this.debugStatements) System.out.println("Deleting combo...");
			String dataString = "deleteCombo:" + comboName;

			String listString = sysUtil.getData(dataString, "ComboList",500);

			if(this.debugStatements) System.out.println("listString: " + listString);
			String[] tempList = listString.split(",");


			for(int i = 0; i < tempList.length; i++)
			{
				String tempItem = tempList[i];
				tempItem = tempItem.replace(" ", "").replace("\n", "").replace("\r", "");
				comboList.add(tempItem);
			}

			for(int i = 0; i < comboList.size(); i++)
			{
				if(this.debugStatements) System.out.println("comboList item: " + comboList.get(i));
			}

		}
		else
		{
			System.out.println("data access not acquired.");
		}

		return comboList;
	}

	public int changeValue(String parent, String parentType, String paramName, int valueIndex)
	{
		int status = 0;

		try
		{
			if(sysUtil.dataAccessStatus() == true)
			{
				String changeString = parent + ":" + paramName + "=" + valueIndex;

				this.sysUtil.sendData("changeValue", changeString, null, 0);
			}
			else
			{
				System.out.println("data access not acquired.");
			}

		}
		catch(Exception e)
		{
			System.out.println("DataAccess_PedalImpl::changeValue error: "+ e);
		}

		return status;
	}


	public boolean checkCommPortStatus()
	{
		boolean status = true;

		if(sysUtil.dataAccessStatus() == true)
		{
			if(this.sysUtil.checkCommPortStatus() == false)
			{
				status = false;
			}
			else status = true;

		}
		else
		{
			System.out.println("data access not acquired.");
		}

		return status;
	}
}
