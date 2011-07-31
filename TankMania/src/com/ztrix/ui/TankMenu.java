package com.ztrix.ui;

import java.awt.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import javax.swing.*;

import com.ztrix.*;
import com.ztrix.interfaces.IEventer;
import com.ztrix.util.Logger;

public class TankMenu extends JMenuBar implements ActionListener,ItemListener{
	
	private IEventer _eventer; 
	
	private JMenu _game;
	private JMenuItem _connect;
	private JMenuItem _connectlocal;
	private JMenuItem _exit;
	
	private JMenu _server;
	private JMenuItem _startServer;
	private JMenuItem _pauseServer;
	
	private JMenu _option;
	private JCheckBoxMenuItem _autoshoot;
	private JMenuItem _replay;
	
	private String lastInput="166.111.68.68:29997";

	public TankMenu(){
		_game = new JMenu("game");
		_connect = new JMenuItem("new game");
		_connect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
		_connect.addActionListener(this);
		_connectlocal=new JMenuItem("Connect Local Server");
		_connectlocal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,InputEvent.CTRL_MASK));
		_connectlocal.addActionListener(this);
		_exit = new JMenuItem("exit");
		_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_MASK));
		_exit.addActionListener(this);
		
		_server = new JMenu("Server");
		_startServer = new JMenuItem("Start Server");
		_startServer.addActionListener(this);
		_startServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		_pauseServer = new JMenuItem("Pause Server");
		_pauseServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK));
		_pauseServer.addActionListener(this);
		
		_option=new JMenu("Option");
		_autoshoot=new JCheckBoxMenuItem("Auto Shoot");
		_autoshoot.addItemListener(this);
		_replay=new JMenuItem("Replay");
		_replay.addActionListener(this);
		
		this.add(_game);
		_game.add(_connect);
		_game.add(_connectlocal);
		_game.add(_exit);
		
		this.add(_server);
		_server.add(_startServer);
		_server.add(_pauseServer);
		
		this.add(_option);
		_option.add(_autoshoot);
		_option.add(_replay);
		
		
		_game.setMnemonic(KeyEvent.VK_G);
		_server.setMnemonic(KeyEvent.VK_S);
		_option.setMnemonic(KeyEvent.VK_O);
	}
	
	public void setEventer(IEventer et){
		_eventer = et;
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == _connect){
			String inputValue = (String)JOptionPane.showInputDialog(this, "Server address: ", "New Tank Game", JOptionPane.QUESTION_MESSAGE, null, null, lastInput);
			Logger.debug("@TankMenu.actionPerformed: input = " + inputValue);
			if (inputValue == null || !inputValue.matches("[\\d\\.]+:\\d+")) return;
			lastInput=new String(inputValue);
			_eventer.onEvent(Const.NEW_GAME, inputValue);
		}else if (e.getSource() == _exit){
			TankFacade.getInstance().sendNotification(Const.EXIT_GAME, "0", null);
		}else if(e.getSource()==_startServer){
			TankFacade.getInstance().sendNotification(Const.START_SERVER, null, null);
		}else if(e.getSource()==_pauseServer){
			TankFacade.getInstance().sendNotification(Const.PAUSE_SERVER, null, null);
		}else if(e.getSource()==_connectlocal){
			_eventer.onEvent(Const.NEW_GAME, Const.LOCAL_SERVER+":"+Const.LOCAL_PORT);
		}else if(e.getSource()==_replay){
			JFileChooser chooser=new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("log", "log");
			chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	_eventer.onEvent(Const.REPLAY, chooser.getSelectedFile().getPath());
		    }
		}
	}
	
	public void itemStateChanged(ItemEvent e){
		if(e.getSource()==_autoshoot){
			boolean b=_autoshoot.getState();
			_autoshoot.setState(b);
			_eventer.onEvent(Const.AUTO_FIRE, b);
		}
	}

}
