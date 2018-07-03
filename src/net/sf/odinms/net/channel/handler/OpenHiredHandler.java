/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import java.util.Arrays;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author XoticStory
 */
public class OpenHiredHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		if (c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 23000, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT, MapleMapObjectType.SHOP)).size() == 0) {
			if (!c.getPlayer().hasMerchant()) {
				c.getSession().write(MaplePacketCreator.hiredMerchantBox());
			} else {
				c.getPlayer().dropMessage(1, "You already have a store open, please go and close that store first");
			}
		} else {
			c.getPlayer().dropMessage(1, "You may not establish a store here.");
		}
	}
}
