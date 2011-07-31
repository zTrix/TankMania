package com.ztrix.util;

import java.io.*;
import java.util.*;
import java.awt.*;

import com.ztrix.Const;
import com.ztrix.data.*;

public class Texter{
	private static String logFile = "tank.log";
	private static BufferedWriter writer;

	public static BufferedReader getBuffReader(String name){
		FileReader fr = null;
		try{
			fr = new FileReader(name);
		}catch(Exception e){
			Logger.fatal("@Texter.getBuffReader read " + name + " file error, exception = " + e.toString());
		}
		BufferedReader br = new BufferedReader(fr);
		return br;
	}

	public static MapInfo parseMapConfig(String configfile){
		configfile = Const.RESOURCE_DIR + "/" + configfile;
		MapInfo ret = new MapInfo();
		try{
			BufferedReader bf = getBuffReader(configfile);
			String s = bf.readLine();
			String []ary = s.split(" ");
			ret.mapW = Integer.parseInt(ary[0]);
			ret.mapH = Integer.parseInt(ary[1]);
			ret.mapFile = bf.readLine();
//			Logger.debug("" + ret.mapFile.length());
			ret.wallNum = Integer.parseInt(bf.readLine());
			for (int i = 0; i < ret.wallNum; i++){
				s = bf.readLine();
				ary = s.split("\\s");
				Rectangle r = new Rectangle(Integer.parseInt(ary[0]), Integer.parseInt(ary[1]), 
											Integer.parseInt(ary[2]), Integer.parseInt(ary[3]));
				ret.walls.add(r);
			}
		}catch(Exception e){
			Logger.fatal("@Texter.parseMapConfig error, exception = " + e.toString());
		}
		return ret;
	}
	
	public static void savelog(String line){
		if (writer == null){
			try{
				 writer = new BufferedWriter(new FileWriter(logFile));
			}catch(Exception e){
				Logger.error("@Texter.savelog: open log file failed, exception is " + e.toString());
				return;
			}
		}
		try{
			writer.write(line + "\r\n");
			writer.flush();
		}catch(Exception e){
			Logger.error("@Texter.savelog: write log file failed, exception is " + e.toString());
		}
	}
	
	public static void emptylog(){
		if (writer == null) return;
		try{
			writer.close();
		}catch(Exception e){
			Logger.error("@Texter.emptylog: close log file failed, exception is " + e.toString());
		}
		writer = null;
	}
	
	public static double distance(double sx,double sy,double ex,double ey){
//		return Math.sqrt((sx-ex)*(sx-ex)+(sy-ey)*(sy-ey));
		return Math.hypot(sx-ex, sy-ey);
	}
	
	public static double sqrareDis(double sx,double sy,double ex,double ey){
		return ((sx-ex)*(sx-ex)+(sy-ey)*(sy-ey));
	}
}
