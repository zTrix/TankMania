package com.ztrix.ui;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.geom.*;
import java.awt.*;

import com.ztrix.Const;
import com.ztrix.TankFacade;
import com.ztrix.util.Logger;
import com.ztrix.data.TankState;
import com.ztrix.interfaces.*;

public class Bullet extends JPanel{
	private final String BULLET_FILE = "bullet_0.png";
	private final String BULLET_DESTORY = "effect_1.png";
	
	private IEventer _eventer;
	
	private BufferedImage _bulletimg;
	private BufferedImage _bulletDstImg;
	private BufferedImage _bulletDestoryImg;
	
	private boolean _destroyed = false;
	
	public Bullet(){
		super(true);
		
		loadImg();
	}
	
	public void destroyed(){
		_destroyed=true;
		repaint();
	}
	
	public boolean isDestroyed(){
		return _destroyed;
	}
	
	public void initState(int x,int y,float angle){
		setLoc(x,y);
		rotateBullet((double)angle);
		this.repaint();
	}

	public void setLoc(int x, int y){
		if (_destroyed)return;
		this.setLocation(x-50, y-50);
	}
	
	public void rotateBullet(double angle){
		AffineTransform transform = AffineTransform.getRotateInstance(angle, 20, 20);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		_bulletDstImg = op.filter(_bulletimg, null);
	}
	
	public void setEventer(IEventer et){
		_eventer = et;
	}
	
	private void loadImg(){
		try{
			File f = new File(Const.RESOURCE_DIR + "/" +  BULLET_FILE);
			File fd=new File(Const.RESOURCE_DIR+"/"+BULLET_DESTORY);
//			Logger.debug("@Bullet f exist = " + f.exists());
			_bulletimg = ImageIO.read(f);
			_bulletDestoryImg=ImageIO.read(fd);
		}catch(IOException e){
			Logger.fatal("@Bullet.loadImg: game terminated cuz bullet file not found, exception : " + e.toString());
//			_eventer.onEvent(Const.LOAD_MAP_ERR, null);
			return;
		}
//		Logger.debug("@Tank.loadImg : _w = " + _w + " _h = " + _h);
		
		_bulletDstImg = _bulletimg;
		
		this.setSize(100, 100);
		this.setBackground(new Color(255,255,255,0));
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if(this.getX()!=0||this.getY()!=0){
			if(_destroyed){
				g2.drawImage(_bulletDestoryImg, 30, 40, null);
			}else g2.drawImage(_bulletDstImg, 30, 30, null);
		}
	}
}
