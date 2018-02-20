package main;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;
//import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.Enumeration;
import gnu.io.CommPort;
//import gnu.io;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
/*import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;*/

public final class SystemUtility {
	boolean debugStatements = false;
	boolean errorStatements = true;

	private String parentOS;
	private String controlDirectoryPath;
	private String comboDirectoryPath;
	private String componentDirectoryPath;
	private String dataAccessMode;
	private String commPortName;
	private boolean commPortReady = false;
	private boolean pedalConnectionReady = false;
	private boolean dataAccessAcquired = false;
	CommPortIdentifier portIdentifier;
	CommPort commPort;
	SerialPort serialPort;
    InputStream inputStream;
    OutputStream outputStream;
    boolean shuttingDownApp = false;

	//private static final Singleton INSTANCE = new Singleton();
	private static final SystemUtility INSTANCE = new SystemUtility();//String parentOS, String comboDirectoryPath, String componentDirectoryPath)

	private SystemUtility() {}

	public void initializeSystemUtility(String parentOS, String componentDirectoryPath, String controlDirectoryPath,
			String comboDirectoryPath)
	{
		this.parentOS = parentOS;
		this.controlDirectoryPath = controlDirectoryPath;
		this.comboDirectoryPath = comboDirectoryPath;
		this.componentDirectoryPath = componentDirectoryPath;
		//this.dataAccessMode = "host";

		System.out.println("parentOS: " + parentOS);
		System.out.println("componentDirectoryPath: " + componentDirectoryPath);
		System.out.println("controlDirectoryPath: " + controlDirectoryPath);
		System.out.println("comboDirectoryPath: " + comboDirectoryPath);

		this.dataAccessAcquired = true;

	}

	public void initializeSystemUtility(String parentOS, String commPortName)
	{
		this.parentOS = parentOS;
		//this.commPortName = commPortName;
		//this.dataAccessMode = "pedal";

		System.out.println("parentOS: " + parentOS);
		//System.out.println("commPortName: " + commPortName);
		if(dataAccessMode.compareTo("host") == 0)
		{
			this.dataAccessAcquired = true;//false;
		}
		else
		{
			this.dataAccessAcquired = true;//false;
		}
		//this.dataAccessAcquired = true;//false;
	}

	public boolean dataAccessStatus()
	{
		return this.dataAccessAcquired;
	}

    public List<String> listPorts()
    {
    	List<String> portList = new ArrayList<String>();
    	try
    	{
        	System.out.println("Listing ports: ");
        	java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

            while ( portEnum.hasMoreElements() )
            {
                CommPortIdentifier portIdentifier = portEnum.nextElement();
                System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
                portList.add(portIdentifier.getName());
            }

    	}
        catch(Exception e)
    	{
        	if(this.errorStatements) System.out.println("SystemUtility::listPorts  error: " + e);
    	}
        return portList;
    }

