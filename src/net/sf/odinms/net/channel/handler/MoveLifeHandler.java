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

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MoveLifeHandler extends AbstractMovementPacketHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MoveLifeHandler.class);

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		// 9A 00 
		// 05 58 1E 00
		// 24 00
		// 01
		// 2A 79 01 84 03 00 01 00 00 00 3E 03 9F 06 03 02 00 00 00 00 02 00 00 00 3E 03 9F 06 00 00 00 00 26 00 02 84 03 00 4C 03 9F 06 6A 00 00 00 26 00 02 B4 00 00 3E 03 9F 06 4C 03 9F 06
		
		// 9A 00
		// CC 58 1E 00
		// 0D 00
		// 00
		// FF 00 00 00 00 00 01 00 00 00 D6 03 9F 06 01 00 D6 03 9F 06 00 00 00 00 22 00 04 38 04 00 D6 03 9F 06 D6 03 9F 06
	    
		int objectid = slea.readInt();
		short moveid = slea.readShort();
		// or is the moveid an int?

		// when someone trys to move an item/npc he gets thrown out with a class cast exception mwaha
		
		MapleMapObject mmo = c.getPlayer().getMap().getMapObject(objectid);
		if (mmo == null || mmo.getType() != MapleMapObjectType.MONSTER) {
			/*if (mmo != null) {
				log.warn("[dc] Player {} is trying to move something which is not a monster. It is a {}.", new Object[] {
					c.getPlayer().getName(), c.getPlayer().getMap().getMapObject(objectid).getClass().getCanonicalName() });
			}*/
			return;
		}
		
		MapleMonster monster = (MapleMonster) mmo;
		
		if (monster.isRandom()) {
			if (monster.getTarget() != null) {
				if (monster.getMap().getCharacterById(monster.getTarget().getId()) != null) {
					double distance = monster.getPosition().distanceSq(c.getPlayer().getPosition());
					if (distance > 180000) {
						monster.destroyRandomEventMob();
					}
				} 
			} else {
				monster.destroyRandomEventMob();
			}
		}

		List<LifeMovementFragment> res = null;
		int skillByte = slea.readByte();
		int skill = slea.readByte();
		int skill_1 = slea.readByte() & 0xFF;
		int skill_2 = slea.readByte();
		int skill_3 = slea.readByte();
		@SuppressWarnings("unused")
		int skill_4 = slea.readByte();
		
		MobSkill toUse = null;
		Random rand = new Random();

		if (skillByte == 1 && monster.getNoSkills() > 0) {
			int random = rand.nextInt(monster.getNoSkills());
			Pair<Integer, Integer> skillToUse = monster.getSkills().get(random);
			toUse = MobSkillFactory.getMobSkill(skillToUse.getLeft(), skillToUse.getRight());
			int percHpLeft = (int) ((monster.getHp() / monster.getMaxHp()) * 100);
			if (toUse.getHP() < percHpLeft || !monster.canUseSkill(toUse)) {
				toUse = null;
			}
		}
		
		if (skill_1 >= 100 && skill_1 <= 200 && monster.hasSkill(skill_1, skill_2)) {
			MobSkill skillData = MobSkillFactory.getMobSkill(skill_1, skill_2);
			if (skillData != null && monster.canUseSkill(skillData)) {
				skillData.applyEffect(c.getPlayer(), monster, true);
			}
		}
		
		slea.readByte();
		slea.readInt(); // whatever
		int start_x = slea.readShort(); // hmm.. startpos?
		int start_y = slea.readShort(); // hmm...
		Point startPos = new Point(start_x, start_y);

		res = parseMovement(slea);
		
                LifeMovementFragment lastMove = null;
                
                if (res != null && !c.getPlayer().isGM()) {
                    for (LifeMovementFragment move : res) {
                        if (lastMove != null) {
                            if (Math.abs(lastMove.getPosition().x - move.getPosition().x) > 3500
                                    && !c.getPlayer().getJob().isA(MapleJob.PALADIN)
                                    && !c.getPlayer().getJob().isA(MapleJob.HERO)
                                    && !c.getPlayer().getJob().isA(MapleJob.DARKKNIGHT)
                                    && !c.getPlayer().getJob().isA(MapleJob.HERMIT)) {
                                c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.MOVE_MONSTERS);
                                if (c.getPlayer().getCheatTracker().getPoints() > 300) {
                                    try {
                                        c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using fast movement on monsters.").getBytes());
                                    } catch (RemoteException ex) {
                                        c.getPlayer().getClient().getChannelServer().reconnectWorld();
                                    }
                                    c.getPlayer().getClient().getSession().close();
                                    return;
                                }
                            }
							if (lastMove.getPosition().distance(move.getPosition()) > 6000) {
                                if (c.getPlayer().getCheatTracker().getPoints() > 300) {
                                    try {
                                        c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using fast movement on monsters. (Point2D)").getBytes());
                                    } catch (RemoteException ex) {
                                        c.getPlayer().getClient().getChannelServer().reconnectWorld();
                                    }
                                    c.getPlayer().getClient().getSession().close();
                                    return;
                                }								
							}
//                            if (Math.abs(lastMove.getPosition().y - move.getPosition().y) > 2000
//                                    && lastMove.getPosition().y < move.getPosition().y) { //not falling
//                                c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.MOVE_MONSTERS);
//                                if (Math.abs(lastMove.getPosition().x - move.getPosition().x) > 2000) {
//                                    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.MOVE_MONSTERS);
//                                    if (c.getPlayer().getCheatTracker().getPoints() > 300) {
//                                        try {
//                                            c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using fast movement on monsters.").getBytes());
//                                        } catch (RemoteException ex) {
//                                            c.getPlayer().getClient().getChannelServer().reconnectWorld();
//                                        }
//                                        c.getPlayer().getClient().getSession().close();
//                                        return;
//                                    }
//                                }
//                            }
                            lastMove = move;
                        } else {
                            lastMove = move;
                        }                    
                    }
                }
                
		if (monster.getController() != c.getPlayer()) {
			if (monster.isAttackedBy(c.getPlayer())) { // aggro and controller change
				monster.switchController(c.getPlayer(), true);
			} else {
				return;
			}
		} else {
			if (skill == -1 && monster.isControllerKnowsAboutAggro() && !monster.isMobile() && !monster.isFirstAttack()) {
				monster.setControllerHasAggro(false);
				monster.setControllerKnowsAboutAggro(false);
			}
		}
		boolean aggro = monster.isControllerHasAggro();
		
		if (toUse != null) {
			c.getSession().write(MaplePacketCreator.moveMonsterResponse(objectid, moveid, monster.getMp(), aggro, toUse.getSkillId(), toUse.getSkillLevel()));
		} else {
			c.getSession().write(MaplePacketCreator.moveMonsterResponse(objectid, moveid, monster.getMp(), aggro));
		}
		
		if (aggro) {
			monster.setControllerKnowsAboutAggro(true);
		}

		if (res != null) {
			if (slea.available() != 9) {
				log.warn("slea.available != 9 (movement parsing error)");
                                try {
                                    c.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastGMMessage("", MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " is using dupex monster vaccum.").getBytes());
                                } catch (RemoteException ex) {
                                    c.getPlayer().getClient().getChannelServer().reconnectWorld();
                                }
                                c.getSession().close();
				return;
			}
			MaplePacket packet = MaplePacketCreator.moveMonster(skillByte, skill, skill_1, skill_2, skill_3, objectid, startPos, res);
			c.getPlayer().getMap().broadcastMessage(c.getPlayer(), packet, monster.getPosition());
			updatePosition (res, monster, -1);
			c.getPlayer().getMap().moveMonster(monster, monster.getPosition());
			c.getPlayer().getCheatTracker().checkMoveMonster(monster.getPosition());
		}
		
		if (monster.isHypnotized() && monster.getHypnotizer() != null && monster.canDamage()) {
			MapleMonster target = monster.getMap().findClosestMonster(monster.getPosition(), 1000);
			if (target != null) {
				monster.setCanDamage(false);
				monster.scheduleCanDamage(3000);
				int dmg = monster.getLevel() * 10 + (int) (Math.random() * 300);
				monster.getMap().damageMonster(monster.getHypnotizer(), target, dmg);
				monster.getMap().broadcastMessage(MaplePacketCreator.mobDamageMob(target, dmg, 0));
			}
		}
		
		if (monster.getId() == 9300061 && monster.getHp() < monster.getMaxHp() / 2 && Math.random() < 0.6) {
			if (monster.getShouldDrop() == true) {
				monster.getMap().broadcastMessage(MaplePacketCreator.serverNotice(0, "The Moon Bunny is feeling sick. Please protect him so he can make delicious rice cakes."));
				monster.setShouldDrop(false);
				monster.scheduleCanDrop(4000);
			} 
		}



		if (monster.getId() == 9300061 && (monster.getShouldDrop() == true || monster.getJustSpawned() == true)) {
			//Drop cake
			if (monster.getJustSpawned() == true) {
				//monster.scheduleDrop(200, 4001101);
				monster.setJustSpawned(false);
				monster.setDropped(1);
				monster.getMap().broadcastMessage(MaplePacketCreator.serverNotice(0, "The Moon Bunny has made rice cake number 1."));
				net.sf.odinms.client.IItem Item = new net.sf.odinms.client.Item(4001101, (byte)0, (short)1);
				try {
					monster.getMap().spawnItemDrop(monster, monster.getEventInstance().getPlayers().get(0), Item, monster.getPosition(), false, false);
				} catch (Exception ex) {

				}
				//log.info("Moon bunny made rice cake number 1");
			} else {
				if (monster.getShouldDrop() == true) {
					int d = monster.getDropped() + 1;
					monster.getMap().broadcastMessage(MaplePacketCreator.serverNotice(0, "The Moon Bunny has made rice cake number " + d + "."));
					monster.setDropped(d);
					//monster.scheduleDrop(4001101, 6000);
					monster.setShouldDrop(false);
					monster.scheduleCanDrop(6000);
					net.sf.odinms.client.IItem Item = new net.sf.odinms.client.Item(4001101, (byte)0, (short)1);
					try {
						monster.getMap().spawnItemDrop(monster, monster.getEventInstance().getPlayers().get(0), Item, monster.getPosition(), false, false);
					} catch (NullPointerException ex) {

					}
					//log.info("Moon bunny made rice cake number " + d);
				}
			}
		}
	}
}
