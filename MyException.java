import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.*;

public class MyException extends Exception
{

    public MyException(String exc)
    {
        super(exc);
    }
    public String getMessage()
    {
        return super.getMessage();
    }
}