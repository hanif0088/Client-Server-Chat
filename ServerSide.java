import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.event.*;
//import sun.misc.*;
import javax.net.ssl.*;
//import java.security.KeyStore;
import java.security.*;
import java.security.cert.*;

public class ServerSide
{
	public static void main(String[] args)
	{
		int     						PORT_NUM = 6789;
		SSLContext              		sslContext;
		KeyManagerFactory       		keyManagerFactory;
		KeyStore                		keyStore;
		char[]                  		keyStorePassphrase;

		SSLServerSocketFactory  		sslServerSocketFactory;
		SSLServerSocket         		sslServerSocket;
		SSLSocket               		sslNormalSocket;

		DataInputStream					dis;
		CTC 							myCTC;
		ServerSide						myServerSide=new ServerSide();
		MyHashtable 					myHashtable;

		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Server Started....>>>>>>>>>>>>>>>>>>>>>>>>");
		try
		{
			if(new File("user.dat").exists())
			{
				System.out.println("new file exists!");
				dis=new DataInputStream(new FileInputStream("user.dat"));
				myHashtable= new MyHashtable(dis);
			}
			else
			{
				myHashtable= new MyHashtable();
			}

			sslContext = SSLContext.getInstance("SSL");
			keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyStore  = KeyStore.getInstance("JKS");
			keyStorePassphrase = "passphrase".toCharArray(); // obviously not secure
			keyStore.load(new FileInputStream("testkeys"), keyStorePassphrase);// "testkeys" is the file name

			keyManagerFactory.init(keyStore, keyStorePassphrase);
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
			sslServerSocketFactory = sslContext.getServerSocketFactory();
			//sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

			//System.out.println("Got a server socket factory...");
			sslServerSocket =(SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT_NUM);
			//System.out.println("Got a secure server socket...");
			//try {Thread.sleep(1000);} catch(Exception ee){}

			while(true)
			{
				sslNormalSocket = (SSLSocket)sslServerSocket.accept();
				//System.out.println("Got a normal socket...");
				System.out.println(" Connection accepted! <.accept()>");
				myCTC=new CTC(sslNormalSocket,myHashtable);
			}

		}
		catch (IOException ioe)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			ioe.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
		catch (NoSuchAlgorithmException nsae)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			nsae.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
		catch (KeyStoreException kse)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			kse.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
		catch (CertificateException ce)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			ce.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
		catch (UnrecoverableKeyException uke)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			uke.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
		catch (KeyManagementException kme)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			kme.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
		catch (Exception e)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ServerSide> Exception in ServerSide!");
			System.out.println(">>>>>>>>>> Error creating secure socket.  Stack trace follows.");
			e.printStackTrace();
			System.out.println("########################################################################");
			System.exit(1);
		}
	}
	//============================================================================================
	ServerSide()
	{

	}

}//end of class