    static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
	public void getPedalDataAccess()
	{
		//if(this.dataAccessMode.compareTo("pedal") == 0)
		this.dataAccessMode = "pedal";
		{
			while(this.commPortReady == false && this.shuttingDownApp == false)
			{
				//listPorts();
				try
				{
		        	System.out.println("Trying connection");

			        /*CommPortIdentifier*/ portIdentifier = CommPortIdentifier.getPortIdentifier( this.commPortName );
			        System.out.println("portIdentifier: " + portIdentifier.getName());
			        String osRunning = System.getProperty("os.name");
			        System.out.println("OS Running: " + osRunning);
			        if( portIdentifier.isCurrentlyOwned() )
			        {
			            System.out.println( "Error: Port is currently in use by: " + portIdentifier.getCurrentOwner());
			            commPort.close();
			        }
			        else
			        {
			            int timeout = 3000;
			            commPort = portIdentifier.open( this.getClass().getName(), timeout );
			            serialPort = ( SerialPort )this.commPort;
			            serialPort.setSerialPortParams( 230400/*57600*/, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
			            inputStream = serialPort.getInputStream();
			            outputStream = serialPort.getOutputStream();
			            System.out.println("comm port is ready.");
			            this.dataAccessAcquired = true;
			            this.commPortReady = true;
			            this.pedalConnectionReady = true;
			        }
				}
				catch(Exception e)
				{
					System.out.println(e.toString());
					if(e.toString().contains("NoSuchPortException"))
					{
						this.commPortReady = false;
					}
					else
					{
						if(this.errorStatements) System.out.println("SystemUtility initialization error: " + e);
					}
				}
				try
				{
					TimeUnit.SECONDS.sleep(2);
				}
				catch(Exception e)
				{

				}
			}

		}
	}


	public String dataAccessMode()
	{
		return this.dataAccessMode;
	}

    public boolean checkCommPortStatus()
    {
    	boolean status = true;
    	try
    	{
        	if(this.commPortReady == false)
        	{
        		System.out.println("Please connect the pedal USB port");
        		status = false;
        		//initializeCommPort();

        	}
    	}
    	catch(Exception e)
    	{
    		if(this.errorStatements) System.out.println("SystemUtility::checkCommPortStatus: " + e);
    	}

		return status;
    }

    public boolean getPedalConnectionStatus()
    {
    	return this.pedalConnectionReady;
    }

   public static SystemUtility getInstance( )
   {
	      return INSTANCE;
   }

	protected void setParentOS(String _parentOS)
	{
		this.parentOS = _parentOS;
		System.out.println("parentOS: " + this.parentOS);
	}

	protected void setComboDirectoryPath(String _comboDirectoryPath)
	{
		this.comboDirectoryPath = _comboDirectoryPath;
		System.out.println("comboDirectoryPath: " + this.comboDirectoryPath);
	}

	protected void setComponentDirectoryPath(String _componentDirectoryPath)
	{
		this.componentDirectoryPath = _componentDirectoryPath;
		System.out.println("componentDirectoryPath: " + this.componentDirectoryPath);
	}

	protected void setControlDirectoryPath(String _controlDirectoryPath)
	{
		this.controlDirectoryPath = _controlDirectoryPath;
		System.out.println("controlDirectoryPath: " + this.controlDirectoryPath);
	}

	protected void setCommPort(String _commPort)
	{
		this.commPortName = _commPort;
		System.out.println("commPort: " + this.commPortName);
	}

	public void setDataAccessMode(String dataAccessMode)
	{
		this.dataAccessMode = dataAccessMode;
	}


	public String getParentOS()
	{
		return this.parentOS;
	}

	public String getComboDirectoryPath()
	{
		return this.comboDirectoryPath;
	}

	public String getComponentDirectoryPath()
	{
		return this.componentDirectoryPath;
	}

	public String getControlDirectoryPath()
	{
		return this.controlDirectoryPath;
	}

	public String getCommPort()
	{
		return this.commPortName;
	}

	public boolean getCommPortStatus()
	{
		return this.commPortReady;
	}

	public String getDataAccessMode()
	{
		return this.dataAccessMode;
	}


    public String sendData(String header, String dataIn, String responseHeader, int waitTime)
    {
    	//int status = 0;
    	String dataOut = new String();

    	if(this.commPortReady == true  && this.shuttingDownApp == false)
    	{
    		//if(this.pedalConnectionReady == true)
    		{
    			System.out.println("SystemUtility::sendData: sending data.");
    	    	try
    	    	{
    	    		String stringOut = null;
    	    		if(dataIn == null) stringOut = header+"\n";
    	    		else stringOut = header+":"+dataIn+"\n";

    	    		int loopCount = (stringOut.length()/4000);
    	        	int stringOutLength = stringOut.length();
    	        	if(this.debugStatements) System.out.println("stringOutLength: " + stringOutLength);
    	    		int beginIndex = 0,endIndex = 0;
    	    		for(int loopIndex = 0; loopIndex < loopCount+1; loopIndex++)//do
    	    		{
    	    			beginIndex = 4000*loopIndex;
    	    			endIndex = 4000*(loopIndex)+4000;
    	    			if(endIndex > stringOutLength) endIndex = stringOutLength;
    	    			if(this.debugStatements) System.out.println("beginIndex: " + beginIndex + "\tendIndex: " + endIndex);

    	    			String subStringOut = stringOut.substring(beginIndex, endIndex);
    	                byte bytesOut[] = (subStringOut+"\n").getBytes();
    	                if(this.debugStatements) System.out.println("subStringOut: " + subStringOut);
    	                if(this.debugStatements) System.out.println("bytesOut: " + new String(bytesOut));

    	                outputStream.write(bytesOut);
    	                outputStream.flush();

    	                TimeUnit.MILLISECONDS.sleep(100);
    	    		}//while(endIndex < stringOutLength);

	                byte bytesIn[] = new byte[4000];
	                String startTag = "<"+responseHeader+">";
	                String endTag = "</"+responseHeader+">";
	                boolean responseStartTagReceived = false;
	                boolean responseEndTagReceived = false;
	                //boolean responseReceived = false;
    	    		beginIndex = -1; endIndex = -1;

	                if(responseHeader != null && responseHeader.isEmpty() == false)
	                {
		                TimeUnit.MILLISECONDS.sleep(waitTime);
		            	String responseData = new String();
		                while(responseEndTagReceived == false)
		                {
		                	if(inputStream.available() > 0)
		                	{
			                	inputStream.read(bytesIn);
			                	byte[] filteredBytesIn = new byte[4000];
			                	int filteredBytesInIndex = 0;
			                	for(int i = 0; i < bytesIn.length; i++)
			                	{
			                		if(bytesIn[i] >= ' ') filteredBytesIn[filteredBytesInIndex++] = bytesIn[i];
			                	}

			                	if(this.debugStatements) System.out.println("filteredBytesInIndex: " + filteredBytesInIndex);
			                	if(filteredBytesInIndex > 0)
			                	{
				                	String stringIn = new String(filteredBytesIn);
				                	responseData = responseData.concat(stringIn);
				                	if(this.debugStatements) System.out.println("responseData: " + responseData);
				                	if(responseData.contains("<"+responseHeader+">"))
				                	{
				                		responseStartTagReceived = true;
					                	if(responseData.contains("</"+responseHeader+">"))
					                	{
					                		responseEndTagReceived = true;
					                	}
				                	}

				                	if(responseStartTagReceived && responseEndTagReceived)
				                	{
					                	beginIndex = responseData.indexOf(startTag);
					                	endIndex = responseData.indexOf(endTag);
					                	if(this.debugStatements) System.out.println("valid response data: " + responseData);
					                	if(this.debugStatements) System.out.println("responseStartTagReceived: " + responseStartTagReceived + "\tresponseEndTagReceived: " + responseEndTagReceived);
					                	if(this.debugStatements) System.out.println("responseHeader: " + responseHeader + "\tbeginIndex: " + beginIndex + "\tendIndex: " + endIndex);
				                	}
			                	}
		                	}

		                	TimeUnit.MILLISECONDS.sleep(100);
		                	bytesIn = new byte[4000];
		                }
		                if(beginIndex >= 0 && endIndex >= 0 && endIndex > beginIndex/*responseData.contains(responseHeader)*/)
		                {
		                	dataOut = responseData.substring(beginIndex+2+responseHeader.length(),endIndex);
		                	if(dataOut.length() < 5) this.pedalConnectionReady = false;
		                }
		                else
		                {
		                	if(this.errorStatements) System.out.println("sendData error occured");
		                	this.pedalConnectionReady = false;
		                }

	                }

    	    	}
    	    	catch(Exception e)
    	    	{
    	    		if(this.errorStatements) System.out.println("SystemUtility::sendData error: " + e);
    	    	}
    		}
        	/*else
        	{
        		System.out.println("Pedal connection not ready");
        	}*/
    	}
    	else
    	{
    		if(this.errorStatements) System.out.println("Comm port not ready: sendData");
    	}

    	return dataOut;
    }

    public String getData(String request, String responseHeader, int waitTime)
    {
    	String data = new String();
    	String dataString = "";
    	if(this.commPortReady == true  && this.shuttingDownApp == false)
    	{
 
    		if(/*this.pedalConnectionReady == */true)
    		{
    			System.out.println("SystemUtility::getData: getting data.");
	        	try
	        	{
					boolean validated = false;
	        		while(validated == false)
	        		{
								boolean done = false;
	    	    		int beginIndex = -1,endIndex = -1;
		            	String responseData = new String();
		        		while(done == false)
		        		{
			        		byte stringOut[] = (request+"\n").getBytes();
			                outputStream.write(stringOut);
			                outputStream.flush();
			                TimeUnit.MILLISECONDS.sleep(waitTime);
			                outputStream.flush();
			                byte bytesIn[] = new byte[4000];
			                byte cleanBytesIn[] = new byte[4000];
			                int inputByteCount = 0;
			                boolean responseHeaderReceived = false;
			                String startTag = "<"+responseHeader+">";
			                String endTag = "</"+responseHeader+">";
			                boolean responseStartTagReceived = false;
			                boolean responseEndTagReceived = false;
			                boolean responseReceived = false;
			                while(/*((inputStream.available() < request.length()) || (inputStream.available() >= request.length()+2)) && */(endIndex < 0))
			                {
			    	    		if(inputStream.available() > 0)
			    	    		{
				                	inputStream.read(bytesIn);
				                	int cleanByteIndex = 0;
				                	//System.out.println("bytesIn: ");
				                	for(int byteIndex = 0; byteIndex < 4000; byteIndex++)
				                	{
				                		if(0 < bytesIn[byteIndex] )
				                		{
				                			cleanBytesIn[cleanByteIndex++] = bytesIn[byteIndex];
				                			
				                		}
				                		//System.out.print("[" + byteIndex + "]:" + bytesIn[byteIndex] + ",");
				                	}
				                	//System.out.println();
				                	String cleanStringIn = new String(cleanBytesIn);//.toString();
				                	
				                	//System.out.println("StringIn: " + StringIn);

				                	if(cleanStringIn.contains(startTag))
				                	{
				                		responseStartTagReceived = true;
				                		//System.out.println("responseStartHeaderReceived tag recieved");
					                	/*if(stringIn.contains(endTag))
					                	{
					                		responseEndTagReceived = true;

					                		System.out.println("responseEndHeaderReceived tag recieved");
					                	}*/
				                	}

				                	if(responseStartTagReceived/*responseStartHeaderReceived*/)
				                	{
				                		responseData = responseData.concat(cleanStringIn);
					                	beginIndex = responseData.indexOf(startTag);
					                	endIndex = responseData.indexOf(endTag);
				                		//if(debugStatements) System.out.println("valid response data: " + responseData);
				                		//if(debugStatements) System.out.println("responseStartTagReceived: " + responseStartTagReceived + "\tresponseEndTagReceived: " + responseEndTagReceived);
				                		//if(debugStatements) System.out.println("responseHeader: " + responseHeader + "\tbeginIndex: " + beginIndex + "\tendIndex: " + endIndex);
				                		done = true;
				                	}
			    	    		}
			    	    		else
			    	    		{
			    	    			TimeUnit.SECONDS.sleep(1);
			    	    			break;
			    	    		}

			                	TimeUnit.MILLISECONDS.sleep(100);
			                	bytesIn = new byte[4000];
			                }

		        		}
		                if( 0 <= beginIndex && 0 <= endIndex &&  beginIndex < endIndex /*responseData.contains(responseHeader)*/)
		                {
							System.out.println("responseData1: " + responseData);
		                	data = responseData.substring(beginIndex+2+responseHeader.length(),endIndex);
		                	if(data.length() < 5) this.pedalConnectionReady = false;
		                	int checkSumIndex = 0;
		                	int stringTotal = 0;
		                	int checkSum = 0;
		                	//data = data.replaceAll("\r?\n", "");  // clean data from any newline or return characters
		                	
		                	//System.out.println("data: " + data);
		                	if((checkSumIndex = data.indexOf("#")) > 0)
		                	{
		                		dataString = data.substring(0,checkSumIndex);
		                		for(int dataStringIndex = 0; dataStringIndex < dataString.length(); dataStringIndex++)
		                		{
		                			if(dataString.charAt(dataStringIndex) > 31)
		                			{
		                				stringTotal += dataString.charAt(dataStringIndex);
		                			}
		                		}
		                		System.out.println("checkSum: " + data.substring(checkSumIndex+1));
		                		checkSum = Integer.parseInt(data.substring(checkSumIndex+1));
		                		System.out.println("checkSum: " + checkSum + "\tstringTotal: " + stringTotal);

		                		if(checkSum == stringTotal)
		                		{
		                			validated = true;
		                		}
		                	}
		                	else
		                	{
		                		dataString = data;
		                		validated = true; // no checksum provided
		                	}

		                	//System.out.println("data: " + data);
		                }
		                else
		                {
		                	System.out.println("getData error occured");
		                	//this.pedalConnectionReady = false;
		                }

							}

	        	}
	        	catch(Exception e)
	        	{
	        		if(this.errorStatements) System.out.println("SystemUtility::getData inputStream error: " + e);
	        	}
    		}
    	}
    	else
    	{
    		if(this.errorStatements) System.out.println("Comm port not ready: getData");
    	}

    	return data;
    }

    public void shutdownPort()
    {
    	this.shuttingDownApp = true;
    	this.commPortReady = false;
    	if(commPort != null)
    	{
    		commPort.close();
    	}

    }

}
