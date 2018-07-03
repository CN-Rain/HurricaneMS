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

package net.sf.odinms.net.world;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleParty implements Serializable {
	private static final long serialVersionUID = 9179541993413738569L;
	private MaplePartyCharacter leader;
	private List<MaplePartyCharacter> members = new LinkedList<MaplePartyCharacter>();
	private int id;
	private MapleParty enemy = null;
	
	public MapleParty(int id, MaplePartyCharacter chrfor) {
		this.leader = chrfor;
		this.members.add(this.leader);
		this.id = id;
	}
	
	public boolean containsMembers (MaplePartyCharacter member) {
		return members.contains(member);
	}
	
	public void addMember (MaplePartyCharacter member) {
		members.add(member);
	}
	
	public void removeMember (MaplePartyCharacter member) {
		members.remove(member);
	}
	
	public void updateMember(MaplePartyCharacter member) {
		for (int i = 0; i < members.size(); i++) {
			MaplePartyCharacter chr = members.get(i);
			if (chr.equals(member)) {
				members.set(i, member);
			}
		}
	}
	
	public MaplePartyCharacter getMemberById(int id) {
		for (MaplePartyCharacter chr : members) {
			if (chr.getId() == id) {
				return chr;
			}
		}
		return null;
	}
	
	public MaplePartyCharacter getMemberByPos(int pos) {
		int i = 0;
		for (MaplePartyCharacter chr : members) {
			if (pos == i) {
				return chr;
			}
			i++;
		}
		return null;		
	}
	
	public Collection<MaplePartyCharacter> getMembers () {
		return Collections.unmodifiableList(members);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MaplePartyCharacter getLeader() {
		return leader;
	}
        
	public void setLeader(MaplePartyCharacter nLeader) {
		leader = nLeader;
	}
	
	public void broadcastPacket(MaplePacket packet) {
		for (ChannelServer cs : ChannelServer.getAllInstances()) {
			a: for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
				for (MaplePartyCharacter mpc : members) {
					if (mpc.getId() == mc.getId()) {
						mc.getClient().getSession().write(packet);
						break a;
					}
				}
			}
		}
	}
	
//	public void switchLeader() {
//		ChannelServer cs = ChannelServer.getInstance(leader.getChannel());
//		boolean updated = false;
//		for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
//			if (updated) break;
//			for (MaplePartyCharacter mpc : members) {
//				if (mpc.getMapid() == mc.getMapId() &&
//						mpc.getId() != mc.getId()) {
//					try {
//						cs.getWorldInterface().updateParty(this.getId(), PartyOperation.CHANGE_LEADER, mpc);
//					} catch (RemoteException ex) {
//						Logger.getLogger(MapleParty.class.getName()).log(Level.SEVERE, null, ex);
//					}
//					broadcastPacket(MaplePacketCreator.serverNotice(5, "Due to the party leader disconnecting, " 
//							+ mpc.getName() + " has been assigned as the new party leader."));
//					updated = true;
//					break;
//				}
//			}
//		}
//		if (!updated) {
//			broadcastPacket(MaplePacketCreator.serverNotice(5, 
//					"A new leader could not be assigned because no one was in the party leader's map."));			
//		}
//	}

	public MapleParty getEnemy() {
		return enemy;
	}

	public void setEnemy(MapleParty enemy) {
		this.enemy = enemy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MapleParty other = (MapleParty) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
