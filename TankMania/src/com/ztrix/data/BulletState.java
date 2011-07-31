package com.ztrix.data;

import com.ztrix.Const;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.util.Logger;

public class BulletState{
	public int timeStamp = -1;
	
	public int id = -1;
	public int x = 0;
	public int y = 0;
	public int sx=0;
	public int sy=0;
	public int dx=0;
	public int dy=0;
	public double angle = 0.0f;
	private int startStamp=-1;
	
	public BulletState(){
		
	}
	
	public BulletState(BulletState s){
		if(s!=null){
			id=s.id;
			x=s.x;
			y=s.y;
			angle=s.angle;
		}
	}
	
	public void changeState(int t){
		if(t>=Const.BULLET_LIFE+startStamp){
			x=dx;
			y=dy;
		}else{
			x=(int)(sx+1.0*(t-startStamp)*(dx-sx)/Const.BULLET_LIFE);
			y=(int)(sy+1.0*(t-startStamp)*(dy-sy)/Const.BULLET_LIFE);
		}
	}
	
	public BulletState(String s,int st){
		String []ary=s.split(" ");
		if(s!=null){
			id=Integer.parseInt(ary[2]);
			sx=x=Integer.parseInt(ary[3]);
			sy=y=Integer.parseInt(ary[4]);
			dx=Integer.parseInt(ary[5]);
			dy=Integer.parseInt(ary[6]);
			angle=Math.atan2(dy-y,dx-x)+Math.PI/2;
			startStamp=st;
		}
	}
	
//	public BulletState(int bid,int xx, int yy, float ta){
//		id = bid;
//		x = xx;
//		y = yy;
//		angle = ta;
//	}
	
	public void printState(){
		Logger.only("@BulletState.printState:starttime="+startStamp+" id="+id+" x="+x+" y="+y+" angle="+angle);
	}
}
