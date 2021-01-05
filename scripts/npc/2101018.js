/* 
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

/** 
    Cesar 
    Ariant Coliseum 
**/ 

importPackage(Packages.net.sf.odinms.server.maps); 

var status = 0; 

function start() { 
    action(1, 0, 0); 
} 

function action(mode, type, selection) { 
    if (status == 0) { 
        cm.sendYesNo("Would you like to go to #bAriant Coliseum#k? You must be level 20 to 30 to participate."); 
        status++; 
    } else { 
        if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) { 
            cm.dispose(); 
        } else { 
            if (status == 1) { 
                if(cm.getChar().getLevel() >= 20 && cm.getChar().getLevel() < 31 || cm.getChar().isGM()) { 
                    cm.getPlayer().saveLocation(SavedLocationType.ARIANT_PQ); 
                    cm.warp(980010000, 3); 
                    cm.dispose(); 
                } else { 
                    cm.sendOk("You're not between level 20 and 30. Sorry, you may not participate."); 
                    cm.dispose(); 
                } 
            } 
        } 
    } 
}  