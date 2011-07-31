package com.ztrix.mediator;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.geom.*;
import javax.swing.Timer;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.cmd.AcceptCmd;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.data.*;
import com.ztrix.interfaces.*;
import com.ztrix.proxy.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.*;

public class ClientMediator extends Mediator implements IEventer,ActionListener,IThreader{
	private int _id=-1;
	public boolean destroyd=false;
	private PrintWriter _writer;
	private BufferedReader _reader;
	private MyThread _thread;
	private TankState _state=null;
	private Socket _socket;
	private String _lastRotateGun;
	private String _lastRotTank;
	private ConcurrentLinkedQueue<String> _move = new ConcurrentLinkedQueue<String>();
	
	private double _nextX=-1;
	private double _nextY=-1;
	
	public static final int[][] barrier={{130,100,130,300},
										 {470,100,470,300},
										 {230,200,370,200}};

	public ClientMediator(String name, int id, PrintWriter bos, BufferedReader is,Socket socket){
		super(name + id, null);
		_id = id;
		_socket=socket;
		_writer=bos;
		_reader=is;
		_thread=new MyThread(this);
		_thread.start();
		tellClient("set-id "+_id,0);
		tellClient("load simple.txt",0);
	}
	
	public TankState getState(){
		if(_state==null)return null;
		else return new TankState(_state);
	}
	
	public int getID(){
		return _id;
	}
	
	public double getGunAngle(){
		if(_lastRotateGun==null)return 0;
		String[] ary = _lastRotateGun.split(" ");
		int st = Integer.parseInt(ary[0]);
		int ed = st + Integer.parseInt(ary[6]);
		long t=TimerCmd.timeStamp;
		if(t>ed){
			return _state.gunAngle;
		}else
		if (t == st||st==ed){
			return((double)Float.parseFloat(ary[4]));
		}else{
			float sx = Float.parseFloat(ary[4]);
			float ex = Float.parseFloat(ary[5]);
			
			double rs = sx + (ex-sx)*(t-st)/(ed-st);
			return rs;
		}
	}
	
	public double getTankAngle(){
		if(_lastRotTank==null||_lastRotTank.equals(""))return 0;
		String[] ary = _lastRotTank.split(" ");
		int st = Integer.parseInt(ary[0]);
		int ed = st + Integer.parseInt(ary[6]);
		long t=TimerCmd.timeStamp;
		if(t>ed){
			return _state.tankAngle;
		}else
		if (t == st||st==ed){
			return((double)Float.parseFloat(ary[4]));
		}else{
			float sx = Float.parseFloat(ary[4]);
			float ex = Float.parseFloat(ary[5]);
			
			double rs = sx + (ex-sx)*(t-st)/(ed-st);
			return rs;
		}
	}
	
	public void stop(){
		_thread.stop();
		try{
			_socket.close();
		}catch(Exception e){
			Logger.error("@ClientMediator.stop error");
		}
		// kill tank
	}
	
	public void actionPerformed(ActionEvent e){

	}
	
	private void tellClient(String s,int offset){
		if(s!=null){
			_writer.println((TimerCmd.timeStamp+offset)+" "+s);
			_writer.flush();
		}
	}
	
	private void tellClientWithTime(String s){
		_writer.println(s);
		_writer.flush();
	}
	
	public void toberun(){
		if(_writer==null||_reader==null){
			Logger.error("@ClientMediator.toberun: _writer or _reader null");
			return;
		}
		String s;
		while(true){
			s=null;
			try{
				s=_reader.readLine();
			}catch(Exception e){
				Logger.error("@ClientMediator.toberun: _read error, excep is "+e.toString());
				break;
			}
			if(s!=null&&s.length()>0){
				handleResquest(new String(s));
			}
		}
		Arena.addReq(_id, Const.LOSE_CONNECTION, null);
	}
	
