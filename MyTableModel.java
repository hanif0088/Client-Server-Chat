import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.*;
import java.text.*;
import javax.swing.table.*;

public class MyTableModel extends AbstractTableModel implements TableModel
{
	MyListModel 		myListModel;
	final int 			columnNumber=2;

	//=====================================================================================
	MyTableModel()
	{
		myListModel=new MyListModel();
	}

	//=========================================================================
	@Override
	public int getRowCount()
	{
		return myListModel.size();
	}

	//=========================================================================
	@Override
	public int getColumnCount()
	{
		return columnNumber;//2
	}

	//==================================================================================
	@Override
	public Object getValueAt(int row,int col)
	{
		BuddyInfo	myBuddyInfo;
		myBuddyInfo = myListModel.elementAt(row);
		return myBuddyInfo.getField(col);
	}

	//=======================================================================================
	public void addRecord(BuddyInfo	myBuddyInfo)
	{
		myListModel.addElement(myBuddyInfo);
		//System.out.println(myBuddyInfo.buddysUsername);
		fireTableDataChanged();
	}

	//=====================================================================================
	public void replaceRecord(BuddyInfo myBuddyInfo,int index)
	{
		//mmyBuddyInfo=myListModel.remove(index);
		myListModel.setElementAt(myBuddyInfo,index);
	}

	//======================================================================================
	BuddyInfo  getBuddyInfoObject(String username)
	{
		String			buddysUsername = username;
		BuddyInfo		myBuddyInfo = new BuddyInfo();
		Boolean 		isFound = false;
		myBuddyInfo = null;

		Enumeration<BuddyInfo> e = myListModel.elements();
   		while ( e.hasMoreElements() && !isFound)
   		{
			BuddyInfo  tempBuddyInfo = e.nextElement();
			if(buddysUsername.equals( tempBuddyInfo.buddysUsername ) )
			{
				myBuddyInfo = tempBuddyInfo;
				isFound = true;
			}
		}
		return myBuddyInfo;
	}
	//==================================================================================================
	Boolean isUsernameOnListModel(String username)
	{
		String			buddysUsername = username;
		Boolean 		isFound = false;

		Enumeration<BuddyInfo> e = myListModel.elements();
   		while ( e.hasMoreElements() && !isFound)
   		{
			BuddyInfo  tempBuddyInfo = e.nextElement();
			if(buddysUsername.equals( tempBuddyInfo.buddysUsername ) )
			{
				isFound = true;
			}
		}
		return isFound;
	}


}//end of class

