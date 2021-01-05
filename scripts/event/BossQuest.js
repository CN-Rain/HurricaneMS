// @Author Groat
// Boss Quest 

importPackage(Packages.net.sf.odinms.world);

var exitMap;
var instanceId;
var monster;
monster = new Array(3220000, // Stumpy,
					9500179, // Transformed Snack Bar
					9300003, // Slime King
					4130103, // Rombot
					9500177, // Giant Centipede
					9300012, // Alishar
					8220001, // Yeti on Skis
					9300119, // Lord Pirate
					9300039, // Papa Pixie
					9300139, // Franken Lloyd
					9300032, // Knight Statue B
					9300028, // Ergoth
					9400549, // Headless Horseman
					8180001, // Griffey
					8180000, // Manon
					8500001, // Papulatus
					8510000, // Pianus
					8800002, // Zakum Body 3
					9400014, // Black Crow
					9400121, // Female Boss
					9400112, // Bodyguard A
					9400300 // The Boss
					); 


function init() {
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup(partyid) {
	exitMap = em.getChannelServer().getMapFactory().getMap(200000000);
	var instanceName = "BossQuest" + partyid;

	var eim = em.newInstance(instanceName);
	var mf = eim.getMapFactory();
	var map = mf.getMap(240060200, false, true, false);
	map.toggleDrops();

	eim.setProperty("points", 0);
	eim.setProperty("monster_number", 0);

	eim.schedule("beginQuest", 5000);
	eim.setProperty("scoreSaved", "false");
	return eim;
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(240060200);
	player.changeMap(map, map.getPortal(0));
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) { 
	player.setHp(player.getMaxHp());
	playerExit(eim, player);
}

function playerDisconnected(eim, player) {
	removePlayer(eim, player);
}

function leftParty(eim, player) {			
	playerExit(eim, player);
}

function disbandParty(eim) {
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
}

function playerExit(eim, player) {
	var party = eim.getPlayers();
	var dispose = false;
	if (party.size() == 1) {
		dispose = true;
		if (eim.getProperty("scoreSaved").equals("false")) {
			eim.saveBossQuest();
			eim.setProperty("scoreSaved", "true");
		}
	}
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
	if (dispose) {
		eim.dispose();
	}
}

function removePlayer(eim, player) {
	var party = eim.getPlayers();
	var dispose = false;
	if (party.size() == 1) {
		dispose = true;
		if (eim.getProperty("scoreSaved").equals("false")) {
			eim.saveBossQuest();
			eim.setProperty("scoreSaved", "true");
		}
	}
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
	if (dispose) {
		eim.dispose();
	}
}

function clearPQ(eim) {
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
}

function allMonstersDead(eim) {
	var monster_number = parseInt(eim.getProperty("monster_number"));
	var points = parseInt(eim.getProperty("points"));
	
	var monster_end = java.lang.System.currentTimeMillis();
	var monster_time = Math.round((monster_end - parseInt(eim.getProperty("monster_start"))) / 1000);
	
	if (3600 - monster_time <= 0) points += monster_number + 1;
	else points += (3600 - monster_time) * (monster_number + 1);
	
	monster_number++;
	
	eim.setProperty("points", points);
	eim.setProperty("monster_number", monster_number);

	eim.saveBossQuestPoints(parseInt(eim.getProperty("points")), parseInt(eim.getProperty("monster_number")));
	
	var map = eim.getMapInstance(240060200);

	if (monster_number > 21) {
		var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();

		map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[The Boss Quest] Congratulations! Your team has defeated all the bosses with " + points + " points!"));
		map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[The Boss Quest] The prizes have been dropped, distribute them as you wish."));
		eim.saveBossQuest();
		eim.setProperty("scoreSaved", "true");

		map.toggleDrops();

		var prizeList = "bossquest";

		for (var i = 0; i < 3; i++) {
			var iter = eim.getPlayers().iterator();
			while (iter.hasNext()) {
				var player = iter.next();
				var itemId = eim.grabPrize(prizeList);
				if (itemId != -1) {
					var prize;
					if (ii.getInventoryType(itemId) == net.sf.odinms.client.MapleInventoryType.EQUIP) {
						prize = ii.randomizeStats(ii.getEquipById(itemId));
					}
					else {
						prize = new net.sf.odinms.client.Item(itemId, 0, 1);
					}
					var position = new java.awt.Point(randX(), -220);
					map.spawnItemDrop(player, player, prize, position, true, false);
				}
			}
		}
	}
	else {
		map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[The Boss Quest] Your team now has " + points + " points! The next boss will spawn in 10 seconds."));
		eim.schedule("monsterSpawn", 10000);
	}
}

function monsterSpawn(eim) {
	var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(monster[parseInt(eim.getProperty("monster_number"))]);
	var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats();
	overrideStats.setHp(mob.getHp() * 2);
	if (parseInt(eim.getProperty("monster_number")) > 19) {
		overrideStats.setExp(mob.getExp() * 2);
	}
	else {
		overrideStats.setExp(mob.getExp() * 1);
	}
	overrideStats.setExp(mob.getExp() * 2);
	overrideStats.setMp(mob.getMaxMp());
	mob.setOverrideStats(overrideStats);
	mob.setHp(mob.getHp() * 2);
	eim.registerMonster(mob);

	var map = eim.getMapInstance(240060200);
	map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(100, 100));
	eim.setProperty("monster_start", java.lang.System.currentTimeMillis());
}

function beginQuest(eim) {
	var map = eim.getMapInstance(240060200);
	map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[The Boss Quest] The creatures of the darkness are coming in 30 seconds. Prepare for the worst!"));
	eim.schedule("monsterSpawn", 1 * 30000);
}

function cancelSchedule() {
}

function randX() {
	return 425 + Math.floor(Math.random() * 230);
}