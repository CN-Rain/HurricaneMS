package net.sf.odinms.client;

import java.io.IOException;

import net.sf.odinms.tools.*;

import org.jibble.pircbot.*;

public class IRCClient extends PircBot {
	MapleClient c;
	String server, channel, name;
	
	public IRCClient(MapleClient c, String name, String server, String channel)
	{
		this.c = c;
		setName(name);
		this.server = server;
		this.channel = channel;
		this.name = name;
	}
	
	public void Connect()
	{
		try {
			connect(server);
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
        joinChannel(channel);
	}
    
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	if (message.equalsIgnoreCase("!charinfo"))
            SendMessage("I am currently playing on " + c.getPlayer().getName());
    	else if(!message.contains(name))
    		c.getSession().write(MaplePacketCreator.serverNotice(6, "<" + sender + "> " + message));
    	else
    		c.getSession().write(MaplePacketCreator.serverNotice(5, "<" + sender + "> " + message));
    }
    
    public void onJoin(String channel, String sender, String login, String hostname) {
    	if(sender != name)
    		c.getSession().write(MaplePacketCreator.serverNotice(6, sender + " (" + hostname + ") has joined " + channel));
    	else
    		c.getSession().write(MaplePacketCreator.serverNotice(5, "You have joined " + channel));
    }
    
    public void onPrivateMessage(String sender, String login, String hostname, String message) {
    	c.getSession().write(MaplePacketCreator.serverNotice(5, "-" + sender + "- " + message));
    }
    
    protected void onAction(String sender, String login, String hostname, String target, String action) {
    	c.getSession().write(MaplePacketCreator.serverNotice(6, "*" + sender + " " + action));
    }
    
    protected void onTopic(String channel, String topic) {
    	c.getSession().write(MaplePacketCreator.serverNotice(5, topic));
    }
    
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
    	c.getSession().write(MaplePacketCreator.serverNotice(5, "-" + sourceNick + "- " + notice));
    }
    
    protected void onPart(String channel, String sender, String login, String hostname) {
    	if(sender != name)
    		c.getSession().write(MaplePacketCreator.serverNotice(5, sender + " has left " + channel));
    }
    
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
    	if(oldNick != name && newNick != name)
    		c.getSession().write(MaplePacketCreator.serverNotice(5, "*" + oldNick + " has changed their nick to " + newNick));
    }
    
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
		c.getSession().write(MaplePacketCreator.serverNotice(5, "*" + recipientNick + " as been kicked for" + reason));
    }
    
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
    	c.getSession().write(MaplePacketCreator.serverNotice(5, "*" + sourceNick + " has quit (" + reason + ")"));
    }
    
    protected void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
    	c.getSession().write(MaplePacketCreator.serverNotice(5, "*" + sourceNick + " has set mode " + mode));
    }
    
    protected void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
    	c.getSession().write(MaplePacketCreator.serverNotice(5, "*" + sourceNick + " has set mode " + mode + " on " + targetNick));
    }
    
    public void ChangeNick(String nick) {
    	this.name = nick;
    	changeNick(nick);
    }
    
    public void SendMessage(String msg)
    {
    	SendMessage(channel, msg);
    }
    
    public String getChannel()
    {
    	return channel;
    }
    
    public String getEveryone()
    {
    	User[] u = getUsers(channel);
    	String ol = "";
    	for(int i = 0; i < u.length; i++)
    	{
    		if(i + 1 != u.length)
    			ol += u[i].getNick() + ", ";
    		else
    			ol += u[i].getNick();
    	}
    	return ol;
    }
    
    public void SendMessage(String channel, String msg)
    {
    	sendMessage(channel, msg);
    	c.getSession().write(MaplePacketCreator.serverNotice(6, "<" + name + "> " + msg));
    }
}