package com.ztrix.mediator;

import org.puremvc.java.patterns.mediator.*;

import org.puremvc.java.interfaces.*;

import com.ztrix.Const;
import com.ztrix.data.MapInfo;
import com.ztrix.interfaces.*;
import com.ztrix.*;
import com.ztrix.ui.*;
import com.ztrix.util.Logger;
import com.ztrix.util.Texter;

public class GameMapMediator extends Mediator implements IEventer{
	private GameMap _viewCpnt;
	private MapInfo _mapInfo;

	public GameMapMediator(String name, Object viewCpnt){
		super(name, viewCpnt);
		_viewCpnt = (GameMap)viewCpnt;
		_viewCpnt.setEventer(this);
	}

	public String[] listNotificationInterests(){
		String [] ret = {Const.LOAD_MAP};
		return ret;
	}

	public void handleNotification(INotification notification){
		String s = notification.getName();
		if (s==Const.LOAD_MAP){
			String configfile = (String)notification.getBody();
			_mapInfo = Texter.parseMapConfig(configfile);
			
			int l = app().getInsets().left;
			int r = app().getInsets().right;
			int t = app().getInsets().top;
			int b = app().getInsets().bottom;
			app().setSize(l + _mapInfo.mapW + r, t + _mapInfo.mapH + b + app().getJMenuBar().getHeight()+20);
			Logger.debug("@GameMapMediator.handleNotification w = " + (l + _viewCpnt.getW() + r) + " h = " + (t + _viewCpnt.getH() + b));
			
			_viewCpnt.loadMap(_mapInfo);
			_viewCpnt.repaint();
			_viewCpnt.setVisible(true);
		}
	}

	public void onEvent(String eventName, Object body){
		if (eventName.equals(Const.LOAD_MAP_ERR)){
			this.sendNotification(Const.EXIT_GAME, null, null);
		}
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	private MainFrame app(){
		return appFacade().app();
	}
}
