import javax.swing.*;
import java.util.*;
import java.io.*;
import java.text.Format;
import java.text.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class User
{
    String                  username;
    String                  password;
    CTC                     myCTC;
    PriorityQueue<String>   pendingListQueue=new PriorityQueue<String>();
    PriorityQueue<String>   offlineCommandQueue=new PriorityQueue<String>();
    Vector<String>          buddyList=new Vector<String>();
    Vector<String>          blockedList=new Vector<String>();

    //=================================================================================
    User()
    {
    }
    //====================================================
    User(String urUsername,String urPassword)
    {
        //myCTC=urCTC;
        username=urUsername;
        password=urPassword;
    }
    //==================================================================================
    User(DataInputStream dis) throws IOException
    {
        int size;
        username = dis.readUTF();
        password = dis.readUTF();

        size = dis.readInt();
        for(int j=0;j< size ;j++)
        {
            buddyList.addElement( dis.readUTF() );
        }

        size = dis.readInt();
        for(int j=0;j< size ;j++)
        {
            pendingListQueue.add( dis.readUTF() );
        }

        size = dis.readInt();
        for(int j=0;j< size ;j++)
        {
            offlineCommandQueue.add( dis.readUTF() );
        }

        size = dis.readInt();
        for(int j=0;j< size ;j++)
        {
            blockedList.addElement( dis.readUTF() );
        }

    }
    //===================================================================================
    void store(DataOutputStream dos) throws IOException
    {
        dos.writeUTF(username);
        dos.writeUTF(password);

        dos.writeInt(buddyList.size());
        for(int j=0;j<buddyList.size();j++)
        {
            dos.writeUTF(buddyList.elementAt(j));
        }

        dos.writeInt(pendingListQueue.size());
        Iterator<String> it = pendingListQueue.iterator();
        while(it.hasNext())
        {
            dos.writeUTF(it.next());
        }

        dos.writeInt(offlineCommandQueue.size());
        Iterator<String> newIt = offlineCommandQueue.iterator();
        while(newIt.hasNext())
        {
            dos.writeUTF(newIt.next());
        }

        dos.writeInt(blockedList.size());
        for(int j=0;j<blockedList.size();j++)
        {
            dos.writeUTF(blockedList.elementAt(j));
        }

    }

}//end of class

