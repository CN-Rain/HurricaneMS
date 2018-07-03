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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
*
* @author Acrylic (Terry Han)
*/

public class BuyCSItemHandler extends AbstractMaplePacketHandler {
	private final static int[] blockedItems = {5000028, 5400000};
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int action = slea.readByte();
		if (action == 3) {
			slea.skip(1);
			int useNX = slea.readInt();
			int snCS = slea.readInt();
			CashItemInfo item = CashItemFactory.getItem(snCS);
			for (int i = 0; i < blockedItems.length; i++) {
				if (item.getId() >= 5211000 && item.getId() <= 5211048 || item.getId() >= 1812002 && item.getId() <= 1812007 || item.getId() == blockedItems[i]) {
					new ServernoticeMapleClientMessageCallback(1, c).dropMessage("This item is not available for purchase.");
					c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
					c.getSession().write(MaplePacketCreator.enableCSUse0());
					c.getSession().write(MaplePacketCreator.enableCSUse1());
					c.getSession().write(MaplePacketCreator.enableCSUse2());
					return;
				}
			}
			if (c.getPlayer().getCSPoints(useNX) >= item.getPrice()) {
				c.getPlayer().modifyCSPoints(useNX, -item.getPrice());
			} else {
				//lol we hate you so we disconnect...
				c.disconnect();
				return;
			}
			if (item.getId() >= 5000000 && item.getId() <= 5000100) {
				int petId = MaplePet.createPet(item.getId());
				if (petId == -1) {
					return;
				}
				MapleInventoryManipulator.addById(c, item.getId(), (short) 1, "Cash Item was purchased.", null, petId);
			} else {
				MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "Cash Item was purchased.", null, -1);
			}
			c.getSession().write(MaplePacketCreator.showBoughtCSItem(item.getId()));
			c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
			c.getSession().write(MaplePacketCreator.enableCSUse0());
			c.getSession().write(MaplePacketCreator.enableCSUse1());
			c.getSession().write(MaplePacketCreator.enableCSUse2());
			c.getSession().write(MaplePacketCreator.enableActions());
		} else if (action == 5) {
			try {
				Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM wishlist WHERE characterid = ?");
				ps.setInt(1, c.getPlayer().getId());
				ps.executeUpdate();
				ps.close();

				int i = 10;
				while (i > 0) {
					int sn = slea.readInt();
					if (sn != 0) {
						ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
						ps.setInt(1, c.getPlayer().getId());
						ps.setInt(2, sn);
						ps.executeUpdate();
						ps.close();
					}
					i--;
				}
			} catch (SQLException se) {}
			c.getSession().write(MaplePacketCreator.updateWishList(c.getPlayer().getId()));
		} else if (action == 30) {
			int snCS = slea.readInt();
			CashItemInfo item = CashItemFactory.getItem(snCS);
			if (c.getPlayer().getMeso() >= item.getPrice()) {
				c.getPlayer().gainMeso(-item.getPrice(), false);
				MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "Quest Item was purchased.", null, -1);
				MapleInventory etcInventory = c.getPlayer().getInventory(MapleInventoryType.ETC);
				byte slot = etcInventory.findById(item.getId()).getPosition();
				c.getSession().write(MaplePacketCreator.showBoughtCSQuestItem(slot, item.getId()));
			} else {
				//we hate you so we disconnect..
				c.disconnect();
				return;
			}
			c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
			c.getSession().write(MaplePacketCreator.enableCSUse0());
			c.getSession().write(MaplePacketCreator.enableCSUse1());
			c.getSession().write(MaplePacketCreator.enableCSUse2());
			return;
		}
	}
}