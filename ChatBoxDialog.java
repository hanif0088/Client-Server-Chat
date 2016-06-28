import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.*;
import java.net.ConnectException;
import java.lang.Exception.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.*;
import java.lang.Exception.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.imageio.*;

public class ChatBoxDialog extends JDialog implements ActionListener,KeyListener,DropTargetListener
{
	public static void main(String[] x)
	{
		//new ChatBoxDialog();
	}
	//=========================================================================================
	JTextPane 				chatTextPane;
	JScrollPane 			scrollingTextPane;
	JLabel          		textPaneLabel;
    JTextArea       		sendMessageTextArea;
    JLabel          		textAreaLabel;
    JScrollPane 			scrollingTextArea;
    JButton         		sendButton;
    JButton					exitButton;
    JPanel          		buttonPanel;
    JPanel          		textAreaPanel;
    JPanel          		textPanePanel;
    JPanel          		areaAndPanePanel;
	Container               cp;
	StyledDocument 			doc;
	java.util.List<File> 	fileList;
	Transferable			transferableData;
	DropTarget   			dropTarget;
	String 					myTitleUsername;
	CTS						myCTS;
	ClientSideFrame 		myClientSideFrame;
	String 					receiverUsername;
	JProgressBar			myJProgressBar;
	JPanel          		proBarPanel;

    ChatBoxDialog(String titleUsername,CTS urCTS,ClientSideFrame urClientSideFrame,String receiver)
    {
		receiverUsername = receiver;
		myTitleUsername = titleUsername;
		myCTS = urCTS;
		myCTS.setChatBoxDialog(this);
		myClientSideFrame = urClientSideFrame;
		setLocationRelativeTo(myClientSideFrame);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setActionCommand("SEND");
        getRootPane().setDefaultButton(sendButton);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        exitButton.setActionCommand("EXIT");

        buttonPanel=new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(exitButton);

		textAreaLabel = new JLabel("Type message: ");
		sendMessageTextArea = new JTextArea(6,28);
		scrollingTextArea = new JScrollPane(sendMessageTextArea);
		sendMessageTextArea.addKeyListener(this);

        chatTextPane = new JTextPane();
        chatTextPane.setPreferredSize(new Dimension(350,150));
        chatTextPane.setContentType("text/html");
        chatTextPane.setEditable(false);
		scrollingTextPane = new JScrollPane(chatTextPane);
		scrollingTextPane.setPreferredSize(new Dimension(350,150));
        //scrollingTextPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);//new
        //scrollingTextPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//new
		textPaneLabel = new JLabel("Chat conversation:");

    	textAreaPanel = new JPanel();
        textPanePanel = new JPanel();
        areaAndPanePanel = new JPanel(new BorderLayout());

        textAreaPanel.add(textAreaLabel);
        textAreaPanel.add(scrollingTextArea);
        textPanePanel.add(textPaneLabel);
		textPanePanel.add(scrollingTextPane);

		areaAndPanePanel.add(textPanePanel,BorderLayout.NORTH);
		areaAndPanePanel.add(textAreaPanel,BorderLayout.SOUTH);

		myJProgressBar = new JProgressBar();
		proBarPanel = new JPanel();
		proBarPanel.add( myJProgressBar );

		cp=getContentPane();
		cp.setLayout(new BorderLayout());
        cp.add(areaAndPanePanel,BorderLayout.NORTH);
        cp.add(proBarPanel,BorderLayout.CENTER);
        cp.add(buttonPanel,BorderLayout.SOUTH);
        setupMainFrame();

		myJProgressBar.setVisible(false);
        doc = chatTextPane.getStyledDocument();
        dropTarget = new DropTarget(this,this);
    }

