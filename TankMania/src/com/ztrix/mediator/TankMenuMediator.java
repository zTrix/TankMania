package com.ztrix.mediator;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.data.MapInfo;
import com.ztrix.interfaces.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class TankMenuMediator extends Mediator implements IEventer{
	
	private TankMenu _viewCpnt;

	public TankMenuMediator(String name, Object viewCpnt){
		super(name, viewCpnt);
		_viewCpnt = (TankMenu)viewCpnt;
		_viewCpnt.setEventer(this);
	}

	public String[] listNotificationInterests(){
		String [] ret = new String[0];
		return ret;
	}

	public void handleNotification(INotification notification){
		String s = notification.getName();
		if (s.equals(Const.LOAD_MAP)){

		}
	}

	public void onEvent(String eventName, Object body){
		if (eventName==Const.NEW_GAME){
			this.sendNotification(Const.START_UP, body, null);
		}else if(eventName==Const.AUTO_FIRE){
			AIMediator.autoFire=(Boolean)body;
		}else if(eventName==Const.REPLAY){
			this.sendNotification(Const.REPLAY, body, null);
		}
	}
	
	public TankMenu tankMenu(){
		return _viewCpnt;
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
