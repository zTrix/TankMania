package com.ztrix.cmd;

import javax.swing.*;
import java.awt.event.*;
import java.util.Date;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.ztrix.*;
import com.ztrix.util.*;

public class TimerCmd extends SimpleCommand implements ActionListener{
	public static long timeStamp = 0;
	public static long startSysTime = -1;
	public static long startTimeStamp = -1;
	private static Timer _t;

	public void execute(INotification notification){
		Logger.info("@TimerCmd: execute");
		if(_t==null){
			_t = new Timer(Const.TIME_STAMP_INTERVAL, this);
			startSysTime=System.currentTimeMillis();
			timeStamp=startTimeStamp;
			_t.start();
		}
	}
	
	public static void stop(){
		_t.stop();
	}
	
	public void actionPerformed(ActionEvent e){
		synchronized(this){
			timeStamp = startTimeStamp + (System.currentTimeMillis() - startSysTime) / 10;
//			try{
				appFacade().sendNotification(Const.TICK, null, null);
//			}catch(Exception ee){
//				Logger.error("@TimerCmd.error"+ee.toString());
//			}
		}
//		if(timeStamp % 200 == 0){
//			this.sendNotification(Const.SEND_MSG, null, Const.SYNC);
//		}
	}
	
	private TankFacade appFacade(){
		return (TankFacade)facade;
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}

