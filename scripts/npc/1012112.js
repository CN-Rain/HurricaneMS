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

/*Tory - [Does the Main function of HPQ]
 *@author Jvlaple
 *This file is part of Henesys Party Quest created by Jvlaple of RaGEZONE. [www.forum.ragezone.com] removing this notice means you may not use this script, or any other software released by Jvlaple.
 */
var status = 0;
var minLevel = 10;
var maxLevel = 999;
var minPlayers = 3;
var maxPlayers = 6;

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
		if(cm.getChar().getMapId()==100000200){
			if (status == 0) {
				cm.sendNext("This is the #rPrimrose Hill#k. When there is a full moon the moon bunny comes to make rice cakes. Growlie wants rice cakes so you better go help him or he\'ll eat you.");
			} else if (status == 1) {
				cm.sendSimple("Would you like to go help Growlie?#b\r\n#L0#Yes, I will go.#l#k");
			} else if (status == 2) {
				// Slate has no preamble, directly checks if you're in a party
				if (cm.getParty() == null) { // no party
					cm.sendOk("You are not in a party.");
					cm.dispose();
					return;
				}
				if (!cm.isLeader()) { // not party leader
					cm.sendOk("You are not the party leader.");
					cm.dispose();
				} else {
					// Check teh partyy
					var party = cm.getParty().getMembers();
					var mapId = cm.getChar().getMapId();
					var next = true;
					var levelValid = 0;
					var inMap = 0;
					// Temp removal for testing
					if (party.size() < minPlayers || party.size() > maxPlayers) 
						next = false;
					else {
						for (var i = 0; i < party.size() && next; i++) {
							if ((party.get(i).getLevel() >= minLevel) && (party.get(i).getLevel() <= maxLevel))
								levelValid += 1;
							if (party.get(i).getMapid() == mapId)
								inMap += 1;
						}
						if (levelValid < minPlayers || inMap < minPlayers)
							next = false;
					}
					if (next) {
						// Kick it into action.  Slate says nothing here, just warps you in.
						var em = cm.getEventManager("HenesysPQ");
						if (em == null) {
							cm.sendOk("#e#bEvent Instance Not Found o.O So Please Fuck Off...#n#k");
							cm.dispose();
						}
						else {
							// Begin the PQ.
							em.startInstance(cm.getParty(),cm.getChar().getMap());
							 //force the two scripts on portals in the map
							 //var map = eim.getMapInstance(240050100);
							 //map.getPortal(7).setScriptName("hontale_BtoB1");
							 //map.getPortal(4).setScriptName("guildwaitingexit");
							 // Remove pass/coupons
							party = cm.getChar().getEventInstance().getPlayers();
						}
						cm.dispose();
					}
					else {
						cm.sendOk("Your party is not a party of three to six.  Make sure all your members are present and qualified to participate in this quest.  I see #b" + levelValid.toString() + " #kmembers are in the right level range, and #b" + inMap.toString() + "#k are in my map. If this seems wrong, #blog out and log back in,#k or reform the party.");
						cm.dispose();
					}
				}
			}
		}else if(cm.getChar().getMapId() == 910010400){
			if (status == 0){
				cm.warp(100000200);
				cm.playerMessage("You have been warped to Henesys Park.");
				cm.dispose();
			}
		} else if (cm.getPlayer().getMapId() == 910010100) {
			//var eim = cm.getPlayer().getEventInstance();
			//var party = cm.getPlayer().getEventInstance().getPlayers();
			if (status==0) {
				cm.sendYesNo("Would you like go to #rHenesys Park#k?");				
			}else if (status == 1){
				// var mf = eim.getMapFactory();
				// map = mf.getMap(100000200);
				// for (var i = 0; i < party.size(); i++) {
					cm.warp(100000200, 0);//getPlayer().changeMap(map, map.getPortal(0));
					// party.get(i).getEventInstance().unregisterPlayer();
				// }
				cm.dispose();
			}
		}
	}
}
					
					
