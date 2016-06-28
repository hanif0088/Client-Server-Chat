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

public class Talker
{
	SSLSocketFactory    			sslSocketFactory;
	SSLContext          			sslContext;
	KeyManagerFactory   			keyManagerFactory;
	KeyStore            			keyStore;
	char[]              			keyStorePassphrase;
	SSLSocket           			sslSocket;

	private DataOutputStream 		dos;
	private BufferedReader			myBufferedReader;
	private Socket					mySocket;
	private String 					myUserID;

	//====================================================================================================
	public Talker(String serverIPAddress,int PORT_NUM, String userID)throws Exception
	{

		//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());  // rather than edit the java.security file
		System.setProperty("javax.net.ssl.trustStore","samplecacerts");
		System.setProperty("javax.net.ssl.trustStorePassword","changeit");

		sslContext          = SSLContext.getInstance("SSL");
		keyManagerFactory   = KeyManagerFactory.getInstance("SunX509");
		keyStore            = KeyStore.getInstance("JKS");

		keyStorePassphrase = "passphrase".toCharArray();
		keyStore.load(new FileInputStream("testkeys"), keyStorePassphrase); // "testkeys" is the file name

		keyManagerFactory.init(keyStore, keyStorePassphrase);
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

		sslSocketFactory = sslContext.getSocketFactory();

		sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

		//System.out.println("got here");
		//sslSocket = (SSLSocket)socketFactory.createSocket("www.verisign.com", 443);
		sslSocket = (SSLSocket)sslSocketFactory.createSocket(serverIPAddress, PORT_NUM);

		sslSocket.startHandshake();
		//System.out.println("got SSLSocket...");

		myUserID=userID;
		dos = new DataOutputStream(sslSocket.getOutputStream());
		myBufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

	}
	//==========================================================================================================
	public Talker(SSLSocket sslSocket, String userID)throws Exception
	{
		myUserID=userID;
		dos = new DataOutputStream(sslSocket.getOutputStream());
		myBufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
	}

	//========================================================================
	public void setUsername(String username)
	{
		myUserID= username;
	}
	//==============================================================================================================
	public void send(String message)throws IOException
	{
		message = message+"\n";
		System.out.println("< Talker >"+myUserID + " sent >>> "+ message);
		dos.writeBytes(message);
	}
	//=======================================================================================================

	public  String receive()throws IOException
	{
		String msgReceived=null;
		msgReceived = myBufferedReader.readLine();
		System.out.println("< Talker >"+myUserID +" received <<< " + msgReceived);
		return msgReceived;
	}
	//=======================================================================================================
	public  String	expect(String expectedPefix) throws Exception
	{
		String response=null ;
		response = receive();
		if(!(response.toUpperCase()).startsWith(expectedPefix))
		{
			throw new Exception ("Expected "+ expectedPefix +" but received "+ response);
		}
		else
			return response;
	}

}//end of class Talker