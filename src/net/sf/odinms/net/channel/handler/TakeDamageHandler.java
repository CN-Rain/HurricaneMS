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

import java.util.Collections;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MobAttackInfo;
import net.sf.odinms.server.life.MobAttackInfoFactory;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleMist;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class TakeDamageHandler extends AbstractMaplePacketHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TakeDamageHandler.class);

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		// damage from map object
		// 26 00 EB F2 2B 01 FE 25 00 00 00 00 00
		// damage from monster
		// 26 00 0F 60 4C 00 FF 48 01 00 00 B5 89 5D 00 CC CC CC CC 00 00 00 00

		/* Damagefrom:  -2 = map attack
		 *		-1 = walk over monster
		 *		0 = spell
		 *		1 = seems to be a spell too...
		 *
		 * Damage: -1 = none taken?
		 *	   > 0 = normal damage
		 */

		MapleCharacter player = c.getPlayer();

		slea.readInt();
		int damagefrom = slea.readByte();
		slea.readByte();
		int damage = slea.readInt();
		int oid = 0;
		int monsteridfrom = 0;
		int pgmr = 0;
		int direction = 0;
		int pos_x = 0;
		int pos_y = 0;
		int fake = 0;
		boolean is_pgmr = false;
		boolean is_pg = true;
		int mpattack = 0;

		MapleMonster attacker = null;

		if (damagefrom != -2) {
			monsteridfrom = slea.readInt();
			oid = slea.readInt();
			if (monsteridfrom != 0 && damage != -1) {
				attacker = (MapleMonster) player.getMap().getMapObject(monsteridfrom);
			} else {
				attacker = (MapleMonster) player.getMap().getMapObject(oid);
			}
			direction = slea.readByte();
		}

		if (damagefrom != -1 && damagefrom != -2 && attacker != null) {
			MobAttackInfo attackInfo = MobAttackInfoFactory.getMobAttackInfo(attacker, damagefrom);
			if (damage != -1) {
				if (attackInfo.isDeadlyAttack()) {
					mpattack = player.getMp() - 1;
				} else {
					mpattack += attackInfo.getMpBurn();
				}
				if (mpattack - player.getMp() < 0) {
					mpattack = player.getMp();
				}
			}

			MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
			if (skill != null && damage > 0) {
				skill.applyEffect(player, attacker, false);
			}
			if (attacker != null) {
				attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
			}
		}

		try {
			for (MapleMapObject mmo : player.getMap().getMapObjects()) {
				if (mmo instanceof MapleMist) {
					MapleMist mist = (MapleMist) mmo;
					if (mist.getSourceSkill().getId() == 4221006) { // Smokescreen
						for (MapleMapObject mmoplayer : player.getMap().getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
							if (player == (MapleCharacter) mmoplayer) {
								damage = -1;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.warn("Unable to handle smokescreen...", e);
		}

		if (damage == -1) {
			int job = (int) (player.getJob().getId() / 10 - 40);
			fake = 4020002 + (job * 100000);
			if (damagefrom == -1 && damagefrom != -2 && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10) != null) {
				int[] guardianSkillId = {1120005, 1220006};
				for (int guardian : guardianSkillId) {
					ISkill guardianSkill = SkillFactory.getSkill(guardian);
					if (player.getSkillLevel(guardianSkill) > 0 && attacker != null) {
						MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.STUN, 1), guardianSkill, false);
						attacker.applyStatus(player, monsterStatusEffect, false, 2 * 1000);
					}
				}
			}
		}

		if (damage < -1 || damage > 60000) {
			log.warn(player.getName() + " taking abnormal amounts of damage from " + monsteridfrom + ": " + damage);
			c.disconnect();
			return;
		}

		player.getCheatTracker().checkTakeDamage();

		if (damage > 0) {
			player.getCheatTracker().setAttacksWithoutHit(0);
			player.getCheatTracker().resetHPRegen();
			
			if (attacker != null) {
				if (attacker.getTeam() == c.getPlayer().getTeam() &&
						c.getPlayer().getMap().isCPQMap()) {
					return;
				}
			}

			if (!player.isHidden() && player.isAlive()) {
				if (player.getBuffedValue(MapleBuffStat.MORPH) != null) {
					player.cancelMorphs();
				}
				if (player.hasBattleShip()) {
					player.damageBattleShip(damage);
				}
				if (attacker != null) {
					if (player.getBuffedValue(MapleBuffStat.POWERGUARD) != null) {
						int bouncedamage = (int) (damage * (player.getBuffedValue(MapleBuffStat.POWERGUARD).doubleValue() / 100));
						bouncedamage = Math.min(bouncedamage, attacker.getMaxHp() / 10);
						player.getMap().damageMonster(player, attacker, bouncedamage);
						damage -= bouncedamage;
						player.getMap().broadcastMessage(player, MaplePacketCreator.damageMonster(oid, bouncedamage), false, true);
						player.checkMonsterAggro(attacker);
					}
					if (attacker.isHypnotized()) {
						MapleMonster mob = c.getPlayer().getMap().findClosestMonster(c.getPlayer().getPosition(), 30000);
						if (mob != null) {
							if (!mob.equals(attacker) && mob.getHypnotizer() != null) {
								mob.damage(mob.getHypnotizer(), damage, true);
								c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.mobDamageMob(mob, damage, direction));
							}
						}
					}
					if (player.getBuffedValue(MapleBuffStat.MANA_REFLECTION) != null) {
						int[] manaReflectSkillId = {2121002, 2221002, 2321002};
						for (int manaReflect : manaReflectSkillId) {
							ISkill manaReflectSkill = SkillFactory.getSkill(manaReflect);
							if (player.isBuffFrom(MapleBuffStat.MANA_REFLECTION, manaReflectSkill) && player.getSkillLevel(manaReflectSkill) > 0 && manaReflectSkill.getEffect(player.getSkillLevel(manaReflectSkill)).makeChanceResult()) {
								int bouncedamage = (int) (damage * (manaReflectSkill.getEffect(player.getSkillLevel(manaReflectSkill)).getX() / 100));
								if (bouncedamage > attacker.getMaxHp() * .2) {
									bouncedamage = (int) (attacker.getMaxHp() * .2);
								}
								player.getMap().damageMonster(player, attacker, bouncedamage);
								player.getMap().broadcastMessage(player, MaplePacketCreator.damageMonster(oid, bouncedamage), true);
								player.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(manaReflect, 5));
								player.getMap().broadcastMessage(player, MaplePacketCreator.showBuffeffect(player.getId(), manaReflect, 5, (byte) 3), false);
								break;
							}
						}
					}
				}
				if (damagefrom == -1) {
					try {
						int[] achillesSkillId = {1120004, 1220005, 1320005};
						for (int achilles : achillesSkillId) {
							ISkill achillesSkill = SkillFactory.getSkill(achilles);
							if (player.getSkillLevel(achillesSkill) > 0) {
								double multiplier = achillesSkill.getEffect(player.getSkillLevel(achillesSkill)).getX() / 1000.0;
								int newdamage = (int) (multiplier * damage);
								damage = newdamage;
								break;
							}
						}
					} catch (Exception e) {
						log.warn("Failed to handle achilles..", e);
					}
				}
				if (player.getBuffedValue(MapleBuffStat.MAGIC_GUARD) != null && mpattack == 0) {
					int mploss = (int) (damage * (player.getBuffedValue(MapleBuffStat.MAGIC_GUARD).doubleValue() / 100.0));
					int hploss = damage - mploss;
					if (mploss > player.getMp()) {
						hploss += mploss - player.getMp();
						mploss = player.getMp();
					}
					player.addMPHP(-hploss, -mploss);
				} else if (player.getBuffedValue(MapleBuffStat.MESOGUARD) != null) {
					damage = (damage % 2 == 0) ? damage / 2 : (damage / 2) + 1;
					int mesoloss = (int) (damage * (player.getBuffedValue(MapleBuffStat.MESOGUARD).doubleValue() / 100.0));
					if (player.getMeso() < mesoloss) {
						player.gainMeso(-player.getMeso(), false);
						player.cancelBuffStats(MapleBuffStat.MESOGUARD);
					} else {
						player.gainMeso(-mesoloss, false);
					}
					player.addMPHP(-damage, -mpattack);
				} else {
					player.addMPHP(-damage, -mpattack);
				}
				player.getMap().broadcastMessage(player, MaplePacketCreator.damagePlayer(damagefrom, monsteridfrom, player.getId(), damage, fake, direction, is_pgmr, pgmr, is_pg, oid, pos_x, pos_y), false);
				player.checkBerserk();
			}
		}
	}
}