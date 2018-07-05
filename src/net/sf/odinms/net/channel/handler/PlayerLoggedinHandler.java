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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.CharacterNameAndId;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.CharacterIdChannelPair;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.PlayerBuffValueHolder;
import net.sf.odinms.net.world.PlayerCoolDownValueHolder;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PlayerLoggedinHandler extends AbstractMaplePacketHandler {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayerLoggedinHandler.class);

	@Override
	public boolean validateState(MapleClient c) {
		return !c.isLoggedIn();
	}

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int cid = slea.readInt();
		MapleCharacter player = null;
		try {
			player = MapleCharacter.loadCharFromDB(cid, c, true);
			c.setPlayer(player);
		} catch (SQLException e) {
			log.error("Loading the char failed", e);
		}
		c.setAccID(player.getAccountID());
		int state = c.getLoginState();
		boolean allowLogin = true;
		ChannelServer channelServer = c.getChannelServer();
		synchronized (this) {
			try {
				WorldChannelInterface worldInterface = channelServer.getWorldInterface();
				if (state == MapleClient.LOGIN_SERVER_TRANSITION) {
					for (String charName : c.loadCharacterNames(c.getWorld())) {
						if (worldInterface.isConnected(charName)) {
							log.warn(MapleClient.getLogMessage(player, "Attempting to double login with " + charName));
							allowLogin = false;
							break;
						}
					}
				}
			} catch (RemoteException e) {
				channelServer.reconnectWorld();
				allowLogin = false;
			}
			if (state != MapleClient.LOGIN_SERVER_TRANSITION || !allowLogin) {
				c.setPlayer(null); //REALLY prevent the character from getting deregistered as it is not registered
				c.getSession().close();
				return;
			}
			c.updateLoginState(MapleClient.LOGIN_LOGGEDIN);
		}

		ChannelServer cserv = ChannelServer.getInstance(c.getChannelByWorld());
		cserv.addPlayer(player);

		try {
			WorldChannelInterface wci = ChannelServer.getInstance(c.getChannelByWorld()).getWorldInterface();
			List<PlayerBuffValueHolder> buffs = wci.getBuffsFromStorage(cid);
			if (buffs != null) {
				c.getPlayer().silentGiveBuffs(buffs);
			}
			List<PlayerCoolDownValueHolder> cooldowns = wci.getCooldownsFromStorage(cid);
			if (cooldowns != null) {
				c.getPlayer().giveCoolDowns(cooldowns);
			}
		} catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		}
		
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT skillid,starttime,length FROM cooldowns WHERE charid = ?");
            ps.setInt(1, c.getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getLong("length") + rs.getLong("starttime") - System.currentTimeMillis() <= 0) {
                    continue;
                }
                c.getPlayer().giveCoolDowns(rs.getInt("skillid"), rs.getLong("starttime"), rs.getLong("length")); 
            }
            ps = con.prepareStatement("DELETE FROM cooldowns WHERE charid = ?");
            ps.setInt(1, c.getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }  
		
		c.getSession().write(MaplePacketCreator.getCharInfo(player));

		player.getMap().addPlayer(player);
		try {
			Collection<BuddylistEntry> buddies = player.getBuddylist().getBuddies();
			int buddyIds[] = player.getBuddylist().getBuddyIds();

			cserv.getWorldInterface().loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
			if (player.getParty() != null) {
				channelServer.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
			}

			CharacterIdChannelPair[] onlineBuddies = cserv.getWorldInterface().multiBuddyFind(player.getId(), buddyIds);
			for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
				BuddylistEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
				ble.setChannel(onlineBuddy.getChannel());
				player.getBuddylist().put(ble);
			}
			c.getSession().write(MaplePacketCreator.updateBuddylist(buddies));

			c.getPlayer().sendMacros();

			try {
				c.getPlayer().showNote();
			} catch (SQLException e) {
				log.error("LOADING NOTE", e);
			}

			if (player.getGuildId() > 0) {
				c.getChannelServer().getWorldInterface().setGuildMemberOnline(player.getMGC(), true, c.getChannel());
				c.getSession().write(MaplePacketCreator.showGuildInfo(player));
			}
		} catch (RemoteException e) {
			log.info("REMOTE THROW", e);
			channelServer.reconnectWorld();
		}
		player.updatePartyMemberHP();

		player.sendKeymap();
		
		//player.sendCooldowns();

		//c.getSession().write(MaplePacketCreator.weirdStatUpdate());

		for (MapleQuestStatus status : player.getStartedQuests()) {
			if (status.hasMobKills()) {
				c.getSession().write(MaplePacketCreator.updateQuestMobKills(status));
			}
		}

		CharacterNameAndId pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
		if (pendingBuddyRequest != null) {
			player.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), -1, false));
			c.getSession().write(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName()));
		}

		player.checkMessenger();

		player.checkBerserk();
		
		if (player.getLevel() <= 8) {
			player.giveItemBuff(2022118);
			c.getSession().write(MaplePacketCreator.serverNotice(5, "You have been buffed by the power of Hurricane!"));
			c.getSession().write(MaplePacketCreator.serverNotice(5, "Type ~help to see all help availiable."));
		}
	}
}
