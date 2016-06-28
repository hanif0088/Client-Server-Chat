import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Object;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;

class FileSender implements Runnable
{
	String 					myIPaddress;
	int						myPortNumber;
	Socket					mySocket;
	DataOutputStream 		netOutputStream;
	String					pathFileName;
	File					inFile;
	FileInputStream			inFileStream;
	JProgressBar    		myJProgressBar;
	ProgressUpdater 		myProgressUpdater ;
	String 					senderUsername;

	//====================================================================================
	FileSender(String urIPaddress,String  portNumber,String urFileName,JProgressBar urJProgressBar,String sender)throws Exception
	{
		myIPaddress = urIPaddress;
		myPortNumber = Integer.parseInt(portNumber.trim());
		pathFileName = urFileName;
		myJProgressBar = urJProgressBar;
		senderUsername = sender;

		mySocket = new Socket(myIPaddress,myPortNumber);
		netOutputStream = new DataOutputStream(mySocket.getOutputStream());
		inFile = new File( pathFileName );
		inFileStream = new FileInputStream( inFile );
		new Thread(this).start();
	}

	//==================================================================================
	public void run()
	{
		int		numBytesRead;
		long 	total = 0;
		byte[]	buffer = new byte[1000];

		try
		{
			myProgressUpdater = new ProgressUpdater(myJProgressBar);
			myJProgressBar.setMaximum( (int)inFile.length() );

			byte[] nameBytesArray = senderUsername.getBytes();
			int arrayLength = nameBytesArray.length;
			netOutputStream.writeInt( arrayLength  );//write the arrayLength
			netOutputStream.write( nameBytesArray, 0 , arrayLength );

			numBytesRead = inFileStream.read( buffer );
			myProgressUpdater.setValue((int)total );
			SwingUtilities.invokeLater( myProgressUpdater );
			while( numBytesRead > 0 )
			{
				Thread.sleep(100);
				//System.out.println(" Inside < FileSender > run() inside while loop");
				netOutputStream.write( buffer, 0 , numBytesRead );
				total = total + numBytesRead;
				myProgressUpdater.setValue((int)total );
				SwingUtilities.invokeLater( myProgressUpdater );
				numBytesRead = inFileStream.read( buffer );
			}
			//System.out.println(" Inside < FileSender > run() outside while loop ");
			netOutputStream.close();
			inFileStream.close();
		}

        catch(Exception xe)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            System.out.println("< FileSender > Exception in FileSender...run().");
            xe.printStackTrace();
            System.out.println("#######################################################");
        }
	}
}