import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;

class BuddyInfo
{
	String				buddysUsername;
	boolean				buddysStatus = false;
	ChatBoxDialog		myChatBoxDialog;

	//===========================================================================
	BuddyInfo()
	{
	}
	//===============================================================================
	BuddyInfo(String username, boolean status)
	{
		buddysUsername = username;
		buddysStatus = status;
	}

	//===================================================================================
    @Override
    public String toString()
    {
        return buddysUsername + buddysStatus ;
    }
	//==================================================================================
    String getField(int fieldIndex)
    {

        if(fieldIndex == 0)
        {
			if(buddysStatus == true)
				return "Online";
            else
            	return "Offline";
		}
        else if(fieldIndex == 1)
            return buddysUsername;
        else
            return null;
    }
    //==============================================================================================
    static BuddyInfo dummyBuddyInfo()
    {
        BuddyInfo  myBuddyInfo = new BuddyInfo();
        myBuddyInfo.buddysUsername = "john";
        myBuddyInfo.buddysStatus = false;
        return myBuddyInfo;
	}

}