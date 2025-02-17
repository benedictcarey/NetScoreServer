package com.ui;

import java.awt.BorderLayout;
import java.io.PrintStream;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
//import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.server.WSHandler;
import com.server.WebSocketServer;
 
public class UIMain extends JPanel
                             implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton mLoad, mSend;
	//private JTextArea jTextArea;
    private JFileChooser mFileChooser;
    private JLabel mImage;
    private byte[] mData;
    private WebSocketServer mWebSocketServer;
    public int clientChange;
    public String printStream;
    TimerTask task;
    JFileChooser chooser;
    String choosertitle = "select storage location for maxscore generated files";
    public static File usersPath;
	public int sessionCounter;
	public PrintStream standardOut;
 
    public UIMain() {
        super(new BorderLayout());
		
        //Create a file chooser
        mFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                ".png.jpg", "png","jpg");
        mFileChooser.setFileFilter(filter);
        mLoad = new JButton("Load");
        mLoad.addActionListener(this);
        
        mSend = new JButton("Send");
        mSend.addActionListener(this);
        mSend.setEnabled(false);
        
        // button panel
        JPanel buttonPanel = new JPanel(); 
        buttonPanel.add(mLoad);
		buttonPanel.add(mSend);
		
        add(buttonPanel, BorderLayout.PAGE_START);
        
        // image panel
		JPanel imageViewer = new JPanel();
		mImage = new JLabel();
		mImage.setSize(300, 300);
		imageViewer.add(mImage);
		add(imageViewer, BorderLayout.CENTER);
		// Text area
		JPanel jPanel = new JPanel();
		JTextArea jTextArea;
		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(jTextArea));
		jPanel.add(jTextArea);
		
		add(new JScrollPane(jTextArea));
		//imageViewer.add(jTextArea);
		add(jPanel, BorderLayout.EAST);
		// keeps reference of standard output stream
        standardOut = System.out;
         
        // re-assigns standard output stream and error output stream
		standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);

		//jTextArea.append( printStream );
		

		// WebSocketServer, File chooser
        chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {  
          System.out.println("getCurrentDirectory(): " 
             +  chooser.getCurrentDirectory());
          System.out.println("getSelectedFile() : " 
             +  chooser.getSelectedFile());
          usersPath = chooser.getSelectedFile();
          }   		
        mWebSocketServer = new WebSocketServer();
		mWebSocketServer.start();
    }

    @Override
    
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mLoad) {
	        
            int returnVal = mFileChooser.showOpenDialog(UIMain.this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = mFileChooser.getSelectedFile();     
                
                // load image data to byte array
                try {        	
                	BufferedImage bi = ImageIO.read(file);
            		ByteArrayOutputStream out = new ByteArrayOutputStream();
            		ImageIO.write(bi, "png", out);
            		mData = out.toByteArray();
            		out.close();
                } catch (IOException exception) {
                	exception.printStackTrace();
                }      
                mImage.setIcon(new ImageIcon(mData));
                mSend.setEnabled(true);
            	}
        } 
        
        else if (e.getSource() == mSend) {
        	ArrayList<WSHandler> sessions = WSHandler.getAllSessions();
        	for (WSHandler session : sessions) {
        		session.sendImage(mData);
        	}
        	mSend.setEnabled(false);
        }  
    }
    
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MaxScore Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new UIMain());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(1080, 768);
        
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        frame.setLocation((int)(width - frameWidth) / 2, (int)(height - frameHeight) / 2);
    }
 
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }
}

