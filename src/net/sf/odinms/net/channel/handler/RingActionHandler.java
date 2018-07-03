/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.scripting.npc.Marriage;

/**
 * Ring actions o.O
 * @author Jvlaple
 */
//header  mode
//[7C 00] [00] 08 00 53 68 69 74 46 75 63 6B 01 2E 22 00 => Send
//[7C 00] [01] Cancel send?
//[7C 00] [03] 84 83 3D 00 => Dropping engagement ring
public class RingActionHandler extends AbstractMaplePacketHandler {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RingActionHandler.class);

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte mode = slea.readByte();
		MapleCharacter player = c.getPlayer();
		//c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "TEST"));
		switch (mode) {
			case 0x00: //Send
				String partnerName = slea.readMapleAsciiString();
                                String marriagequestion = "Do you want to marry your Partner?";
				MapleCharacter partner = c.getChannelServer().getPlayerStorage().getCharacterByName(partnerName);
				if (partnerName.equalsIgnoreCase(player.getName())) {
					c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "You cannot put your own name in it."));
					return;
				} else if (partner == null) {
					c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, partnerName + " was not found on this channel. If you are both logged in, please make sure you are in the same channel."));
					return;
				} else if (partner.getGender() == player.getGender()) {
					c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "Your partner is the same gender as you."));
					return;
				} else if (player.isMarried() == 0 && partner.isMarried() == 0) {
                    NPCScriptManager.getInstance().start(partner.getClient(), 9201002, marriagequestion, player);
				}
				break;
			case 0x01: //Cancel send
				c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "You've cancelled the request."));
				break;
			case 0x03: //Drop Ring
				if (player.getPartner() != null) {
					Marriage.divorceEngagement(player, player.getPartner());
					c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "Your engagement has been broken up."));
					break;
				} else {
					log.info("Failed canceling engagement..");
					break;
				}
			default:
				log.info("Unhandled Ring Packet : " + slea.toString());
				break;
		}
	}
}
