package com.ztrix.util;

import com.ztrix.cmd.TimerCmd;

public class Logger{
	public static boolean loggerSwitch = true;
	public static boolean fatalSwitch  = true;
	public static boolean errorSwitch  = true;
	public static boolean infoSwitch   = true;
	public static boolean debugSwitch  = true;
	public static boolean useDebugCnter= false;
	public static boolean only         = false;
	
	public static int counter  = 0;
	public static int debugCounter = 0;

	public static void fatal(String msg){
		if (!loggerSwitch || !fatalSwitch || only) return;
		System.out.println("" + counter + " fatal: " + msg);
		counter++;
	}

	public static void error(String msg){
		if (!loggerSwitch || !errorSwitch || only) return;
		System.out.println("" + counter + " error: " + msg);
		counter++;
	}

	public static void info(String msg){
		if (!loggerSwitch || !infoSwitch || only) return;
		System.out.println("" + counter + " info: " + msg);
		counter++;
	}

	public static void debug(String msg){
		if (!loggerSwitch || !debugSwitch || only) return;
		int cnt = useDebugCnter ? debugCounter : counter;
		System.out.println(TimerCmd.timeStamp + " : " + cnt + " debug: " + msg);
		if (useDebugCnter) debugCounter ++;
		else counter++;
	}
	
	public static void only(String msg){
		if(!loggerSwitch||!only)return;
		System.out.println(TimerCmd.timeStamp + " : " + counter + " only: " + msg);
	}
	

}
