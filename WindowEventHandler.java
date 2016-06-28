import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.*;

class WindowEventHandler extends WindowAdapter
{
	CTS 	myCTS;
	String	username;

	//=========================================================================================================
	WindowEventHandler(CTS urCTS,String tempUsername)
	{
		myCTS = urCTS;
		username = tempUsername;
	}

	//====================================================================================================
	public void windowClosing(WindowEvent evt)
	{
		try
		{
	 		myCTS.myTalker.send("LOG_OUT "+ username);

			for(int i=0;i<myCTS.myClientSideFrame.myJTable.getRowCount();i++)
			{
				BuddyInfo myBuddyInfo = myCTS.myClientSideFrame.myTableModel.myListModel.getElementAt(i);
				if( myBuddyInfo.myChatBoxDialog != null)
				{
					myBuddyInfo.myChatBoxDialog.dispose();
				}
			}
	 		//JOptionPane.showMessageDialog(null,"<Client WindowEvent> Window Closed....User is logging out");
	 		System.out.println("<Client WindowEvent> Window Closed....User is logging out");
	 		System.exit(0);
		}
        catch(IOException ioe)
        {
            System.out.println("################### Hanif caught the Exception #######################");
            System.out.println("<Client WindowEvent> Exception in WindowEventHandler!");
            ioe.printStackTrace();
            //JOptionPane.showMessageDialog(null,"<Client WindowEvent> Exception in WindowEventHandler!");
            System.out.println("#######################################################");
        }
	}
}