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


public class AddBuddyDialog extends JDialog implements ActionListener,DocumentListener
{
    public static void main(String[] x)
    {
        //new AddBuddyDialog();
    }
    //=========================================================================================
    String                  buddyUsername = null;
    JButton                 okButton;
    JButton                 cancelButton;
    JPanel                  buttonPanel;

    JLabel                  usernameLabel;
    JTextField              usernameTextField;
    JPanel                  usernamePanel;

    JPanel                  mainPanel;
    GridBagConstraints      gbc;
    Validate                myValidate;
    ClientSideFrame			myClientSideFrame;

    AddBuddyDialog(ClientSideFrame urClientSideFrame)
    {
		myClientSideFrame = urClientSideFrame;
        myValidate = new Validate();
        usernameTextField = new JTextField(15);
        usernameLabel=new JLabel("Username:");
        usernamePanel=new JPanel();
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");

        okButton.setActionCommand("OK");
        cancelButton.setActionCommand("CANCEL");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        cancelButton.setVerifyInputWhenFocusTarget(false);

        buttonPanel=new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getRootPane().setDefaultButton(okButton);

        mainPanel=new JPanel(new GridBagLayout());

        gbc= new GridBagConstraints();
        gbc.insets= new Insets(5,0,5,0);

        gbc.gridx=0;
        gbc.gridy=1;
        mainPanel.add(usernamePanel,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        mainPanel.add(buttonPanel,gbc);
        add(mainPanel);

        okButton.setEnabled(false);
        usernameTextField.getDocument().addDocumentListener(this);
        setupMainFrame();
        this.setLocationRelativeTo(myClientSideFrame);
    }

    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
        String usernameString = usernameTextField.getText().trim();
        if( !usernameString.equals(""))
        {
            okButton.setEnabled(true);
        }
    }
    //========================================================================================================
    public void changedUpdate(DocumentEvent de)
    {
    }
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
        String usernameString = usernameTextField.getText().trim();
        if( usernameString.equals(""))
        {
            okButton.setEnabled(false);
        }
    }

    //=======================================================================================================
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("CANCEL"))
        {
            dispose();
        }
        //=========================================================================================
        else if(e.getActionCommand().equals("OK"))
        {
            buddyUsername = usernameTextField.getText().trim();
            dispose();
        }
    }//end of actionPerformed()

    //============================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width / 4, d.height / 4);
        setLocation(d.width / 4, d.height / 4);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Add Buddy Dialog");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }


}//end of class LoginDialog

