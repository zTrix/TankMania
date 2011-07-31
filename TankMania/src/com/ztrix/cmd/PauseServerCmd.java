package com.ztrix.cmd;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;

import com.ztrix.MainFrame;
import com.ztrix.TankFacade;
import com.ztrix.util.*;
import com.ztrix.proxy.*;
import com.ztrix.*;

public class PauseServerCmd extends SimpleCommand{
	
	public void execute(INotification notification){
		Logger.info("@PauseServerCmd : ");
		if(serverProxy().connected){
			serverProxy().close();
			app().resetAll();
		}
		TankServer.getInstance().stopServer();
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
