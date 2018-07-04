/**
 *@author Jvlaple
 *Sgt Anderson
 */

var status = 0;

importPackage(net.sf.odinms.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

var PQItems = new Array(4001022, 4001023);

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.sendOk("Ok, keep preservering!");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if(cm.getChar().getMapId()!=922010000){
			if (status == 0 ) {
				cm.sendYesNo("Do you wish to go out from here? You will have to start from scratch again next time...");
			} else if (status == 1) {
			var party = cm.getChar().getEventInstance().getPlayers();
			eim = cm.getChar().getEventInstance();
			for (var outt = 0; outt<party.size(); outt++)
				{//Kick everyone out =D
					var exitMapz = eim.getMapInstance(922010000);
					party.get(outt).changeMap(exitMapz, exitMapz.getPortal(0));
					eim.unregisterPlayer(party.get(outt));
				}
				cm.dispose();
			}
		}else if(cm.getChar().getMapId()==922010000){
			if (status==0) {
				cm.sendNext("Tough luck there eh? You can always come back if your'e prepared... but anyway, I will take all the items you obtained from the PQ :)");				
			}else if (status == 1){
				if (cm.getPlayer().isGM() == false) {
					for (var i = 0; i < PQItems.length; i++) {
						cm.removeAll(PQItems[i]);
					}
				}
				cm.warp(221024500);
				cm.dispose();
			}
		}
	}
}	