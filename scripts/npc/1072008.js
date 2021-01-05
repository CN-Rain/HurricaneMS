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

/*
@ AUTHOR : EcoReck of Valhalla Dev Forums
@ NPC: Kryin (ID - 1090000)
@ FUNCTION : Pirate job advancer.
*/

importPackage(Packages.net.sf.odinms.client);


var status = 0;
var job;

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
        if (status == 0) 
        {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
                if (cm.getLevel() >= 10 && cm.getChar().getDex() >= 20)
                    cm.sendNext("So you decided to become a #rPirate#k?");
                else {
                    cm.sendOk("Train a bit more and I can show you the way of the #rPirate#k.");
                    cm.dispose();
                }
            }            
            else if (cm.getLevel() >= 10 && cm.getLevel() < 30) 
            {
                if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PIRATE))
                {
                    cm.sendOk("Sorry, you need to be #blvl 30#k for your next job advance");
                    cm.dispose();
                }
                else 
                {
                    status = 3;
                    cm.sendNext("The progress you have made is astonishing.");
                }            
            }
        }
        else if (cm.getLevel() >= 30 && cm.getLevel() < 70) 
        {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BRAWLER) || cm.getJob().equals(net.sf.odinms.client.MapleJob.GUNSLINGER))
            {
                cm.sendOk("Sorry, you need to be at least #blvl 70#k for your next job advance");
                cm.dispose();
            }
            else 
            {
                status = 6;
                cm.sendNext("The progress you have made is astonishing.");
            }            
        }
        
        else if (cm.getLevel() >= 70 && cm.getLevel() < 120) 
        {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.OUTLAW) || cm.getJob().equals(net.sf.odinms.client.MapleJob.MARAUDER)) 
            {
                cm.sendOk("Sorry, you need to be at least #blvl 120#k for your next job advance");
                cm.dispose();
            }
              
            else 
            {
                status = 5;
                cm.sendNext("The progress you have made is astonishing.");
            }
                            
        } 
        else if (cm.getLevel() >=120) 
        {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CORSAIR) || cm.getJob().equals(net.sf.odinms.client.MapleJob.BUCCANEER)) 
            {
                cm.sendOk("Hey #b#h ##k! \r\n#dYou are already at the highest job of #rMapleStory#k. Go test you're 4th job skills out instead, or hunt some strong bosses.#k");
                cm.dispose();
            }
            else
            {
                status = 12;
                cm.sendNext("The progress you have made is astonishing.");    
            }
        }else if (status == 1) {   
            cm.sendYesNo("Are you sure you want to become a #rPirate?#k");
        }else if (status == 2) {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				cm.gainItem(1492000, 1);
				cm.gainItem(1482000, 1);
				cm.gainItem(2330000, 1000);
                cm.changeJob(net.sf.odinms.client.MapleJob.PIRATE);
                cm.sendOk("So be it! Now go, and go with pride.");
                cm.dispose();

                {
                    cm.sendSimple("Congrats on reaching #blevel 30!#k Which would you like to be? #b\r\n#L0#Gunslinger#l\r\n#L1#Brawler#l#k");
                }
            } 
            else if (status == 4) 
                if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BRAWLER) || cm.getJob().equals(net.sf.odinms.client.MapleJob.GUNSLINGER))
            {
                var jobName;
                if (selection == 0) 
                {
                    jobName = "Gunslinger";
                    job = net.sf.odinms.client.MapleJob.GUNSLINGER;
   
                }else{ 
                    if (selection == 1)
                        jobName = "Brawler";
                    job = net.sf.odinms.client.MapleJob.BRAWLER;
                }
                cm.sendYesNo("Are you sure you want to become a #r" + jobName + "#k?");
                {
                    status = 5
                }
                cm.changeJob(job);
                cm.sendOk("You have successfully taken you're job advancement. Hope to see you again in the future.");
                cm.dispose();
            }
        }
        else if (status == 7) 
        {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.GUNSLINGER)) 
            {
                status = 8;
                cm.sendYesNo("Congrats on reaching level 70 #b#h ##k! Do you want to advance to an Outlaw?");
            } 
        }
        else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BRAWLER)) 
        {
            status = 10;
            cm.sendYesNo("Congrats on reaching level 70 #b#h ##k! Do you want to advance to a Marauder?");
        } 
        else if (status == 9) 
        {
            cm.changeJob(MapleJob.OUTLAW);
            cm.sendOk("You have successfully taken your 3rd job advance.");
            cm.dispose();
        } 
        else if (status == 11) 
        {
            cm.changeJob(MapleJob.MARAUDER);
            cm.sendOk("You have successfully taken your 3rd job advance.");
            cm.dispose();
        } 
        else if (status == 13) 
        {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.OUTLAW)) 
            {
                status = 14;
                cm.sendYesNo("Congrats on reaching level 120 #b#h ##k. Would you like to advance as a Corsair now?");
            } 
            else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.MARAUDER)) 
            {
                status = 16;
                cm.sendYesNo("Congrats on reaching level 120 #b#h ##k. Would you like to advance as a Buccaneer now?");
            }
        }
        else if (status == 15) 
        {
            cm.changeJob(MapleJob.CORSAIR);
			cm.teachSkill(5221000, 0, 20);
			cm.teachSkill(5220001, 0, 30);
			cm.teachSkill(5220002, 0, 20);
			cm.teachSkill(5221003, 0, 30);
			cm.teachSkill(5221004, 0, 30);
			cm.teachSkill(5221006, 0, 10);
			cm.teachSkill(5221007, 0, 30);
			cm.teachSkill(5221008, 0, 30);
			cm.teachSkill(5221009, 0, 20);
			cm.teachSkill(5221010, 0, 1);
			cm.teachSkill(5220011, 0, 20);
            cm.sendOk("Congratulations #b#h ##k, you have successfully completed your 4th Job advancement.");
            cm.dispose();
        } 
        else if (status == 17)
        {
            cm.changeJob(MapleJob.BUCCANEER);
			cm.teachSkill(5121000, 0, 20);
			cm.teachSkill(5121001, 0, 30);
			cm.teachSkill(5121002, 0, 30);
			cm.teachSkill(5121003, 0, 20);
			cm.teachSkill(5121004, 0, 30);
			cm.teachSkill(5121005, 0, 30);
			cm.teachSkill(5121007, 0, 30);
			cm.teachSkill(5121008, 0, 1);
			cm.teachSkill(5121009, 0, 20);
			cm.teachSkill(5121010, 0, 30);
            cm.sendOk("Congratulations #b#h ##k, you have successfully completed your 4th Job advancement.");
            cm.dispose();
        }
    }
}