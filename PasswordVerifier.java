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

public class PasswordVerifier extends InputVerifier
{
	String PASSWORD_PATTERN = "^[A-Za-z0-9@#$%_-]{6,20}$";   //real pass
	//String PASSWORD_PATTERN ="(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,10}";
	JTextField		myTextField;
	String 			passwordString;
	boolean         isPasswordValid=false;
	LoginDialog		myLoginDialog;
	Matcher 		matcher;
	Pattern 		pattern;

	PasswordVerifier(LoginDialog urLoginDialog)
	{
		myLoginDialog=urLoginDialog;
	}

	@Override
	public boolean verify(JComponent c)
	{

		System.out.println("Entered in PasswordVerifier ");
		isPasswordValid=false;
		myTextField = (JTextField)c;
		passwordString = myTextField.getText().trim();

		if(passwordString.equals(""))
			return true;

		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(passwordString);
		isPasswordValid = matcher.matches();

		if(isPasswordValid==false)
		{
			JOptionPane.showMessageDialog(c,"Password length should be 6 to 20 with any A-Z,a-z,0-9 @#$%_- ");
			return false;
		}
		return true;
	}
}


//myLoginDialog.loginButton.setEnabled(true);