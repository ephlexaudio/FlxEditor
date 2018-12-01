package main;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public final class SystemUtility {
	boolean debugStatements = true;
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

	private static final SystemUtility INSTANCE = new SystemUtility();

	private SystemUtility() {}

		public void initializeSystemUtility(String parentOS, String componentDirectoryPath, String controlDirectoryPath,
		String comboDirectoryPath)
		{
			this.parentOS = parentOS;
			this.controlDirectoryPath = controlDirectoryPath;
			this.comboDirectoryPath = comboDirectoryPath;
			this.componentDirectoryPath = componentDirectoryPath;

			System.out.println("parentOS: " + parentOS);
			System.out.println("componentDirectoryPath: " + componentDirectoryPath);
			System.out.println("controlDirectoryPath: " + controlDirectoryPath);
			System.out.println("comboDirectoryPath: " + comboDirectoryPath);

			this.dataAccessAcquired = true;

		}

		public void initializeSystemUtility(String parentOS, String commPortName)
		{
			this.parentOS = parentOS;
			this.dataAccessAcquired = true;
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
			this.dataAccessMode = "pedal";
			{
				while(this.commPortReady == false && this.shuttingDownApp == false)
				{
					try
					{
						System.out.println("Trying connection");

						portIdentifier = CommPortIdentifier.getPortIdentifier( this.commPortName );
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
							int timeout = 6000;
							Class<SystemUtility> portClass = (Class<SystemUtility>) this.getClass();
							String className = portClass.getName();
							System.out.println("className: " + className);
							commPort = portIdentifier.open( className, timeout );
							serialPort = ( SerialPort )this.commPort;
							serialPort.setSerialPortParams( 230400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
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
			String dataOut = new String();

			if(this.commPortReady == true  && this.shuttingDownApp == false)
			{
				{
					System.out.println("SystemUtility::sendData: sending data.");
					try
					{
						String stringOut = null;
						if(dataIn == null) stringOut = header+"\n";
						else stringOut = header+":"+dataIn+"\n";

						/* The Linux USB buffer is 4000 bytes in size.
						 * comboJson is the first 4000 bytes of the combo file data from the host.  The file size will usually be
						 * greater than 4000, so the for loop below writes in the 4000-byte blocks.
						 */


						int loopCount = (stringOut.length()/4000);
						int stringOutLength = stringOut.length();
						if(this.debugStatements) System.out.println("stringOutLength: " + stringOutLength);
						int beginIndex = 0,endIndex = 0;
						for(int loopIndex = 0; loopIndex < loopCount+1; loopIndex++)
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

							TimeUnit.MILLISECONDS.sleep(100);
						}

						byte bytesIn[] = new byte[4000];
						String startTag = "<"+responseHeader+">";
						String endTag = "</"+responseHeader+">";
						boolean responseStartTagReceived = false;
						boolean responseEndTagReceived = false;
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
							if(beginIndex >= 0 && endIndex >= 0 && endIndex > beginIndex)
							{
								dataOut = responseData.substring(beginIndex+2+responseHeader.length(),endIndex);
								if(dataOut.length() < 5) this.pedalConnectionReady = false;
							}
							else
							{
								if(this.errorStatements) System.out.println("sendData error occured");
							}

						}

					}
					catch(Exception e)
					{
						if(this.errorStatements) System.out.println("SystemUtility::sendData error: " + e);
					}
				}
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

				if(true)
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
							int loopCount = 0;
							while(done == false)
							{
								byte stringOut[] = (request+"\n").getBytes();
								outputStream.write(stringOut);

								System.out.println(request);
								TimeUnit.MILLISECONDS.sleep(waitTime);
								byte bytesIn[] = new byte[4000];
								String cleanStringIn = new String();
								String startTag = "<"+responseHeader+">";
								String endTag = "</"+responseHeader+">";
								boolean responseStartTagReceived = false;
								boolean responseEndTagReceived = false;

								while(!(    (cleanStringIn.contains(startTag)) &&  (cleanStringIn.contains(endTag))  ) && (endIndex < 0) )
								{
									if(inputStream.available() > 0)
									{
										inputStream.read(bytesIn);
										System.out.println("bytesIn: ");
										for(int byteIndex = 0; byteIndex < 4000; byteIndex++)
										{
											if(0 < bytesIn[byteIndex] )
											{
												cleanStringIn += (char)bytesIn[byteIndex];
											}
										}
										if(debugStatements) System.out.println("cleanStringIn.length: " + cleanStringIn.length());
										if(debugStatements) System.out.println("cleanStringIn: " + cleanStringIn);


										if(cleanStringIn.contains(startTag))
										{
											responseData = cleanStringIn;
											beginIndex = responseData.indexOf(startTag);
											endIndex = responseData.indexOf(endTag);
											if(debugStatements) System.out.println("valid response data: " + responseData);
											if(debugStatements) System.out.println("responseStartTagReceived: " + responseStartTagReceived + "\tresponseEndTagReceived: " + responseEndTagReceived);
											if(debugStatements) System.out.println("responseHeader: " + responseHeader + "\tbeginIndex: " + beginIndex + "\tendIndex: " + endIndex);
											done = true;
										}
									}
									else
									{
										loopCount++;
										if(loopCount == 3) return "";
										break;
									}

									TimeUnit.MILLISECONDS.sleep(100);
									bytesIn = new byte[4000];
								}

							}
							if( 0 <= beginIndex && 0 <= endIndex &&  beginIndex < endIndex )
							{
								if(debugStatements) System.out.println("responseData1: " + responseData);

								data = responseData.substring(beginIndex+2+responseHeader.length(),endIndex);
								if(data.length() < 5) this.pedalConnectionReady = false;
								int checkSumIndex = 0;
								int stringTotal = 0;
								int checkSum = 0;
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
									if(debugStatements) System.out.println("checkSum: " + data.substring(checkSumIndex+1));
									checkSum = Integer.parseInt(data.substring(checkSumIndex+1));
									if(debugStatements) System.out.println("checkSum: " + checkSum + "\tstringTotal: " + stringTotal);

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

								System.out.println("data: " + data);
							}
							else
							{
								System.out.println("getData error occured");
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
				try {
					if(this.errorStatements) System.out.println("Comm port not ready: getData");
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					if(this.errorStatements) System.out.println(" error with Comm port not ready: getData: " + e);
				}
			}

			return data;
		}



		public void shutdownPort()
		{
			try
			{
				inputStream.close();
				outputStream.close();
			}
			catch(Exception e)
			{

			}

			this.shuttingDownApp = true;
			this.commPortReady = false;
			if(commPort != null)
			{
				commPort.close();
			}

		}


	}
