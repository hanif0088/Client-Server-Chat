import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Object;

class MyHashtable extends Hashtable <String, User>
{
	public MyHashtable()
	{

	}
	//==============================================================================
	public MyHashtable(DataInputStream dis) throws IOException
	{
		int numOfUsers;
		numOfUsers=dis.readInt();
		for(int k=0;k<numOfUsers;k++)
		{
			User myUser = new User(dis);
			this.put(myUser.username,myUser);
		}
	}

	//==========================================================================
	void store(DataOutputStream dos) throws IOException
	{
		dos.writeInt(this.size());

		Set<String> keys = this.keySet();
		for(String key: keys)
		{
			//User myUser=this.get(key);
			this.get(key).store(dos);
		}
	}

}//end of class