package com.ztrix;

public class Const{
	// pureMVC constants
	public static final String GAME_MAP_MEDIATOR = "GAME_MAP_MEDIATOR";
	public static final String SERVER_PROXY = "SERVER_PROXY";
	public static final String TANK_MEDIATOR = "TANK_MEDIATOR";
	public static final String BULLET_MEDIATOR = "BULLET_MEDIATOR";
	public static final String CLIENT_MEDIATOR = "CLIENT_MEDIATOR";
	public static final String AI_MEDIATOR="AI_MEDIATOR";
	public static final String SERVER_BULLET_MEDIATOR="SERVER_BULLET_MEDIATOR";
	public static final String REPLAY_MEDIATOR="REPLAY_MEDIATOR";
	
	public static final String TANK_STATE_PROXY = "TANK_STATE_PROXY";

	// notifications
	public static final String START_UP = "START_UP";
	public static final String EXIT_GAME = "EXIT_GAME";
	public static final String TICK = "TICK";
	public static final String START_TIMER = "START_TIMER";
	public static final String LOAD_MAP = "LOAD_MAP";
	public static final String MSG_GOT = "MSG_GOT";
	public static final String SEND_MSG = "SEND_MSG";
	public static final String BORN = "BORN";
	public static final String MOVE_TO = "MOVE_TO";
	public static final String ROTATE_TO = "ROTATE_TO";
	public static final String FIRE = "FIRE";
	public static final String SYNC = "SYNC";
	public static final String ACCEPT_CLIENT = "ACCEPT_CLIENT";
	public static final String PLAY_SND="PLAY_SND";
	public static final String TALK="TALK";
	public static final String REPLAY="REPLAY";
	
	public static final String START_SERVER = "START_SERVER";
	public static final String PAUSE_SERVER = "PAUSE_SERVER";
	public static final String BROAD_CAST = "BROAD_CAST";
	public static final String DELAY_BROAD_CAST = "DELAY_BROAD_CAST";
	public static final String MOVE_ALL_TANK = "MOVE_ALL_TANK";
	public static final String STOP_MOVE = "STOP_MOVE";
	// notification types
	public static final String CONNECT = "CONNECT";
	public static final String INIT_PROTOCAL = "INIT_PROTOCAL";

	// events
	public static final String LOAD_MAP_ERR = "LOAD_MAP_ERR";
	public static final String NEW_GAME = "NEW_GAME";
	public static final String AUTO_FIRE = "AUTO_FIRE";
	
	// param
	public static final String NO_PREFIX = "NO_PREFIX";
	
	// dir and url
	public static final String RESOURCE_DIR = "res";
	public static final String SERVER_URL = "166.111.68.68";
	public static final int SERVER_PORT = 29997;
	public static final String LOGO_FILE = "tanklogo.png";
	public static final String LOCAL_SERVER = "127.0.0.1";
	public static final int LOCAL_PORT = 7654;
	
	// time interval
	public static final int TIME_STAMP_INTERVAL = 8;
	public static final double BULLET_SPEED=100/1.5;
	public static final int BULLET_LIFE=267;
	
	// music
	public static final String SHOT="SHOT";
	public static final String EXPLOSION="EXPLOSION";
	
	// tank action in server
	public static final String MOVE = "MOVE";
	public static final String STILL = "STILL";
	public static final String BORN_ME = "BORN_ME";
	
	public static final String LOSE_CONNECTION = "LOSE_CONNECTION";
}
