package com.ztrix.mediator;

import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.data.MapInfo;
import com.ztrix.interfaces.*;
import com.ztrix.*;
import com.ztrix.data.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class ServerBulletMediator extends Mediator{
	private ConcurrentHashMap<Integer,String> _abullets;
	private ConcurrentHashMap<Integer,BulletState> _bulletstate;
	private ConcurrentLinkedQueue<String> _delayOrder = new ConcurrentLinkedQueue<String>();
	
	public ServerBulletMediator(String name, Object viewCpnt){
		super(name, viewCpnt);
		_abullets=new ConcurrentHashMap<Integer,String>();
		_bulletstate=new ConcurrentHashMap<Integer,BulletState>();
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.TICK,Const.BROAD_CAST,Const.DELAY_BROAD_CAST};
		return ret;
	}
	
	public ConcurrentHashMap<Integer,BulletState> getBullets(){
		return _bulletstate;
	}

	public void handleNotification(INotification notification){
		String noti = (String)notification.getName();
		String body= (String)notification.getBody();
		if (noti==Const.TICK){
			int t=(int)TimerCmd.timeStamp;
			
			Vector<Integer> todestroy=new Vector<Integer>();
			for(Enumeration enu=_bulletstate.keys();enu.hasMoreElements();){
				int bid=(Integer)enu.nextElement();
				BulletState blt=_bulletstate.get(bid);
				blt.changeState(t);
				if(inbar(blt.x, blt.y)){
					todestroy.add(bid);
				}
			}
			
			for(Enumeration enu=todestroy.elements();enu.hasMoreElements();){
				int bid=(Integer)enu.nextElement();
				this.sendNotification(Const.BROAD_CAST,"destroy bullet "+bid,null);
			}
			

			while(true){
				if(_delayOrder.size()==0)break;
				String a=_delayOrder.peek();
				if(a==null)break;
				int st=Integer.parseInt(a.split(" ")[0]);
				if(st<t){
					_delayOrder.poll();
					if(_delayOrder.size()==0)break;
				}else if(st==t){
					this.sendNotification(Const.BROAD_CAST, a, Const.NO_PREFIX);
					_delayOrder.poll();
					int bid=Integer.parseInt(a.split(" ")[3]);
					_abullets.remove(bid);
					_bulletstate.remove(bid);
					if(_delayOrder.size()==0)break;
				}else{
					break;
				}
			}
		}else if(noti==Const.BROAD_CAST){
			if(body.indexOf("move bullet")>-1){
				int bid=Integer.parseInt(body.split(" ")[2]);
				_abullets.put(bid, body);
				BulletState bs=new BulletState(body, (int)TimerCmd.timeStamp);
				bs.printState();
				_bulletstate.put(bid, bs);
			}else if(body.indexOf("destroy bullet")>-1){
				int bid=0;
				if(notification.getType()==Const.NO_PREFIX)bid=Integer.parseInt(body.split(" ")[3]);
				else bid=Integer.parseInt(body.split(" ")[2]);
				
				_abullets.remove(bid);
				_bulletstate.remove(bid);
			}
		}else if(noti==Const.DELAY_BROAD_CAST){
			int t=(int)TimerCmd.timeStamp;
			int startTime=Integer.parseInt(notification.getType())+t;
			_delayOrder.add(startTime+" "+body);
		}
	}
	
	private boolean inbar(int x,int y){
		int margin=2;
		if(x>130-margin&&x<130+margin&&y>100-margin&&y<300+margin)return true;
		if(x>470-margin&&x<470+margin&&y>100-margin&&y<300+margin)return true;
		if(x>230-margin&&x<370+margin&&y>200-margin&&y<200+margin)return true;
		return false;
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
