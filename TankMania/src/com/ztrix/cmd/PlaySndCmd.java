package com.ztrix.cmd;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;
import java.io.*;
import java.util.*;

import com.ztrix.util.*;
import com.ztrix.Const;
import com.ztrix.TankFacade;
import com.ztrix.util.Logger;
import com.ztrix.data.TankState;
import com.ztrix.interfaces.*;
import sun.audio.*;

public class PlaySndCmd extends SimpleCommand {
	private String SHOT="shot.wav";
//	private String SHOT="Laser_Pulse_Shot.mp3";
	private String EXPO="explosion.wav";
	
	public void execute(INotification notification){
		try{
			if(notification.getBody()==Const.SHOT){
				InputStream in = new FileInputStream(Const.RESOURCE_DIR+"/"+SHOT);
				AudioStream as = new AudioStream(in);
				AudioPlayer.player.start(as);
			}else if(notification.getBody()==Const.EXPLOSION){
				InputStream in = new FileInputStream(Const.RESOURCE_DIR+"/"+EXPO);
				AudioStream as = new AudioStream(in);
				AudioPlayer.player.start(as);
			}
		}catch(Exception e){
			Logger.error("@PlaySndCmd :e="+e.toString());
		}
	}

}
