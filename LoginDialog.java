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


public class LoginDialog extends JDialog implements ActionListener,DocumentListener
{
    public static void main(String[] x)
    {
        System.out.println("*********************Client*********************************");
        new LoginDialog();
    }
    //---------------------------------------------------------------------------------------
    CTS                     myCTS;
    String                  username;
    String                  password;
    JButton                 loginButton;
    JButton                 registerButton;
    JButton                 cancelButton;
    JPanel                  buttonPanel;

    JLabel                  usernameLabel;
    JTextField              usernameTextField;
    JPanel                  usernamePanel;

    JLabel                  passwordLabel;
    JTextField              passwordTextField;
    JPanel                  passwordPanel;

    JPanel                  mainPanel;
    GridBagConstraints      gbc;
    ClientSideFrame         myClientSideFrame;
    String                  messageReceived;
    Validate                myValidate;

	//====================================================================================================
    LoginDialog()
    {
        myValidate = new Validate();
        usernameTextField = new JTextField(15);
        usernameLabel=new JLabel("Username:");
        usernamePanel=new JPanel();
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        passwordTextField=new JTextField(15);
        passwordLabel=new JLabel("Password:");
        passwordPanel=new JPanel();
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);

        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        registerButton = new JButton("Register");

        loginButton.setActionCommand("LOGIN");
        cancelButton.setActionCommand("CANCEL");
        registerButton.setActionCommand("REGISTER");
        registerButton.addActionListener(this);
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);
        cancelButton.setVerifyInputWhenFocusTarget(false);

        buttonPanel=new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        getRootPane().setDefaultButton(loginButton);

        mainPanel=new JPanel(new GridBagLayout());

        gbc= new GridBagConstraints();
        gbc.insets= new Insets(5,0,5,0);

        gbc.gridx=0;
        gbc.gridy=1;
        mainPanel.add(usernamePanel,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        mainPanel.add(passwordPanel,gbc);

        gbc.gridx=0;
        gbc.gridy=3;
        mainPanel.add(buttonPanel,gbc);
        add(mainPanel);

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        usernameTextField.getDocument().addDocumentListener(this);
        passwordTextField.getDocument().addDocumentListener(this);
        setupMainFrame();
    }

    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
        //System.out.println("insertUpdate");
        String usernameString = usernameTextField.getText().trim();
        String passwordString = passwordTextField.getText().trim();

        if( !usernameString.equals("") &&  !passwordString.equals(""))
        {
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
        }
    }
    //========================================================================================================
    public void changedUpdate(DocumentEvent de)
    {
        //System.out.println("chabgeUpdate");
    }
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
        //System.out.println("removeUpdate");
        String usernameString = usernameTextField.getText().trim();
        String passwordString = passwordTextField.getText().trim();

        if( usernameString.equals("") ||  passwordString.equals(""))
        {
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
        }
    }
    //=======================================================================================================
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("CANCEL"))
        {
            dispose();
        }
        //=============================================================================================
        else if(e.getActionCommand().equals("REGISTER"))
        {
            System.out.println("registerButton clicked");
            if ( myValidate.isUsernameValid(usernameTextField.getText().trim() ) == true )
            {
                if( myValidate.isPasswordValid( passwordTextField.getText().trim() ) == true )
                {
                    doRegister();
                }
                else
                {
                    JOptionPane.showMessageDialog(this,"Password length should be 6 to 20 with any A-Z,a-z,0-9 @#$%_- ");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Username length should be 3 to 15 with any A-Z,a-z,0-9");
            }
        }
        //=========================================================================================
        else if(e.getActionCommand().equals("LOGIN"))
        {
            System.out.println("loginButton clicked");
            doLogin();
        }
    }//end of actionPerformed()

    //=============================================================================
    void doRegister()
    {
        try
        {
            username = usernameTextField.getText().trim();
            password = passwordTextField.getText().trim();

            myCTS = new CTS("127.0.0.1",6789,username);
            myCTS.myTalker.send("REG_USER "+ username);

            messageReceived= myCTS.myTalker.receive();
            if(messageReceived.startsWith("NOGOOD_REG_USER") )
            {
                JOptionPane.showMessageDialog(this,"<Client LoginDialog> User already registered");
            }
            else if( messageReceived.startsWith("+OK_USERNAME_ACPT") )
            {
                myCTS.myTalker.send("REG_PASS "+ password);

                messageReceived = myCTS.myTalker.receive();
                if(messageReceived.startsWith("NOGOOD_REG_PASS"))
                {
                    System.out.println("NOGOOD_REG_PASS ");
                    JOptionPane.showMessageDialog(this,"<Client LoginDialog> Password not valid");
                }
                else if(messageReceived.startsWith("+OK_REG_CMPT"))
                {
                    System.out.println("+OK_REG_CMPT");
                    //JOptionPane.showMessageDialog(this,"<Client LoginDialog> Registration completed.");
                    loginButton.doClick();
                }
            }

        }//end of try

        catch(ConnectException ce)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            ce.printStackTrace();
            System.out.println("<Client LoginDialog> Exception in LoginDialog actionPerformed()....doRegister()!)");
            JOptionPane.showMessageDialog(this,"<Client LoginDialog> Could not connect to the Server.Server is offline!");
            System.out.println("#####################################################################################");

        }
        catch(Exception e)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            e.printStackTrace();
            System.out.println("<Client LoginDialog> Exception in LoginDialog actionPerformed()....doRegister()!)");
            JOptionPane.showMessageDialog(this,"<Client LoginDialog> Exception in LoginDialog actionPerformed()....doRegister()!)");
            System.out.println("#####################################################################################");
        }

    }//end of doRegister()

    //====================================================================================================
    void doLogin()
    {
        try
        {
            username = usernameTextField.getText().trim();
            password = passwordTextField.getText().trim();

            myCTS = new CTS("127.0.0.1",6789,username);
            myCTS.myTalker.send("LOGIN_USER "+ username);
            messageReceived= myCTS.myTalker.receive();

            if(messageReceived.startsWith("+OK_LOGIN_USER") )
            {
                myCTS.myTalker.send("LOGIN_PASS "+ password);
                messageReceived= myCTS.myTalker.receive();
                if(messageReceived.startsWith("NOGOOD_LOGIN") )
                {
                    JOptionPane.showMessageDialog(this,"<Client LoginDialog> Username or password didn't matched.");
                }
                else if (messageReceived.startsWith("NO_GOOD_USER_ALREADY_LOGGED_IN") )
                {
                    JOptionPane.showMessageDialog(this,"<Client LoginDialog> User already logged in somewhere!");
                }
                else if (messageReceived.startsWith("+OK_LOGIN_CMPT") )
                {
                    //JOptionPane.showMessageDialog(this,"<Client LoginDialog> Login completed");
                    myClientSideFrame= new ClientSideFrame(this,myCTS);
                    new Thread(myCTS).start();
                    dispose();
                }
            }

        }//end of try
        catch(ConnectException ce)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            ce.printStackTrace();
            System.out.println("<Client LoginDialog> Exception in LoginDialog actionPerformed()....doRegister()!)");
            JOptionPane.showMessageDialog(this,"<Client LoginDialog> Could not connect to the Server.Server is offline!");
            System.out.println("#####################################################################################");
        }
        catch(Exception e)
        {
            System.out.println("################### Hanif caught the Exception ##############################");
            System.out.println("<Client LoginDialog> Exception in LoginDialog actionPerformed()....doLogin()!");
            e.printStackTrace();
            //JOptionPane.showMessageDialog(this,"<Client LoginDialog> Exception in LoginDialog actionPerformed()....doLogin()!)");
            System.out.println("#####################################################################");
        }

    }//end of doLogin()

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
        setTitle("Login Dialog");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }

}//end of class LoginDialog












    //System.out.println(passwordVerifier.isPasswordValid);
    //System.out.println("password is  "+passwordTextField.getText());