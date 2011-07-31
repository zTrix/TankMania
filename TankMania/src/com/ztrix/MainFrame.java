package com.ztrix;

import com.ztrix.util.*;
import com.ztrix.proxy.*;
import com.ztrix.listener.*;
import com.ztrix.ui.*;
import com.ztrix.mediator.*;
import com.ztrix.data.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class MainFrame extends JFrame implements MouseListener, MouseMotionListener,KeyListener{
	private GameMap _map;
	private TankLogo _logo;
	private JComponent _glass;
	private TankMenu _tankMenu;
	private TankFacade _fcd;
	
	public static int myTankID = -1;
	public static String myTankName = "zz";
	
	private HashSet _tanks = new HashSet();
	private HashSet _bullets = new HashSet();
	
	private JPanel _mapWrap;
	
	private JTextField _talkbar;
	
	public MainFrame(String title){
		super(title);
		Logger.info("@MainFrame: new");
		
		this.setVisible(true);
		this.addWindowListener(new MyWindowListener());
		
		_tankMenu = new TankMenu();
		this.setJMenuBar(_tankMenu);
		this.setSize(608,427+this.getJMenuBar().getHeight());
		
		_logo = new TankLogo();
		this.getContentPane().add(_logo);
		_logo.setVisible(true);
		_logo.repaint();
		
		_mapWrap=new JPanel(new BorderLayout());
		
		_map = new GameMap();
		_mapWrap.add("Center",_map);
		_map.setVisible(true);
		
		_talkbar=new JTextField(100);
		_mapWrap.add("South",_talkbar);
		_talkbar.setVisible(true);
		_talkbar.addKeyListener(this);
		
		_fcd = (TankFacade)TankFacade.getInstance();
		_fcd.startup(this);
	
//		((ServerProxy)(_fcd.retrieveProxy(Const.SERVER_PROXY))).connectServer();
		
		_glass = new JPanel();
		_glass.setSize(this.getWidth(), this.getHeight());
		this.setGlassPane(_glass);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		
		this.addKeyListener(this);
//		createTank();
//		this.pack();
	}
	
	public void removeLogo(){
		if(this.getContentPane().isAncestorOf(_logo)){
			_logo.setVisible(false);
			this.getContentPane().remove(_logo);
			_talkbar.setVisible(true);
			this.repaint();
		}
	}
	
	public void createTank(int id, String name, int life, int x, int y, float tangle, float gangle){
		Logger.info("@MainFrame.createTank: "+id+" "+name+" "+life+" "+x+" "+y+" "+tangle+" "+gangle);
		Tank tank = new Tank();
		TankMediator tm = new TankMediator(Const.TANK_MEDIATOR, id, tank);
		TankState ts = new TankState(id, name, life, x, y, tangle, gangle);
//		synchronized(_fcd){
			_fcd.registerMediator(tm);
//		}
		synchronized(_tanks){
			_tanks.add(id);
		}
		this.getContentPane().add(tank, 0);
		tank.setLocation(x-50, y-50);
		tm.initTank(ts);
		tank.setVisible(true);
		tank.repaint();
	}
	
	public void createBullet(int id, int x, int y, float angle){
		Logger.debug("create bullet "+id);
		Bullet b = new Bullet();
		BulletMediator bm = new BulletMediator(Const.BULLET_MEDIATOR, id, b);
		this.getContentPane().add(b,0);
		b.initState(x, y, angle);
		Logger.debug("MainFrame1");
		synchronized(_fcd){
			_fcd.registerMediator(bm);
		}
		Logger.debug("MainFrame2");
		_bullets.add(id);
		b.repaint();
		b.setVisible(true);
		_fcd.sendNotification(Const.PLAY_SND, Const.SHOT, null);
	}
	
	public void resetAll(){
		Logger.only("resetAll");
		Iterator it;
		synchronized(_tanks){
			it = _tanks.iterator();
			while(it.hasNext()){
				destroyTank((Integer)it.next());
			}
			_tanks=new HashSet();
		}
		synchronized(_bullets){
			it = _bullets.iterator();
			while(it.hasNext()){
				destroyBullet((Integer)it.next());
			}
			_bullets=new HashSet();
		}
		this.getContentPane().removeAll();
		this.getContentPane().add(_mapWrap);
		_mapWrap.setVisible(true);
		this.repaint();
	}
	
	public void removeBulletIndex(int id){
		synchronized(_bullets){
			_bullets.remove(id);
		}
	}
	
	public void removeTankIndex(int id){
		synchronized(_tanks){
			_tanks.remove(id);
		}
	}
	
	public void destroyTank(int id){
		Logger.debug("@MainFrame.destroyTank:id="+id);
		//remove view
		TankMediator t = ((TankMediator)_fcd.retrieveMediator(Const.TANK_MEDIATOR + id));
		if (t!=null){
			t.destroyTank();
//			synchronized(_fcd){
				_fcd.removeMediator(Const.TANK_MEDIATOR + id);
//			}
		}
	}
	
	public void destroyBullet(int id){
		Logger.info("@MainFrame.destroyBullet");
		//remove view
		BulletMediator bm = ((BulletMediator)_fcd.retrieveMediator(Const.BULLET_MEDIATOR+id));
		if(bm!=null)bm.destroyBullet();
	}
	
	public GameMap gameMap() {
		return _map;
	}
	
	public TankMenu tankMenu(){
		return _tankMenu;
	}
	
	public void mouseClicked(MouseEvent e){
//		Logger.info("@MainFrame.mouseClicked: e = " + e.toString());
//		int [] ary = new int[2];
//		
//		ary[0] = e.getX() - this.getInsets().left - 20;
//		ary[1] = e.getY() - this.getInsets().top - _tankMenu.getHeight() - 20;
//		
//		if (e.getButton() == MouseEvent.BUTTON1){  // left
//			if (!hasMe()) return;
//			_fcd.sendNotification(Const.SEND_MSG, ary, Const.FIRE);
//		}else if (e.getButton() == MouseEvent.BUTTON3){  // right
//			if (!hasMe() && ((ServerProxy)_fcd.retrieveProxy(Const.SERVER_PROXY)).connected){
//				
//				_fcd.sendNotification(Const.SEND_MSG, ary, Const.BORN);
//			}else{
//				_fcd.sendNotification(Const.SEND_MSG, ary, Const.MOVE_TO);
//			}
//		}
	}
	
	public void mouseMoved(MouseEvent e){
		if(ReplayMediator.replayMode)return;
		if (!hasMe() || !((ServerProxy)_fcd.retrieveProxy(Const.SERVER_PROXY)).connected) return;
		int [] ary = new int[2];
		ary[0] = e.getX() - this.getInsets().left;
		ary[1] = e.getY() - this.getInsets().top - _tankMenu.getHeight();
		_fcd.sendNotification(Const.SEND_MSG, ary, Const.ROTATE_TO);
	}
	
	public void mouseDragged(MouseEvent e){
		if(ReplayMediator.replayMode)return;
		if (!((ServerProxy)_fcd.retrieveProxy(Const.SERVER_PROXY)).connected) return;
		int [] ary = new int[2];
		
		ary[0] = e.getX() - this.getInsets().left;
		ary[1] = e.getY() - this.getInsets().top - _tankMenu.getHeight();
		if (e.getButton() == MouseEvent.BUTTON1){  // left
			if (!hasMe()) return;
			_fcd.sendNotification(Const.SEND_MSG, ary, Const.FIRE);
		}else if (e.getButton() == MouseEvent.BUTTON3){  // right
			if (!((ServerProxy)_fcd.retrieveProxy(Const.SERVER_PROXY)).connected) return;
			if (!hasMe()){
				_fcd.sendNotification(Const.SEND_MSG, ary, Const.BORN);
			}else{
				_fcd.sendNotification(Const.SEND_MSG, ary, Const.MOVE_TO);
			}
		}
	}
	
	private boolean hasMe(){
		return _fcd.retrieveMediator(Const.TANK_MEDIATOR + myTankID) != null;
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		if(ReplayMediator.replayMode)return;
		if (!((ServerProxy)_fcd.retrieveProxy(Const.SERVER_PROXY)).connected) return;
		int [] ary = new int[2];
		
		ary[0] = e.getX() - this.getInsets().left;
		ary[1] = e.getY() - this.getInsets().top - _tankMenu.getHeight();
		if (e.getButton() == MouseEvent.BUTTON1){  // left
			if (!hasMe()) return;
			_fcd.sendNotification(Const.SEND_MSG, ary, Const.FIRE);
		}else if (e.getButton() == MouseEvent.BUTTON3){  // right
			if (!((ServerProxy)_fcd.retrieveProxy(Const.SERVER_PROXY)).connected) return;
			if (!hasMe()){
				_fcd.sendNotification(Const.SEND_MSG, ary, Const.BORN);
			}else{
				_fcd.sendNotification(Const.SEND_MSG, ary, Const.MOVE_TO);
			}
		}
	}
	public void mouseReleased(MouseEvent e){}
	
	public void keyPressed(KeyEvent e){
		if(ReplayMediator.replayMode)return;
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			AIMediator.autoFire=!AIMediator.autoFire;
		}else if(e.getKeyCode()==KeyEvent.VK_ENTER){
			if(_talkbar.getText()=="")return;
			_fcd.sendNotification(Const.SEND_MSG, _talkbar.getText(), Const.TALK);
			_talkbar.setText("");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
