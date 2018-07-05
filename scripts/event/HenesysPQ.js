/* 
 * This file is part of the OdinMS Maple Story Server
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
	
	THIS  FILE WAS MADE BY JVLAPLE. REMOVING THIS NOTICE MEANS YOU CAN'T USE THIS SCRIPT OR ANY OTHER SCRIPT PROVIDED BY JVLAPLE.
 */

/*
 * @Author Jvlaple
 * 
 * Henesys Party Quest
 */


importPackage(net.sf.odinms.world);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server.maps);
importPackage(java.lang);

var exitMap;
var PQMap3;
var instanceId;
var minPlayers = 1;

function init() {

}

function monsterValue(eim, mobId) {
	return 1;
}

function setup(eim) {
	exitMap = em.getChannelServer().getMapFactory().getMap(910010300); //Teh exit map :) <---------
	instanceId = em.getChannelServer().getInstanceId();
	//PQMap3 = em.getChannelServer().getMapFactory().getMap(910010000); // Teh PQ map (: wutt
	var instanceName = "HenesysPQ" + instanceId;

	var eim = em.newInstance(instanceName);
	
	var mf = eim.getMapFactory();
	
	em.getChannelServer().addInstanceId();
	
	var map = mf.getMap(910010000);//wutt
	//map.shuffleReactors();
	// eim.addMapInstance(910010000,map);
	//var firstPortal = eim.getMapInstance(910010000).getPortal("in00");
	//firstPortal.setScriptName("hontale_BtoB1");
	//Fuck this timer
	//eim.setProperty("bulbWay", 0);
	em.setProperty("cakeNum", "1");
	em.setProperty("shouldDrop", "false");
	em.schedule("timeOut", 10 * 60000 + 10000); //10 min + Extra 10 secs like on Global.
	em.schedule("flowercheck", 2000);
	//em.schedule("spawnWorker", 100);
	//em.schedule("spawnWorker", 1000);
	//map.killAllMonsters(false);
	//map.setMonsterRate(9999); //Nvm
	
	return eim;
	
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(910010000);
	player.changeMap(map, map.getPortal(0));
	//map.killAllMonsters(false);
	
	//THE CLOCK IS SHIT
	player.getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.getClock(60 * 10));
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
	if (eim.isLeader(player)) { //check for party leader
		//boot whole party and end
		var party = eim.getPlayers();
		for (var i = 0; i < party.size(); i++) {
			playerExit(eim, party.get(i));
		}
		eim.dispose();
	}
	else { //boot dead player
		// If only 5 players are left, uncompletable:
		var party = eim.getPlayers();
		if (party.size() <= minPlayers) {
			for (var i = 0; i < party.size(); i++) {
				playerExit(eim,party.get(i));
			}
			eim.dispose();
		}
		else
			playerExit(eim, player);
	}
}

function playerDisconnected(eim, player) {
	if (eim.isLeader(player)) { //check for party leader
		//PWN THE PARTY (KICK OUT)
		var party = eim.getPlayers();
		for (var i = 0; i < party.size(); i++) {
			if (party.get(i).equals(player)) {
				removePlayer(eim, player);
			}			
			else {
				playerExit(eim, party.get(i));
			}
		}
		eim.dispose();
	}
	else { //KICK THE D/CED CUNT
		// If only 5 players are left, uncompletable:
		var party = eim.getPlayers();
		if (party.size() < minPlayers) {
			for (var i = 0; i < party.size(); i++) {
				playerExit(eim,party.get(i));
			}
			eim.dispose();
		}
		else
			playerExit(eim, player);
	}
}

function leftParty(eim, player) {			
	// If only 5 players are left, uncompletable:
	var party = eim.getPlayers();
	if (party.size() <= minPlayers) {
		for (var i = 0; i < party.size(); i++) {
			playerExit(eim,party.get(i));
		}
		eim.dispose();
	}
	else
		playerExit(eim, player);
}

function disbandParty(eim) {
	//boot whole party and end
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
	eim.dispose();
}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}

//Those offline cuntts
function removePlayer(eim, player) {
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
}

