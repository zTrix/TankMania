package com.ztrix.cmd;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;

import com.ztrix.*;
import com.ztrix.MainFrame;
import com.ztrix.TankFacade;
import com.ztrix.proxy.ServerProxy;
import com.ztrix.util.*;

public class StartServerCmd extends SimpleCommand{
	
	public void execute(INotification notification){
		Logger.info("@StartServerCmd : ");
		if(serverProxy().connected){
			serverProxy().close();
			app().resetAll();
		}
		this.sendNotification(Const.START_TIMER, null, null);
		TankServer t = TankServer.getInstance();
		t.startServer();
		Arena.reset();
		this.sendNotification(Const.ACCEPT_CLIENT, null, null);
		this.sendNotification(Const.START_UP, Const.LOCAL_SERVER+":"+Const.LOCAL_PORT, null);
	}
	
	private ServerProxy serverProxy(){
		return (ServerProxy)(appFacade().retrieveProxy(Const.SERVER_PROXY));
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}

}
