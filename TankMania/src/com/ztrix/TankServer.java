package com.ztrix;

import org.puremvc.java.patterns.facade.*;
import java.net.*;
import java.util.*;

import com.ztrix.util.*;
import com.ztrix.*;
import com.ztrix.mediator.*;
import com.ztrix.proxy.*;
import com.ztrix.cmd.*;

public class TankServer{
	private static TankServer _instance;

	public ServerSocket server;

	
	private TankServer(){
		Logger.info("@TankServer: new");
		TankFacade.getInstance().registerProxy(new TankStateProxy(Const.TANK_STATE_PROXY,null));
	}
	
	public static TankServer getInstance(){ 
		if (_instance == null) _instance = new TankServer(); 
		return _instance;
	}
	
	public void startServer(){
		if(server!=null)return;
		try{
			server = new ServerSocket(Const.LOCAL_PORT);
		}catch(Exception e){
			Logger.fatal("@TankServer.initServer: cannot init server at port "+Const.LOCAL_PORT);
		}
	}
	
	public void stopServer(){
		try{
			AcceptCmd.stopAccept();
			server.close();
			server=null;
		}catch(Exception e){
			Logger.fatal("@TankServer.stopServer: cannot stop server");
		}
	}

}
