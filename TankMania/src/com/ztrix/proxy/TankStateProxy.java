package com.ztrix.proxy;

import org.puremvc.java.patterns.proxy.Proxy;

import com.ztrix.*;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.util.*;
import com.ztrix.data.*;
import com.ztrix.proxy.*;
import com.ztrix.interfaces.*;

import java.net.*;
import java.util.HashSet;
import java.io.*;

public class TankStateProxy extends Proxy implements IThreader{
	
	private HashSet _tanks;
	
	public TankStateProxy(String name, Object data){
		super(name, data);
		_tanks=new HashSet<Integer>();
	}
	
	public void toberun(){
	
	}
	
	public void addTank(int id){
		synchronized(_tanks){
			_tanks.add(id);
		}
	}
	
	public void removeTank(int id){
		synchronized(_tanks){
			_tanks.remove(id);
		}
	}
	
	public Object[] getTanks(){
		synchronized(_tanks){
			return _tanks.toArray(new Integer[0]);
		}
	}

	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
}