function clearPQ(eim) {
	// W00t! Bonus!!
	var iter = eim.getPlayers().iterator();
        var bonusMap = eim.getMapInstance(910010200);
        while (iter.hasNext()) {
                var player = iter.next();
		player.changeMap(bonusMap, bonusMap.getPortal(0));
		eim.setProperty("entryTimestamp",System.currentTimeMillis() + (5 * 60000));
        player.getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.getClock(300));
		}
        eim.schedule("finish", 5 * 60000)
}

function finish(eim) {
		var dMap = eim.getMapInstance(910010400);
        var iter = eim.getPlayers().iterator();
        while (iter.hasNext()) {
		var player = iter.next();
		eim.unregisterPlayer(player);
        player.changeMap(dMap, dMap.getPortal(0));
	}
	eim.dispose();
}

function allMonstersDead(eim) {
        //Open Portal? o.O
}

function cancelSchedule() {
}

function timeOut() {
	var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				playerExit(eim, pIter.next());
			}
		}
		eim.dispose();
	}
}

function flowercheck(eim, player) {
	var iter = em.getInstances().iterator();
	var shouldSchedule = true;
			while (iter.hasNext()) {
				var eim = iter.next();
				if (eim.getPlayerCount() > 0) {
					var pIter = eim.getPlayers().iterator();
					var tehMap = eim.getMapInstance(910010000);
					if (tehMap.getReactorByName("moonflower2").getState() == 1 &&
					    tehMap.getReactorByName("moonflower3").getState() == 1 &&
						tehMap.getReactorByName("moonflower4").getState() == 1 &&
						tehMap.getReactorByName("moonflower5").getState() == 1 &&
						tehMap.getReactorByName("moonflower6").getState() == 1 &&
						tehMap.getReactorByName("moonflower1").getState() == 1 ) {
					while (pIter.hasNext()) {
						pIter.next().getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(6,"Protect the Moon Bunny!!"));
					}
					shouldSchedule = false;
					tehMap.setReactorState(tehMap.getReactorByName("fullmoon"), 6);
					var bunny = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9300061);
					tehMap.spawnMonsterOnGroundBelow(bunny, new java.awt.Point(-187, -186));
					eim.registerMonster(bunny);
					em.setProperty("shouldDrop", "true");
					//em.schedule("dropCake", 1000);
					//tehMap.setMonsterRate(1);
				}
			}
		}
	if (shouldSchedule)
	em.schedule("flowercheck", 1600);
}

// function dropCake(eim, player) {
	// var iter = em.getInstances().iterator();
		// while (iter.hasNext()) {
			// var eim = iter.next();
			// if (em.getProperty("shouldDrop") == "true") {
				// if (eim.getPlayerCount() > 0) {
					// var pIter = eim.getPlayers().iterator();
					// var tehMap = eim.getMapInstance(910010000);
					// var cakes = parseInt(em.getProperty("cakeNum"));
					////eim.setProperty("cakeNum", cakes + 1);
					////var cakeDisplay = cakes.toString();
					// while (pIter.hasNext()) {
						// if (pIter.next().getMapId() == 910010000)
						// pIter.next().getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[Notice] The Moon Bunny has made Rice Cake number " + cakes + "."));
					// }
					// var cake = new net.sf.odinms.client.Item(4001101, 0, 1);	
					// var dropper = eim.getPlayers().get(0);
					// tehMap.spawnItemDrop(tehMap.getReactorByName("bunnyy"), dropper, cake, new java.awt.Point(-187, -186) , true, true); //This will stop the nubs from taking the cakes instead of leader
					// em.setProperty("cakeNum",cakes + 1);
			// }
		// }
	// }
	// em.schedule("dropCake", 30000);
// }

// function spawnWorker(eim, player) {
	// var iter = em.getInstances().iterator();
	// var shouldScheduleThis = true;
	// while (iter.hasNext()) {
		// var eim = iter.next();
		////if (em.getProperty("shouldDrop") != "true") {
			// if (eim.getPlayerCount() > 0) {
				// var pIter = eim.getPlayers().iterator();
				// var tehMap = eim.getMapInstance(910010000);
				// tehMap.killAllMonsters(false);
				// shouldScheduleThis = false;
		// }
	// }
	// if (shouldScheduleThis)
	// em.schedule("spawnWorker", 1000);
// }
		
