package com.ztrix.cmd;

import org.puremvc.java.patterns.command.*;
import org.puremvc.java.interfaces.*;

import com.ztrix.util.*;

public class ExitCmd extends SimpleCommand{
	
	public void execute(INotification notification){
		Logger.info("@ExitCmd : exit");
		String sexitcode = (String)notification.getBody();
		int exitcode = 1;
		if (sexitcode != null){
			exitcode = Integer.parseInt(sexitcode);
		}
		System.exit(exitcode);
	}

}
