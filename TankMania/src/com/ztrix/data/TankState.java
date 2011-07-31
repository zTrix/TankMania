package com.ztrix.data;

import com.ztrix.util.Logger;

public class TankState{
	public int timeStamp = -1;
	
	public int tankID = -1;
	public String tankName = "";
	public int life = -1;
	public int x = 0;
	public int y = 0;
	public float tankAngle = 0.0f;
	public float gunAngle = 0.0f;
	
	public boolean destroyed = false;
	
	public TankState(){
		
	}
	
	public TankState(TankState s){
		if(s!=null){
			tankID=s.tankID;
			tankName=s.tankName;
			life=s.life;
			x=s.x;
			y=s.y;
			tankAngle=s.tankAngle;
			gunAngle=s.gunAngle;
		}
	}
	
	public TankState(int id, String name, int vlife, int xx, int yy, float ta, float ga){
		tankID = id;
		tankName = name;
		life = vlife;
		x = xx;
		y = yy;
		tankAngle = ta;
		gunAngle = ga;
	}
	
	public void printState(){
		Logger.only("@TankState.printState:id="+tankID+" name="+tankName+" life="+life+" x="+x+" y="+y+" ta="+tankAngle+" ga="+gunAngle);
	}
}
