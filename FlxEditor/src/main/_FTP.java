package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTP;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class _FTP {

	private InputStream in;
	private String user;
	private String pwd;
	private String host;
	private FTPClient client = null;
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

	boolean sendFile(File file, String path)  
	{
		boolean status = true;

		String fileName = file.getName();

		try
		{
			this.in = new FileInputStream(file);
			this.client.connect(this.host);
			this.client.login(this.user, this.pwd);
			this.client.cwd("/home/" + path);
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
