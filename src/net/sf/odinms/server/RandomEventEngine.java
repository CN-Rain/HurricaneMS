package net.sf.odinms.server;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author David
 */
public class RandomEventEngine {
	public static final RandomEvent events[] = {
		new RandomEvent(RandomEventType.MAP, 923000100, "[Random Event] Someone wants to see you."), //Cold Cave
		new RandomEvent(RandomEventType.MONSTER, 9300166, "[Random Event] The ground suddenly explodes."), //Bomb
		new RandomEvent(RandomEventType.MONSTER, 9100003, "[Random Event] A snake came out of the ground."), //9100003 [Jr necki]
	};
	
	public RandomEventEngine() {
		activateEvent();
	}
	
	public void activateEvent() {
            if(deactivateEvent(false)) {
                TimerManager.getInstance().schedule(new Runnable() {
			public void run() {
				doEvent();
				activateEvent();
			}
		} , (int) ((Math.random() * 30) * 60000));
            }
            else {
                // Doesn't work.
                return;
            }
	}
        
        public boolean deactivateEvent(boolean activation) {
           if (activation) {
               return true;
           }
           else {
               return false;
           }
        }
	
	public void doEvent() {
		int times = (int) (8 * Math.random());
		int rot = 0;
		for (ChannelServer cs : ChannelServer.getAllInstances()) {
			if (rot >= times) break;
			for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
				if (rot >= times) break;
				if (Math.random() < (Math.min(0.2 + 
						mc.getCheatTracker().getPoints()/1000, 1)) && mc.canRandomEvent()) {
					RandomEvent chosen = events[(int) (Math.random() * events.length)];
					if (chosen.getType() == RandomEventType.MAP) {
						mc.saveLocation(SavedLocationType.RANDOM_EVENT);
						mc.changeMap(cs.getMapFactory().getMap(chosen.getId()), 
								cs.getMapFactory().getMap(chosen.getId()).getPortal("sp"));
					} else if (chosen.getType() == RandomEventType.MONSTER) {
						int mobId = chosen.getId();
						MapleMonster mob = MapleLifeFactory.getMonster(mobId);
						mob.setRandom(true);
						mob.setTarget(mc);
						mc.getMap().spawnMonsterOnGroundBelow(mob, mc.getPosition());
					}
					if (chosen.getMessage() != null && !chosen.getMessage().equals("")) {
						mc.getClient().getSession().write(
								MaplePacketCreator.serverNotice(5, chosen.getMessage()));
					}
					rot++;
				}
			}
		}
	}
}
