package com.ztrix.mediator;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.event.*;
import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.TankFacade;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.data.MapInfo;
import com.ztrix.data.TankState;
import com.ztrix.interfaces.*;
import com.ztrix.proxy.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class BulletMediator extends Mediator implements IEventer, ActionListener{
	
	private Bullet _viewCpnt;
	private int _id = -1;
	private String _curMove;
	

	public BulletMediator(String name, int id, Object viewCpnt){
		super(name + id, viewCpnt);
		Logger.info("@BulletMediator");
		_id = id;
		_viewCpnt = (Bullet)viewCpnt;
		_viewCpnt.setEventer(this);
	}
	
	public void actionPerformed(ActionEvent e){
		_viewCpnt.setVisible(false);
		app().getContentPane().remove(_viewCpnt);
		app().getContentPane().repaint();
		app().removeBulletIndex(_id);
		synchronized(appFacade()){
			appFacade().removeMediator(Const.BULLET_MEDIATOR+_id);
		}
	}
	
	public void destroyBullet(){	
		_viewCpnt.destroyed();
		Timer tm=new Timer(500,this);
		tm.setRepeats(false);
		tm.start();
	}
	
	public void addOrder(String order){
		Logger.debug("@BulletMediator.addOrder:order="+order);
//		synchronized(this){
			_curMove=new String(order);
//		}
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.TICK};
		return ret;
	}

	public void handleNotification(INotification notification){
		String noname = notification.getName();
		if (noname.equals(Const.TICK)){
			if(_viewCpnt.isDestroyed())return;
			if(_curMove==null)return;
			long t = TimerCmd.timeStamp;
			String []ary = _curMove.split(" ");
			int st = Integer.parseInt(ary[0]);
			int ed = st + Integer.parseInt(ary[8]);

			if (t <= ed && t >= st){
				if (t == st){
					_viewCpnt.setLoc((int)Float.parseFloat(ary[4]), (int)Float.parseFloat(ary[5]));
				}else{
					float sx = Float.parseFloat(ary[4]);
					float ex = Float.parseFloat(ary[6]);
					float sy = Float.parseFloat(ary[5]);
					float ey = Float.parseFloat(ary[7]);
					double eco = 1.0*(t-st)/(ed-st);
					int xx = (int)(sx + (ex-sx)*eco);
					int yy = (int)(sy + (ey-sy)*eco);
//					Logger.debug("@BulletMediator.handleNotification: bullet x="+xx+" y="+yy);
					_viewCpnt.setLoc(xx, yy);
					_viewCpnt.repaint();
				}
			}
		}
	}

	public void onEvent(String eventName, Object body){
		
	}
	
	public Bullet bullet(){
		return _viewCpnt;
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
