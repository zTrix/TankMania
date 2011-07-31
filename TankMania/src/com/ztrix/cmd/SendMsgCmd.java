package com.ztrix.cmd;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.TankFacade;
import com.ztrix.util.*;
import com.ztrix.proxy.*;
import com.ztrix.*;

public class SendMsgCmd extends SimpleCommand{
	
	public void execute(INotification notification){
//		Logger.info("@SendMsgCmd : msg");
		if (notification.getType() == Const.CONNECT){
			String[]ary = ((String)(notification.getBody())).split("[^\\d\\.]");
			serverProxy().connect(ary[0], Integer.parseInt(ary[1]));
			serverProxy().startListen();
		}else if(notification.getType() == Const.FIRE){
			serverProxy().tellServer("shoot");
		}else if(notification.getType() == Const.ROTATE_TO){
			int []ary = (int[])notification.getBody();
			serverProxy().tellServer("rotate-to "+ary[0]+" "+ary[1]);
		}else if(notification.getType()==Const.MOVE_TO){
			int []ary = (int[])notification.getBody();
			serverProxy().tellServer("move-to "+ary[0]+" "+ary[1]);
		}else if(notification.getType() == Const.INIT_PROTOCAL){
			serverProxy().tellServer("sync 0");
			serverProxy().tellServer("get-status");
		}else if(notification.getType() == Const.BORN){
			int []ary = (int[])notification.getBody();
			serverProxy().tellServer("born " + MainFrame.myTankName + " " + ary[0] + " " + ary[1] + " 0 0");
		}else if(notification.getType() == Const.SYNC){
			serverProxy().tellServer("sync "+TimerCmd.timeStamp);
		}else if(notification.getType()==Const.TALK){
			serverProxy().tellServer("talk "+notification.getBody());
		}
	}
	
	private ServerProxy serverProxy(){
		return (ServerProxy)appFacade().retrieveProxy(Const.SERVER_PROXY);
	}

	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
}
