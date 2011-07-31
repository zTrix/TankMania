package com.ztrix.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.ztrix.*;
import com.ztrix.util.*;

public class StartupCmd extends SimpleCommand {

	public void execute(INotification notification){
		Logger.info("@StartupCmd: execute");
		Texter.emptylog();
		app().removeLogo();
		app().resetAll();
		this.sendNotification(Const.SEND_MSG, notification.getBody(), Const.CONNECT);
		this.sendNotification(Const.SEND_MSG, null, Const.INIT_PROTOCAL);
		this.sendNotification(Const.START_TIMER, null, null);
	}
	
	private TankFacade appFacade(){
		return (TankFacade)facade;
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