    //=======================================================================================================
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("EXIT"))
        {
			dispose();
        }
        else if(e.getActionCommand().equals("SEND"))
        {
			sendMessage();
        }

	}
	//========================================================================
	void sendMessage()
	{
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.RED);
		StyleConstants.setFontSize(keyWord, 14);
		StyleConstants.setBold(keyWord, true);
		try
		{
			if(sendMessageTextArea.getText().trim().length()>0)
			{
				String msg = sendMessageTextArea.getText().trim();
				msg = msg.replace('\n',(char)178);
				String msg2 = msg.replace((char)178,'\n');
				//doc.insertString(doc.getLength(), sendMessageTextArea.getText().trim()+"\n", keyWord );
				doc.insertString(doc.getLength(), msg2+"\n", keyWord );
				//I'm sending chat msg to server with his username and actual msg
				myCTS.myTalker.send("CHAT_MSG_TO_RCVR "+ receiverUsername +" "+msg);
			}
		}
		catch(IOException ioe)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ChatBoxDialog> Exception in ChatBoxDialog!");
			ioe.printStackTrace();
			myClientSideFrame.dispose();
			dispose();
			//xe.printStackTrace();
			System.out.println("###################################################");
		}
		catch(Exception xe)
		{
			System.out.println("############################Hanif caught the Exception ##################");
			System.out.println("<ChatBoxDialog> Exception in ChatBoxDialog!");
			xe.printStackTrace();
			dispose();
			//xe.printStackTrace();
			System.out.println("###################################################");
		}
		sendMessageTextArea.setText("");
	}
	//========================================================================
	void receivedMessage(String msg)
	{
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.BLUE);
		StyleConstants.setFontSize(keyWord, 14);
		StyleConstants.setBold(keyWord, true);
		try
		{
			String msg3 = msg.replace((char)178,'\n');
			doc.insertString(doc.getLength(), msg3+"\n", keyWord );
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
	}

	//===================================================================================================
	public void	keyReleased(KeyEvent event)
	{
        if(event.getKeyCode() == event.VK_ENTER)
        {

			 if(event.isShiftDown())
			 {
				//System.out.println("ShiftDown");
				sendMessageTextArea.setText(sendMessageTextArea.getText()+"\n");
			 }
			 else
			 {
 				sendButton.doClick();
			 }
        }
	}
    public void keyPressed(KeyEvent event){}
	public void	keyTyped(KeyEvent e){}

	//==================================================================================================
	public void   dragEnter(DropTargetDragEvent dtde){}
	public void   dragExit(DropTargetEvent dte){}
	public void   dragOver(DropTargetDragEvent dtde){}
	public void   dropActionChanged(DropTargetDragEvent dtde){}
	//=============================================================
	public void   drop(DropTargetDropEvent dtde)
	{
		transferableData = dtde.getTransferable();
		try
		{
			if (transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrop(DnDConstants.ACTION_COPY);

				fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));
				for (int n=0; n<fileList.size(); n++)
				{
					//System.out.println(fileList.get(n).getCanonicalPath());
				}

				String pathFileName = fileList.get(0).getCanonicalPath();
				String fileName = pathFileName.substring( pathFileName.lastIndexOf("\\")+1 );
				long   fileSize = fileList.get(0).length();

				int response = JOptionPane.showConfirmDialog(this, "Do you want to send "+fileName+" to "+ receiverUsername , "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION){}

				else if (response == JOptionPane.YES_OPTION)
				{
				  myCTS.myTalker.send("FILE_XFER_REQ_TO_RECEIVER "+ receiverUsername+" "+pathFileName+" "+fileSize );
				}

				else if (response == JOptionPane.CLOSED_OPTION){}
			}
			else
			{
				System.out.println("<ChatBoxDialog> File list flavor not supported.");
			}

		}
		catch (UnsupportedFlavorException  ufe)
		{
			System.out.println("<ChatBoxDialog> Unsupported flavor found!");
			ufe.printStackTrace();
		}
		catch (IOException  ioe)
		{
			System.out.println("<ChatBoxDialog> IOException found getting transferable data!");
			ioe.printStackTrace();
		}

	}//end of drop

	//============================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width / 3, d.height / 2);
        setLocation(d.width / 4, d.height / 4);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(myTitleUsername);
        setVisible(true);
    }


}//end of class