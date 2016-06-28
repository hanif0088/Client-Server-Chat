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

public class Validate
{
	String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
	String PASSWORD_PATTERN = "^[A-Za-z0-9@#$%_-]{6,20}$";   //real pass
	//String PASSWORD_PATTERN ="(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,10}";

	Matcher 		matcher;
	Pattern 		pattern;

	//===========================================================
	Validate()
	{

	}
	//==============================================================
	boolean isUsernameValid(String username)
	{
		pattern = Pattern.compile(USERNAME_PATTERN);
		matcher = pattern.matcher(username);
		return matcher.matches();
	}
	//=====================================================================
	boolean isPasswordValid(String password)
	{
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		return matcher.matches();
	}

}