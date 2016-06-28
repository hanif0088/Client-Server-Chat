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

class BuddyListUpdater implements Runnable
{
	String							message;
	MyTableModel					myTableModel;
	BuddyInfo						myBuddyInfo;
	ClientSideFrame					myClientSideFrame;
	CTS								myCTS;
	FileSender						myFileSender;

	//====================================================================================
	public BuddyListUpdater(String urMessage,MyTableModel urTableModel,ClientSideFrame urClientSideFrame,CTS urCTS)
	{
		myCTS = urCTS;
		myTableModel = urTableModel;
		this.message = urMessage;
		myClientSideFrame = urClientSideFrame;
	}

	//==================================================================================
	public void run()
	{
		if( message.startsWith("FRD_REQ_ACTD_FRM_RCVR ") )
		{
			addAcceptedFriend();
		}

		else if( message.startsWith("ADD_ONLINE_BUDDY_LIST_OLD ") )
		{
			addOnlineBuddyFromOldList();
		}

		else if( message.startsWith("ADD_OFFLINE_BUDDY_LIST_OLD ") )
		{
			addOfflineBuddyFromOldList();
		}
		else if( message.startsWith("I_AM_ONLINE ") )
		{
			updateStatusIfOnline();
		}
		else if( message.startsWith("I_WENT_OFFLINE ") )
		{
			updateStatusIfOffline();
		}
		else if( message.startsWith("REQ_ACTD_CMTD_AND_SENDER_IS_ONLINE ") )
		{
			addAcceptedOnlineFriend();
		}
		else if( message.startsWith("REQ_ACTD_CMTD_AND_SENDER_IS_OFFLINE ") )
		{
			addAcceptedOfflineFriend();
		}
		else if( message.startsWith("DELETE_FRD_FROM_SENDER ") )
		{
			updateDeletedFriend();
		}
		else if( message.startsWith("SOMEONE_DELETED_ME_WHILE_OFFLINE ") )
		{
			showMeMessage();
		}
		else if( message.startsWith("USER_ALREADY_BLOCKED") )
		{
			showWarning();
		}
		else if( message.startsWith("FRD_REQ_DENIED_FRM_RCVR ") )
		{
			processDenied();
		}
		else if( message.startsWith("FRD_REQ_FROM_SENDER ") )
		{
			processFriendRequest();
		}
		else if( message.startsWith("USER_NOT_FOUND") )
		{
			showUserNotFound();
		}
		else if( message.startsWith("CHAT_MSG_FROM_SENDER") )
		{
			processChatMessage();
		}
		else if( message.startsWith("FILE_XFER_REQ_FROM_SENDER ") )
		{
			processFileXFerReq();
		}
		else if( message.startsWith("FILE_XFER_ACPTD_FROM_RECEIVER ") )
		{
			acceptFileXFerReq();
		}

	}

