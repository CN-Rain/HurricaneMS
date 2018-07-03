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

package net.sf.odinms.client.messages.commands;

import java.util.Collection;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleRing;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;

public class CharCommands implements Command {

	@SuppressWarnings("static-access")
	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
		IllegalCommandSyntaxException {
		MapleCharacter player = c.getPlayer();
		if (splitted[0].equals("!lowhp")) {
			player.setHp(1);
			player.setMp(500);
			player.updateSingleStat(MapleStat.HP, 1);
			player.updateSingleStat(MapleStat.MP, 500);
		} else if (splitted[0].equals("!fullhp")) {
			player.setHp(player.getMaxHp());
			player.setMp(player.getMaxMp());
			player.updateSingleStat(MapleStat.HP, player.getMaxHp());
			player.updateSingleStat(MapleStat.MP, player.getMaxMp());
		} else if (splitted[0].equals("!skill")) {
			ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
			int level = getOptionalIntArg(splitted, 2, 1);
			int masterlevel = getOptionalIntArg(splitted, 3, 1);
			if (level > skill.getMaxLevel()) {
				level = skill.getMaxLevel();
			}
			if (masterlevel > skill.getMaxLevel() && skill.isFourthJob()) {
				masterlevel = skill.getMaxLevel();
			} else {
				masterlevel = 0;
			}
			player.changeSkillLevel(skill, level, masterlevel);
		} else if (splitted[0].equals("!sp")) {
			int sp = Integer.parseInt(splitted[1]);
			if (sp + player.getRemainingSp() > Short.MAX_VALUE) {
				sp = Short.MAX_VALUE;
			}
			player.setRemainingSp(sp);
			player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
		} else if (splitted[0].equals("!job")) {
			int jobId = Integer.parseInt(splitted[1]);
			if (MapleJob.getById(jobId) != null) {
				player.changeJob(MapleJob.getById(jobId));
			}
		} else if (splitted[0].equals("!whereami")) {
			new ServernoticeMapleClientMessageCallback(c).dropMessage("You are on map " + player.getMap().getId());
		} else if (splitted[0].equals("!shop")) {
			MapleShopFactory sfact = MapleShopFactory.getInstance();
			int shopId = Integer.parseInt(splitted[1]);
			if (sfact.getShop(shopId) != null) {
				MapleShop shop = sfact.getShop(shopId);
				shop.sendShop(c);
			}
		} else if (splitted[0].equals("!gainmeso")) {
			player.gainMeso(Integer.MAX_VALUE - player.getMeso(), true);
		} else if (splitted[0].equals("!levelup")) {
			if (player.getLevel() < 200) {
				player.levelUp();
				player.setExp(0);
			}
		} else if (splitted[0].equals("!item")) {
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
			short quantity = (short) getOptionalIntArg(splitted, 2, 1);
			if (Integer.parseInt(splitted[1]) >= 5000000 && Integer.parseInt(splitted[1]) <= 5000100) {
				if (quantity > 1) {
					quantity = 1;
				}
				int petId = MaplePet.createPet(Integer.parseInt(splitted[1]));
				//player.equipChanged();
				MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), petId);
				return;
			} else if (ii.isRechargable(Integer.parseInt(splitted[1]))) {
				quantity = (short) ii.getSlotMax(c, Integer.parseInt(splitted[1]));
				MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, "Rechargable item created.", player.getName(), -1);
				return;
			}
			MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), -1);
		} else if (splitted[0].equals("!ring")) {
			int itemId = Integer.parseInt(splitted[1]);
			String partnerName = splitted[2];
			int partnerId = MapleCharacter.getIdByName(partnerName, 0);
			int[] ret = MapleRing.createRing(c, itemId, player.getId(), player.getName(), partnerId, partnerName);
			if (ret[0] == -1 || ret[1] == -1) {
				mc.dropMessage("There was an unknown error.");
				mc.dropMessage("Make sure the person you are attempting to create a ring with is online.");
			}
		} else if (splitted[0].equals("!drop")) {
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
			int itemId = Integer.parseInt(splitted[1]);
			short quantity = (short) (short) getOptionalIntArg(splitted, 2, 1);
			IItem toDrop;
			if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
				toDrop = ii.getEquipById(itemId);
			} else {
				toDrop = new Item(itemId, (byte) 0, (short) quantity);
			}
			toDrop.log("Created by " + player.getName() + " using !drop. Quantity: " + quantity, false);
			toDrop.setOwner(player.getName());
			player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);
		} else if (splitted[0].equals("!maxlevel")) {
			player.setExp(0);
			while (player.getLevel() < 200) {
				player.levelUp();
			}
		} else if (splitted[0].equals("!online")) {
			mc.dropMessage("Characters connected to channel " + c.getChannel() + ":");
			Collection<MapleCharacter> chrs = c.getChannelServer().getInstance(c.getChannel()).getPlayerStorage().getAllCharacters();
			for (MapleCharacter chr : chrs) {
				mc.dropMessage(chr.getName() + " at map ID: " + chr.getMapId());
			}
			mc.dropMessage("Total characters on channel " + c.getChannel() + ": " + chrs.size());
		} else if (splitted[0].equals("!saveall")) {
			Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
			for (ChannelServer cserv : cservs) {
				mc.dropMessage("Saving all characters in channel " + cserv.getChannel() + "...");
				Collection<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharacters();
				for (MapleCharacter chr : chrs) {
					chr.saveToDB(true);
				}
			}
			mc.dropMessage("All characters saved.");
		} else if (splitted[0].equals("!ariantpq")) {
			if (splitted.length < 2) {
				player.getMap().AriantPQStart();
			} else {
				c.getSession().write(MaplePacketCreator.updateAriantPQRanking(splitted[1], 5, false));
			}
		} else if (splitted[0].equals("!scoreboard")) {
			player.getMap().broadcastMessage(MaplePacketCreator.showAriantScoreBoard());
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
				new CommandDefinition("lowhp", "", "", 4),
				new CommandDefinition("fullhp", "", "", 4),
				new CommandDefinition("skill", "", "", 4),
				new CommandDefinition("sp", "", "", 4),
				new CommandDefinition("job", "", "", 4),
				new CommandDefinition("whereami", "", "", 4),
				new CommandDefinition("shop", "", "", 4),
				new CommandDefinition("gainmeso", "", "", 4),
				new CommandDefinition("levelup", "", "", 4),
				new CommandDefinition("item", "", "", 4),
				new CommandDefinition("drop", "", "", 4),
				new CommandDefinition("maxlevel", "", "", 4),
				new CommandDefinition("online", "", "",4),
				new CommandDefinition("ring", "", "", 4),
				new CommandDefinition("saveall", "", "Saves all chars. Please use it wisely, quite expensive command.", 4),
				new CommandDefinition("ariantpq", "", "", 4),
				new CommandDefinition("scoreboard", "", "", 4),
		};
	}
}

