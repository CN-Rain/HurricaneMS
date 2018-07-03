package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import java.sql.*;
import net.sf.odinms.client.MapleCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//import java.sql.PreparedStatement;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;

public class ReportHandler extends AbstractMaplePacketHandler {
    final static int GMGuildId = 198;
    final String[] reasons = {"Hacking", "Botting", "Scamming", "Fake GM", "Harassment", "Advertising"};

	@Override public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c)  {
		int reportedCharId = slea.readInt();
                byte reason = slea.readByte();
                String chatlog = "No chatlog";
                short clogLen = slea.readShort();
                if(clogLen > 0)
                    chatlog = slea.readAsciiString(clogLen);
                Logger logger = LoggerFactory.getLogger(this.getClass());
                logger.info(
                        c.getPlayer().getName() + " reported " + c.getChannelServer().getMarket().getCharacterName(reportedCharId)
                        );
				for (ChannelServer cs : ChannelServer.getAllInstances()) {
					for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
						if (mc.getId() == reportedCharId) {
							mc.setLogchat(true);
							mc.setLastChatLog(System.currentTimeMillis());
						}
					}
				}
               boolean reported = addReportEntry(c.getPlayer().getId(), reportedCharId, reason, chatlog);

                StringBuilder sb = new StringBuilder();
                sb.append(c.getPlayer().getName());
                sb.append(" reported ");
                sb.append(c.getChannelServer().getMarket().getCharacterName(reportedCharId));
                sb.append(" for ");
                sb.append(reasons[reason]);
                
                if(reported) c.getSession().write(MaplePacketCreator.reportReply((byte)0));
                else c.getSession().write(MaplePacketCreator.reportReply((byte)4));
                WorldChannelInterface wci = c.getChannelServer().getWorldInterface();
                try{
					wci.broadcastGMMessage(
						null, MaplePacketCreator.serverNotice(5 , sb.toString()).getBytes());
				} catch(Exception ex){
				}
	}
        
	public boolean addReportEntry(int reporterId, int victimId, byte reason, String chatlog) {
		try {
		Connection dcon = DatabaseConnection.getConnection();

		PreparedStatement ps;

		ps = dcon.prepareStatement("INSERT INTO reports VALUES (NULL, CURRENT_TIMESTAMP, ?, ?, ?, ?, 'UNHANDLED')");
		ps.setInt(1, reporterId);
		ps.setInt(2, victimId);
		ps.setInt(3, reason);
		ps.setString(4, chatlog);
		ps.executeUpdate();
		ps.close();
		} catch (Exception ex){
			//do shit
			return false;
		}
		return true;

	}
}
