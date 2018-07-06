/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * This file is part of the "Renoria" Game.
 * Copyright (C) 2008
 * IDGames.
 */

package net.sf.odinms.server;

import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author David
 */
public class RandomMapEffectEngine {
	public static final WeatherEntry entries[] = {
		new WeatherEntry(0.9f, 5120002),
		new WeatherEntry(1.1f, 5120009),
		new WeatherEntry(1.2f, 5120003)
	};
	
	public RandomMapEffectEngine() {
		activateEvent();
	}
	
	public void activateEvent() {
		TimerManager.getInstance().schedule(new Runnable() {
			public void run() {
				doEvent();
				activateEvent();
			}
		} , (int) ((Math.random() * 60) * 60000));
	}
	
	public void doEvent() {
		for (ChannelServer cs : ChannelServer.getAllInstances()) {
			if (Math.random() < 0.3) {
				WeatherEntry selection = entries[(int)(Math.random() * entries.length)];
				int calc = Math.round(3 * selection.getRate());
				cs.broadcastPacket(MaplePacketCreator.serverNotice(6, "[EXP Random Event] EXP Rate has been changed to " + calc + "x."));
				cs.broadcastPacket(MaplePacketCreator.startMapEffect("The EXP Rate has been changed to " + calc + "x!", selection.getId(), true));
				cs.setExpRate(calc);
				final ChannelServer a = cs;
				TimerManager.getInstance().schedule(new Runnable() {
					public void run() {
						a.broadcastPacket(MaplePacketCreator.removeMapEffect());
					}
				}, 5000);
			}
		}
	}
	
	private static class WeatherEntry {
		float rate;
		int id;
		
		public WeatherEntry (float rate, int id) {
			this.rate = rate;
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public float getRate() {
			return rate;
		}
	}
}
