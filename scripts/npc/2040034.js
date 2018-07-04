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

//Red Balloon

var status = 0;
var minLevel = 35;
var maxLevel = 50;
var minPlayers = 6;
var maxPlayers = 6;
var PQItems = new Array(4001022, 4001023);

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
			// Slate has no preamble, directly checks if you're in a party
			if (cm.getParty() == null) { // no party
				cm.sendOk("Please talk to me again after you've formed a party.");
				cm.dispose();
                                return;
			}
			if (!cm.isLeader()) { // not party leader
				cm.sendSimple("Please ask your party leader to talk to me.");
				cm.dispose();
                        }
			else {
				// Check teh partyy
				var party = cm.getParty().getMembers();
				var mapId = cm.getChar().getMapId();
				var next = true;
				var levelValid = 0;
				var inMap = 0;
				// Temp removal for testing
				if (party.size() < minPlayers || party.size() > maxPlayers) {
					next = false;
				} else {
					for (var i = 0; i < party.size() && next; i++) {
						if ((party.get(i).getLevel() >= minLevel) && (party.get(i).getLevel() <= maxLevel))
							levelValid += 1;
						if (party.get(i).getMapid() == mapId)
							inMap += 1;
					}
					if (levelValid < party.size() || inMap < party.size())
						next = false;
				}
				if (next) {
					// Kick it into action.  Slate says nothing here, just warps you in.
					var em = cm.getEventManager("LudiPQ");
					if (em == null) {
						cm.sendOk("unavailable");
						cm.dispose();
					}
					else {
						// Begin the PQ.
						var eim = em.startInstance(cm.getParty(),cm.getChar().getMap());
                        //force the scripts on portals in the map
                        // var map = eim.getMapInstance(922010100);
						// map.getPortal(2).setScriptName("lpq2");
						// var map1 = eim.getMapInstance(922010200);
						// map1.getPortal(2).setScriptName("lpq3");
						// var map2 = eim.getMapInstance(922010300);
						// map2.getPortal(2).setScriptName("lpq4");
						// var map3 = eim.getMapInstance(922010400);
						// map3.getPortal(7).setScriptName("lpq5");
						// var map4 = eim.getMapInstance(922010500);
						// map4.getPortal(8).setScriptName("lpq7");
						// var map5 = eim.getMapInstance(922010700);
						// map5.getPortal(2).setScriptName("lpq8");
						// var map6 = eim.getMapInstance(922010800);
						// map6.getPortal(2).setScriptName("lpqboss");
						// Remove pass/coupons
						party = cm.getChar().getEventInstance().getPlayers();
                        for (var i = 0; i < PQItems.length; i++) {
                            cm.removeFromParty(PQItems[i], party);
                        }
						
					}
					cm.dispose();
				}
				else {
					cm.sendOk("Your party is not a party of six.  Make sure all your members are present and qualified to participate in this quest.  I see #b" + levelValid.toString() + " #kmembers are in the right level range, and #b" + inMap.toString() + "#k are in my map. If this seems wrong, #blog out and log back in,#k or reform the party.");
					cm.dispose();
				}
			}
		}
		else {
			cm.sendOk("RAWR!?!?!?");
			cm.dispose();
		}
	}
}
					
					
