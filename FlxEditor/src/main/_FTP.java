package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
//import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTP;

/*import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;*/

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class _FTP {
	 
	//private FileInputStream fis=null;
	private InputStream in;
	//private OutputStream out;
	private String user;
	private String pwd;
	private String host;
	private FTPClient client = null;
	/*private File file;
	private boolean ptimestamp = true;*/
	private DoubleProperty progress = new SimpleDoubleProperty();
	
	_FTP(String user, String pwd, String host)
	{
		this.user = user;
		this.pwd = pwd;
		this.host = host;
		
		this.client = new FTPClient();
	}
	
	public DoubleProperty getProgressProperty()
	{
		return this.progress;
	}
	
	boolean sendFile(File file)
	{
		boolean status = true;

		//boolean ptimestamp = true;
		String fileName = file.getName();
		
		//String command;
		try
		{
			this.in = new FileInputStream(file);
			this.client.connect(this.host);
			this.client.login(this.user, this.pwd);
			this.client.cwd("/home/Updates");
			this.client.setFileType(FTP.BINARY_FILE_TYPE);
			this.client.storeFile(fileName, this.in);
			this.client.disconnect();
			this.progress.set(0.0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status = false;

		}

		return status;		
	}


}
