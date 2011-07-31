package com.ztrix;

import com.ztrix.util.Logger;
import com.ztrix.util.Texter;
import com.ztrix.mediator.*;
import com.ztrix.Const;
import com.ztrix.TankFacade;
import com.ztrix.proxy.*;
import com.ztrix.util.*;
import com.ztrix.*;
import com.ztrix.data.*;

import java.util.*;

public class Arena{
	private static Arena _instance;
	private static int _bulletCnter=0;
	
	private Arena(){
		
	}
	
	public static Arena getInstance(){
		if(_instance==null)_instance=new Arena();
		return _instance;
	}
	
	public static void reset(){
		
	}
	
	public static void addReq(int id,String action,TankState curState){
//		Logger.debug("Arena.addReq:id="+id+" "+action);
		if(action.indexOf("rotate-to")>-1){ // rotate gun
			String[] ary=action.split(" ");
			float ex=Float.parseFloat(ary[1]);
			float ey=Float.parseFloat(ary[2]);
			double da=0;
			double sa=clientMediator(id).getGunAngle();
			da=Math.atan2(ey-curState.y,ex-curState.x)+Math.PI/2;
			while(da<sa)da+=Math.PI*2;
			while(da-sa>Math.PI)da-=Math.PI*2;
			int tim=(int)Math.abs((da-sa)*100/Math.PI);
			appFacade().sendNotification(Const.BROAD_CAST, "rotate-gun tank "+id+" "+sa+" "+da+" "+tim, null);
		}else if(action.indexOf("shoot")>-1){
			Logger.debug("Arena.addReq.shoot:id="+id+" "+action);
			double bulletAngle=clientMediator(id).getGunAngle();
			while(bulletAngle<0)bulletAngle+=Math.PI*2;
			bulletAngle%=Math.PI*2;
			int bid=_bulletCnter++;
			int ex=(int)(curState.x+Math.sin(bulletAngle)*400);
			int ey=(int)(curState.y-Math.cos(bulletAngle)*400);
			int sx=(int)(curState.x+Math.sin(bulletAngle)*20);
			int sy=(int)(curState.y-Math.cos(bulletAngle)*20);
			appFacade().sendNotification(Const.BROAD_CAST, "create bullet "+bid+" 0 "+id+" "+sx+" "+sy+" "+bulletAngle, null);
			appFacade().sendNotification(Const.BROAD_CAST, new StringBuffer(30).append("move bullet ").append(bid).append(" ").append(sx).append(" ").append(sy).append(" ").append(ex).append(" ").append(ey).append(" ").append(Const.BULLET_LIFE).toString(), null);
			appFacade().sendNotification(Const.DELAY_BROAD_CAST, "destroy bullet "+bid,"267");
		}else if(action.indexOf("move-to")>-1){
			String[] ary=action.split(" ");
			float ex=Float.parseFloat(ary[1]);
			float ey=Float.parseFloat(ary[2]);
			double da=0;
			double sa=clientMediator(id).getTankAngle();
			da=Math.atan2(ey-curState.y,ex-curState.x)+Math.PI/2;
			while(da<sa)da+=Math.PI*2;
			while(da-sa>Math.PI)da-=Math.PI*2;
			int timeRot=(int)Math.abs((da-sa)*100/Math.PI);
			appFacade().sendNotification(Const.BROAD_CAST, "rotate-tank tank "+id+" "+sa+" "+da+" "+timeRot, null);
			int sx=curState.x;int sy=curState.y;
			int timeMov=(int)(Texter.distance(sx,sy,ex,ey)/0.8);
			
//			int timeMov=(int)(Math.hypot(sx-ex, sy-ey));
//			Logger.only("Arena move tank sx="+sx+" sy="+sy+" ex="+ex+" ey="+ey+" time="+timeMov+" dis="+Texter.distance(sx, sy, ex, ey));
			if(canGo(sx,sy,(ex-sx)/timeMov+sx,(ey-sy)/timeMov+sy))
				appFacade().sendNotification(Const.BROAD_CAST,"move tank "+id+" "+sx+" "+sy+" "+(int)ex+" "+(int)ey+" "+timeMov,""+timeRot);
		}else if(action==Const.STOP_MOVE){
			appFacade().sendNotification(Const.BROAD_CAST,new StringBuffer(30).append("move tank ").append(id).append(" ").append(curState.x).append(" ").append(curState.y).append(" ").append(curState.x).append(" ").append(curState.y).append(" ").append(0).toString(),""+0);
		}else if(action==Const.LOSE_CONNECTION){
			tankStateProxy().removeTank(id);
			appFacade().sendNotification(Const.BROAD_CAST, "destroy tank "+id, null);
		}else if(action.indexOf("born")>-1){
			String[] ary=action.split(" ");
			if(canBorn(Integer.parseInt(ary[2]),Integer.parseInt(ary[3]))){
				tankStateProxy().addTank(id);
				Logger.debug("Arena.addReq: tank num="+tankStateProxy().getTanks().length);
				appFacade().sendNotification(Const.BROAD_CAST,"create tank "+id+" "+ary[1]+" 0 0 1000 500 "+ary[2]+" "+ary[3]+" 0 0",null);
			}
		}
	}
	
	public static boolean canGo(double sx,double sy,double nx,double ny){
		if(sx>110&&sx<150&&sy>90&&sy<310){
			if(Math.abs(nx-130)<Math.abs(sx-130)||Texter.sqrareDis(nx, ny, 130, 200)<Texter.sqrareDis(sx, sy, 130, 200)){
				return false;
			}
		}else if(sx>450&&sx<490&&sy>90&&sy<310){
			if(Math.abs(nx-470)<Math.abs(sx-470)||Texter.sqrareDis(nx, ny, 470, 200)<Texter.sqrareDis(sx, sy, 470, 200)){
				return false;
			}
		}else if(sx>220&&sx<380&&sy>180&&sy<220){
			if(Math.abs(ny-200)<Math.abs(sy-200)||Texter.sqrareDis(nx, ny, 300, 200)<Texter.sqrareDis(sx, sy, 300, 200)){
				return false;
			}
		}
		return true;
	}
	
	private static boolean canBorn(int sx,int sy){
		if(sx>110&&sx<150&&sy>90&&sy<310){
			return false;
		}else if(sx>450&&sx<490&&sy>90&&sy<310){
			return false;
		}else if(sx>220&&sx<380&&sy>180&&sy<220){
			return false;
		}
		return true;
	}
	
	private static TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private static ClientMediator clientMediator(int id){
//		synchronized(appFacade()){
		return (ClientMediator)appFacade().retrieveMediator(Const.CLIENT_MEDIATOR+id);
//		}
	}
	
	private static TankStateProxy tankStateProxy(){
		return (TankStateProxy)appFacade().retrieveProxy(Const.TANK_STATE_PROXY);
	}
}
