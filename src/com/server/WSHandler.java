package com.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.ui.FileWatcher;
import com.ui.UIMain;


@WebSocket
public class WSHandler extends WebSocketHandler {
	private Session mSession;
	private static ArrayList<WSHandler> sessions = new ArrayList<WSHandler>();
	public String sFclient;
	public int sFclientOut = 0;
	public int zerocounter = 0;
	public static int sessionCounter = 0;
	public static String printerSwing;
	public static String part = "1";
	TimerTask task;
	TimerTask task2;
	
	public static ArrayList<WSHandler> getAllSessions() {
		return sessions;
	}
	
    @OnWebSocketClose
    
    public void onClose(int statusCode, String reason) {
        sessions.remove(this);
        System.out.println("Close: statusCode = " + statusCode + ", reason = " + reason + ", sessions = " + sessions.size());
        System.out.println( "Player: " + sessions + " has left" );
        sessionCounter = sessionCounter - 1;
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
        printerSwing = "Error: + " + t.getMessage();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
    	sessionCounter = sessionCounter + 1;
    	mSession = session;
    	sessions.add(this);
        System.out.println("Connect: " + session.getRemoteAddress().getAddress()); 
        System.out.println( "Player: " + sessionCounter + " has Joined" );
       final String testVar = ( UIMain.usersPath + "/" + sessionCounter + ".png" );
  			try {        	
       			File f = new File(testVar);
       			BufferedImage bi = ImageIO.read(f);
       			ByteArrayOutputStream out = new ByteArrayOutputStream();
       			ImageIO.write(bi, "png", out);
					ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
					mSession.getRemote().sendBytes(byteBuffer);
					out.close();
					byteBuffer.clear();
			        }
			       	catch (IOException e) {
			       		e.printStackTrace();
			        }	
  		final String textVar = ( UIMain.usersPath + "/" + sessionCounter + ".txt" );
  			try {        	
				File j = new File(textVar);
				FileInputStream bt = new FileInputStream(j);
				ByteArrayOutputStream tout = new ByteArrayOutputStream();
    			IO.copy(bt, tout);
    				ByteBuffer byteBuffer = ByteBuffer.wrap(tout.toByteArray());
    				mSession.getRemote().sendBytes(byteBuffer);
    				tout.close();
    				System.out.println( byteBuffer);
    				byteBuffer.clear();
		        	}
		       		catch (IOException e) {
		       			e.printStackTrace();
		       		}	
  			
        } 
   
	@OnWebSocketMessage  //part request from websocket client (remote browser)
    public void onMessage( Session session, final String message ) {
		sFclientOut = Integer.parseInt(message);
    	if ((sFclientOut > 0) & (sFclientOut < 500)) {
            System.out.println("Part " + sFclientOut + " requested");  
            System.out.println( UIMain.usersPath + "/" + message + ".png" );
            System.out.println( UIMain.usersPath + "/" + message + ".txt" );
            try {        	
    			File f = new File(UIMain.usersPath + "/" + message + ".png");
    			BufferedImage bi = ImageIO.read(f);
    			ByteArrayOutputStream out = new ByteArrayOutputStream();
    			ImageIO.write(bi, "png", out);
				ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
				mSession.getRemote().sendBytes(byteBuffer);
				out.close();
				byteBuffer.clear();
		        }
       			catch (IOException e) {
       			e.printStackTrace();
       			}
            final String testVar = ( UIMain.usersPath + "/" + message + ".png" );
            task = new FileWatcher( new File(testVar) ) {
        		//@SuppressWarnings("static-access")
				protected void onChange( File file ) {
        			// here we code the action on a change
        			System.out.println( "File "+ file.getName() +" has changed!" );
        			try {
        				Thread.sleep(350);
            			File f = new File(testVar);
            			BufferedImage bi = ImageIO.read(f);
            			ByteArrayOutputStream out = new ByteArrayOutputStream();
            			ImageIO.write(bi, "png", out);
    					ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
    					mSession.getRemote().sendBytes(byteBuffer);
    					out.close();
    					byteBuffer.clear();
    			        }
    			       	catch (IOException | InterruptedException e) {
    			       		e.printStackTrace();
    			        }	
            }
        };
        	
    	Timer timer1 = new Timer(); {
    	timer1.schedule(task , new Date(), 1 );
    	}
    	final String textVar = ( UIMain.usersPath + "/" + message + ".txt" );
    	task2 = new FileWatcher( new File(textVar) ) {
    		protected void onChange( File file ) {
			// here we code the action on a change
    			System.out.println( "File "+ file.getName() +" has changed!" );
    			try {        	
    				File j = new File(textVar);
    				FileInputStream bt = new FileInputStream(j);
    				ByteArrayOutputStream tout = new ByteArrayOutputStream();
    				IO.copy(bt, tout);
    				ByteBuffer byteBuffer2 = ByteBuffer.wrap(tout.toByteArray());
    				mSession.getRemote().sendBytes(byteBuffer2);
    				tout.close();
    				System.out.println( byteBuffer2);
    				byteBuffer2.clear();
		        	}
		       		catch (IOException e) {
		       			e.printStackTrace();
		       		}		
    		}
    	};
    	Timer timer2 = new Timer(); {
        	timer2.schedule(task2 , new Date(), 1 );
        	}
    	}
    	else if (message.equals("0")) {
    		zerocounter = zerocounter + 1;
    		if (zerocounter >= 2) {
    			task.cancel();
    			task2.cancel();
    		}
		}
    	else if (message.equals( "501")) {
    		System.out.println( "Client Pinged: " + message );
    	}
	}
	
	@OnWebSocketMessage
	public void onMessage(byte[] buffer, int offset, int length) throws UnsupportedEncodingException {
	      String sFclientOutStr = new String(buffer, "UTF-8");
			sFclientOut = Integer.parseInt(sFclientOutStr);
	      System.out.println(sFclientOut);
	    	if ((sFclientOut > 0) & (sFclientOut < 500)) {
	            System.out.println("Part " + sFclientOut + " joined");  
	            System.out.println( UIMain.usersPath + "/" + sFclientOutStr + ".png" );
	            final String testVar = ( UIMain.usersPath + "/" + sFclientOutStr + ".png" );
	            task = new FileWatcher( new File(testVar) ) {
	        		protected void onChange( File file ) {
	        			// here we code the action on a change
	        			System.out.println( "File "+ file.getName() +" has changed!" );
	        			try {        	
	            			File f = new File(testVar);
	            			BufferedImage bi = ImageIO.read(f);
	            			ByteArrayOutputStream out = new ByteArrayOutputStream();
	            			ImageIO.write(bi, "png", out);
	    					ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
	    					mSession.getRemote().sendBytes(byteBuffer);
	    					out.close();
	    					byteBuffer.clear();
	    			        }
	    			       	catch (IOException e) {
	    			       		e.printStackTrace();
	    			        }	
	            }
	        };
	    	Timer timer1 = new Timer(); {
	    	timer1.schedule(task , new Date(), 1 );
	    	}
	    	
	    	}
	    	else if (sFclientOutStr.equals("0")) {
	    		zerocounter = zerocounter + 1;
	    		if (zerocounter >= 2) {
	    			task.cancel();
	    		}
			}
	    	else if (sFclientOutStr.equals( "501")) {
	    		System.out.println( "Client says: " + sFclientOutStr );
	    	}
	      
	}
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		// TODO Auto-generated method stub
		factory.register(WSHandler.class);
	}
	
	public void sendImage(byte[] data) {
		if (mSession == null)
			return;
		
		try {        	
			ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            mSession.getRemote().sendBytes(byteBuffer);
            byteBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}