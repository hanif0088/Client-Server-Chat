import java.io.*;
import java.util.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
//import sun.misc.*;
import javax.net.ssl.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
//import java.security.KeyStore;
import java.security.*;
import java.security.cert.*;


public class SingleFileReceiver implements Runnable
{

	DataInputStream			netInputStream;
	FileOutputStream		outFileStream;
	Socket 					normalSocket;
	FileReceiverManager 	myFileReceiverManager;
	ProgressUpdater 		myProgressUpdater;

	SingleFileReceiver(Socket mySocket,FileReceiverManager urFileReceiverManager)
	{
		try
		{
			normalSocket = mySocket;
			netInputStream = new DataInputStream(normalSocket.getInputStream());
			myFileReceiverManager = urFileReceiverManager;
			new Thread(this).start();
		}
		catch(Exception ioe)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			ioe.printStackTrace();
			System.out.println("<SingleFileReceiver > Exception in SingleFileReceiver()!");
			System.out.println("#############################################################################");
		}
	}

	//===========================================================================
	public void run()
	{
		Job		fileJob;
		int		numBytesInFile;
		int		numBytesRead;
		int		totalBytesRead;

		try
		{
			byte[] buffer = new byte[1000];

			int senderNameLength = netInputStream.readInt();
			byte[] buffer2 = new byte[senderNameLength];
			netInputStream.read(buffer2,0,senderNameLength);
			String senderUsername = new String(buffer2, "UTF-8");
			//System.out.println("<SingleFileReceiver> Sender username to check the key is :" + senderUsername);

			if( myFileReceiverManager.fileJobHashTable.containsKey(senderUsername)== true )
			{
				//fileJob = myFileReceiverManager.fileJobHashTable.remove(senderUsername);

				fileJob = myFileReceiverManager.fileJobHashTable.get(senderUsername);
				//System.out.println("<SingleFileReceiver> Got the file Job with the key 1111111!!!");
				myFileReceiverManager.fileJobHashTable.remove(senderUsername);

				if(fileJob != null)
				{
					//System.out.println("<SingleFileReceiver> Got the file Job with the key and it's not null!!!");

					outFileStream = new FileOutputStream( new File( fileJob.outFileName ) );
					myProgressUpdater = new ProgressUpdater(fileJob.myJProgressBar);

					numBytesInFile = (int)fileJob.longFileSize ;
					fileJob.myJProgressBar.setMaximum( numBytesInFile );

					numBytesRead = netInputStream.read(buffer);
					totalBytesRead = numBytesRead;
					myProgressUpdater.setValue( totalBytesRead );
					SwingUtilities.invokeLater( myProgressUpdater );

					//System.out.println(" Inside < SingleFileReceiver > right before while loop!");
					while( totalBytesRead < numBytesInFile )
					{
						Thread.sleep(100);
						//System.out.println(" Inside < SingleFileReceiver > run() while loop");
						outFileStream.write( buffer,0,numBytesRead );
						numBytesRead = netInputStream.read(buffer);
						totalBytesRead = totalBytesRead + numBytesRead;
						myProgressUpdater.setValue(totalBytesRead );
						SwingUtilities.invokeLater( myProgressUpdater );
					}
					outFileStream.write( buffer,0,numBytesRead );
					//System.out.println(" Inside < SingleFileReceiver > run() outside while loop");
					netInputStream.close();
					outFileStream.close();
				}
			}
		}
		catch(Exception ioe)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			ioe.printStackTrace();
			System.out.println(" < SingleFileReceiver > Exception in SingleFileReceiver()....run()!");
			System.out.println("#############################################################################");
		}
	}
}