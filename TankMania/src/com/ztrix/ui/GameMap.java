package com.ztrix.ui;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import com.ztrix.Const;
import com.ztrix.util.Logger;
import com.ztrix.interfaces.*;
import com.ztrix.mediator.ReplayMediator;
import com.ztrix.data.*;

public class GameMap extends JPanel{
	
	private MapInfo _mapInfo;
	private IEventer _eventer;
	public static StringBuffer talkString=new StringBuffer(300);
	private BufferedImage _mapimg;
	private int _w = -1;
	private int _h = -1;

	public GameMap(){
		super(true);  // use double-buffer to make flicker-free
		talkString.append("Chat Room:\n");
	}

	public void loadMap(MapInfo info){
		String mapname = info.mapFile;
		Logger.info("@GameMap.loadMap: mapname = " + mapname);
		_mapInfo = info;
		try{
			File f = new File(Const.RESOURCE_DIR + "/" +  mapname);
//			Logger.debug("@GameMap f exist = " + f.exists());
			_mapimg = ImageIO.read(f);
		}catch(IOException e){
			Logger.fatal("@GameMap.loadMap: game terminated cuz map file not found, exception : " + e.toString());
			_eventer.onEvent(Const.LOAD_MAP_ERR, null);
			return;
		}
		_w = _mapInfo.mapW;
		_h = _mapInfo.mapH;
	}

	public void setEventer(IEventer et){
		_eventer = et;
	}

	public int getW(){
		return _w;
	}

	public int getH(){
		return _h;
	}
	
	public MapInfo mapInfo(){
		return _mapInfo;
	}
	
	
	public void addTalk(String s){
		talkString.append(s+"\n");
		this.repaint();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(_mapimg, 0, 0, null);
//		if(ReplayMediator.replayMode)return;
		String []ary=talkString.toString().split("\n");
		for(int i=ary.length-1;i>ary.length-9&&i>=0;i--)
			g.drawString(ary[i],20, 200+20*(i-ary.length+9));
	}

}
