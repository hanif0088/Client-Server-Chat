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
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.table.TableColumn;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class ClientSideFrame extends JFrame implements ActionListener,ListSelectionListener,MouseListener
{
    public static void main(String[] x)
    {
       //ClientSideFrame();
    }

    //=========================================================================================
    JButton                     startChatButton;
    JButton                     deleteBuddyButton;
    JButton                     blockBuddyButton;
    JButton                     addBuddyButton;
    JButton                     logoutButton;
    JPanel                      southButtonPanel;
    JPanel                      northButtonPanel;
    Container                   contentPane;
    String                      addBuddyUsername;
    LoginDialog                 myLoginDialog;
    Validate                    myValidate;
    CTS                         myCTS;
    JScrollPane                 myScrollPane;
    String                      username;

    JTable                      myJTable;
    MyTableModel                myTableModel;
    ListSelectionModel          myListSelectionModel;
    DefaultTableColumnModel     myColumnModel;
    JScrollPane                 scroller;
    TableColumn                 myTableColumn;
    static final String[]       ColumnHeader= {"Status","User Name"};
    int[]                       selectionList = new int[20];
    FileReceiverManager			myFileReceiverManager;

    ClientSideFrame(LoginDialog urLoginDialog,CTS urCTS)
    {
		myFileReceiverManager = new FileReceiverManager();
        myTableModel = new MyTableModel();
        myJTable=new JTable(myTableModel);
        myJTable.setFont(new Font("Courier", Font.BOLD,12));
        myJTable.setMinimumSize(new Dimension(10, 10));
        buildColumns();

        myJTable.setColumnModel(myColumnModel);
        scroller=new  JScrollPane(myJTable);
        myJTable.setPreferredScrollableViewportSize(new Dimension(420, 60));

        myListSelectionModel=myJTable.getSelectionModel();
        myListSelectionModel.addListSelectionListener(this);
		myJTable.addMouseListener(this);

        myLoginDialog = urLoginDialog;
        username = myLoginDialog.username;
        myCTS = urCTS;
        myCTS.setListModel(myTableModel,this);

        myValidate = new Validate();
        logoutButton=new JButton("LogOut");
        logoutButton.addActionListener(this);
        startChatButton =new JButton("Start Chat");
        startChatButton.addActionListener(this);
        deleteBuddyButton=new JButton("Delete Buddy");
        deleteBuddyButton.addActionListener(this);
        deleteBuddyButton.setEnabled(false);
        blockBuddyButton=new JButton("Block Buddy");
        blockBuddyButton.addActionListener(this);
        blockBuddyButton.setEnabled(false);

        addBuddyButton=new JButton("Add Buddy");
        addBuddyButton.addActionListener(this);

        southButtonPanel=new JPanel(new FlowLayout());
        southButtonPanel.add(startChatButton);
        southButtonPanel.add(deleteBuddyButton);
        southButtonPanel.add(blockBuddyButton);
        southButtonPanel.add(addBuddyButton);
        southButtonPanel.add(logoutButton);

        contentPane=getContentPane();
        contentPane.add(scroller,BorderLayout.CENTER);
        contentPane.add(southButtonPanel,BorderLayout.SOUTH);
        setupMainFrame();
        this.addWindowListener(new WindowEventHandler(myCTS,username) );

        startChatButton.setEnabled(false);
        deleteBuddyButton.setEnabled(false);
    }

    //==================================================================================================================
    public void valueChanged(ListSelectionEvent lse)
    {
        //System.out.println("ListSelectionEvent entered");
        startChatButton.setEnabled(false);
        deleteBuddyButton.setEnabled(false);
        blockBuddyButton.setEnabled(false);

        selectionList=myJTable.getSelectedRows();
        if(selectionList.length==1)
        {
            startChatButton.setEnabled(true);
            deleteBuddyButton.setEnabled(true);
            blockBuddyButton.setEnabled(true);
        }
        else if(selectionList.length>1)
        {
            deleteBuddyButton.setEnabled(true);
            blockBuddyButton.setEnabled(true);
        }
    }
    //==============================================================================
	public void mousePressed( MouseEvent e ){}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e)
    {
		if (e.getClickCount() == 2 && !e.isConsumed())
		{
		    e.consume();
			Point p = e.getPoint();
			int rowNumber = myJTable.rowAtPoint( p );
			//System.out.println("rowNumber pressed "+rowNumber);
			ListSelectionModel model = myJTable.getSelectionModel();
			model.setSelectionInterval( rowNumber, rowNumber );
			startChatWhenMouseClicked(rowNumber);
		}
	}

    //============================================================================================================
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if(e.getSource() == logoutButton)
            {
                myCTS.myTalker.send("LOG_OUT "+ username);
				for(int i=0;i<myJTable.getRowCount();i++)
				{

					BuddyInfo myBuddyInfo = myTableModel.myListModel.getElementAt(i);
					if( myBuddyInfo.myChatBoxDialog != null)
					{
						myBuddyInfo.myChatBoxDialog.dispose();
					}

				}
                this.dispose();

            }
            else if(e.getSource() == startChatButton)
            {
				startChat();
            }

            else if(e.getSource() == deleteBuddyButton)
            {
				doDeleteBuddy();
            }

            else if(e.getSource() == blockBuddyButton)
            {
				doBlockBuddy();
            }

            else if(e.getSource() == addBuddyButton)
            {
                doAddingBuddy();
            }

        }//end of try

        catch(Exception xe)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            System.out.println("<ClientSideFrame> Exception in ClientSideFrame actionPerformed().");
            xe.printStackTrace();
            JOptionPane.showMessageDialog(this,"<ClientSideFrame> Exception in ClientSideFrame actionPerformed()!)");
            System.out.println("#######################################################");
        }
    }
    //====================================================================================
    void doBlockBuddy()throws IOException
    {
		int n=0;
		for (int i=0; i<selectionList.length; i++)
		{
			BuddyInfo myBuddyInfo = myTableModel.myListModel.getElementAt( (selectionList[i]-n) );
			//I'm deleting and blocking my friend. So I'm sending my friend's username to the server.
			myCTS.myTalker.send("DELETE_AND_BLOCK_FRD_TO_RCVR "+ myBuddyInfo.buddysUsername );
			if(myBuddyInfo.myChatBoxDialog !=null)
			{
				myBuddyInfo.myChatBoxDialog.dispose();
			}
			myTableModel.myListModel.removeElementAt((selectionList[i]-n));
			n++;
		}
		myJTable.repaint();
		myTableModel.fireTableDataChanged();
		blockBuddyButton.setEnabled(false);
	}
	//=====================================================================================================
    void doDeleteBuddy()throws IOException
    {
		int n=0;
		for (int i=0; i<selectionList.length; i++)
		{
			BuddyInfo myBuddyInfo = myTableModel.myListModel.getElementAt( (selectionList[i]-n) );
			//I'm deleting my friend. So I'm sending my friend's username to the server.
			myCTS.myTalker.send("DELETE_FRD_TO_RCVR "+ myBuddyInfo.buddysUsername );
			//System.out.println( myBuddyInfo.buddysUsername );

			if(myBuddyInfo.myChatBoxDialog !=null)
			{
				myBuddyInfo.myChatBoxDialog.dispose();
			}
			myTableModel.myListModel.removeElementAt((selectionList[i]-n));
			n++;
		}
		myJTable.repaint();
		myTableModel.fireTableDataChanged();
		deleteBuddyButton.setEnabled(false);
	}
	//==============================================================================
	void startChatWhenMouseClicked(int row)
	{
		//getting my friend's username from listModel
		BuddyInfo myBuddyInfo = myTableModel.myListModel.getElementAt( row );
		String titleUsername = null;
		//System.out.println(myBuddyInfo.buddysUsername);
		if(myBuddyInfo.buddysStatus == true)
		{
			titleUsername = myBuddyInfo.buddysUsername+" online";
		}
		else
		{
			titleUsername = myBuddyInfo.buddysUsername+" offline";
		}

		//if dialog  doesnot exist opening my friend's new ChatBoxDialog
		if(myBuddyInfo.myChatBoxDialog == null)
		{
			System.out.println("<ClientSideFrame> "+myBuddyInfo.buddysUsername+"myChatBoxDialog is null, So creating new myChatBoxDialog");
			myBuddyInfo.myChatBoxDialog = new ChatBoxDialog(titleUsername,myCTS,this,myBuddyInfo.buddysUsername);
		}
		else//it's already exists
		{
			System.out.println("<ClientSideFrame> "+myBuddyInfo.buddysUsername+"myChatBoxDialog isn't null, So update old myChatBoxDialog");
			myBuddyInfo.myChatBoxDialog.setVisible(true);
			myBuddyInfo.myChatBoxDialog.setTitle(titleUsername);
		}
	}
	//==============================================================================
	void startChat()throws IOException
	{
		//getting my friend's username from listModel
		BuddyInfo myBuddyInfo = myTableModel.myListModel.getElementAt( selectionList[0] );
		String titleUsername = null;
		//System.out.println(myBuddyInfo.buddysUsername);
		if(myBuddyInfo.buddysStatus == true)
		{
			titleUsername = myBuddyInfo.buddysUsername+" online";
		}
		else
		{
			titleUsername = myBuddyInfo.buddysUsername+" offline";
		}

		//if dialog  doesnot exist opening my friend's new ChatBoxDialog
		if(myBuddyInfo.myChatBoxDialog == null)
		{
			System.out.println("<ClientSideFrame> "+myBuddyInfo.buddysUsername+"myChatBoxDialog is null, So creating new myChatBoxDialog");
			myBuddyInfo.myChatBoxDialog = new ChatBoxDialog(titleUsername,myCTS,this,myBuddyInfo.buddysUsername);
		}
		else//it's already exists
		{
			System.out.println("<ClientSideFrame> "+myBuddyInfo.buddysUsername+"myChatBoxDialog isn't null, So update old myChatBoxDialog");
			myBuddyInfo.myChatBoxDialog.setVisible(true);
			myBuddyInfo.myChatBoxDialog.setTitle(titleUsername);
		}
	}

    //=======================================================================
    void doAddingBuddy() throws IOException
    {
        //AddBuddyDialog myAddBuddyDialog = new AddBuddyDialog(this);
        //addBuddyUsername = myAddBuddyDialog.buddyUsername;
		try
		{
			addBuddyUsername = JOptionPane.showInputDialog(this,"Please enter the buddy's username:").trim();
			if (addBuddyUsername != null)
			{
				if(myValidate.isUsernameValid(addBuddyUsername) && !addBuddyUsername.equals(""))
				{
					if ( !myTableModel.isUsernameOnListModel(addBuddyUsername) && !addBuddyUsername.equals(username)  )
					{
						//I'm sending a friend req with friend's username to the server
						myCTS.myTalker.send("INITIATE_FRD_REQ_TO_RCVR "+ addBuddyUsername);
					}
					else
					{
						JOptionPane.showMessageDialog(this,"<ClientSideFrame> Buddy's username is in your list or same as yours.");
					}
				}
				else
				{
					JOptionPane.showMessageDialog(this,"<ClientSideFrame> Buddy's username isn't valid.");
				}
			}
		}
        catch(NullPointerException npe)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            System.out.println("<ClientSideFrame> Exception in ClientSideFrame actionPerformed() doAddingBuddy()!");
            System.out.println("<ClientSideFrame> Buddy's username is null.");
            //npe.printStackTrace();
            //JOptionPane.showMessageDialog(this,"<ClientSideFrame> Buddy's username is null.");
            System.out.println("#######################################################");
        }
    }

    //==========================================================================================
    void buildColumns()
    {
        myColumnModel=new DefaultTableColumnModel();
        for(int m=0;m<2;m++)
        {
            myTableColumn=new TableColumn(m);
            myTableColumn.setPreferredWidth(100);
            myTableColumn.setMinWidth(50);
            myTableColumn.setHeaderValue(ColumnHeader[m]);
            myColumnModel.addColumn(myTableColumn);
        }
    }
    //======================================================================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;

        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/3, d.height / 3);
        setLocation(d.width / 5, d.height / 5);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(myLoginDialog.username);
        setVisible(true);
    }

}//end of Frame class


