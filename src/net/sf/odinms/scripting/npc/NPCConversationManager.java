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

package net.sf.odinms.scripting.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.scripting.event.EventManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.MapleSquadType;
import net.sf.odinms.server.maps.MapleMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.handler.DueyActionHandler;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.MonsterCarnival;
import net.sf.odinms.server.PlayerNPCEngine;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.market.MarketEngine;
import net.sf.odinms.server.market.MarketEngine.ItemEntry;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {

	private MapleClient c;
	private int npc;
	private String getText;
	private MapleCharacter chr;
	private List<MaplePartyCharacter> otherParty;

	public NPCConversationManager(MapleClient c, int npc) {
		super(c);
		this.c = c;
		this.npc = npc;
	}
        
	public NPCConversationManager(MapleClient c, int npc, MapleCharacter chr) {
		super(c);
		this.c = c;
		this.npc = npc;
		this.chr = chr;
	}
	
	public NPCConversationManager(MapleClient c, int npc, List<MaplePartyCharacter> otherParty, int b) {
		super(c);
		this.c = c;
		this.npc = npc;
		this.otherParty = otherParty;
	}	
	
	/**
	 * Added in for consistency with PQ Npcs
	 * @return
	 */
	public MapleCharacter getChar() {
		return c.getPlayer();
	}
	
   public void closeDoor(int mapid)
   {
	   getClient().getChannelServer().getMapFactory().getMap(mapid).setReactorState();
   }

   public void openDoor(int mapid)
   {
	   getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
   }
   
	public int getNpc() {
		return npc;
	}
        
	public boolean isMorphed() {
		boolean morph = false;

		Integer morphed = getPlayer().getBuffedValue(MapleBuffStat.MORPH);
		if (morphed != null) {
			morph = true;
		}
		return morph;
	}
        
	public int getMorphValue() { // 1= mushroom, 2= pig, 3= alien, 4= cornian, 5= arab retard
		try {
			int morphid = getPlayer().getBuffedValue(MapleBuffStat.MORPH).intValue();
			return morphid;
		} catch (NullPointerException n) {
			return -1;
		}
	}

	public void dispose() {
		NPCScriptManager.getInstance().dispose(this);
	}

	public void sendNext(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01"));
	}

	public void sendPrev(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00"));
	}

	public void sendNextPrev(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01"));
	}

	public void sendOk(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00"));
	}

	public void sendYesNo(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, ""));
	}

	public void sendAcceptDecline(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, ""));
	}

	public void sendSimple(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, ""));
	}

	public void sendStyle(String text, int styles[]) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalkStyle(npc, text, styles));
	}

	public void sendGetNumber(String text, int def, int min, int max) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
	}

	public void sendGetText(String text) {
		getClient().getSession().write(MaplePacketCreator.getNPCTalkText(npc, text));
	}

	public void setGetText(String text) {
		this.getText = text;
	}

	public String getText() {
		return this.getText;
	}

	public void openShop(int id) {
		MapleShopFactory.getInstance().getShop(id).sendShop(getClient());
	}

	public void openNpc(int id) {
		dispose();
		NPCScriptManager.getInstance().start(getClient(), id, null, null);
	}

	public void changeJob(MapleJob job) {
		getPlayer().changeJob(job);
	}

	public MapleJob getJob() {
		return getPlayer().getJob();
	}

	public void startQuest(int id) {
		MapleQuest.getInstance(id).start(getPlayer(), npc);
	}

	public void completeQuest(int id) {
		MapleQuest.getInstance(id).complete(getPlayer(), npc);
	}

	public void forfeitQuest(int id) {
		MapleQuest.getInstance(id).forfeit(getPlayer());
	}

	public void gainMeso(int gain) {
		getPlayer().gainMeso(gain, true, false, true);
	}

	public void gainExp(int gain) {
		getPlayer().gainExp(gain * c.getChannelServer().getExpRate(), true, true);
	}

	/**
	 * use getPlayer().getLevel() instead
	 * @return
	 */
	@Deprecated
	public int getLevel() {
		return getPlayer().getLevel();
	}

	public void unequipEverything() {
		MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
		MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
		List<Byte> ids = new LinkedList<Byte>();
		for (IItem item : equipped.list()) {
			ids.add(item.getPosition());
		}
		for (byte id : ids) {
			MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
		}
	}

	public void teachSkill(int id, int level, int masterlevel) {
		getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
	}

	public void clearSkills() {
		Map<ISkill, MapleCharacter.SkillEntry> skills = getPlayer().getSkills();
		for (Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
			getPlayer().changeSkillLevel(skill.getKey(), 0, 0);
		}
	}

	public MapleClient getC() {
		return getClient();
	}

	public void rechargeStars() {
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		IItem stars = getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) 1);
		if (ii.isThrowingStar(stars.getItemId()) || ii.isBullet(stars.getItemId())) {
			stars.setQuantity(ii.getSlotMax(getClient(), stars.getItemId()));
			getC().getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, (Item) stars));
		}
	}

	public EventManager getEventManager(String event) {
		return getClient().getChannelServer().getEventSM().getEventManager(event);
	}

	public void showEffect(String effect) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
	}

	public void playSound(String sound) {
		getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
	}

	@Override
	public String toString() {
		return "Conversation with NPC: " + npc;
	}

	public void updateBuddyCapacity(int capacity) {
		getPlayer().setBuddyCapacity(capacity);
	}

	public int getBuddyCapacity() {
		return getPlayer().getBuddyCapacity();
	}

	public void setHair(int hair) {
		getPlayer().setHair(hair);
		getPlayer().updateSingleStat(MapleStat.HAIR, hair);
		getPlayer().equipChanged();
	}

	public void setFace(int face) {
		getPlayer().setFace(face);
		getPlayer().updateSingleStat(MapleStat.FACE, face);
		getPlayer().equipChanged();
	}

	@SuppressWarnings("static-access")
	public void setSkin(int color) {
		getPlayer().setSkinColor(getPlayer().getSkinColor().getById(color));
		getPlayer().updateSingleStat(MapleStat.SKIN, color);
		getPlayer().equipChanged();
	}

	public void warpParty(int mapId) {
		MapleMap target = getMap(mapId);
		for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
			MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
			if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
				curChar.changeMap(target, target.getPortal(0));
			}
		}
	}

	public void warpPartyWithExp(int mapId, int exp) {
		MapleMap target = getMap(mapId);
		for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
			MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
			if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
				curChar.changeMap(target, target.getPortal(0));
				curChar.gainExp(exp, true, false, true);
			}
		}
	}

	public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
		MapleMap target = getMap(mapId);
		for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
			MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
			if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
				curChar.changeMap(target, target.getPortal(0));
				curChar.gainExp(exp, true, false, true);
				curChar.gainMeso(meso, true);
			}
		}
	}

	public void warpRandom(int mapid) {
		MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
		Random rand = new Random();
		MaplePortal portal = target.getPortal(rand.nextInt(target.getPortals().size())); //generate random portal
		getPlayer().changeMap(target, portal);
	}

	public int itemQuantity(int itemid) {
		MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
		MapleInventory iv = getPlayer().getInventory(type);
		int possesed = iv.countById(itemid);
		return possesed;
	}

	public MapleSquad createMapleSquad(MapleSquadType type) {
		MapleSquad squad = new MapleSquad(c.getChannel(), getPlayer());
		if (getSquadState(type) == 0) {
			c.getChannelServer().addMapleSquad(squad, type);
		} else {
			return null;
		}
		return squad;
	}

	public MapleCharacter getSquadMember(MapleSquadType type, int index) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		MapleCharacter ret = null;
		if (squad != null) {
			ret = squad.getMembers().get(index);
		}
		return ret;
	}

	public int getSquadState(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			return squad.getStatus();
		} else {
			return 0;
		}
	}

	public void setSquadState(MapleSquadType type, int state) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			squad.setStatus(state);
		}
	}

	public boolean checkSquadLeader(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			if (squad.getLeader().getId() == getPlayer().getId()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void removeMapleSquad(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			if (squad.getLeader().getId() == getPlayer().getId()) {
				squad.clear();
				c.getChannelServer().removeMapleSquad(squad, type);
			}
		}
	}

	public int numSquadMembers(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		int ret = 0;
		if (squad != null) {
			ret = squad.getSquadSize();
		}
		return ret;
	}

	public boolean isSquadMember(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		boolean ret = false;
		if (squad.containsMember(getPlayer())) {
			ret = true;
		}
		return ret;
	}

	public void addSquadMember(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			squad.addMember(getPlayer());
		}
	}

	public void removeSquadMember(MapleSquadType type, MapleCharacter chr, boolean ban) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			squad.banMember(chr, ban);
		}
	}

	public void removeSquadMember(MapleSquadType type, int index, boolean ban) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			MapleCharacter chr = squad.getMembers().get(index);
			squad.banMember(chr, ban);
		}
	}

	public boolean canAddSquadMember(MapleSquadType type) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		if (squad != null) {
			if (squad.isBanned(getPlayer())) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public void warpSquadMembers(MapleSquadType type, int mapId) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
		if (squad != null) {
			if (checkSquadLeader(type)) {
				for (MapleCharacter chr : squad.getMembers()) {
					chr.changeMap(map, map.getPortal(0));
				}
			}
		}
	}

	public static boolean makeRing(MapleClient mc, String partner, int ringId) {
		int partnerId = MapleCharacter.getIdByName(partner, 0);
		int[] ret = net.sf.odinms.client.MapleRing.createRing(mc, ringId, mc.getPlayer().getId(), mc.getPlayer().getName(), partnerId, partner);
		if (ret[0] == -1 || ret[1] == -1) {
			return false;
		} else {
			return true;
		}
	}

	public void resetReactors() {
		getPlayer().getMap().resetReactors();
	}

	public void displayGuildRanks() {
		MapleGuild.displayGuildRanks(getClient(), npc);
	}

	public void openDuey() {
		c.getSession().write(MaplePacketCreator.sendDuey((byte) 8, DueyActionHandler.loadItems(this.getPlayer())));
	}
    
	/**
	 * This returns the OTHER character associated with this CM.
	 * @return
	 */
	public MapleCharacter getCharacter() {
		return chr;
	}

   public MapleCharacter getCharByName(String namee) {
	   try {
		return getClient().getChannelServer().getPlayerStorage().getCharacterByName(namee);
	   } catch (Exception e) {
		   return null;
	   }
   }

	public void warpAllInMap(int mapid, int portal) {
			MapleMap outMap;
			MapleMapFactory mapFactory;
			mapFactory = ChannelServer.getInstance(c.getChannel()).getMapFactory();
			outMap = mapFactory.getMap(mapid);
			for (MapleCharacter aaa : outMap.getCharacters()) {
				//Warp everyone out
				mapFactory = ChannelServer.getInstance(aaa.getClient().getChannel()).getMapFactory();
				aaa.getClient().getPlayer().changeMap(outMap, outMap.getPortal(portal));
				outMap = mapFactory.getMap(mapid);
				aaa.getClient().getPlayer().getEventInstance().unregisterPlayer(aaa.getClient().getPlayer()); //Unregister them all
			}
	}

   public int countMonster() {
		MapleMap map = c.getPlayer().getMap();
		double range = Double.POSITIVE_INFINITY;
		List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays
				.asList(MapleMapObjectType.MONSTER));
		return monsters.size();
	}

	public int countReactor() {
		MapleMap map = c.getPlayer().getMap();
		double range = Double.POSITIVE_INFINITY;
			List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays
					.asList(MapleMapObjectType.REACTOR));
			return reactors.size();
	}

	public int getDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		int dayy = cal.get(Calendar.DAY_OF_WEEK);
		return dayy;
	}

	public void giveNPCBuff(MapleCharacter chr, int itemID) {
		MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
		MapleStatEffect statEffect = mii.getItemEffect(itemID);
		statEffect.applyTo(chr);
	}

	public void giveWonkyBuff(MapleCharacter chr){
		long what = Math.round(Math.random() * 4);
		int what1 = (int)what;
		int Buffs[] = {2022090, 2022091, 2022092, 2022093} ;
		int buffToGive = Buffs[what1];
		MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
		MapleStatEffect statEffect = mii.getItemEffect(buffToGive);
		//for (MapleMapObject mmo =  this.getParty()) {
		MapleCharacter character = (MapleCharacter) chr;
		statEffect.applyTo(character);
		//}
	}
	
	
	public void addItemToMarket(int itemid, int quantity, int price) {
		this.c.getChannelServer().getMarket().addItem(itemid, quantity, price, c.getPlayer().getId());
	}
	
	public void gainNeckson(int gain) {
		c.getPlayer().modifyCSPoints(4, gain);
		if (gain > 0) {
			playerMessage(5, "You have gained Neckson cash (+" + gain + ")");
		} else {
			playerMessage(5, "You have lost Neckson cash (-" + gain + ")");
		}
	}
	
	public void removeItemFromMarket(int itemid, int quantity) {
		this.c.getChannelServer().getMarket().removeItem(itemid, quantity, c.getPlayer().getId());
	}
	
	public void buyItem(int itemId, int quantity, int price, int charId) {
		try {
			for (ItemEntry ie : c.getChannelServer().getMarket().getItems()) {
				if (ie.getId() == itemId && ie.getPrice() == price &&
						ie.getOwner() == charId) {
					if (ie.getQuantity() < quantity) {
						c.getSession().write(MaplePacketCreator.serverNotice(1, 
								"You're trying to buy more than available!"));
						return;
					}
					if (ie.getQuantity() * ie.getPrice() > c.getPlayer().getMeso()) {
						c.getSession().write(MaplePacketCreator.serverNotice(1, 
								"You don't have enough mesos!"));
						return;					
					}
					int cost = ie.getPrice() * ie.getQuantity();
					c.getChannelServer().getMarket().removeItem(itemId, quantity, charId);
					c.getPlayer().gainMeso(-cost, true, true, true);
					gainItem(itemId, (short)quantity);
					for (ChannelServer cs : ChannelServer.getAllInstances()) {
						for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
							if (mc.getId() == charId) {
								mc.gainMeso(cost, false, true, true);
								mc.getClient().getSession().write(MaplePacketCreator.
										serverNotice(5, "[Market] You have gained " + cost + " mesos from " + 
										c.getPlayer().getName() + "."));
								return;
							}
						}
					}
					//OMG the other player was not found..
					Connection con = DatabaseConnection.getConnection();
					try {
						PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
						ps.setInt(1, charId);
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							int meso = rs.getInt("meso");
							int gain = meso + cost;
							ps = con.prepareStatement("UPDATE characters SET meso = ? WHERE id = ?");
							ps.setInt(1, gain);
							ps.setInt(2, charId);
							ps.executeUpdate();
						}
						ps.close();
						rs.close();
					} catch (SQLException fuckyoucunt) {

					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void showInventory(int type) {
		String send = "";
		MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
		for (IItem item : invy.list()) {
			send += "#L" + item.getPosition() +
					"##v" + item.getItemId() + "# Quantity: #b" + item.getQuantity() + "#k#l\\r\\n";
		}
		sendSimple(send);
	}
	
	public String getInventory (int type) {
		String send = "";
		MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
		for (IItem item : invy.list()) {
			send += "#L" + item.getPosition() + 
					"##v" + item.getItemId() + "# Quantity: #b" + item.getQuantity() + "#k#l\\r\\n";
		}
		return send;
	}
	
	public IItem getItem(int slot, int type) {
		MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
		for (IItem item : invy.list()) {
			if (item.getPosition() == slot) {
				return item;
			}
		}
		return null;
	}
	
	public String getMarket() {
		MarketEngine me = c.getChannelServer().getMarket();
		String ret = "";
		int count = 0;
		for (ItemEntry ie : me.getItems()) {
			if (ie.getOwner() == c.getPlayer().getId()) //Don't let him see their own items
				continue;
			ret += "#L" + count + "##v" + 
					ie.getId() + 
					"# #bQuantity#k: " + 
					ie.getQuantity() + 
					" #bCost#k: " + 
					ie.getPrice() + " mesos" + 
					" #b Owner: #k" + 
					me.getCharacterName(ie.getOwner()) + 
					"#l\\r\\n";
			count ++;
		}
		return ret;
	}
	
	public String getMarketRetrival() {
		MarketEngine me = c.getChannelServer().getMarket();
		String ret = "";
		int count = 0;
		for (ItemEntry ie : me.getItems()) {
			if (ie.getOwner() != c.getPlayer().getId()) //Only own items
				continue;
			ret += "#L" + count + "##v" + 
					ie.getId() + 
					"# #bQuantity#k: " + 
					ie.getQuantity() + 
					" #bCost#k: " + 
					ie.getPrice() + " mesos" +
					"#l\\r\\n";
			count ++;
		}
		return ret;		
	}
	
	public List<ItemEntry> getMyMarketItems() {
		List<ItemEntry> ret = new LinkedList<ItemEntry>();
		synchronized (c.getChannelServer().getMarket().getItems()) {
			for (ItemEntry ie : c.getChannelServer().getMarket().getItems()) {
				if (ie.getOwner() == c.getPlayer().getId()) {
					ret.add(ie);
				}
			}
		}
		return ret;
	}
	
	public void retrieveMarketItem(int position) {
		List<ItemEntry> items = getMyMarketItems();
		ItemEntry ie = items.get(position);
		gainItem(ie.getId(), (short) ie.getQuantity());
		removeItemFromMarket(ie.getId(), ie.getQuantity());
	}
	
	public List<ItemEntry> getMarketItems() {
		List<ItemEntry> ret = new LinkedList<ItemEntry>();
		synchronized (c.getChannelServer().getMarket().getItems()) {
			for (ItemEntry ie : c.getChannelServer().getMarket().getItems()) {
				if (ie.getOwner() != c.getPlayer().getId())
					ret.add(ie);
			}
		}
		return ret;
	}
	
	public void warpSquadMembersClock(MapleSquadType type, int mapId, int clock, int mapExit) {
		MapleSquad squad = c.getChannelServer().getMapleSquad(type);
		MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
                MapleMap map1 = c.getChannelServer().getMapFactory().getMap(mapExit);
		if (squad != null) {
			if (checkSquadLeader(type)) {
				for (MapleCharacter ch : squad.getMembers()) {
					ch.changeMap(map, map.getPortal(0));
						ch.getClient().getSession().write(MaplePacketCreator.getClock(clock));
				}
				map.scheduleWarp(map, map1, (long) clock * 1000);
			}
		}
	}
        
	public MapleSquad getSquad(MapleSquadType Type) {
		return c.getChannelServer().getMapleSquad(Type);
	}
	
	public int calcAvgLvl(int map) {
		int num = 0;
		int avg = 0;
		for (MapleMapObject mmo : 
			c.getChannelServer().getMapFactory().getMap(map).getAllPlayer()) {
				avg += ((MapleCharacter) mmo).getLevel();
			num ++;
		}
		avg /= num;
		return avg;
	}
	
	public void sendCPQMapLists() {
		String msg = "Pick a field:\\r\\n";
		for (int i = 0; i < 6; i++) {
			if (fieldTaken(i)) {
				if (fieldLobbied(i)) {
					msg += "#b#L" + i + "#Monster Carnival Field " + (i + 1) + " Avg Lvl: " + 
							calcAvgLvl(980000100 + i * 100) + "#l\\r\\n";
				} else {
					continue;
				}
			} else {
				msg += "#b#L" + i + "#Monster Carnival Field " + (i + 1) + "#l\\r\\n";
			}
		}
		sendSimple(msg);
	}
	
	public boolean fieldTaken(int field) {
		if (c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().size() != 0)
			return true;
		if (c.getChannelServer().getMapFactory().getMap(980000101 + field * 100).getAllPlayer().size() != 0)
			return true;		
		if (c.getChannelServer().getMapFactory().getMap(980000102 + field * 100).getAllPlayer().size() != 0)
			return true;
		return false;		
	}
	
	public boolean fieldLobbied(int field) {
		if (c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().size() != 0)
			return true;
		return false;
	}
	
	public void cpqLobby(int field) {
		try {
			MapleMap map;
			ChannelServer cs = c.getChannelServer();
			map = cs.getMapFactory().getMap(980000100 + 100 * field);
			for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
				MapleCharacter mc;
				mc = cs.getPlayerStorage().getCharacterByName(mpc.getName());
				if (mc != null) {
					mc.changeMap(map, map.getPortal(0));
					mc.getClient().getSession().write(MaplePacketCreator.serverNotice(5, 
					"You will now recieve challenges from other parties. If you do not accept a challenge in 3 minutes, you will be kicked out."));
					mc.getClient().getSession().write(MaplePacketCreator.getClock(3 * 60));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public MapleCharacter getChrById(int id) {
		ChannelServer cs = c.getChannelServer();
		return cs.getPlayerStorage().getCharacterById(id);
	}
	
	public void startCPQ(final MapleCharacter challenger, int field) {
		try {
			if (challenger != null) {
				if (challenger.getParty() == null) throw new RuntimeException("Challenger's party was null!");
				for (MaplePartyCharacter mpc : challenger.getParty().getMembers()) {
					MapleCharacter mc;
					mc = c.getChannelServer().getPlayerStorage().getCharacterByName(mpc.getName());
					if (mc != null) {
						mc.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(0));
						mc.getClient().getSession().write(MaplePacketCreator.getClock(10));
					}
				}
			}
			final int mapid = c.getPlayer().getMap().getId() + 1;
			TimerManager.getInstance().schedule(new Runnable() {
				@Override public void run() {
					MapleMap map;
					ChannelServer cs = c.getChannelServer();
					map = cs.getMapFactory().getMap(mapid);
					new MonsterCarnival(getPlayer().getParty(), challenger.getParty(), mapid);
					map.broadcastMessage(MaplePacketCreator.serverNotice(5, 
							"Monster Carnival has begun!"));
				}
			}, 10000);
			mapMessage(5, "Monster Carnival will begin in 10 seconds!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void challengeParty(int field) {
		MapleCharacter leader = null;
		MapleMap map = c.getChannelServer().getMapFactory().getMap(980000100 + 100 * field);
		for (MapleMapObject mmo : map.getAllPlayer()) {
			MapleCharacter mc = (MapleCharacter) mmo;
			if (mc.getParty().getLeader().getId() == mc.getId()) {
				leader = mc;
				break;
			}
		}
		if (leader != null) {
			if (!leader.isChallenged()) {
				List<MaplePartyCharacter> fuckwits = new LinkedList<MaplePartyCharacter>();
				for (MaplePartyCharacter fucker : c.getPlayer().getParty().getMembers()) {
					fuckwits.add(fucker);
				}
				NPCScriptManager.getInstance().start(
						"cpqchallenge", leader.getClient(), npc, fuckwits);
			} else {
				sendOk("The other party is currently taking on a different challenge.");
			}
		} else {
			sendOk("Could not find leader!");
		}
	}
	
	public int applyForLord() {
		return c.getPlayer().applyForLord();
	}
	
	public String getLords() {
		Connection con = DatabaseConnection.getConnection();
		String ret = "";
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM lordvotes");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int votes = rs.getInt("votes");
				int charid = rs.getInt("charid");
				String name = c.getChannelServer().getMarket().getCharacterName(charid);
				ret += "#L" + charid + "##b" + name + "#k #evotes#n: " + votes + "\\r\\n";
			}
			return ret;
		} catch (Exception ex) {
			return "There was a database error.\\r\\n";
		}
	}
	
	public boolean voteForLord(int cid) {
		return c.getPlayer().voteForLord(cid);
	}
	
	public String getCharName(int id) {
		return c.getChannelServer().getMarket().getCharacterName(id);
	}
	
	public int calculateCPQRanking() {
		return getPlayer().getCpqRanking();
	}
	
	public void createEngagement(MapleCharacter arg1, MapleCharacter arg2) {
		Marriage.createEngagement(arg1, arg2);
	}
	
	public int createMarriage(int hchr, int wchr) {
		Marriage.createMarriage(hchr, wchr);
		return 1;
	}
	
	public List<MapleCharacter> getPartyMembers() {
		if (getPlayer().getParty() == null) {
			return null;
		}
		List<MapleCharacter> chars = new LinkedList<MapleCharacter>(); // creates an empty array full of shit..
		for (ChannelServer channel : ChannelServer.getAllInstances()) {
			for (MaplePartyCharacter a : getPlayer().getParty().getMembers()) {
				MapleCharacter ch = channel.getPlayerStorage().getCharacterByName(a.getName());
				if (ch != null) { // double check <3
					chars.add(ch);
				}
			}
		}
		return chars;
	}
	
	public int partyMembersInMap() {
		int inMap = 0;
		for (MapleCharacter char2 : getPlayer().getMap().getCharacters()) {
			if (char2.getParty() == getPlayer().getParty()) {
				inMap++;
			}
		}
		return inMap;
	}
	
    public void removeHiredMerchantItem(int id) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM hiredmerchant WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
        }
    }

    public int getHiredMerchantMesos() {
        Connection con = DatabaseConnection.getConnection();
        int mesos;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT MerchantMesos FROM characters WHERE id = ?");
            ps.setInt(1, getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            rs.next();
            mesos = rs.getInt("MerchantMesos");
            rs.close();
            ps.close();

        } catch (SQLException se) {
            return 0;
        }
        return mesos;
    }

    public void setHiredMerchantMesos(int set) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET MerchantMesos = ? WHERE id = ?");
            ps.setInt(1, set);
            ps.setInt(2, getPlayer().getId());
            ps.executeUpdate();
            ps.close();

        } catch (SQLException se) {
        }
    }

    public List<Pair<Integer, IItem>> getHiredMerchantItems() {
        Connection con = DatabaseConnection.getConnection();
        List<Pair<Integer, IItem>> items = new ArrayList<Pair<Integer, IItem>>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchant WHERE ownerid = ?");
            ps.setInt(1, getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("type") == 1) {
                    Equip eq = new Equip(rs.getInt("itemid"), (byte) 0, -1);
                    eq.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                    eq.setLevel((byte) rs.getInt("level"));
                    eq.setStr((short) rs.getInt("str"));
                    eq.setDex((short) rs.getInt("dex"));
                    eq.setInt((short) rs.getInt("int"));
                    eq.setLuk((short) rs.getInt("luk"));
                    eq.setHp((short) rs.getInt("hp"));
                    eq.setMp((short) rs.getInt("mp"));
                    eq.setWatk((short) rs.getInt("watk"));
                    eq.setMatk((short) rs.getInt("matk"));
                    eq.setWdef((short) rs.getInt("wdef"));
                    eq.setMdef((short) rs.getInt("mdef"));
                    eq.setAcc((short) rs.getInt("acc"));
                    eq.setAvoid((short) rs.getInt("avoid"));
                    eq.setHands((short) rs.getInt("hands"));
                    eq.setSpeed((short) rs.getInt("speed"));
                    eq.setJump((short) rs.getInt("jump"));
                    eq.setOwner(rs.getString("owner"));
                    items.add(new Pair<Integer, IItem>(rs.getInt("id"), eq));
                } else if (rs.getInt("type") == 2) {
                    Item newItem = new Item(rs.getInt("itemid"), (byte) 0, (short) rs.getInt("quantity"));
                    newItem.setOwner(rs.getString("owner"));
                    items.add(new Pair<Integer, IItem>(rs.getInt("id"), newItem));
                }
            }
            ps.close();
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        return items;
    }
	
	public boolean createPlayerNPC() {
		try {
			if (this.getPlayer().getLevel() < 200) return false;
			int type = 0;
			if (this.getPlayer().getJob().isA(MapleJob.WARRIOR)) {
				type = PlayerNPCEngine.WARRIOR_ID;
			} else if (this.getPlayer().getJob().isA(MapleJob.MAGICIAN)) {
				type = PlayerNPCEngine.MAGICIAN_ID;
			} else if (this.getPlayer().getJob().isA(MapleJob.BOWMAN)) {
				type = PlayerNPCEngine.BOWMAN_ID;
			} else if (this.getPlayer().getJob().isA(MapleJob.THIEF)) {
				type = PlayerNPCEngine.THIEF_ID;
			} else {
				return false;
			}
			try {
				return PlayerNPCEngine.createGeneralNPC(type, this.getPlayer());
			} catch (Throwable ex) {
				Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		} catch (Exception ex) {
			Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
			return false;		
		}
	}
}
