import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Object;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;

class ProgressUpdater implements Runnable
{
	int 			progressValue;
	JProgressBar    myJProgressBar;

	//=============================================================================
	public	ProgressUpdater( JProgressBar urJProgressBar )
	{
		myJProgressBar = urJProgressBar;
		progressValue = 0;
	}

	//==================================================================================
	public void run()
	{
		myJProgressBar.setVisible(true);
		myJProgressBar.setValue(progressValue);
		if(myJProgressBar.getMaximum() == progressValue )
		{
			myJProgressBar.setVisible(false);
		}
	}

	//======================================================================
	public void setValue(int value)
	{
		progressValue = value;
	}
}


