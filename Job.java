import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;


public class Job
{
	String			senderUsarname;
	String			outFileName;
	long			longFileSize;
	JProgressBar    myJProgressBar;

	Job(String senderName,String urFileName, long urFileSize,JProgressBar  urJProgressBar)
	{
		senderUsarname = senderName ;
		outFileName = urFileName ;
		longFileSize = urFileSize;
		myJProgressBar = urJProgressBar;
	}

}
