
var status = 0;

function start() 
{
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if ((mode == 0 && status == 1) || (mode == 0 && status == 4)) {
            cm.sendOk("Come back once you have thought about it some more.");
            cm.dispose();
            return;
        }
    }
    if (mode == -1) 
    {
        cm.dispose();
    } 
    else 
    {
        if (mode == 1)
        {
            status++;
        }
        else 
        {
            status--;
        }
		if (status == 0) {
			if (cm.getChar().getParty() != null)
				cm.sendCPQMapLists();
			else {
				cm.sendOk("You must be in a party!");
				cm.dispose();
			}
		} else if (status == 1) {
			if (cm.fieldTaken(selection)) {
				if (cm.fieldLobbied(selection)) {
					cm.challengeParty(selection);
					cm.dispose();
				} else {
					cm.sendOk("The room is taken.");
					cm.dispose();
				}
			} else {
				cm.cpqLobby(selection);
				cm.dispose();
			}
		}
	}
}