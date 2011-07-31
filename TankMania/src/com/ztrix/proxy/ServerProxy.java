package com.ztrix.proxy;

import org.puremvc.java.patterns.proxy.Proxy;

import com.ztrix.*;
import com.ztrix.cmd.TimerCmd;
import com.ztrix.util.*;
import com.ztrix.data.*;
import com.ztrix.proxy.*;
import com.ztrix.interfaces.*;

import java.net.*;
import java.io.*;

public class ServerProxy extends Proxy implements IThreader{
	private Socket _skt;
	private BufferedReader _in;
	private PrintWriter _out;
	private MyThread _thread;
	
	public boolean connected = false;
	
	public ServerProxy(String name, Object data){
		super(name, data);
	}
	
	public void startListen(){
		_thread = new MyThread(this);
		_thread.start();
	}
	
	public void stopListen(){
		_thread.stop();
	}
	
	public void toberun(){
		String hear =null;
		while(true){
			hear=null;
			if(!connected||_in==null||_skt==null){
				Texter.emptylog();
				continue;
			}
			try{
				hear = _in.readLine();
				if (hear == null) continue;
				this.sendNotification(Const.MSG_GOT, new String(hear), null);
				Texter.savelog(hear);
			}catch(SocketException e){
				Logger.error("@ServerProxy.intaractServer: hear failed, exception is " + e.toString());
				continue;
			}catch(IOException e){
				
			}
		}
	}
	
	public void tellServer(String msg){
		if(_out!=null){
			_out.println(msg);
			_out.flush();
		}
	}
	
	public void connect(String ip, int port){
		Logger.info("@ServerProxy.connect: ip="+ip+" port="+port);
		if (_skt!=null){
			try{
				_skt.close();
			}catch(Exception e){
				Logger.debug("@ServerProxy.connect close failed: exception e = " + e.toString());
			}
		}
		try{
			_skt = new Socket(ip, port);
			_skt.setTcpNoDelay(true);
			_in = new BufferedReader(new InputStreamReader(_skt.getInputStream()));
			_out = new PrintWriter(_skt.getOutputStream());
		}catch(Exception e){
			Logger.fatal("@ServerProxy.init socket error" + e.toString());
			facade.sendNotification(Const.EXIT_GAME, null, null);
		}
		connected = true;
	}
	
	public void close(){
		connected=false;
		stopListen();
		if(_skt!=null){
			try{
				_skt.close();
				_in=null;
				_out=null;
			}catch(Exception e){
				Logger.debug("@ServerProxy.connect close failed: exception e = " + e.toString());
			}
			_skt=null;
		}
	}

	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
}