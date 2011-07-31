package com.ztrix;

import org.puremvc.java.patterns.facade.*;

import com.ztrix.util.*;
import com.ztrix.*;
import com.ztrix.mediator.*;
import com.ztrix.proxy.*;
import com.ztrix.cmd.*;

public class TankFacade extends Facade{
	private MainFrame _app;
	
	private TankFacade(){
		Logger.info("@TankFacade: facade new");
	}
	
	public static Facade getInstance(){ 
		if (instance == null || !(instance instanceof TankFacade)) instance = new TankFacade(); 
		return (Facade)instance;
	}
	
	public void startup(MainFrame app){
		Logger.info("@TankFacade.startup: facade startup");
		_app = app;
		
		regMediator();
	}
	
	public MainFrame app(){
		return _app;
	}
	
	protected void initializeController(){
		super.initializeController();
		this.registerCommand(Const.MSG_GOT, HearMsgCmd.class);
		this.registerCommand(Const.SEND_MSG, SendMsgCmd.class);
		this.registerCommand(Const.PLAY_SND, PlaySndCmd.class);
		this.registerCommand(Const.EXIT_GAME, ExitCmd.class);
		this.registerCommand(Const.START_UP, StartupCmd.class);
		this.registerCommand(Const.START_TIMER, TimerCmd.class);
		this.registerCommand(Const.START_SERVER, StartServerCmd.class);
		this.registerCommand(Const.PAUSE_SERVER, PauseServerCmd.class);
		this.registerCommand(Const.ACCEPT_CLIENT, AcceptCmd.class);
		initModel();
	}
	
	protected void initModel(){
		this.registerProxy(new ServerProxy(Const.SERVER_PROXY, null));
	}
	
	private void regMediator(){
		this.registerMediator(new ReplayMediator(Const.REPLAY_MEDIATOR,null));
		this.registerMediator(new ServerBulletMediator(Const.SERVER_BULLET_MEDIATOR,null));
		this.registerMediator(new GameMapMediator(Const.GAME_MAP_MEDIATOR, _app.gameMap()));
		this.registerMediator(new TankMenuMediator(Const.GAME_MAP_MEDIATOR, _app.tankMenu()));
		this.registerMediator(new AIMediator(Const.AI_MEDIATOR,null));
	}
}