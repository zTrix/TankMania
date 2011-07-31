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
import com.ztrix.data.*;

public class TankLogo extends JPanel{
	
	private IEventer _eventer;

	private BufferedImage _logoimg;
	private int _w = -1;
	private int _h = -1;

	public TankLogo(){
		super(true);  // use double-buffer to make flicker-free
		loadLogo();
	}

	public void loadLogo(){
		String logoname = Const.LOGO_FILE;
		Logger.info("@TankLogo.loadLogo: logoname = " + logoname);
		try{
			File f = new File(Const.RESOURCE_DIR + "/" +  logoname);
			Logger.debug("@TankLogo f exist = " + f.exists());
			_logoimg = ImageIO.read(f);
		}catch(IOException e){
			Logger.error("@TankLogo.loadLogo: game terminated cuz logo file not found, exception : " + e.toString());
			return;
		}
		_w = _logoimg.getWidth();
		_h = _logoimg.getHeight();
		this.setSize(_w, _h);
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

	public void paintComponent(Graphics g){
		Logger.debug("@TankLogo.paintComponent");
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(_logoimg, 0, 0, null);
	}

}

