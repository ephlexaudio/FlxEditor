package main;
/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the file transfer from local to remote.
 *   $ CLASSPATH=.:../build javac ScpTo.java
 *   $ CLASSPATH=.:../build java ScpTo file1 user@remotehost:file2
 * You will be asked passwd. 
 * If everything works fine, a local file 'file1' will copied to
 * 'file2' on 'remotehost'.
 *
 */



import com.jcraft.jsch.*;

import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
/*import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;*/

/*import java.awt.*;
import javax.swing.*;*/
import java.io.*;
/*import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;*/


public class SFTP{
	private FileInputStream fis=null;
	private InputStream in;
	private OutputStream out;
	private Session session;
	private Channel channel;
	private String user;
	private String host;
	private JSch jsch = null;
	//private File file;
	private boolean ptimestamp = true;
	private DoubleProperty progress = new SimpleDoubleProperty();
	
	SFTP(String user, String host)
	{
		this.user = user;
		this.host = host;
		this.jsch = new JSch();
	}

	public DoubleProperty getProgressProperty()
	{
		return this.progress;
	}
	
	boolean openSession()
	{
		boolean status = true;

		
		try {
			this.session = this.jsch.getSession(this.user, this.host, 22);
			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			this.session.setUserInfo(ui);
			this.session.connect();
			

		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			status = false;
		}
		
		return status;
	}
	
	boolean shutoffPedal()
	{
		boolean status = true;
		String command;
		try
		{
			
			command="/sbin/poweroff";
			this.channel=this.session.openChannel("exec");
			((ChannelExec)this.channel).setCommand(command);
			/*this.out=this.channel.getOutputStream();
			this.in=this.channel.getInputStream();*/

			this.channel.setInputStream(null);
			this.in=this.channel.getInputStream();

		      //channel.setOutputStream(System.out);

		      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
		      //((ChannelExec)channel).setErrStream(fos);
		      ((ChannelExec)this.channel).setErrStream(System.err);
		      
			this.channel.connect();
			checkAck(this.in);
			this.channel.disconnect();

		}
		catch(Exception e)
		{
			status = false;
		}
		
		return status;
	}

	boolean sendFile(File file)
	{
		boolean status = true;

		boolean ptimestamp = true;
		String fileName = file.getName();
		
		String command;
		try
		{
			// exec 'scp -t rfile' remotely
			command="scp " + (ptimestamp ? "-p" :"") +" -t /home/Updates/"+ fileName;
			this.channel=this.session.openChannel("exec");
			((ChannelExec)this.channel).setCommand(command);
			// get I/O streams for remote scp
			this.out=this.channel.getOutputStream();
			this.in=this.channel.getInputStream();

			this.channel.connect();
		    //this.file = new File(fileName);


			if(this.ptimestamp)
			{
				command="T "+(file.lastModified()/1000)+" 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command+=(" "+(file.lastModified()/1000)+" 0\n"); 
				this.out.write(command.getBytes()); 
				this.out.flush();
				checkAck(this.in);
				/*if(checkAck(in)!=0)
				{
					System.exit(0);
				}*/
			}

			// send "C0644 filesize filename", where filename should not include '/'
			long filesize=file.length();
			command="C0777 "+filesize+" ";
			if(fileName.lastIndexOf('/')>0)
			{
				command+=fileName.substring(fileName.lastIndexOf('/')+1);
			}
			else
			{
				command+=fileName;
			}
			command+="\n";
			this.out.write(command.getBytes()); 
			this.out.flush();
			checkAck(this.in);
			int fileSize = (int) (file.length()/1024);

			// send a content of lfile
			this.fis=new FileInputStream(file);
			byte[] buf=new byte[1024];
			double fileSendProgressInc = 1.0/fileSize;

			double fileSendProgress = 0.0;
			
			while(true)
			{
				int len=this.fis.read(buf, 0, buf.length);
				
				fileSendProgress += fileSendProgressInc;
				this.progress.set(fileSendProgress);
				if(len<=0) break;
				this.out.write(buf, 0, len); //out.flush();
			}
			
			this.fis.close();
			this.fis=null;
			// send '\0'
			buf[0]=0; 
			this.out.write(buf, 0, 1); 
			this.out.flush();
			checkAck(this.in);
			/*if(checkAck(in)!=0)
			{
				System.exit(0);
			}*/
			this.out.close();
			this.channel.disconnect();
			this.progress.set(0.0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status = false;

		}


		return status;		
	}

	boolean terminateOfxMain()
	{
		boolean status = true;
		String command;
		try
		{
			
			command="/root/killOfx";
			this.channel=this.session.openChannel("exec");
			((ChannelExec)this.channel).setCommand(command);
			this.out=this.channel.getOutputStream();
			this.in=this.channel.getInputStream();

			this.channel.connect();	
			checkAck(this.in);
			/*byte[] buf=new byte[1024];
			buf[0]=0; 
			this.out.write(buf, 0, 1); 
			this.out.flush();
			checkAck(this.in);*/
			//this.out.close();
			this.channel.disconnect();

		}
		catch(Exception e)
		{
			status = false;
		}
		
		return status;
	}

	
	boolean closeSession()
	{
		boolean status = true;

		this.session.disconnect();

		return status;
	}


	static int checkAck(InputStream in) throws IOException{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0) return b;
		if(b==-1) return b;

		if(b==1 || b==2){
			StringBuffer sb=new StringBuffer();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			}
			while(c!='\n');
			if(b==1){ // error
				System.out.print(sb.toString());
			}
			if(b==2){ // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	public static class MyUserInfo implements UserInfo/*, UIKeyboardInteractive*/{
		String passwd;

		
		public String getPassword(){ return passwd; }
		
		public boolean promptYesNo(String str)
		{
			return true;
		}

		public String getPassphrase(){ return null; }
		
		public boolean promptPassphrase(String message){ return true; }
		
		public boolean promptPassword(String message)
		{
			passwd = "root";
			return true;
		}
		
		public void showMessage(String message)
		{
			//JOptionPane.showMessageDialog(null, message);
		}
		
		public String[] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String[] prompt,
				boolean[] echo)
		{
			return null;
		}
	}
}