	private void handleResquest(String s){
		Logger.info("@ClientMediator.handleResquest id="+_id+" :s="+s);
		if(s.indexOf("sync")>-1){
			tellClient("sync "+s.split(" ")[1],0);
		}else if(s.indexOf("get-status")>-1){
			Integer[] ts=(Integer[])tankStateProxy().getTanks();
			Logger.debug("ClientMediator.handleResquest: ts.len="+ts.length);
			for(int i=0;i<ts.length;i++){
				ClientMediator cm=(ClientMediator)appFacade().retrieveMediator(Const.CLIENT_MEDIATOR+ts[i].intValue());
				TankState t=cm.getState();
				if(t==null||cm.destroyd)continue;
				tellClient("create tank "+t.tankID+" "+t.tankName+" 0 0 "+t.life+" 500 "+t.x+" "+t.y+" "+cm.getTankAngle()+" "+cm.getGunAngle(),0);
			}
		}else if(s.indexOf("born")>-1){
			if(_state==null)
				Arena.addReq(_id,s,null);
		}else if(s.indexOf("move-to")>-1){
			if(_state!=null){
				Arena.addReq(_id, s, getState());
			}
		}else if(s.indexOf("rotate-to")>-1){ //rotate gun
			if(_state!=null){
				Arena.addReq(_id,s,getState());
			}
		}else if(s.indexOf("shoot")>-1){
			if(_state!=null){
				Arena.addReq(_id, s, getState());
			}
		}else if(s.indexOf("talk")>-1){
			if(_state!=null)s="talk " +_state.tankName+" say: "+s.substring(5);
			this.sendNotification(Const.BROAD_CAST, s, null);
		}
	}
	
	public void handleSendMsg(String s){
		
	}
	
