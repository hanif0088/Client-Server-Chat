import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import javax.swing.event.*;
//import sun.misc.*;
import javax.net.ssl.*;
//import java.security.KeyStore;
import java.security.*;
import java.security.cert.*;

class CTC implements Runnable
{
    DataOutputStream        dos;
    Talker                  myTalker;
    Boolean                 isDone=false;
    String                  message;
    String                  username;
    String                  password;
    Validate                myValidate;
    User                    myUser;
    MyHashtable             ctcHashtable=new MyHashtable();

    //=================================================================================
    CTC(SSLSocket sslNormalSocket,MyHashtable urHashtable)throws Exception
    {
        myTalker=new Talker(sslNormalSocket,"Server");
        ctcHashtable = urHashtable;
        myValidate = new Validate();
        myUser= new User();
        new Thread(this).start();
    }

    //===================================================================================
    public void run()
    {
        try
        {
            doRegOrLogin();

            while(!isDone)
            {
                message=myTalker.receive();
                if(message.startsWith("LOG_OUT"))
                {
                    doLogOut();
                }
                else if(message.startsWith("INITIATE_FRD_REQ_TO_RCVR "))
                {
                    processRequest();
                }

                else if(message.startsWith("FRD_REQ_DENIED_TO_SENDER"))
                {
                    processDenial();
                }
                else if(message.startsWith("FRD_REQ_ACPTD_TO_SENDER"))
                {
                    processAccepted();
                }
                else if(message.startsWith("DELETE_FRD_TO_RCVR ") )
                {
                    processDeletion();
                }
                else if(message.startsWith("DELETE_AND_BLOCK_FRD_TO_RCVR") )
                {
                    processBlockAndDeletion();
                }

               	else if(message.startsWith("CHAT_MSG_TO_RCVR ") )
                {
                    processChatMessage();
                }

                else if(message.startsWith("FILE_XFER_REQ_TO_RECEIVER ") )
                {
					processFileXferReq();
				}
                else if(message.startsWith("FILE_XFER_ACPTD_TO_SENDER ") )
                {
					processFileXferAccepted();
				}

            }

        }//end of try

        catch(MyException me)
        {
            System.out.println("################### Hanif caught custom MyException ##############################");
            System.out.println("<Server CTC> MyException in CTC run().Unsuccessful Login or Reg.");
            System.out.println(me.getMessage());
            me.printStackTrace();
            //JOptionPane.showMessageDialog(null,"<Server CTC> MyException in CTC run().Unsuccessful Login or Reg.");
            System.out.println("######################################################################");
        }
        catch(Exception e)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            notifyBuddiesThatUserIsOffline(myUser);
            myUser.myCTC = null;
            System.out.println("<Server CTC> Exception in CTC run().User connection lost.");
            e.printStackTrace();
            //JOptionPane.showMessageDialog(null,"<Server CTC> Exception in CTC run().User connection lost.");
            System.out.println("#####################################################################################");
        }

    }//end of run()
    //===============================================================================================
	void processFileXferReq()throws IOException
	{
		String[] elements = message.split(" ");
		String 	receiverUsername = elements[1];
		String  pathFileName = elements[2];
		String  fileSize = elements[3];

        if( ctcHashtable.get( receiverUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//when my friend is online
        {
			//Server is forwarding that req to my friend with my username
			ctcHashtable.get( receiverUsername ).myCTC.myTalker.send("FILE_XFER_REQ_FROM_SENDER "+myUser.username+" "+pathFileName+" "+fileSize);
        }
 	}

 	//=============================================================================================================
 	void processFileXferAccepted()throws IOException
 	{
		String[] elements = message.split(" ");
		String 	senderUsername = elements[1];
		String  pathFileName = elements[2];
		String  IPaddress = elements[3];
		String  portNumber = elements[4];

        if( ctcHashtable.get( senderUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//when my friend is online
        {
			//Server is forwarding that req to my friend with my username
			ctcHashtable.get( senderUsername ).myCTC.myTalker.send("FILE_XFER_ACPTD_FROM_RECEIVER "+myUser.username+" "+pathFileName+" "+IPaddress+" "+portNumber);
        }
	}
	//=========================================================================================================
	void processChatMessage()throws IOException
	{
		//server got the chat msg from me with my friend's username and content of msg
		String tempMsg = message;
		String[] elements = message.split(" ");
		String 	receiverUsername = elements[1];//my username

		String myMessage = tempMsg.substring( tempMsg.indexOf(" ")+1 );
		String 	chatMessage = myMessage.substring( myMessage.indexOf(" ")+1 );


		//String[] elements = message.split(" ");
		//String 	receiverUsername = elements[1];
		//String  chatMessage = elements[2];
        if( ctcHashtable.get( receiverUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//when my friend is online
        {
            //Server is forwarding that chat msg to my friend with my username and content of the message
            ctcHashtable.get( receiverUsername ).myCTC.myTalker.send( "CHAT_MSG_FROM_SENDER "+myUser.username+" "+chatMessage);
        }
        else//when my friend is offline
        {
            //Server is adding that chat msg to my friend's queue
            ctcHashtable.get( receiverUsername ).offlineCommandQueue.add( "CHAT_MSG_FROM_SENDER "+myUser.username+" "+chatMessage);
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
	}

    //========================================================================
    void doLogOut()
    {
        notifyBuddiesThatUserIsOffline(myUser);
        //JOptionPane.showMessageDialog(null,"<Server CTC> Server received"+message);
        isDone=true;
        myUser.myCTC = null;
    }

    //========================================================================
    void processRequest()throws IOException
    {
        //I sent a req to my friend with his username.Server gets it and will forward that req to my friend with my username
        String receiverUsername = message.substring( message.indexOf(" ")+1 );
        if(ctcHashtable.containsKey(receiverUsername) == false)
        {
            myTalker.send("USER_NOT_FOUND "+receiverUsername);
        }
        else if(myUser.blockedList.contains(receiverUsername)== true )
        {
			myTalker.send("USER_ALREADY_BLOCKED "+receiverUsername);
		}
        else if( ctcHashtable.get( receiverUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//when my friend is online
        {
			if( ctcHashtable.get( receiverUsername ).blockedList.contains(myUser.username)== false )
			{
				if( ctcHashtable.get( receiverUsername ).pendingListQueue.contains( "FRD_REQ_FROM_SENDER "+myUser.username ) == false )
				{
					//Server is forwarding that req to my friend with my username
					ctcHashtable.get( receiverUsername ).myCTC.myTalker.send( "FRD_REQ_FROM_SENDER "+myUser.username );
					//adding that request  with my username to my friend's pendingListQueue
					ctcHashtable.get( receiverUsername ).pendingListQueue.add( "FRD_REQ_FROM_SENDER "+myUser.username );
					System.out.println("FRD_REQ_FROM_SENDER "+myUser.username+" added to queue" );
					dos=new DataOutputStream(new FileOutputStream("user.dat"));
					ctcHashtable.store(dos);
				}
			}
			//when my friend blocked me...the server send me ""USER_NOT_FOUND "
			else//( ctcHashtable.get( receiverUsername ).blockedList.contains(myUser.username)== true )
			{
				myTalker.send("USER_NOT_FOUND "+receiverUsername);
			}
        }
        else//when my friend is offline
        {
			if( ctcHashtable.get( receiverUsername ).blockedList.contains(myUser.username)== false )
			{
				if( ctcHashtable.get( receiverUsername ).pendingListQueue.contains( "FRD_REQ_FROM_SENDER "+myUser.username ) == false )
				{
					ctcHashtable.get( receiverUsername ).pendingListQueue.add( "FRD_REQ_FROM_SENDER "+myUser.username );
					System.out.println("FRD_REQ_FROM_SENDER "+myUser.username+" added to queue" );
					dos=new DataOutputStream(new FileOutputStream("user.dat"));
					ctcHashtable.store(dos);
				}
			}
			//when my friend blocked me...the server send me ""USER_NOT_FOUND "
			else//( ctcHashtable.get( receiverUsername ).blockedList.contains(myUser.username)== true )
			{
				myTalker.send("USER_NOT_FOUND "+receiverUsername);
			}
        }
    }

    //==================================================================================
    void processBlockAndDeletion()throws IOException
    {
        //I sent a delete command with my frined's username.Server gets it and will forward that delete command to my friend with my username

        String receiverUsername = message.substring( message.indexOf(" ")+1 );
        if( ctcHashtable.get( receiverUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//when my friend is online
        {
            //Server is forwarding that command to my friend with my username
            ctcHashtable.get( receiverUsername ).myCTC.myTalker.send( "DELETE_FRD_FROM_SENDER "+myUser.username );
            //server deleting my friend from my list
            if( myUser.buddyList.removeElement( receiverUsername ) )
            {
            }
            myUser.blockedList.addElement( receiverUsername );
            //server deleting me from my friend's list
            if( ctcHashtable.get( receiverUsername ).buddyList.removeElement( myUser.username ) )
            {

            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
        else//when my friend is offline
        {
            //Server is adding the command to my friend's queue
            ctcHashtable.get( receiverUsername ).offlineCommandQueue.add( "SOMEONE_DELETED_ME_WHILE_OFFLINE "+ myUser.username  );
            //server deleting my friend from my list
            if( myUser.buddyList.removeElement( receiverUsername ) )
            {
            }
            myUser.blockedList.addElement( receiverUsername );
            //server deleting me from my friend's list
            if( ctcHashtable.get( receiverUsername ).buddyList.removeElement( myUser.username ) )
            {
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
    }
    //==================================================================================
    void processDeletion()throws IOException
    {
        //I sent a delete command with my frined's username.Server gets it and will forward that delete command to my friend with my username
        String receiverUsername = message.substring( message.indexOf(" ")+1 );

        if( ctcHashtable.get( receiverUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//when my friend is online
        {
            //Server is forwarding that command to my friend with my username
            ctcHashtable.get( receiverUsername ).myCTC.myTalker.send( "DELETE_FRD_FROM_SENDER "+myUser.username );
            //server deleting my friend from my list
            if( myUser.buddyList.removeElement( receiverUsername ) )
            {
            }
            //server deleting me from my friend's list
            if( ctcHashtable.get( receiverUsername ).buddyList.removeElement( myUser.username ) )
            {
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
        else//when my friend is offline
        {
            //Server is adding the command to my friend's queue
            ctcHashtable.get( receiverUsername ).offlineCommandQueue.add( "SOMEONE_DELETED_ME_WHILE_OFFLINE "+ myUser.username  );
            //server deleting my friend from my list
            if( myUser.buddyList.removeElement( receiverUsername ) )
            {
            }
            //server deleting me from my friend's list
            if( ctcHashtable.get( receiverUsername ).buddyList.removeElement( myUser.username ) )
            {
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
    }

    //========================================================================
    void processDenial()throws IOException
    {
        //server got friend's NO responce with my username. So server will forward  that NO responce with my friend's username to me
        String senderUsername = message.substring( message.indexOf(" ")+1 );
        if( ctcHashtable.get( senderUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//checking if I'm not offline
        {
            ctcHashtable.get( senderUsername ).myCTC.myTalker.send( "FRD_REQ_DENIED_FRM_RCVR "+ myUser.username  );
            //removing that request with my username from my friend's pendingListQueue bcoz his reponse was NO
            if(myUser.pendingListQueue.remove("FRD_REQ_FROM_SENDER "+senderUsername) )
            {
                System.out.println("FRD_REQ_FROM_SENDER "+senderUsername+ " removed from queue" );
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
        else//if i'm offline
        {
            ctcHashtable.get( senderUsername ).offlineCommandQueue.add( "FRD_REQ_DENIED_FRM_RCVR "+ myUser.username  );
            //removing that request with my username from my friend's pendingListQueue bcoz his reponse was NO
            if(myUser.pendingListQueue.remove("FRD_REQ_FROM_SENDER "+senderUsername) )
            {
                System.out.println("FRD_REQ_FROM_SENDER "+senderUsername+ " removed from queue" );
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
    }

    //========================================================================
    void processAccepted()throws IOException
    {
        //server got friend's YES responce with my username. So server will forward  that YES responce with my friend's username to me
        String senderUsername = message.substring( message.indexOf(" ")+1 );
        if( ctcHashtable.get( senderUsername ).myCTC !=null )//which is (anyUser.myCTC !=null)//checking if I'm not offline
        {
            //and server will tell my friend that I'm online and that got that YES responce
            myTalker.send( "REQ_ACTD_CMTD_AND_SENDER_IS_ONLINE "+ senderUsername  );
            ctcHashtable.get( senderUsername ).myCTC.myTalker.send( "FRD_REQ_ACTD_FRM_RCVR "+ myUser.username  );
            myUser.buddyList.addElement( senderUsername );
            ctcHashtable.get( senderUsername ).buddyList.addElement( myUser.username );

            //removing that request with my username from my friend's pendingListQueue bcoz his reponse was YES
            if(myUser.pendingListQueue.remove("FRD_REQ_FROM_SENDER "+senderUsername) )
            {
                System.out.println("FRD_REQ_FROM_SENDER "+senderUsername+ " removed from queue" );
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }

        else
        {
            //and tell my friend that I'm offline with my username
            myTalker.send( "REQ_ACTD_CMTD_AND_SENDER_IS_OFFLINE "+ senderUsername  );
            //if I'm offline add the responce to my responceQueue
            //ctcHashtable.get( senderUsername ).offlineCommandQueue.add( "FRD_REQ_ACTD_FRM_RCVR "+ myUser.username  );
            myUser.buddyList.addElement( senderUsername );
            ctcHashtable.get( senderUsername ).buddyList.addElement( myUser.username );

            //removing that request with my username from my friend's pendingListQueue bcoz his reponse was YES
            if(myUser.pendingListQueue.remove("FRD_REQ_FROM_SENDER "+senderUsername) )
            {
                System.out.println("FRD_REQ_FROM_SENDER "+senderUsername+ " removed from queue" );
            }
            dos=new DataOutputStream(new FileOutputStream("user.dat"));
            ctcHashtable.store(dos);
        }
    }

    //================================================================================
    void doRegOrLogin() throws MyException,IOException
    {
        message=myTalker.receive();

        if(message.startsWith("REG"))
        {
            username = message.substring( message.indexOf(" ")+1 );
            if(ctcHashtable.containsKey(username))
            {
                System.out.println("<Server CTC> NOGOOD_REG_USER user_already_registered");
                myTalker.send("NOGOOD_REG_USER user_already_registered");
                //throw new Exception ();
                throw new MyException("<Server CTC> User already registered (Custom exception)");
            }
            else
            {
                myTalker.send("+OK_USERNAME_ACPT username_accepted!");
                message=myTalker.receive();
                password = message.substring( message.indexOf(" ")+1 );

                if(myValidate.isPasswordValid(password))
                {
                    myUser=new User(username,password);
                    ctcHashtable.put(username,myUser);
                    dos=new DataOutputStream(new FileOutputStream("user.dat"));
                    ctcHashtable.store(dos);
                    myTalker.send("+OK_REG_CMPT registration completed");
                    myUser.myCTC = null;
                }

                else
                {
                    myTalker.send("NOGOOD_REG_PASS password_not_valid");
                    //throw new Exception ();
                    throw new MyException("<Server CTC> Password not valid (Custom exception)");
                }
            }

        }//end of if (1st)

        //-----------------------------------------------------------------------------------
        else if(message.startsWith("LOGIN"))
        {
            username = message.substring( message.indexOf(" ")+1 );
            myTalker.send("+OK_LOGIN_USER username_received");

            message=myTalker.receive();
            password = message.substring( message.indexOf(" ")+1 );

            if(ctcHashtable.containsKey(username)== true)
            {
                myUser= ctcHashtable.get(username);
                if(myUser.password.equals(password)==false )
                {
                    myTalker.send("NOGOOD_LOGIN username_password_not_matched");
                    //throw new Exception ();
                    throw new MyException("<Server CTC> Username password did not matched (Custom exception)");
                }

            }
            if(ctcHashtable.containsKey(username)==false)
            {
                myTalker.send("NOGOOD_LOGIN username_password_not_matched");
                //throw new Exception ();
                throw new MyException("<Server CTC> Username password not matched (Custom exception)");
            }
            else
            {
                if(myUser.myCTC == null)
                {
                    myTalker.send("+OK_LOGIN_CMPT login completed");
                    myUser.myCTC=this;
                    sendUserHisBuddiesInfo(myUser);
                    sendUserHisOldPendingListQueue(myUser);
                    sendUserHisOfflineCommandQueue(myUser);
                    notifyBuddiesThatUserIsOnline(myUser);
                }
                else
                {
                    myTalker.send("NO_GOOD_USER_ALREADY_LOGGED_IN");
                    throw new MyException("<Server CTC> User already logged in (Custom exception)");
                }
            }

        }//end of else if

    }//end of doRegOrLogin()

    //======================================================================================
    void sendUserHisBuddiesInfo(User urUser) throws IOException
    {
        User myUser = urUser;
        for(int j=0;j<myUser.buddyList.size();j++)
        {
            if( ctcHashtable.get( myUser.buddyList.elementAt(j) ).myCTC !=null )//which is (anyUser.myCTC !=null)
            {
                myTalker.send("ADD_ONLINE_BUDDY_LIST_OLD "+myUser.buddyList.elementAt(j) );
            }
            else
            {
                myTalker.send("ADD_OFFLINE_BUDDY_LIST_OLD "+myUser.buddyList.elementAt(j) );
            }
        }
    }
    //======================================================================================
    void sendUserHisOldPendingListQueue(User urUser) throws IOException
    {
        User myUser = urUser;
        Iterator<String> it = myUser.pendingListQueue.iterator();
        while(it.hasNext())
        {
            myTalker.send(it.next());
        }
    }
    //======================================================================================
    void sendUserHisOfflineCommandQueue(User urUser) throws IOException
    {
        User myUser = urUser;
        Iterator<String> it = myUser.offlineCommandQueue.iterator();
        while(it.hasNext())
        {
            myTalker.send(it.next());
        }
        myUser.offlineCommandQueue.clear();
        dos=new DataOutputStream(new FileOutputStream("user.dat"));
        ctcHashtable.store(dos);
    }

    //======================================================================================
    void notifyBuddiesThatUserIsOnline(User urUser) throws IOException
    {
        User myUser = urUser;
        for(int j=0;j<myUser.buddyList.size();j++)
        {
            if( ctcHashtable.get( myUser.buddyList.elementAt(j) ).myCTC !=null )//which is (anyUser.myCTC !=null)
            {
                ctcHashtable.get( myUser.buddyList.elementAt(j) ).myCTC.myTalker.send("I_AM_ONLINE "+myUser.username );
            }
        }
    }

    //======================================================================================
    void notifyBuddiesThatUserIsOffline(User urUser)
    {
        try
        {
            User myUser = urUser;
            for(int j=0;j<myUser.buddyList.size();j++)
            {
                if( ctcHashtable.get( myUser.buddyList.elementAt(j) ).myCTC !=null )//which is (anyUser.myCTC !=null)
                {
                    //Server will send my friend that I'm offline with my username
                    ctcHashtable.get( myUser.buddyList.elementAt(j) ).myCTC.myTalker.send("I_WENT_OFFLINE "+myUser.username );
                }
            }
        }
        catch(Exception ess)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            System.out.println("<Server CTC> Exception in notifyBuddiesThatUserIsOffline()in CTC !");
            ess.printStackTrace();
            //JOptionPane.showMessageDialog(null,"<Server CTC> Exception in notifyBuddiesThatUserIsOffline()in CTC !");
            System.out.println("#####################################################################################");
        }
    }

}//end of class CTC


