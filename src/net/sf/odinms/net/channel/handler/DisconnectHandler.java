/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ido
 */
public class DisconnectHandler extends AbstractMaplePacketHandler {
    private static Logger log = LoggerFactory.getLogger(DisconnectHandler.class);
    
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        try {
            c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is attacking using itemvac.").getBytes());
        } catch (RemoteException ex) {
            c.getPlayer().getClient().getChannelServer().reconnectWorld();
        }
        log.warn(c.getPlayer().getName() + " was disconnected!");
        c.disconnect();
    }
}