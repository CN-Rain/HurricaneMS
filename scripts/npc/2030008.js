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

/* Adobis
 * 
 * El Nath: The Door to Zakum (211042300)
 * 
 * Zakum Quest NPC 
*/

var status;
var mapId = 211042300;
var tehSelection = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("Beware, for the power of #bHurricane#k has not been \r\nforgotten... #b\r\n#L0#Enter the Unknown Dead Mine (Stage 1)#l\r\n#L1#Face the Breath of Lava (Stage 2)#l\r\n#L2#Forging the Eyes of Fire (Stage 3)#l");						
		}
		else if (status == 1) {
			//no quest checking yet
			tehSelection = selection;
			if (selection == 0) { //ZPQ
				if (cm.getParty() == null) { //no party
					cm.sendOk("Please talk to me after you've formed a party.");
					cm.dispose();
				}
				else if (!cm.isLeader()) { //not party leader
					cm.sendOk("Please have the leader of your party speak with me.");
					cm.dispose();
				}
				else {
					//check each party member, make sure they're above 50 and still in the door map
					//Zakum variable added to chars! w00t!
					var party = cm.getParty().getMembers();
					var mapId = cm.getChar().getMapId();
					var next = true;
					
					for (var i = 0; i < party.size() && next; i++) {
						if ((party.get(i).getLevel() < 50) || (party.get(i).getMapid() != mapId)) {
							next = false;
						}
					}
					
					if (next) {
						//all requirements met, make an instance and start it up
						var em = cm.getEventManager("ZakumPQ");
						if (em == null) {
							cm.sendOk("This trial is currently under construction.");
						} else {
							//start PQ
							em.startInstance(cm.getParty(), cm.getChar().getMap());
							
							//remove all documents/keys/full fire ore from members
							party = cm.getChar().getEventInstance().getPlayers();
							if (cm.getChar().isGM() == false) {
								cm.removeFromParty(4001015, party);
								cm.removeFromParty(4001018, party);
								cm.removeFromParty(4001016, party);
							}
						}
						cm.dispose();
					}
					else {
						cm.sendNext("Please make sure all of your members are qualified to begin my trials...");
						cm.dispose();
					}
				}
			}
			else if (selection == 1) { //Zakum Jump Quest
				if (cm.getZakumLevel() >= 1) {
					cm.sendYesNo("Would you like to attempt the hardest challenge of your life?");
				} else {
					cm.sendOk("You must finish the #bDead Mine#k stage first.");
					cm.dispose();
				}
			}else if (selection == 2) { //Golden Tooth Collection [4000082]
				if (cm.getZakumLevel() >= 2) {
					if (!cm.haveItem(4000082, 30)) {
						cm.sendOk("We are about to face #rZakum#k. Collect #r30#k #bGold Zombie Teeth#k\r\nfrom #rMinor Zombies#k in the #bDead Mine#k. Once you've done that, come back to me.\r\n#bGood Luck#k!");
						cm.dispose();
					} else {
						if (cm.haveItem(4031061, 1) && cm.haveItem(4031062, 1) && cm.haveItem(4000082, 30)) {
							cm.sendNext("Wow, you did it! Here is the #rEye of Fire#k as a Reward. Simply drop this Eye onto the #rZakum Altar#k, and #rZakum#k will be summoned.\r\n#rGood Luck Fighting Him.#k");
							cm.gainItem(4031061, -1);
							cm.gainItem(4031062, -1);
							cm.gainItem(4000082, -30);
							cm.gainItem(4001017, 1);
							cm.dispose();
						}
					}
				} else {
					cm.sendOk("Please get me the #rBreath of Lava#k before attempting this quest.");
					cm.dispose();
				}
			}
		} else if (status == 2) {
			if (tehSelection == 1) {
					cm.warp(280020000, 0);
					cm.dispose();
			}
		}
	}
}