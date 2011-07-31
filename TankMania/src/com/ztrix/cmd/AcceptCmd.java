package com.ztrix.cmd;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;

import java.io.*;
import java.net.*;
import java.util.Vector;

import com.ztrix.Const;
import com.ztrix.MainFrame;
import com.ztrix.TankFacade;
import com.ztrix.TankServer;
import com.ztrix.proxy.ServerProxy;
import com.ztrix.util.*;
import com.ztrix.interfaces.*;
import com.ztrix.*;
import com.ztrix.mediator.*;

public class AcceptCmd extends SimpleCommand implements IThreader{
	private static MyThread _thread;
	private static int tankCnt=0;
	private static int bulletCnt=0;
	private static ServerSocket serverRef;
	public static Vector<ClientMediator> clients;
	
	public void execute(INotification notification){
		clients=new Vector<ClientMediator>();
		Logger.info("@AcceptCmd : ");
		if(tankServer().server!=null){
			if(serverRef==tankServer().server)return;
			serverRef=tankServer().server;
			_thread=new MyThread(this);
			_thread.start();
		}
	}
	
	public static void stopAccept(){
		_thread.stop();
	}
	
	public void toberun(){
		Socket s;
		PrintWriter pw;
	    BufferedReader br;
		while(true){
			try{
				s=tankServer().server.accept();
			}catch(Exception e){
				Logger.error("@AcceptCmd.toberun: accept client error, exception is "+e.toString());
				continue;
			}
			try{
				pw=new PrintWriter(s.getOutputStream());
				br=new BufferedReader(new InputStreamReader(s.getInputStream()));
				int newtankid=tankCnt++;
				ClientMediator ncm=new ClientMediator(Const.CLIENT_MEDIATOR,newtankid,pw,br,s);
				TankFacade.getInstance().registerMediator(ncm);
				clients.add(ncm);
			}catch(Exception e){
				Logger.error("@AcceptCmd.toberun: create out/in stream error, exception is "+e.toString());
				continue;
			}
		}
	}
	
	private TankServer tankServer(){
		return TankServer.getInstance();
	}
	
	private ServerProxy serverProxy(){
		return (ServerProxy)(appFacade().retrieveProxy(Const.SERVER_PROXY));
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}

}
