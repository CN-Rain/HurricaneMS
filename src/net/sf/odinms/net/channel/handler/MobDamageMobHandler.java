/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 * Handler for Mobs damaging Mobs.
 * @author Jvlaple
 */
public class MobDamageMobHandler extends AbstractMaplePacketHandler {
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MobDamageMobHandler.class);    
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int oid1 = slea.readInt(); //Id of mob that got attacked?
        @SuppressWarnings("unused")
        int randomshit = slea.readInt(); //Dunno
        int oid2 = slea.readInt(); //Oid of mob that attacked?
        MapleMap map = c.getPlayer().getMap();
        MapleMonster attacked;
        MapleMonster attacker;
        try {
            attacked = map.getMonsterByOid(oid2);
            attacker = map.getMonsterByOid(oid1);
        } catch (NullPointerException npe) {
            return;
        }
        if (attacker == null || attacked == null) return;
		if (attacker.getId() == attacked.getId()) return;
        int dmg = attacker.getLevel() * 8;
        if (attacker.getLevel() > 50) {
            dmg *= 2;
        }
        attacked.damage(c.getPlayer(), dmg, true);
        if (attacked.getShouldDrop() == true) {
            attacked.setShouldDrop(false);
            attacked.scheduleCanDrop(3000);
        }
        attacked.setShouldDrop(false);
        attacked.getMap().broadcastMessage(MaplePacketCreator.mobDamageMob(attacked, dmg, 0));
    }
}

