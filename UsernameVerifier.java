import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameVerifier extends InputVerifier
{
	String 			USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
	JTextField		myTextField;
	String 			usernameString;
	boolean         isUsernameValid=false;
	LoginDialog		myLoginDialog;
	Matcher 		matcher;
	Pattern 		pattern;

	UsernameVerifier(LoginDialog urLoginDialog)
	{
		myLoginDialog=urLoginDialog;
	}

	@Override
	public boolean verify(JComponent c)
	{

		System.out.println("Entered in UsernameVerifier ");
		isUsernameValid=false;
		myTextField = (JTextField)c;
		usernameString = myTextField.getText().trim();

		if(usernameString.equals(""))
			return true;

		pattern = Pattern.compile(USERNAME_PATTERN);
		matcher = pattern.matcher(usernameString);
		isUsernameValid = matcher.matches();

		if(isUsernameValid==false)
		{
			JOptionPane.showMessageDialog(c,"Username length should be 3 to 15 with any A-Z,a-z,0-9");
			return false;
		}
		return true;
	}
}


//myLoginDialog.loginButton.setEnabled(true);