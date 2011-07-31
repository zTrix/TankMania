package com.ztrix.mediator;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.data.MapInfo;
import com.ztrix.interfaces.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class AIMediator extends Mediator{
	public static boolean autoFire=false;
	public static boolean useAI=false;
	
	public AIMediator(String name, Object viewCpnt){
		super(name, viewCpnt);
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.TICK};
		return ret;
	}

	public void handleNotification(INotification notification){
		String noti = notification.getName();
		if (noti==Const.TICK&&TimerCmd.timeStamp%20==0){
			if(autoFire){
				this.sendNotification(Const.SEND_MSG, null, Const.FIRE);
			}
		}
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
