/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import java.util.LinkedList;
import net.sf.odinms.client.ChatLog.ChatEntry;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class GeneralchatHandler extends AbstractMaplePacketHandler {
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if(c.getPlayer().getMap().getDisableChat() && !c.getPlayer().isGM())
        {
            c.getSession().write(MaplePacketCreator.serverNotice(5, "Chat is currently disabled."));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
		String text = slea.readMapleAsciiString();
		int show = slea.readByte();
		if (text.length() > 140 && !c.getPlayer().isGM()) {
		try {
				c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using unlimited text, length: " + text.length()).getBytes());
			} catch (RemoteException ex) {
				c.getPlayer().getClient().getChannelServer().reconnectWorld();
			}
			c.getSession().close();
			return;
		}
		if (!CommandProcessor.getInstance().processCommand(c, text)) {
			if (c.getPlayer().isLogchat()) {
				if (System.currentTimeMillis() - c.getPlayer().getLastChatLog() > 60000) { //1 minute..
					c.getPlayer().setLogchat(false);
				}
			}
			if (c.getPlayer().isLogchat()) { //chatlog enabled?
				c.getPlayer().getChatLog().log(new ChatEntry(text));
			}
			if(!c.getPlayer().getIRCAllTalk()) {
				if (!c.getPlayer().isGM() || show == 1) {
					c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, c.getPlayer().isGM() && c.getChannelServer().allowGmWhiteText(), show));
				} else {
					MaplePacket message = null;
					switch (c.getPlayer().getChatMode()) {
						case 0:
							message = MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 0);
							break;
						case 1:
							message = MaplePacketCreator.getChatText(c.getPlayer().getId(), text, true, 0);
							break;
						case 2:
							message = MaplePacketCreator.serverNotice(6, c.getPlayer().getName() + " : " + text);
							break;
						case 3:
							message = MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " : " + text);
							break;							
						case 4:
							message = MaplePacketCreator.yellowChat(c.getPlayer().getName() + " : " + text);
							break;
						case 5:
							message = MaplePacketCreator.serverNotice(2, c.getChannel(), c.getPlayer().getName() + " : " + text, Math.random() > 0.5);
							break;
						case 6:
							String[] lines = {"", "", "", ""};

							if(text.length() > 30) {
								lines[0] = text.substring(0, 10);
								lines[1] = text.substring(10, 20);
								lines[2] = text.substring(20, 30);
								lines[3] = text.substring(30);
							}

							else if(text.length() > 20) {
								lines[0] = text.substring(0, 10);
								lines[1] = text.substring(10, 20);
								lines[2] = text.substring(20);
							}

							else if(text.length() > 10) {
								lines[0] = text.substring(0, 10);
								lines[1] = text.substring(10);
							}

							else if(text.length() <= 10) {
								lines[0] = text;
							}

							LinkedList<String> list = new LinkedList<String>();
							list.add(lines[0]);
							list.add(lines[1]);
							list.add(lines[2]);
							list.add(lines[3]);

							message = MaplePacketCreator.getAvatarMega(c.getPlayer(), c.getChannel(), 5390001, list, Math.random() > 0.5);
							break;
						case 7:
							message = MaplePacketCreator.toSpouse(c.getPlayer().getName(), text, 4);
							break;
						case 8:
							message = MaplePacketCreator.serverNotice(3, c.getChannel(), c.getPlayer().getName() + " : " + text, Math.random() > 0.5);
							break;
						case 9:
							message = MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text);
							break;
						case 10:
							message = MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1);
							break;
						default:
							c.getPlayer().dropMessage("You can't talk cuz I said so :D");
							break;
					}
					if (message != null) {
						switch (c.getPlayer().getChatRange()) {
							case 0:
								c.getPlayer().getMap().broadcastMessage(message);
								break;
							case 1:
								c.getChannelServer().broadcastPacket(message);
								break;
							case 2:
								try {
									c.getChannelServer().getWorldInterface().broadcastMessage("", message.getBytes());
								} catch (RemoteException ex) {
									c.getPlayer().dropMessage("Error occured, stupid fucking RMI..");
									c.getChannelServer().reconnectWorld();
								}
								break;
							default:
								c.getPlayer().dropMessage("You can't talk cuz I said so :D");
								break;
						}
						boolean showChat = true;
						switch (c.getPlayer().getChatMode()) {
							case 0:
							case 1:
								showChat = false;
								break;
						}
						if (showChat) {
							c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
						}
					}
				}
			} else {
				if(c.hasIrc()) {
					c.getIRC().SendMessage(text);
				} else {
					c.getSession().write(MaplePacketCreator.serverNotice(5, "Error: You are not connected to an IRC network (use !irc alltalk)"));
				}
			}
		}
	}
}