	//=========================================================================
	void acceptFileXFerReq()
	{
		String[] elements = message.split(" ");
		String 	receiverUsername = elements[1];
		String  pathFileName = elements[2];
		String  IPaddress = elements[3];
		String  portNumber = elements[4];
		System.out.println("At last after accepted Sender received "+receiverUsername+" "+pathFileName+" "+IPaddress+" "+portNumber );
		BuddyInfo buddyInfo = myTableModel.getBuddyInfoObject(receiverUsername);
		try
		{
			myFileSender = new FileSender(IPaddress,portNumber,pathFileName,buddyInfo.myChatBoxDialog.myJProgressBar,myClientSideFrame.username);
		}
		catch(Exception exc)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			System.out.println("<BuddyListUpdater> Exception in acceptFileXFerReq()!");
			exc.printStackTrace();
			System.out.println("#####################################################################################");
		}
	}

	//=============================================================================
	void processFileXFerReq()
	{
		try
		{
			String[] 	elements = message.split(" ");
			String 		senderUsername = elements[1];
			String  	pathFileName = elements[2];
			String  	fileSize = elements[3];
			long		longFileSize = Long.parseLong(fileSize);

			String  	fileName = pathFileName.substring( pathFileName.lastIndexOf("\\")+1 );//get the last portion of file name

			//System.out.println("<BuddyListUpdater> just parsed the command to add in the job "+senderUsername+" "+ pathFileName+" "+longFileSize );
			//my friend got that forwarded req with my username from the server
			int response = JOptionPane.showConfirmDialog(myClientSideFrame, "Do you want to accept the file "+ fileName , "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION){}
			else if (response == JOptionPane.YES_OPTION)
			{
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setDialogTitle("Specify a file to save");
				int userSelection = fileChooser.showSaveDialog(myClientSideFrame);
				if (userSelection == JFileChooser.APPROVE_OPTION)
				{
			    	File fileToSave = fileChooser.getSelectedFile();
    				String outFilePathName = fileToSave.getCanonicalPath() ;
    				BuddyInfo myBuddyInfo = myTableModel.getBuddyInfoObject(senderUsername);//it's initiator(A) buddyinfo
    				//myClientSideFrame.myFileReceiverManager.addJob(senderUsername,outFilePathName,longFileSize ,myBuddyInfo.myChatBoxDialog.myJProgressBar);//it's initiator(A) JProgressBar
					//System.out.println(" Inside < Buddy list updater> just added the job (0000)!");

					String titleUsername = null;
					if(myBuddyInfo.buddysStatus == true)
					{
						titleUsername = myBuddyInfo.buddysUsername+" online";
					}
					else
					{
						titleUsername = myBuddyInfo.buddysUsername+" offline";
					}

					//if my dialog does not exists opening my new ChatBoxDialog
					if(myBuddyInfo.myChatBoxDialog == null)
					{
						//System.out.println("<BuddyListUpdater> "+myBuddyInfo.buddysUsername+" myChatBoxDialog is null, So creating new myChatBoxDialog with chat msg");
						myBuddyInfo.myChatBoxDialog = new ChatBoxDialog(titleUsername,myCTS,myClientSideFrame,myBuddyInfo.buddysUsername);
    					myClientSideFrame.myFileReceiverManager.addJob(senderUsername,outFilePathName,longFileSize ,myBuddyInfo.myChatBoxDialog.myJProgressBar);//it's initiator(A) JProgressBar
    			        //System.out.println(" Inside < Buddy list updater> just added the job (1)!");

					}
					else//if it's already exists
					{
						if(myBuddyInfo.myChatBoxDialog.isShowing()== true)
						{
							//System.out.println("<BuddyListUpdater> "+myBuddyInfo.buddysUsername+" myChatBoxDialog isnot null and it's showing, So update chat msg");
							myBuddyInfo.myChatBoxDialog.setTitle(titleUsername);

    			            myClientSideFrame.myFileReceiverManager.addJob(senderUsername,outFilePathName,longFileSize ,myBuddyInfo.myChatBoxDialog.myJProgressBar);//it's initiator(A) JProgressBar
    			            //System.out.println(" Inside < Buddy list updater> just added the job (2)!");
						}
						else
						{
							//System.out.println("<BuddyListUpdater> "+myBuddyInfo.buddysUsername+" myChatBoxDialog isnot null and it's not showing, So update chat msg");
							myBuddyInfo.myChatBoxDialog.setVisible(true);
							myBuddyInfo.myChatBoxDialog.setTitle(titleUsername);
    						myClientSideFrame.myFileReceiverManager.addJob(senderUsername,outFilePathName,longFileSize ,myBuddyInfo.myChatBoxDialog.myJProgressBar);//it's initiator(A) JProgressBar
    						//System.out.println(" Inside < Buddy list updater> just added the job (3)!");
						}
					}
				}
			  	//my friend's responce is YES. He is sending the responce with my username to the server
			  	int portNumber = myClientSideFrame.myFileReceiverManager.portNumber;
			  	myCTS.myTalker.send("FILE_XFER_ACPTD_TO_SENDER "+ senderUsername+" "+pathFileName+" "+"127.0.0.1"+" "+portNumber);
			}
			else if (response == JOptionPane.CLOSED_OPTION){}

		}
		catch(IOException ioe)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			System.out.println("<BuddyListUpdater> Exception in processFriendRequest()!");
			ioe.printStackTrace();
			//myClientSideFrame.dispose();
			System.out.println("#####################################################################################");
		}
	}




	//====================================================================================================================
	void showWarning()
	{
		String receiverUsername = message.substring( message.indexOf(" ")+1 );//I initiated a req but server said my friend's username isn't found
		System.out.println("<BuddyListUpdater> You already blocked the user " +receiverUsername);
		JOptionPane.showMessageDialog(myClientSideFrame,"<BuddyListUpdater> You already blocked the user " +receiverUsername);
	}
	//====================================================================
	void showUserNotFound()
	{
		String receiverUsername = message.substring( message.indexOf(" ")+1 );//I initiated a req but server said my friend's username isn't found
		System.out.println("<BuddyListUpdater> Server couldn't find " +receiverUsername);
		JOptionPane.showMessageDialog(myClientSideFrame,"<BuddyListUpdater> Server couldn't find " +receiverUsername);
	}
	//==============================================================================
	void processFriendRequest()
	{
		try
		{
			//my friend got that forwarded req with my username from the server
			String senderName = message.substring( message.indexOf(" ")+1 );

			JDialog.setDefaultLookAndFeelDecorated(true);
			int response = JOptionPane.showConfirmDialog(myClientSideFrame, "Do you want to accept friend request from "+ senderName , "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION)
			{
			  myCTS.myTalker.send("FRD_REQ_DENIED_TO_SENDER "+ senderName );
			}
			else if (response == JOptionPane.YES_OPTION)
			{
			  //my friend's responce is YES. He is sending the responce with my username to the server
			  myCTS.myTalker.send("FRD_REQ_ACPTD_TO_SENDER "+ senderName );
			}
			else if (response == JOptionPane.CLOSED_OPTION)
			{
			  //myTalker.send("FRD_REQ_NO_RES_TO_SENDER "+ senderName );
			}
		}
		catch(IOException ioe)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			System.out.println("<BuddyListUpdater> Exception in processFriendRequest()!");
			ioe.printStackTrace();
			//JOptionPane.showMessageDialog(myClientSideFrame,"<BuddyListUpdater> Exception in processFriendRequest()!");
			myClientSideFrame.dispose();
			System.out.println("#####################################################################################");
		}
	}
	//======================================================================================
	void processDenied()
	{
		//I got my friend's NO responce with his username from server. So nothing to do
		String receiverUsername = message.substring( message.indexOf(" ")+1 );
		System.out.println(receiverUsername +" denied your request <BuddyListUpdater>.");
		//JOptionPane.showMessageDialog(myClientSideFrame,receiverUsername +" denied your request <BuddyListUpdater>.");
	}
	//============================================================================
	void showMeMessage()
	{
		String receiverUsername = message.substring( message.indexOf(" ")+1 );
		System.out.println(receiverUsername+" deleted you while you were offline.<BuddyListUpdater>");
		//JOptionPane.showMessageDialog(myClientSideFrame,receiverUsername+" deleted you while you were offline.<BuddyListUpdater>" );
	}
	//==============================================================================
	void processChatMessage()
	{
		//My friend got my chat message with my username and content of the message from server
		String tempMsg = message;

		String[] elements = message.split(" ");
		String 	senderUsername = elements[1];//my username

		String myMessage = tempMsg.substring( tempMsg.indexOf(" ")+1 );
		String 	chatMessage = myMessage.substring( myMessage.indexOf(" ")+1 );

		//System.out.println("sender was "+senderUsername);
		//System.out.println("msg was "+chatMessage);
		//my friend pulls out my myBuddyInfo
		BuddyInfo myBuddyInfo = myTableModel.getBuddyInfoObject(senderUsername);
		String titleUsername = null;
		if(myBuddyInfo.buddysStatus == true)
		{
			titleUsername = myBuddyInfo.buddysUsername+" online";
		}
		else
		{
			titleUsername = myBuddyInfo.buddysUsername+" offline";
		}
		//myBuddyInfo.myChatBoxDialog.receivedMessage(chatMessage);

		//if my dialog does not exists opening my new ChatBoxDialog
		if(myBuddyInfo.myChatBoxDialog == null)
		{
			//System.out.println("<BuddyListUpdater> "+myBuddyInfo.buddysUsername+" myChatBoxDialog is null, So creating new myChatBoxDialog with chat msg");
			myBuddyInfo.myChatBoxDialog = new ChatBoxDialog(titleUsername,myCTS,myClientSideFrame,myBuddyInfo.buddysUsername);
			myBuddyInfo.myChatBoxDialog.receivedMessage(chatMessage);
		}
		else//if it's already exists
		{
			if(myBuddyInfo.myChatBoxDialog.isShowing()== true)
           	{
			    //System.out.println("<BuddyListUpdater> "+myBuddyInfo.buddysUsername+" myChatBoxDialog isnot null and it's showing, So update chat msg");
			    myBuddyInfo.myChatBoxDialog.setTitle(titleUsername);
				myBuddyInfo.myChatBoxDialog.receivedMessage(chatMessage);
			}
			else
           	{
			    //System.out.println("<BuddyListUpdater> "+myBuddyInfo.buddysUsername+" myChatBoxDialog isnot null and it's not showing, So update chat msg");
				myBuddyInfo.myChatBoxDialog.setVisible(true);
				myBuddyInfo.myChatBoxDialog.setTitle(titleUsername);
				myBuddyInfo.myChatBoxDialog.receivedMessage(chatMessage);
			}
		}
		//myBuddyInfo.myChatBoxDialog.repaint();
	}

	//============================================================================
	void updateDeletedFriend()
	{
		//My friend deleted me and I was online
		String	username = message.substring( message.indexOf(" ")+1 );
		BuddyInfo myBuddyInfo = myTableModel.getBuddyInfoObject(username);
		myTableModel.myListModel.removeElement( myBuddyInfo );
		if(myBuddyInfo.myChatBoxDialog !=null)
		{
			myBuddyInfo.myChatBoxDialog.dispose();
		}
		//myTableModel.myListModel.removeElement( myTableModel.getBuddyInfoObject(username) );
		//JOptionPane.showMessageDialog(myClientSideFrame,username+" deleted you and you are online <Client BuddyListUpdater>" );
		System.out.println(username+" deleted you and you are online <Client BuddyListUpdater>");
		myTableModel.fireTableDataChanged();
	}
	//============================================================================
	void addAcceptedOnlineFriend()
	{
		//My friend added me and I was online
		String	username = message.substring( message.indexOf(" ")+1 );
		myBuddyInfo = new BuddyInfo(username, true);
		myTableModel.addRecord(myBuddyInfo);
		myTableModel.fireTableDataChanged();
	}
	//============================================================================
	void addAcceptedOfflineFriend()
	{
		//My friend added me and I was offline
		String	username = message.substring( message.indexOf(" ")+1 );
		myBuddyInfo = new BuddyInfo(username, false);
		myTableModel.addRecord(myBuddyInfo);
		myTableModel.fireTableDataChanged();
	}
	//============================================================================
	void addAcceptedFriend()
	{
		//I got my friend's YES responce with his username from server. Now I can add him in my friend's list.
		String	username = message.substring( message.indexOf(" ")+1 );
		System.out.println(username +" accepted your request <Client BuddyListUpdater>.");
		//JOptionPane.showMessageDialog(myClientSideFrame,username +" accepted your request <Client BuddyListUpdater>.");
		myBuddyInfo = new BuddyInfo(username, true);
		myTableModel.addRecord(myBuddyInfo);
		myTableModel.fireTableDataChanged();
	}
	//=====================================================================================
	void addOnlineBuddyFromOldList()
	{
		String username = message.substring( message.indexOf(" ")+1 );
		myBuddyInfo = new BuddyInfo(username, true);
		myTableModel.addRecord(myBuddyInfo);
		myTableModel.fireTableDataChanged();
	}
	//===================================================================================
	void addOfflineBuddyFromOldList()
	{
		String username = message.substring( message.indexOf(" ")+1 );
		myBuddyInfo = new BuddyInfo(username, false);
		myTableModel.addRecord(myBuddyInfo);
		myTableModel.fireTableDataChanged();
	}
	//==================================================================================
	void updateStatusIfOnline()
	{
		//my friend will get that I came online with my username from server
		//so he will update my status to online
		String username = message.substring( message.indexOf(" ")+1 );
		System.out.println(username+" is online now.<Client BuddyListUpdater>");
		//JOptionPane.showMessageDialog(myClientSideFrame,username+" is online now.<Client BuddyListUpdater>" );
		BuddyInfo myBuddyInfo = myTableModel.getBuddyInfoObject(username);
		if(myBuddyInfo !=null)
		{
			myBuddyInfo.buddysStatus = true;
		}
		if(myBuddyInfo.myChatBoxDialog !=null)
		{
			myBuddyInfo.myChatBoxDialog.setTitle(username+" online");
		}
		myTableModel.fireTableDataChanged();
	}
	//========================================================================================
	void updateStatusIfOffline()
	{
		//my friend will get that I went offline with my username from server
		//so he will update my status to offline
		String username = message.substring( message.indexOf(" ")+1 );

		System.out.println(username+" is offline now.<Client BuddyListUpdater>");
		//JOptionPane.showMessageDialog(myClientSideFrame,username+" is offline now.<Client BuddyListUpdater>" );
		BuddyInfo myBuddyInfo = myTableModel.getBuddyInfoObject(username);
		if(myBuddyInfo !=null)
		{
			myBuddyInfo.buddysStatus = false;
		}
		if(myBuddyInfo.myChatBoxDialog !=null)
		{
			myBuddyInfo.myChatBoxDialog.setTitle(username+" offline");
		}
		myTableModel.fireTableDataChanged();
	}

}//end of class