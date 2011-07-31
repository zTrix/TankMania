package com.ztrix.util;

import com.ztrix.interfaces.IThreader;
import com.ztrix.util.*;

public class MyThread extends Thread{
	private IThreader _torun;
	
	public MyThread(IThreader t){
		_torun = t;
	}
	
	public void run(){
		_torun.toberun();
	}
}