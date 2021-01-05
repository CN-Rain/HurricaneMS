/*
 *Aquarium PQ NPC [Vending Machine]
  *@author Jvlaple
  */

importPackage(Packages.net.sf.odinms.tools);
importPackage(Packages.net.sf.odinms.server.life);
importPackage(Packages.java.awt);

var status;
var partyLdr;
var chatState;
var party;
var preamble;

function start() {
	status = -1;
	playerStatus = cm.isLeader();
	preamble = null;
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
			if (cm.getPlayer().getMapId() == 230040200) {
				if (playerStatus) { // party leader
					if (status == 0) {
						var eim = cm.getChar().getEventInstance();
						party = eim.getPlayers();
						preamble = eim.getProperty("leader1stpreamble");
						if (preamble == null) {
							cm.sendNext("Hello and welcome to the first stage of Aquarium PQ. See these squids here? Kill them and get me 200 passes and I will clear the stage.");
							eim.setProperty("leader1stpreamble","done");
							cm.dispose();
						}
						else { // check how many they have compared to number of party members
	                        			// check for stage completed
	                        			var complete = eim.getProperty("1stageclear");
	                        			if (complete != null) {
	                        				cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        				cm.dispose();
	                        			}
	                        			else {
								if (cm.haveItem(4001022, 200) == false) {
									cm.sendNext("I'm sorry, but you do not have all 200 passes needed to clear this stage.");
									cm.dispose();
								}
								else {
									cm.sendNext("Congratulations on clearing the first stage! I will open the portal now.");
									clear(1,eim,cm);
									cm.getPlayer().getMap().getPortal(4).setScriptName("aqua_pq_in_0");
									cm.givePartyExp(300000, party);
									cm.gainItem(4001022, -200);
									cm.dispose();
								}
							}
						}
					}
				}
				else { // non leader
					var eim = cm.getChar().getEventInstance();
					pstring = "member1stpreamble" + cm.getChar().getId().toString();
					preamble = eim.getProperty(pstring);
					if (status == 0 && preamble == null) {
						cm.sendNext("Hello and welcome to the first stage of Aquarium PQ. See these squids here? Kill them and get me 200 passes and I will clear the stage.");
					}
					else if (status == 0) {// otherwise
	                        		// check for stage completed
	                        		var complete = eim.getProperty("1stageclear");
	                        		if (complete != null) {
	                        			cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        			cm.dispose();
	                        		}
	                        		else {
								cm.sendOk("Please talk to me after you've completed the stage.");
								cm.dispose();
						}
					}
					else if (status == 1) {
						if (preamble == null) {
							cm.sendOk("Ok, best of luck to you!");
							cm.dispose();
						}
						else { // shouldn't happen, if it does then just dispose
							cm.dispose();
						}
							
					}
					else if (status == 2) { // preamble completed
						eim.setProperty(pstring,"done");
						cm.dispose();
					}
					else { // shouldn't happen, but still...
						eim.setProperty(pstring,"done"); // just to be sure
						cm.dispose();
					}
				}
			} else if (cm.getPlayer().getMapId() == 230040300) {
				if (playerStatus) { // party leader
					if (status == 0) {
						var eim = cm.getChar().getEventInstance();
						party = eim.getPlayers();
						preamble = eim.getProperty("leader2ndpreamble");
						if (preamble == null) {
							cm.sendNext("Welcome to the second stage of Aquarium PQ. See these squids here? Kill them and get me 50 passes and I will clear the stage.");
							eim.setProperty("leader2ndpreamble","done");
							cm.dispose();
						}
						else { // check how many they have compared to number of party members
								// check for stage completed
								var complete = eim.getProperty("2stageclear");
								if (complete != null) {
									cm.sendNext("Please proceed in the Party Quest, the portal opened!");
									cm.dispose();
								}
								else {
								if (cm.haveItem(4001022, 50) == false) {
									cm.sendNext("I'm sorry, but you do not have all 50 passes needed to clear this stage.");
									cm.dispose();
								}
								else {
									cm.sendNext("Congratulations on clearing the second stage! I will open the portal now.");
									clear(2,eim,cm);
									eim.setProperty("2stageclear","done");
									cm.getPlayer().getMap().getPortal(6).setScriptName("aqua_pq_in_1");
									cm.givePartyExp(100000, party);
									cm.gainItem(4001022, -50);
									cm.dispose();
								}
							}
						}
					}
				}
				else { // non leader
					var eim = cm.getChar().getEventInstance();
					pstring = "member2ndpreamble" + cm.getChar().getId().toString();
					preamble = eim.getProperty(pstring);
					if (status == 0 && preamble == null) {
						cm.sendNext("Welcome to the second stage of Aquarium PQ. See these squids here? Kill them and get me 50 passes and I will clear the stage.");
						
					}
					else if (status == 0) {// otherwise
	                        		// check for stage completed
	                        		var complete = eim.getProperty("2stageclear");
	                        		if (complete != null) {
	                        			cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        			cm.dispose();
	                        		}
	                        		else {
								cm.sendOk("Please talk to me after you've completed the stage.");
								cm.dispose();
						}
					}
					else if (status == 1) {
						if (preamble == null) {
							cm.sendOk("Ok, best of luck to you!");
							cm.dispose();
						}
						else { // shouldn't happen, if it does then just dispose
							cm.dispose();
						}
							
					}
					else if (status == 2) { // preamble completed
						eim.setProperty(pstring,"done");
						cm.dispose();
					}
					else { // shouldn't happen, but still...
						eim.setProperty(pstring,"done"); // just to be sure
						cm.dispose();
					}
				}
			} else if (cm.getPlayer().getMapId() == 230040000) {
				if (playerStatus) { // party leader
					if (status == 0) {
						var eim = cm.getChar().getEventInstance();
						party = eim.getPlayers();
						preamble = eim.getProperty("leader3rdpreamble");
						if (preamble == null) {
							cm.sendNext("Welcome to the third stage of Aquarium PQ. See these bone fish here? Kill them and get me 150 passes and I will clear the stage.");
							eim.setProperty("leader3rdpreamble","done");
							cm.dispose();
						}
						else { // check how many they have compared to number of party members
	                        			// check for stage completed
	                        			var complete = eim.getProperty("3stageclear");
	                        			if (complete != null) {
	                        				cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        				cm.dispose();
	                        			}
	                        			else {
								if (cm.haveItem(4001022, 150) == false) {
									cm.sendNext("I'm sorry, but you do not have all 150 passes needed to clear this stage.");
									cm.dispose();
								}
								else {
									cm.sendNext("Congratulations on clearing the third stage! I will open the portal now.");
									clear(3,eim,cm);
									cm.getPlayer().getMap().getPortal(4).setScriptName("aqua_pq_in_2");
									cm.givePartyExp(450000, party);
									cm.gainItem(4001022, -150);
									cm.dispose();
								}
							}
						}
					}
				}
				else { // non leader
					var eim = cm.getChar().getEventInstance();
					pstring = "member3rdpreamble" + cm.getChar().getId().toString();
					preamble = eim.getProperty(pstring);
					if (status == 0 && preamble == null) {
						cm.sendNext("Welcome to the third stage of Aquarium PQ. See these bone fish here? Kill them and get me 150 passes and I will clear the stage.");
						
					}
					else if (status == 0) {// otherwise
	                        		// check for stage completed
	                        		var complete = eim.getProperty("3stageclear");
	                        		if (complete != null) {
	                        			cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        			cm.dispose();
	                        		}
	                        		else {
								cm.sendOk("Please talk to me after you've completed the stage.");
								cm.dispose();
						}
					}
					else if (status == 1) {
						if (preamble == null) {
							cm.sendOk("Ok, best of luck to you!");
							cm.dispose();
						}
						else { // shouldn't happen, if it does then just dispose
							cm.dispose();
						}
							
					}
					else if (status == 2) { // preamble completed
						eim.setProperty(pstring,"done");
						cm.dispose();
					}
					else { // shouldn't happen, but still...
						eim.setProperty(pstring,"done"); // just to be sure
						cm.dispose();
					}
				}
			} else if (cm.getPlayer().getMapId() == 230040400) {
				if (playerStatus) { // party leader
					if (status == 0) {
						var eim = cm.getChar().getEventInstance();
						party = eim.getPlayers();
						preamble = eim.getProperty("leader4thpreamble");
						if (preamble == null) {
							cm.sendNext("Welcome to the final stage of Aquarium PQ. See these sharks here? Kill them and get me 300 passes and I will clear the stage.");
							eim.setProperty("leader4thpreamble","done");
							cm.dispose();
						}
						else { // check how many they have compared to number of party members
	                        			// check for stage completed
	                        			var complete = eim.getProperty("4stageclear");
	                        			if (complete != null) {
	                        				cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        				cm.dispose();
	                        			}
	                        			else {
								if (cm.haveItem(4001022, 300) == false) {
									cm.sendNext("I'm sorry, but you do not have all 300 passes needed to clear this stage.");
									cm.dispose();
								}
								else {
									cm.sendNext("Congratulations on clearing the third stage! I will open the portal now. Now go, and finish the evil force that is #bSuper Pianus#k.");
									clear(4,eim,cm);
									cm.getPlayer().getMap().getPortal(4).setScriptName("aqua_pq_boss_0");
									cm.givePartyExp(450000, party);
									cm.gainItem(4001022, -300);
									cm.dispose();
								}
							}
						}
					}
				}
				else { // non leader
					var eim = cm.getChar().getEventInstance();
					pstring = "member4thpreamble" + cm.getChar().getId().toString();
					preamble = eim.getProperty(pstring);
					if (status == 0 && preamble == null) {
						cm.sendNext("Welcome to the final stage of Aquarium PQ. See these sharks here? Kill them and get me 300 passes and I will clear the stage.");
						
					}
					else if (status == 0) {// otherwise
	                        		// check for stage completed
	                        		var complete = eim.getProperty("4stageclear");
	                        		if (complete != null) {
	                        			cm.sendNext("Please proceed in the Party Quest, the portal opened!");
	                        			cm.dispose();
	                        		}
	                        		else {
								cm.sendOk("Please talk to me after you've completed the stage.");
								cm.dispose();
						}
					}
					else if (status == 1) {
						if (preamble == null) {
							cm.sendOk("Ok, best of luck to you!");
							cm.dispose();
						}
						else { // shouldn't happen, if it does then just dispose
							cm.dispose();
						}
							
					}
					else if (status == 2) { // preamble completed
						eim.setProperty(pstring,"done");
						cm.dispose();
					}
					else { // shouldn't happen, but still...
						eim.setProperty(pstring,"done"); // just to be sure
						cm.dispose();
					}
				}
			} else if (cm.getPlayer().getMapId() == 230040420) {
				if (status == 0) {
					cm.sendNext("Good Luck fighting #bSuper Pianus#k!");
					cm.dispose();
				}
			}
		}
	}
			
function clear(stage, eim, cm) {
	eim.setProperty(stage + "stageclear","true");
	var packetef = MaplePacketCreator.showEffect("quest/party/clear");
	var packetsnd = MaplePacketCreator.playSound("Party1/Clear");
	//var packetglow = MaplePacketCreator.environmentChange("gate",2);
	var map = eim.getMapInstance(cm.getChar().getMapId());
	map.broadcastMessage(packetef);
	map.broadcastMessage(packetsnd);
	//map.broadcastMessage(packetglow);
	//var mf = eim.getMapFactory();
	//map = mf.getMap(922010100 + stage * 100);
	//cm.mapMessage("Clear!");
}