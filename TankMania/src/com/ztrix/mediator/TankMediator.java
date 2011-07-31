package com.ztrix.mediator;

import java.awt.event.ActionEvent;

import java.util.*;
import java.util.concurrent.*;
import java.awt.event.*;

import javax.swing.Timer;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.data.MapInfo;
import com.ztrix.data.TankState;
import com.ztrix.interfaces.*;
import com.ztrix.proxy.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class TankMediator extends Mediator implements IEventer,ActionListener{
	
	private Tank _viewCpnt;
	private int _id = -1;
	private ConcurrentLinkedQueue<String> _move = new ConcurrentLinkedQueue<String>();
	private ConcurrentLinkedQueue<String> _rotTank = new ConcurrentLinkedQueue<String>();
	private ConcurrentLinkedQueue<String> _rotGun = new ConcurrentLinkedQueue<String>();

	public TankMediator(String name, int id, Object viewCpnt){
		super(name + id, viewCpnt);
//		Logger.info("@MainFrame.createTank: mid6");
		_id = id;
		_viewCpnt = (Tank)viewCpnt;
		_viewCpnt.setEventer(this);
//		Logger.info("@MainFrame.createTank: mid5");
	}
	
	public void initTank(TankState ts){
		_viewCpnt.initState(ts);
		_id = ts.tankID;
	}
	
	public void destroyTank(){
		_viewCpnt.destroyTank();
		this.sendNotification(Const.PLAY_SND, Const.EXPLOSION, null);
		Timer tm=new Timer(700,this);
		tm.setRepeats(false);
		tm.start();
	}
	
	public void actionPerformed(ActionEvent e){
		_viewCpnt.setVisible(false);
		app().getContentPane().remove(_viewCpnt);
		app().getContentPane().repaint();
		app().removeTankIndex(_id);
	}
	
	public void addOrder(String order){
		synchronized(this){
			if(order.indexOf("move") > 0){
//				Logger.debug("@TankMediator.addOrder "+order);
				_move.add(order);
			}else if(order.indexOf("rotate-tank") > 0){
//				Logger.debug("@TankMediator.addOrder "+order);
				_rotTank.add(order);
			}else if(order.indexOf("rotate-gun") > 0){
//				Logger.info("@TankMediator.addOrder: order = " + order);
				_rotGun.add(order);
			}else if(order.indexOf("set-life") > 0){
				Logger.info("@TankMediator.addOrder: order = " + order);
				_viewCpnt.setLife(Integer.parseInt(order.split(" ")[4]));
			}
		}
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.TICK};
		return ret;
	}

	public void handleNotification(INotification notification){
//		Logger.debug("enter TankMediator");
		String noname = notification.getName();
		if (noname==Const.TICK){
			long t = TimerCmd.timeStamp;
			if (_rotTank.size() > 0){
				String []ary = _rotTank.peek().split(" ");
				int st = Integer.parseInt(ary[0]);
				int ed = st + Integer.parseInt(ary[6]);
				while((t > ed) && _rotTank.size() > 0){
					_rotTank.poll();
					if (_rotTank.size() == 0) break;
					ary = _rotTank.peek().split(" ");
					st = Integer.parseInt(ary[0]);
					ed = st + Integer.parseInt(ary[6]);
				}
				
				String[] li = (String[])_rotTank.toArray(new String[0]);
				for(int i=li.length-1;i>=0;i--){
					String s = li[i];
					ary = s.split(" ");
					st = Integer.parseInt(ary[0]);
					ed = st + Integer.parseInt(ary[6]);
					if (t <= ed && t >= st){
						if (t == st){
							_viewCpnt.rotateTank((double)Float.parseFloat(ary[4]));
						}else{
							float sx = Float.parseFloat(ary[4]);
							float ex = Float.parseFloat(ary[5]);
							
							double rs = sx + (ex-sx)*(t-st)/(ed-st);
							_viewCpnt.rotateTank(rs);
							appFacade().app().repaint();
//							_viewCpnt.repaint();
						}
						
						if(_move.size()==0)break;
						int ist=Integer.parseInt(ary[0]);
						st = Integer.parseInt(_move.peek().split(" ")[0]);
						
						while(st<ist&&_move.size()>0){
//							Logger.debug("@TankMediator.handleNotification: t = " + t + " st = " + st + " ed="+ed);
							_move.poll();
							if (_move.size() == 0) break;
							st = Integer.parseInt(_move.peek().split(" ")[0]);
						}
						break;
					}
				}
			}

			else if (_move.size() > 0){
				String []ary = _move.peek().split(" ");
				int st = Integer.parseInt(ary[0]);
				int ed = st + Integer.parseInt(ary[8]);
				
				while((t > ed) && _move.size() > 0){
//					Logger.debug("@TankMediator.handleNotification: t = " + t + " st = " + st + " ed="+ed);
					_move.poll();
					if (_move.size() == 0) break;
					ary = _move.peek().split(" ");
					st = Integer.parseInt(ary[0]);
					ed = st + Integer.parseInt(ary[8]);
				}
				
				if(_move.size()>0){
					String[] li = (String[])_move.toArray(new String[0]);
					for(int i=li.length-1;i>=0;i--){
						String s = li[i];
	//					Logger.debug("@TankMediator.handleNotification s =" + s);
						ary = s.split(" ");
						st = Integer.parseInt(ary[0]);
						ed = st + Integer.parseInt(ary[8]);
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
								_viewCpnt.setLoc(xx, yy);
								Logger.debug("@TankMediator.handleNotification: s= "+s+" ,t="+t+" ,st="+st+" ,ed="+ed+" ,x="+xx+" ,y="+yy);
							}
							while(_move.size()>1)_move.poll();
							break;
						}
					}
				}
				
			}
			
			
			
			if (_rotGun.size() > 0){
				String []ary = _rotGun.peek().split(" ");
				int st = Integer.parseInt(ary[0]);
				int ed = st + Integer.parseInt(ary[6]);
				while((t > ed) && _rotGun.size() > 0){
					_rotGun.poll();
					if (_rotGun.size() == 0) break;
					ary = _rotGun.peek().split(" ");
					st = Integer.parseInt(ary[0]);
					ed = st + Integer.parseInt(ary[6]);
				}
				if(_rotGun.size()>0){
					String[] li = (String[])_rotGun.toArray(new String[0]);
					
					for(int i=li.length-1;i>=0;i--){
						String s = li[i];
						ary = s.split(" ");
						st = Integer.parseInt(ary[0]);
						ed = st + Integer.parseInt(ary[6]);
						if (t <= ed && t >= st){
//							Logger.debug("@TankMediator.handleNotification: s= "+s+" t="+" st="+st+" ed="+ed);
							if (t == st){
								_viewCpnt.rotateGun((double)Float.parseFloat(ary[4]));
							}else{
								float sx = Float.parseFloat(ary[4]);
								float ex = Float.parseFloat(ary[5]);
								
								double rs = sx + (ex-sx)*(t-st)/(ed-st);
								_viewCpnt.rotateGun(rs);
							}
							break;
						}
					}
				}
			}
			appFacade().app().repaint();
		}
	}

	public void onEvent(String eventName, Object body){
		
	}
	
	public Tank tank(){
		return _viewCpnt;
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
