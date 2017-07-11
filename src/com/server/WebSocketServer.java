package com.server;

import org.eclipse.jetty.server.Server;

public class WebSocketServer extends Thread{

    
    @Override
    public void run() {
    	// TODO Auto-generated method stub
    	super.run();

        try {
        	Server server = new Server(8086);
        	server.setHandler(new WSHandler());
        	server.setStopTimeout(0);
        	server.start();
        	server.join();
        	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
