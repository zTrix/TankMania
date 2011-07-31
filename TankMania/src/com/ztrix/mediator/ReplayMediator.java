package com.ztrix.mediator;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;
import java.io.*;
import java.util.*;

import com.ztrix.Const;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.data.MapInfo;
import com.ztrix.interfaces.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class ReplayMediator extends Mediator{
	private BufferedReader _reader;
	private HashMap<Integer,Vector<String>> _orders;	
	public static boolean replayMode=false;
	
	public ReplayMediator(String name, Object viewCpnt){
		super(name, viewCpnt);
		_orders=new HashMap<Integer,Vector<String>>();
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.TICK,Const.REPLAY};
		return ret;
	}

	public void handleNotification(INotification notification){
		String noti = notification.getName();
		String body = (String)notification.getBody();
		if (noti==Const.TICK){
			int t=(int)TimerCmd.timeStamp;
			Vector<String> vs=_orders.get(t);
			if(vs==null)return;
			for(Enumeration enu=vs.elements();enu.hasMoreElements();){
				String cmd=(String)enu.nextElement();
				this.sendNotification(Const.MSG_GOT, cmd, null);
			}
		}else if(noti==Const.REPLAY){
			replayMode=true;
			try{
				_reader = new BufferedReader(new FileReader(body));
			}catch(Exception e){
				Logger.error("@ReplayMediator.read file error");
				replayMode=false;
				return;
			}
			int startTime=-1;
			while(true){
				String s=null;
				try{
					s=_reader.readLine();
				}catch(Exception e){
					Logger.error("@ReplayMediator.read line error");
					break;
				}
				if(s==null)break;
				if(s=="")continue;
				Logger.debug("order="+s);
				int time=Integer.parseInt(s.split(" ")[0]);
				if(startTime==-1)startTime=time;
				if(_orders.get(time)==null)_orders.put(time, new Vector<String>());
				_orders.get(time).add(s);
			}
			
			TimerCmd.startTimeStamp = startTime-100;
			TimerCmd.startSysTime = System.currentTimeMillis();
			app().removeLogo();
			app().resetAll();
			this.sendNotification(Const.START_TIMER, null, null);
		}
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
