import java.io.*;
import java.util.*;
import java.net.*;
import java.net.*;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;

public class FileReceiverManager implements Runnable
{
	ServerSocket				myServerSocket;
	Socket						mySocket;
	int							portNumber;
	Hashtable<String,Job>       fileJobHashTable;
	Job						    myFileJob;
	SingleFileReceiver			mySingleFileReceiver;

	//==============================================================================
	FileReceiverManager()
	{
		try
		{
    		Random myRandom = new Random();
    		portNumber = myRandom.nextInt(1000);
    		portNumber = portNumber + 1000;

			myServerSocket = new ServerSocket(portNumber);
			fileJobHashTable = new Hashtable<String, Job>();
			new Thread(this).start();
		}
		catch(Exception ioe)
		{
			System.out.println("########### Hanif Caught the exception #############################################");
			System.out.println("<FileReceiverManager> Exception in FileReceiverManager()!");
			ioe.printStackTrace();
			System.out.println("###########################################################################");
		}

	}

	//================================================================================
	void addJob(String buddyName,String outFilePathName, long fileSize,JProgressBar urJProgressBar)
	{
		myFileJob = new Job(buddyName,outFilePathName, fileSize,urJProgressBar);
		fileJobHashTable.put(buddyName,myFileJob);
	}
	//=========================================================================================================
	public void run()
	{
		try
		{
			while(true)
			{
				mySocket = myServerSocket.accept();
				System.out.println(" < FileReceiverManager > Connection accepted! <.accept()>");
				mySingleFileReceiver = new SingleFileReceiver(mySocket,this);//create new SingleFileReceiver
			}
		}
		catch(Exception ioe)
		{
			System.out.println("################# Hanif Caught the exception ##################################");
			System.out.println(" <FileReceiverManager> Exception in FileReceiver...run()!");
			ioe.printStackTrace();
			System.out.println("###########################################################################");
		}
	}
}