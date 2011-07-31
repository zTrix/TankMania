package com.ztrix;

import org.puremvc.java.patterns.facade.*;

import com.ztrix.util.*;
import com.ztrix.*;
import com.ztrix.mediator.*;
import com.ztrix.proxy.*;
import com.ztrix.cmd.*;

public class ServerFacade extends Facade{
	
	private ServerFacade(){
		Logger.info("@ServerFacade: facade new");
	}
	
	public static Facade getInstance(){ 
		if (instance == null || !(instance instanceof ServerFacade)) instance = new ServerFacade(); 
		return (Facade)instance;
	}
	
	public void startup(){
		Logger.info("@TankFacade.startup: facade startup");
		
		regMediator();
	}
	
	protected void initializeController(){
		super.initializeController();
		initModel();
	}
	
	protected void initModel(){
	}
	
	private void regMediator(){
	}
}