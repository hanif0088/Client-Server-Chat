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
//import sun.misc.*;
import javax.net.ssl.*;
//import java.security.KeyStore;
import java.security.*;
import java.security.cert.*;


class CTS implements Runnable
{
	Talker 						myTalker;
	String						msgReceived;
	String						username;
	Vector<String>				ctsBuddyList=new Vector<String>();
	Boolean 					isDone = false;
	String						actualUserID;
	//DefaultListModel<String> 	myListModel;
	ClientSideFrame				myClientSideFrame;
	BuddyInfo					myBuddyInfo;
	MyTableModel				myTableModel;
	Vector<ChatBoxDialog>		myChatBoxDialogVector=new Vector<ChatBoxDialog>();

	//=============================================================================================================
	CTS(String serverIPAddress,int PORT_NUM, String userID)throws Exception
	{
		username = userID;
		myTalker=new Talker(serverIPAddress,PORT_NUM,userID);
	}

	//================================================================================================
	void setListModel(MyTableModel urTableModel,ClientSideFrame urClientSideFrame)
	{
		myTableModel = urTableModel;
		myClientSideFrame = urClientSideFrame;
	}
	void setChatBoxDialog(ChatBoxDialog urChatBoxDialog)
	{
		myChatBoxDialogVector.add(urChatBoxDialog);
	}
	//=========================================================================================================
	public void run()
	{
		try
		{
			while(true)
			{
				msgReceived=myTalker.receive();

				if( msgReceived.startsWith("ADD_ONLINE_BUDDY_LIST_OLD ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("ADD_OFFLINE_BUDDY_LIST_OLD ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("I_AM_ONLINE ") )//my friend will get that I came to online with my username from server
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("I_WENT_OFFLINE ") )//my friend will get that I went offline with my username from server
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}

				else if( msgReceived.startsWith("USER_NOT_FOUND ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("FRD_REQ_FROM_SENDER ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("FRD_REQ_ACTD_FRM_RCVR ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("REQ_ACTD_CMTD_AND_SENDER_IS_ONLINE ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("REQ_ACTD_CMTD_AND_SENDER_IS_OFFLINE ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("DELETE_FRD_FROM_SENDER ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("SOMEONE_DELETED_ME_WHILE_OFFLINE ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("FRD_REQ_DENIED_FRM_RCVR") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}

				else if( msgReceived.startsWith("USER_ALREADY_BLOCKED ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}

				else if( msgReceived.startsWith("CHAT_MSG_FROM_SENDER") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("FILE_XFER_REQ_FROM_SENDER ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}
				else if( msgReceived.startsWith("FILE_XFER_ACPTD_FROM_RECEIVER ") )
				{
					SwingUtilities.invokeLater(new BuddyListUpdater(msgReceived,myTableModel,myClientSideFrame,this) );
				}


			}
		}
		catch(SocketException se)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			System.out.println("<Client CTS> Exception in CTS run()!");
			se.printStackTrace();
			JOptionPane.showMessageDialog(myClientSideFrame,"<Client CTS> Server connection lost! (Exception in CTS run())!");
			myClientSideFrame.dispose();
			for(int j=0;j< myChatBoxDialogVector.size(); j++)
			{
				myChatBoxDialogVector.elementAt(j).dispose();
			}
			System.out.println("#####################################################################################");
		}
		catch(Exception e)
		{
			System.out.println("################### Hanif caught the Exception ##############################");
			System.out.println("<Client CTS> Exception in CTS run()!");
			e.printStackTrace();
			JOptionPane.showMessageDialog(myClientSideFrame,"<Client CTS> Exception in CTS run()...(Server connection lost)!");
			myClientSideFrame.dispose();
			for(int j=0;j< myChatBoxDialogVector.size(); j++)
			{
				myChatBoxDialogVector.elementAt(j).dispose();
			}
			System.out.println("#####################################################################################");
		}
	}

}//end of class CTS