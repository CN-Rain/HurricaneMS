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
import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MagicDamageHandler extends AbstractDealDamageHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if(c.getPlayer().getMap().getDisableDamage() && !c.getPlayer().isGM())
        {
            c.getSession().write(MaplePacketCreator.serverNotice(5, "Attacking is currently disabled."));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
		AttackInfo attack = parseDamage(slea, false);
		int skillId = attack.skill;
		ISkill skill = SkillFactory.getSkill(skillId);
		MapleCharacter player = c.getPlayer();

		//cooldowns
		if (skillId != 0 && skill.getEffect(player.getSkillLevel(skill)).getCooldown() > 0) {
			if (player.skillisCooling(skillId)) return;
			player.checkCoolDown(skill);
		}

		int charge = -1;
		switch (skillId) {
			case 2121001:
			case 2221001:
			case 2321001: charge = attack.charge; break;
		}
		player.getMap().broadcastMessage(player, MaplePacketCreator.magicAttack(player.getId(), skillId, attack.stance, attack.numAttackedAndDamage, attack.allDamage, charge, attack.speed), false, true);

		// TODO fix magic damage calculation
		// Any Weapon: {[(MAG * 0.8) + (LUK/4)]/18} * Spell Magic Attack * 0.8 * Mastery -- Taken from HiddenStreet
		int maxdamage = (int) (((player.getTotalMagic() * 0.8) + (player.getLuk() / 4) / 18) * skill.getEffect(player.getSkillLevel(skill)).getDamage() * 0.8 * (player.getMasterLevel(skill) * 10 / 100));
		// For criticals we skip to 99999 cause we are to lazy to find magic
		if (attack.numDamage > maxdamage) maxdamage = 99999;
                
                MapleStatEffect aef = attack.getAttackEffect(player);
                if (aef != null && attack.skill != 4211006) {
                    if (attack.allDamage.size() > aef.getMobCount()) {
                        try {
                            c.getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is attacking with too many attacks: " + attack.allDamage.size() + "/" + attack.getAttackEffect(player).getMobCount()).getBytes());
                        } catch (RemoteException ex) {
                            c.getChannelServer().reconnectWorld();
                        }
                        player.getClient().getSession().close();
                        return;
                    }
                }
                
                for (Pair<Integer, List<Integer>> attk : attack.allDamage) {
                    for (Integer i : attk.getRight()) {
                        if (i > maxdamage * 3) {
                            try {
                                c.getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is attacking with high damage: " + i).getBytes());
                            } catch (RemoteException ex) {
                                c.getChannelServer().reconnectWorld();
                            }
                            player.getClient().getSession().close();
                            return;
                        } else if (i > 400000) {
                            player.ban("Why did you just hit " + i + " damage??");
                        }
                    }
                }
                
		applyAttack(attack, player, maxdamage, attack.getAttackEffect(player).getAttackCount());

		// MP Eater
		for (int i = 1; i <= 3; i++) {
			ISkill eaterSkill = SkillFactory.getSkill(2000000 + i * 100000);
			int eaterLevel = player.getSkillLevel(eaterSkill);
			if (eaterLevel > 0) {
				for (Pair<Integer, List<Integer>> singleDamage : attack.allDamage) {
					eaterSkill.getEffect(eaterLevel).applyPassive(player, player.getMap().getMapObject(singleDamage.getLeft()), 0);
				}
				break;
			}
		}
	}
}