	public void addOrder(String order){
		synchronized(this){

		}
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.BROAD_CAST,Const.TICK};
		return ret;
	}

	public void handleNotification(INotification notification){
//		Logger.debug("@ClientMediator.handleNotification:noti="+notification.getBody());
		String noname = notification.getName();
		if(noname==Const.TICK){
			if(destroyd)return;
			long t=TimerCmd.timeStamp;
			
			String[]ary;
			int st,ed;
			if(_lastRotTank!=null){
				ary=_lastRotTank.split(" ");
				st=Integer.parseInt(ary[0]);
				ed = st + Integer.parseInt(ary[6]);
				if (t <= ed && t >= st){
					int ist=Integer.parseInt(ary[0]);
					if(_move.size()>0){
						st = Integer.parseInt(_move.peek().split(" ")[0]);
						while(st<ist&&_move.size()>0){
							_move.poll();
							if (_move.size() == 0) break;
							st = Integer.parseInt(_move.peek().split(" ")[0]);
						}
					}
				}
			}if (_move.size() > 0){
				ary = _move.peek().split(" ");
				st = Integer.parseInt(ary[0]);
				ed = st + Integer.parseInt(ary[8]);
				
				while((t > ed) && _move.size() > 0){
//					Logger.debug("@TankMediator.handleNotification: t = " + t + " st = " + st + " ed="+ed);
					String p=(String)_move.poll();
					Logger.debug("ClientMediator.TICK _move.poll="+p);
					if (_move.size() == 0) break;
					ary = _move.peek().split(" ");
					st = Integer.parseInt(ary[0]);
					ed = st + Integer.parseInt(ary[8]);
				}
				
				if(_move.size()>0){
					String[] li = (String[])_move.toArray(new String[0]);
					for(int i=li.length-1;i>=0;i--){
						String s = li[i];
						ary = s.split(" ");
						st = Integer.parseInt(ary[0]);
						ed = st + Integer.parseInt(ary[8]);
						if (t <= ed && t >= st){
							if (t == st){//ed==st in this situation
								_state.x=(int)Float.parseFloat(ary[4]);
								_state.y=(int)Float.parseFloat(ary[5]);
								_nextX=_state.x;
								_nextY=_state.y;
							}else{
								float sx = Float.parseFloat(ary[4]);
								float ex = Float.parseFloat(ary[6]);
								float sy = Float.parseFloat(ary[5]);
								float ey = Float.parseFloat(ary[7]);
								double eco = 1.0*(t-st)/(ed-st);
								_state.x = (int)(sx + (ex-sx)*eco);
								_state.y = (int)(sy + (ey-sy)*eco);
								double neco=1.0*(t+1-st)/(ed-st);
								_nextX=(sx + (ex-sx)*neco);
								_nextY=(sy + (ey-sy)*neco);
							}
							while(_move.size()>1)_move.poll();
							break;
						}
					}
				}
			}
			if(_state!=null){
				ConcurrentHashMap<Integer,BulletState> bs=((ServerBulletMediator)(appFacade().retrieveMediator(Const.SERVER_BULLET_MEDIATOR))).getBullets();
				for(Enumeration enu=bs.keys();enu.hasMoreElements();){
					int id=(Integer)enu.nextElement();
					BulletState blt=bs.get(id);
					if(Texter.distance(blt.x, blt.y, _state.x, _state.y)<16){
						this.sendNotification(Const.BROAD_CAST, "destroy bullet "+id,null);
						_state.life-=100;
						if(_state.life<=0){
							this.sendNotification(Const.BROAD_CAST, "destroy tank "+_state.tankID, null);
						}else{
							this.sendNotification(Const.BROAD_CAST, "set-life tank "+_state.tankID+" "+_state.life, null);
						}
					}
				}
			}
			
			//barrier and tank
			if(_state!=null){
				//colision detection
//				Logger.only("ClientMediator.handleNotification: x="+_state.x+" y="+_state.y+" nx="+_nextX+" ny="+_nextY+" "+Texter.sqrareDis(_nextX, _nextY, 470, 200)+" "+Texter.sqrareDis(_state.x, _state.y, 470, 200));
				if(!Arena.canGo(_state.x,_state.y,_nextX,_nextY)){
					Arena.addReq(_id,Const.STOP_MOVE,getState());
				}
				for(Enumeration enu=AcceptCmd.clients.elements();enu.hasMoreElements();){
					ClientMediator cmm=(ClientMediator)enu.nextElement();
					if(cmm.destroyd) return;
					int cmid=cmm.getID();
					if(cmid==_id)continue;
					TankState ts=cmm.getState();
					if(ts==null)continue;
					double curdis=Texter.distance(ts.x, ts.y, _state.x, _state.y);
					double netdis=Texter.distance(ts.x, ts.y, _nextX, _nextY);
					if(curdis<40&&netdis<curdis){
						Arena.addReq(_id,Const.STOP_MOVE,getState());
						Arena.addReq(cmid, Const.STOP_MOVE, ts);
					}
				}
			}
			
			//handle delayed first
//			ary=_delayOrder.toArray(new String[0]);
//			_delayOrder.clear();
//			for(int i=0;i<ary.length;i++){
//				Logger.only("delayArray: "+ary[i]);
//				int ttt=Integer.parseInt(ary[i].split(" ")[0]);
//				if(ttt==t){
//					tellClientWithTime(ary[i]);
//				}else if(ttt>t){
//					_delayOrder.add(ary[i]);
//				}
//			}
			
		}else if (noname==Const.BROAD_CAST){
			String s=(String)notification.getBody();
			String ary[]=s.split(" ");
			
			if(notification.getType()==Const.NO_PREFIX){
				tellClientWithTime(s);
				return;
			}else if(s.indexOf("talk")>-1){
				tellClient(s,0);
				return;
			}
			if(_id==Integer.parseInt(ary[2])){
				if(s.indexOf("rotate-gun")>-1){
					_lastRotateGun=TimerCmd.timeStamp+" "+s;
					if(_state!=null)
					_state.gunAngle=Float.parseFloat(_lastRotateGun.split(" ")[5]);
				}else if(s.indexOf("move tank")>-1){
					Logger.debug("ClientMediator: move add s="+s);
					int offset=Integer.parseInt(notification.getType());
					if(offset==0)_move.clear();
					_move.add((TimerCmd.timeStamp+offset)+" "+s);
				}else if(s.indexOf("create tank")>-1){
					_state=new TankState(_id,ary[3],Integer.parseInt(ary[6]),
						(int)Float.parseFloat(ary[8]),(int)Float.parseFloat(ary[9]),
						Float.parseFloat(ary[10]),Float.parseFloat(ary[11]));
					_state.printState();
				}else if(s.indexOf("rotate-tank")>-1){
					_lastRotTank=TimerCmd.timeStamp+" "+s;
					if(_state!=null)
					_state.tankAngle=Float.parseFloat(_lastRotTank.split(" ")[5]);
				}else if(s.indexOf("destroy tank")>-1){
					this.destroyd=true;
				}
			}
			if(notification.getType()!=null)
				tellClient(s,Integer.parseInt(notification.getType()));
			else
				tellClient(s,0);
		}
	}

	public void onEvent(String eventName, Object body){
		
	}
	
	private TankStateProxy tankStateProxy(){
		return (TankStateProxy)appFacade().retrieveProxy(Const.TANK_STATE_PROXY);
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
	
	private TankServer tankServer(){
		return TankServer.getInstance();
	}
}
