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

public class Tank extends JPanel{
	private final String TANK_FILE = "tank_0.png";
	private final String GUN_FILE = "gun_0.png";
	private final String DESTROY_FILE = "effect_0.png";
	
	private IEventer _eventer;
	
	private BufferedImage _tankimg;
//	private BufferedImage _tankDstImg;
	private BufferedImage _gunimg;
//	private BufferedImage _gunDstImg;
	private BufferedImage _destroyImg;

	private TankState _curState;

	
	public Tank(){
		super(true);
		
		loadImg();
		this.setVisible(true);
	}
	
	public TankState getState(){
		return _curState;
	}
	
	public void initState(TankState st){
		_curState = new TankState(st);
		this.setLoc(_curState.x, _curState.y);
		rotateTank(_curState.tankAngle);
		rotateGun(_curState.gunAngle);
		setLife(_curState.life);
	}
	
//	public void setState(TankState st){
//		this.setLocation(st.x, st.y);
//		rotateTank(st.tankAngle);
//		rotateGun(st.gunAngle);
//		setLife(st.life);
//
//		_curState.tankAngle = st.tankAngle;
//		_curState.gunAngle = st.gunAngle;
//		_curState.life = st.life;
//	}
	
	public void destroyTank(){
		_curState.destroyed = true;
		appFacade().app().repaint();
//		this.repaint();
	}
	
	public void setLife(int l){
		_curState.life = l;
		this.repaint();
	}
	
	public void setLoc(int x, int y){
//		Logger.only(_curState.tankName+"1x="+x+" y="+y);
		if (_curState!=null&&_curState.destroyed)return;
		_curState.x=x-50;_curState.y=y-50;
		this.setLocation(x-50, y-50);
	}
	
	public void rotateTank(double angle){
//		AffineTransform transform = AffineTransform.getRotateInstance(angle, 20, 20);
//		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
//		_tankDstImg = op.filter(_tankimg, null);
		_curState.tankAngle = (float)angle;
	}
	
	public void rotateGun(double angle){
//		AffineTransform transform = AffineTransform.getRotateInstance(angle, 20, 20);
//		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
//		_gunDstImg = op.filter(_gunimg, null);
		_curState.gunAngle = (float)angle;
	}
	
	public void setEventer(IEventer et){
		_eventer = et;
	}
	
	private void loadImg(){
		Logger.info("@Tank.loadImg");
		try{
			File f = new File(Const.RESOURCE_DIR + "/" +  TANK_FILE);
			File fb = new File(Const.RESOURCE_DIR + "/" +  GUN_FILE);
			File fd = new File(Const.RESOURCE_DIR + "/" + DESTROY_FILE);
//			Logger.debug("@Tank f exist = " + f.exists() + " fb exist = " + fb.exists());
			_tankimg = ImageIO.read(f);
			_gunimg = ImageIO.read(fb);
			_destroyImg = ImageIO.read(fd);
		}catch(IOException e){
			Logger.fatal("@Tank.loadImg: game terminated cuz tank file not found, exception : " + e.toString());
//			_eventer.onEvent(Const.LOAD_MAP_ERR, null);
			return;
		}
//		_w = _tankimg.getWidth();
//		_h = _tankimg.getHeight();
//		_gw = _gunimg.getWidth();
//		_gh = _gunimg.getHeight();
//		Logger.debug("@Tank.loadImg : _w = " + _w + " _h = " + _h);
		
//		_tankDstImg = _tankimg;
//		_gunDstImg = _gunimg;
		
		this.setSize(100, 100);
		this.setBackground(new Color(255,255,255,0));
	}
	
	private TankFacade appFacade(){
		return (TankFacade)TankFacade.getInstance();
	}
	
	
//	private int lx=-1;
//	private int ly=-1;
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
//		if(!(this.getX()==lx&&this.getY()==ly)){
//			Logger.only("paint = "+this.getX()+" "+ this.getY());
//			lx=this.getX();
//			ly=this.getY();
//		}
		if (_curState!=null&&_curState.destroyed){
			g2.drawImage(_destroyImg, 30, 30, null);
		}else{
			
			g.setColor(Color.blue);
			Font f = new Font("Courier", Font.BOLD, 14);
			g.setFont(f);
			FontMetrics fm = this.getFontMetrics(f);
			int ww = fm.stringWidth(_curState.tankName);
			g2.drawString(_curState.tankName, (100-ww)/2, 90);
			
			g2.setColor(Color.white);
			g2.drawRect(29, 19, 41, 6);
			if (_curState.life > 300){
				g2.setColor(Color.blue);
			}else if(_curState.life > 100){
				g2.setColor(Color.yellow);
			}else{
				g2.setColor(Color.red);
			}
			g2.fillRect(30, 20, (int)(_curState.life*.04), 5);
			
	//		g2.drawImage(_tankDstImg, 30, 30, null);
	//		g2.drawImage(_gunDstImg, 30, 30, null);
			
			
			g2.rotate(_curState.tankAngle, 50, 50);
			g2.drawImage(_tankimg, 30, 30, null);
			
			
			g2.rotate(_curState.gunAngle-_curState.tankAngle, 50, 50);
			g2.drawImage(_gunimg, 30, 30, null);
		}
	}
}
