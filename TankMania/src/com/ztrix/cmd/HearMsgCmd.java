package com.ztrix.cmd;

import java.util.*;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.TankFacade;
import com.ztrix.proxy.*;
import com.ztrix.ui.GameMap;
import com.ztrix.util.*;
import com.ztrix.mediator.*;
import com.ztrix.*;

public class HearMsgCmd extends SimpleCommand{
	
	public void execute(INotification notification){
//		Logger.info("@HearMsgCmd : msg = " + notification.getBody().toString());
		
		String s = (String)notification.getBody();
		if (s.indexOf("sync")>-1){
			TimerCmd.startTimeStamp = Integer.parseInt(s.split(" ")[0]);
			TimerCmd.startSysTime = System.currentTimeMillis();
		}else if(s.matches("^\\d+\\s(move|rotate-tank|rotate-gun|set-life)\\stank.*")){
			int id = Integer.parseInt(s.split(" ")[3]);
			if(((TankMediator)(appFacade().retrieveMediator(Const.TANK_MEDIATOR + id)))!=null)
			((TankMediator)(appFacade().retrieveMediator(Const.TANK_MEDIATOR + id))).addOrder(s);
		}else if(s.matches("^\\d+\\smove\\sbullet.*")){
			int id = Integer.parseInt(s.split(" ")[3]);
			BulletMediator bm=(BulletMediator)(appFacade().retrieveMediator(Const.BULLET_MEDIATOR + id));
//			if()==null){
//				Logger.only("HearMsgCmd "+id);
//			}else
			if(bm!=null)
				bm.addOrder(s);
		}else if (s.matches("^\\d+\\sset-id\\s\\d*")){
			MainFrame.myTankID = Integer.parseInt(s.split(" ")[2]);
		}else if(s.matches("^\\d+\\sload\\s\\w+\\.\\w*")){
			this.sendNotification(Const.LOAD_MAP, s.split(" ")[2], null);
		}else if(s.matches("^\\d+\\screate\\stank\\s.*")){
			String []ary = s.split(" ");
			if (ary.length < 13) {
				Logger.error("@HearMsgCmd.execute: cannot understand order, s="+s+" ary.length="+ary.length);
				return;
			}
			try{
				appFacade().app().createTank(Integer.parseInt(ary[3]), ary[4], Integer.parseInt(ary[7]), 
											 (int)Float.parseFloat(ary[9]), (int)Float.parseFloat(ary[10]), 
											 Float.parseFloat(ary[11]), Float.parseFloat(ary[12]));
			}catch(Exception e){
				Logger.error("@HearMsgCmd.execute: parse param failed, s="+s+" e="+e.toString());
				return;
			}
		}else if(s.matches("^\\d+\\sdestroy\\stank\\s\\d+")){
			int id = Integer.parseInt(s.split(" ")[3]);
			appFacade().app().destroyTank(id);
		}else if(s.indexOf("create bullet")>-1){
			String []ary = s.split(" ");
			if (ary.length < 9) {
				Logger.error("@HearMsgCmd.execute: cannot understand order, s="+s+" ary.length="+ary.length);
				return;
			}
			try{
				appFacade().app().createBullet(Integer.parseInt(ary[3]), 
											 (int)Float.parseFloat(ary[6]), (int)Float.parseFloat(ary[7]), 
											 Float.parseFloat(ary[8]));
			}catch(Exception e){
				Logger.error("@HearMsgCmd.execute: parse param failed, s="+s+" e="+e.toString());
				return;
			}
		}else if(s.indexOf("destroy bul")>-1){
			int id = Integer.parseInt(s.split(" ")[3]);
			appFacade().app().destroyBullet(id);
		}else if(s.indexOf("talk")>-1){			
			appFacade().app().gameMap().addTalk(s.substring(s.indexOf("talk")+5));
		}
	}
	
	private ServerProxy serverProxy(){
		return (ServerProxy)appFacade().retrieveProxy(Const.SERVER_PROXY);
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}

}
