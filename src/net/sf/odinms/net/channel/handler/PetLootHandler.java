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
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Raz
 * 
 */
public class PetLootHandler extends AbstractMaplePacketHandler {
	final Pair<?, ?>[] necksonCards = {
		new Pair<Integer, Integer>(4031530, 100),
		new Pair<Integer, Integer>(4031531, 250),
	};

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

		if (c.getPlayer().getNoPets() == 0) {
			return;
		}
		MaplePet pet = c.getPlayer().getPet(c.getPlayer().getPetIndex(slea.readInt()));
		slea.skip(13);
		int oid = slea.readInt();
		MapleMapObject ob = c.getPlayer().getMap().getMapObject(oid);
		if (ob == null || pet == null) {
			c.getSession().write(MaplePacketCreator.getInventoryFull());
			return;
		}
		if (ob instanceof MapleMapItem) {
			MapleMapItem mapitem = (MapleMapItem) ob;
			synchronized (mapitem) {
				if (mapitem.isPickedUp()) {
					c.getSession().write(MaplePacketCreator.getInventoryFull());
					return;
				}
				double distance = pet.getPos().distanceSq(mapitem.getPosition());
				c.getPlayer().getCheatTracker().checkPickupAgain();
				if (distance > 160000.0) { // 300^2, 550 is approximatly the range of ultis
					c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.ITEMVAC);
					if (c.getPlayer().getCheatTracker().getPoints() > 50) {
						try {
							c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using itemVAC").getBytes());
						} catch (RemoteException ex) {
							c.getPlayer().getClient().getChannelServer().reconnectWorld();
						}
						c.getSession().close();
					}
					return;
				} else if (distance > 90000.0) {
					c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.SHORT_ITEMVAC);
					if (c.getPlayer().getCheatTracker().getPoints() > 50) {
						try {
							c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using itemVAC").getBytes());
						} catch (RemoteException ex) {
							c.getPlayer().getClient().getChannelServer().reconnectWorld();
						}
						c.getSession().close();
					}
				}
				for (Pair<?, ?> pair : necksonCards) {
					if (mapitem.getMeso() <= 0) {
						if (mapitem.getItem().getItemId() == (Integer)pair.getLeft()) {
							c.getPlayer().getMap().broadcastMessage(
								MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()),
								mapitem.getPosition());
							c.getPlayer().getCheatTracker().pickupComplete();
							c.getPlayer().getMap().removeMapObject(ob);
							int necksonGain = (Integer) pair.getRight();
							c.getSession().write(MaplePacketCreator.serverNotice(5, "You have gained Neckson cash (+" + necksonGain + ")."));
							c.getPlayer().modifyCSPoints(4, necksonGain);
							c.getSession().write(MaplePacketCreator.getShowItemGain(mapitem.getItem().getItemId(), (short)1));
							c.getSession().write(MaplePacketCreator.enableActions());
							return;
						}
					}
				}
				if (mapitem.getMeso() > 0 && mapitem.getDropper() != c.getPlayer()) {
					if(c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1812000) != null) { //Evil hax until I find the right packet - Ramon
						c.getPlayer().gainMeso(mapitem.getMeso(), true, true);
						c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 5, c.getPlayer().getId(), true, c.getPlayer().getPetIndex(pet)), mapitem.getPosition());
						c.getPlayer().getCheatTracker().pickupComplete();
						c.getPlayer().getMap().removeMapObject(ob);
					} else {
						c.getPlayer().getCheatTracker().pickupComplete();
						mapitem.setPickedUp(false);
						c.getSession().write(MaplePacketCreator.enableActions());
						return;
					}
				} else if (mapitem.getDropper() != c.getPlayer()) {
					if (ii.isPet(mapitem.getItem().getItemId())) {
						int petId = MaplePet.createPet(mapitem.getItem().getItemId());
						if (petId == -1) {
							return;
						}
						MapleInventoryManipulator.addById(c, mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), "Pet was picked up", null, petId);
						c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 5, c.getPlayer().getId()), mapitem.getPosition());
						c.getPlayer().getCheatTracker().pickupComplete();
						c.getPlayer().getMap().removeMapObject(ob);
					} else {
						if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), "Picked up by " + c.getPlayer().getName(), true)) {
							c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 5, c.getPlayer().getId(), true, c.getPlayer().getPetIndex(pet)), mapitem.getPosition());
							c.getPlayer().getCheatTracker().pickupComplete();
							c.getPlayer().getMap().removeMapObject(ob);
						} else {
							c.getPlayer().getCheatTracker().pickupComplete();
							return;
						}
					}
				}
				if (mapitem.getDropper() != c.getPlayer())
					mapitem.setPickedUp(true);
			}
		}
		c.getSession().write(MaplePacketCreator.enableActions());
	}
}