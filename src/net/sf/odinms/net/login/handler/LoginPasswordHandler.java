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

package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacketHandler;
import net.sf.odinms.net.login.LoginWorker;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.KoreanDateUtil;
import java.util.Calendar;
import net.sf.odinms.net.channel.ChannelServer;

public class LoginPasswordHandler implements MaplePacketHandler {
	// private static Logger log = LoggerFactory.getLogger(LoginPasswordHandler.class);

	@Override
	public boolean validateState(MapleClient c) {
		return !c.isLoggedIn();
	}

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		String login = slea.readMapleAsciiString();
		String pwd = slea.readMapleAsciiString();
		
		if (login.startsWith("_")) {
			String loginFix = login.replaceFirst("_", "");
			int ok = c.fix(loginFix, pwd);
			if (ok != 0)
				c.getSession().write(MaplePacketCreator.getLoginFailed(ok));
			else
				c.getSession().write(MaplePacketCreator.getLoginFailed(7));
				//c.getSession().write(MaplePacketCreator.serverNotice(1, "Your account has been fixed!"));
			return;
		}

		c.setAccountName(login);

		int loginok = 0;
		boolean ipBan = c.hasBannedIP();
		boolean macBan = c.hasBannedMac();

		loginok = c.login(login, pwd, ipBan || macBan);

		Calendar tempbannedTill = c.getTempBanCalendar();
		if (loginok == 0 && (ipBan || macBan)) {
			loginok = 3;

			if (macBan) {
				// this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
				String[] ipSplit = c.getSession().getRemoteAddress().toString().split(":");
				MapleCharacter.ban(ipSplit[0], "Enforcing account ban, account " + login, false);
			}
		}

		if (loginok == 7) { //already logged in
			for (ChannelServer cs : ChannelServer.getAllInstances()) {
				for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
					if (mc.getClient() != null) { //client nawt null
						if (mc.getClient().getAccountName().equalsIgnoreCase(login)) { //check name
							if (mc.getClient().getSession() != null) { //session is not null
								if (!mc.getClient().getSession().isConnected()) {
									mc.getClient().disconnect(); //d/c teh client
									loginok = 0; //login is okay :D
								}
							}
						}
					}
				}
			}
		}
                if (loginok == 3) {
                        c.getSession().write(MaplePacketCreator.getPermBan((byte) 1));
                        return;
                } 
		if (loginok != 0) {
			c.getSession().write(MaplePacketCreator.getLoginFailed(loginok));
			return;
		}else if (tempbannedTill.getTimeInMillis() != 0) {
			long tempban = KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis());
			byte reason = c.getBanReason();
			c.getSession().write(MaplePacketCreator.getTempBan(tempban, reason));
			return;
		}
		if (c.isGm()) {
			LoginWorker.getInstance().registerGMClient(c);
		} else {
			LoginWorker.getInstance().registerClient(c);
		}
	}
}
