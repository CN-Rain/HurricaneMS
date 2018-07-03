package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
*
* @author Pat
*/

public class UseCatchItemHandler extends AbstractMaplePacketHandler {

	public UseCatchItemHandler() {
	}

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		// 4A 00
		// B9 F4 8B 00 // unknown
		// 01 00 // success??
		// 32 A3 22 00 // itemid
		// 38 37 2B 00 // monsterid
		if (System.currentTimeMillis() - c.getPlayer().getLastCatch() < 2000) {
			c.getSession().write(MaplePacketCreator.serverNotice(5, "You cannot use the rock right now."));
			c.getSession().write(MaplePacketCreator.enableActions());
			return;
		}
 		slea.readInt();
		slea.readShort();
		int itemid = slea.readInt();
		int oid = slea.readInt();
		
		MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(oid);
		if (mob != null) {
			if (mob.getHp() <= mob.getMaxHp() / MapleCharacter.rand(2, 5)) {
				if (itemid == 2270002) {
					c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.catchMonster(oid, itemid, (byte) 1));
					MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2270002, 1, false, true);
				}
				mob.getMap().killMonster(mob, c.getPlayer(), false, false, 0);
				MapleInventoryManipulator.addById(c, 4031868, (short)1, new String(), "", -1);
				c.getSession().write(MaplePacketCreator.serverNotice(5, "You have gained a jewel!"));
				c.getPlayer().setLastCatch(System.currentTimeMillis());
				c.getPlayer().updateAriantScore();
			} else {
				c.getSession().write(MaplePacketCreator.serverNotice(5, "You cannot catch the monster as it is too strong."));
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.catchMonster(oid, itemid, (byte) 0));
			}
			c.getSession().write(MaplePacketCreator.enableActions());
		}
	}
}