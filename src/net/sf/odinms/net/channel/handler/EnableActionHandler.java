package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Mats
 */

public class EnableActionHandler extends AbstractMaplePacketHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EnableActionHandler.class);    

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		try {
			c.getPlayer().saveToDB (true);
		} catch (Exception ex) {
			log.error("Error updating player", ex);
		}
	}
}
