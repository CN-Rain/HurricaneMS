/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy~frz.cc>
Matthias Butz <matze~odinms.de>
Jan Christian Meyer <vimes~odinms.de>

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

//modify by shaun166 to work on new command system pls don delete
package net.sf.odinms.client.messages.commands;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.handler.PlayerLoggedinHandler;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleFoothold;
import net.sf.odinms.server.maps.MapleFootholdTree;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

public class PlayerCommand implements Command {

    private Map<Integer, Long> gmUsages = new LinkedHashMap<Integer, Long>();

    @SuppressWarnings("static-access")
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if (splitted[0].equalsIgnoreCase("~clearinv") || splitted[0].equalsIgnoreCase("!clearinv")) {
            if (splitted.length < 2) {
                mc.dropMessage("eq, use, setup, etc, cash.");
            }
            int x = 0;
            if (splitted[1].equalsIgnoreCase("all")) {
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (byte) x, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) x).getQuantity(), false, true);
                }
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte) x, c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) x).getQuantity(), false, true);
                }
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, (byte) x, c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) x).getQuantity(), false, true);
                }
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) x, c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((byte) x).getQuantity(), false, true);
                }
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, (byte) x, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) x).getQuantity(), false, true);
                }
                mc.dropMessage("All slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("eq")) {
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (byte) x, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) x).getQuantity(), false, true);
                }
                mc.dropMessage("Eq inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("use")) {
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte) x, c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) x).getQuantity(), false, true);
                }
                mc.dropMessage("Use inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("setup")) {
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, (byte) x, c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) x).getQuantity(), false, true);
                }
                mc.dropMessage("Setup inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("etc")) {
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) x, c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((byte) x).getQuantity(), false, true);
                }
                mc.dropMessage("Etc inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("cash")) {
                while (x < 101) {
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) x) == null) {
                        x++;
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, (byte) x, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) x).getQuantity(), false, true);
                }
                mc.dropMessage("Cash inventory slots cleared.");
            } else {
                mc.dropMessage("~clearslot " + splitted[1] + " does not exist!");
            }
        } else if (splitted[0].equalsIgnoreCase("~storage") || splitted[0].equalsIgnoreCase("!storage")) {
            if (c.getPlayer().getMap().getForcedReturnMap() != null) {
                mc.dropMessage("Nice try.");
                return;
            }
            c.getPlayer().getStorage().sendStorage(c, 2080005);
        } else if (splitted[0].equalsIgnoreCase("~str") || splitted[0].equalsIgnoreCase("!str")) {
            int up;
            up = Integer.parseInt(splitted[1]);
            MapleCharacter player = c.getPlayer();
            if (player.getRemainingAp() < up || player.getRemainingAp() < 0 || (player.getStr() + up) < 4 || player.getStr() + up > 32767) {
                mc.dropMessage("Lol cheater, didn't think so!");
            } else {
                player.setStr(player.getStr() + up);
                player.setRemainingAp(player.getRemainingAp() - up);
                player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                player.updateSingleStat(MapleStat.STR, player.getStr());
            }
        } else if (splitted[0].equalsIgnoreCase("~online") || splitted[0].equalsIgnoreCase("!online")) {
            mc.dropMessage("Characters connected to channel " + c.getChannel() + ":");
            Collection<MapleCharacter> chrs = c.getChannelServer().getInstance(c.getChannel()).getPlayerStorage().getAllCharacters();
            for (MapleCharacter chr : chrs) {
                if (chr.getGMLevel() <= c.getPlayer().getGMLevel()) {
                    mc.dropMessage(chr.getName() + " at map ID: " + chr.getMapId());
                }
            }
            mc.dropMessage("Total characters on channel " + c.getChannel() + ": " + chrs.size());
        } else if (splitted[0].equalsIgnoreCase("~int") || splitted[0].equalsIgnoreCase("!int")) {
            int up;
            up = Integer.parseInt(splitted[1]);
            MapleCharacter player = c.getPlayer();
            if (player.getRemainingAp() < up || player.getRemainingAp() < 0 || (player.getInt() + up) < 4 || player.getInt() + up > 32767) {
                mc.dropMessage("I don't think so");
            } else {
                player.setInt(player.getInt() + up);
                player.setRemainingAp(player.getRemainingAp() - up);
                player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                player.updateSingleStat(MapleStat.INT, player.getInt());
            }
        } else if (splitted[0].equalsIgnoreCase("~dex") || splitted[0].equalsIgnoreCase("!dex")) {
            int up;
            up = Integer.parseInt(splitted[1]);
            MapleCharacter player = c.getPlayer();
            if (player.getRemainingAp() < up || player.getRemainingAp() < 0 || (player.getDex() + up) < 4 || player.getDex() + up > 32767) {
                mc.dropMessage("I didn't think so!");
            } else {
                player.setDex(player.getDex() + up);
                player.setRemainingAp(player.getRemainingAp() - up);
                player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                player.updateSingleStat(MapleStat.DEX, player.getDex());
            }
        } else if (splitted[0].equalsIgnoreCase("~luk") || splitted[0].equalsIgnoreCase("!luk")) {
            int up;
            up = Integer.parseInt(splitted[1]);
            MapleCharacter player = c.getPlayer();
            if (player.getRemainingAp() < up || player.getRemainingAp() < 0 || (player.getLuk() + up) < 4 || player.getLuk() + up > 32767) {
                mc.dropMessage("I didn't think so!");
            } else {
                player.setLuk(player.getLuk() + up);
                player.setRemainingAp(player.getRemainingAp() - up);
                player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                player.updateSingleStat(MapleStat.LUK, player.getLuk());
            }

//            } else if(splitted[0].equalsIgnoreCase("!hiredmerchant")){
//				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showHiredMerchant(c.getPlayer(), 5030000, splitted[1]));
        } else if (splitted[0].equalsIgnoreCase("~switch") || splitted[0].equalsIgnoreCase("!switch")) {
            if (splitted.length < 2) {
                mc.dropMessage("Syntax: ~switch <charname>");
                return;
            }
            String chrName = splitted[1];
            if (chrName.equalsIgnoreCase(c.getPlayer().getName())) {
                mc.dropMessage("You are already logged onto " + c.getPlayer().getName() + "!");
                return;
            }
            int accId = c.getAccID();
            int charId = -999;
            int chrAccId = -999;
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ?");
                ps.setString(1, chrName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    charId = rs.getInt("id");
                    chrAccId = rs.getInt("accountid");
                } else {
                    mc.dropMessage("Character not found!");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (chrAccId != accId && !c.getPlayer().isInvincible()) {
                mc.dropMessage("The character is not on your account!");
                return;
            }
            WorldLocation wl = c.getChannelServer().getWorldInterface().getLocation(chrName);
            if (wl != null) {
                mc.dropMessage("The character is already online. Disconnect the character first. The character is on channel " + wl.channel + ".");
                return;
            }
            c.disconnect(false);
			c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
            PlayerLoggedinHandler handler = new PlayerLoggedinHandler();
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeInt(charId);
            SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new net.sf.odinms.tools.data.input.ByteArrayByteStream(mplew.getPacket().getBytes()));
            handler.handlePacket(slea, c);
            mc.dropMessage("Sucessfully changed character to " + c.getPlayer().getName() + ".");
        } else if (splitted[0].equalsIgnoreCase("~dropall") || splitted[0].equalsIgnoreCase("!dropall")) {
            MapleCharacter monster = c.getPlayer();
            MapleFootholdTree footholds = monster.getMap().getFootholds();
            MapleMap map = monster.getMap();
            if (splitted.length < 2) {
                mc.dropMessage("Syntax: ~dropall <eq/use/setup/etc/cash>");
                return;
            }
            MapleInventoryType type = null;
            if (splitted[1].equalsIgnoreCase("eq")) {
                type = MapleInventoryType.EQUIP;
            } else if (splitted[1].equalsIgnoreCase("use")) {
                type = MapleInventoryType.USE;
            } else if (splitted[1].equalsIgnoreCase("setup")) {
                type = MapleInventoryType.SETUP;
            } else if (splitted[1].equalsIgnoreCase("etc")) {
                type = MapleInventoryType.ETC;
            } else if (splitted[1].equalsIgnoreCase("cash")) {
                type = MapleInventoryType.CASH;
            }
            if (type == null) {
                mc.dropMessage("Unknown type.");
                return;
            }
            MapleInventory invy = c.getPlayer().getInventory(type);
            List<Pair<Byte, Short>> toDrop = new LinkedList<Pair<Byte, Short>>();

            for (IItem item : invy.list()) {
                toDrop.add(new Pair<Byte, Short>(item.getPosition(), item.getQuantity()));
            }
            Point[] toPoint = new Point[toDrop.size()];
            int shiftDirection = 0;
            int shiftCount = 0;

            int curX = Math.min(Math.max(monster.getPosition().x - 25 * (toDrop.size() / 2), footholds.getMinDropX() + 25),
                    footholds.getMaxDropX() - toDrop.size() * 25);
            int curY = Math.max(monster.getPosition().y, footholds.getY1());
            while (shiftDirection < 3 && shiftCount < 1000) {
                // TODO for real center drop the monster width is needed o.o"
                if (shiftDirection == 1) {
                    curX += 25;
                } else if (shiftDirection == 2) {
                    curX -= 25;
                // now do it
                }
                for (int i = 0; i < toDrop.size(); i++) {
                    MapleFoothold wall = footholds.findWall(new Point(curX, curY), new Point(curX + toDrop.size() * 25, curY));
                    if (wall != null) {
                        //System.out.println("found a wall. wallX " + wall.getX1() + " curX " + curX);
                        if (wall.getX1() < curX) {
                            shiftDirection = 1;
                            shiftCount++;
                            break;
                        } else if (wall.getX1() == curX) {
                            if (shiftDirection == 0) {
                                shiftDirection = 1;
                            }
                            shiftCount++;
                            break;
                        } else {
                            shiftDirection = 2;
                            shiftCount++;
                            break;
                        }
                    } else if (i == toDrop.size() - 1) {
                        //System.out.println("ok " + curX);
                        shiftDirection = 3;
                    }
                    final Point dropPos = map.calcDropPos(new Point(curX + i * 25, curY), new Point(monster.getPosition()));
                    toPoint[i] = new Point(curX + i * 25, curY);
                    MapleInventoryManipulator.drop(c, type, toDrop.get(i).getLeft(), toDrop.get(i).getRight(), dropPos);
                }
            }
            mc.dropMessage("Done!");
        } else if (splitted[0].equalsIgnoreCase("~gm") || splitted[0].equalsIgnoreCase("!gm")) {
            if (splitted.length == 1) {
                mc.dropMessage("Syntax: ~gm <message>");
                return;
            }
            if (gmUsages.get(c.getPlayer().getId()) != null) {
                long lastUse = gmUsages.get(c.getPlayer().getId());
                if (System.currentTimeMillis() - lastUse < 60 * 1000 * 2) {
                    mc.dropMessage("You can only message GM's once in 2 minutes.");
                    return;
                } else {
                    mc.dropMessage("Sending message..");
                    c.getChannelServer().broadcastGMPacket(MaplePacketCreator.serverNotice(5,
                            "[" + c.getPlayer().getName() + "] " + StringUtil.joinStringFrom(splitted, 1)));
                    gmUsages.put(c.getPlayer().getId(), System.currentTimeMillis());
                    mc.dropMessage("Done, please wait for a reply.");
                }
            } else {
                mc.dropMessage("Sending message..");
                c.getChannelServer().broadcastGMPacket(MaplePacketCreator.serverNotice(5,
                        "[" + c.getPlayer().getName() + " - GM Message] " + StringUtil.joinStringFrom(splitted, 1)));
                gmUsages.put(c.getPlayer().getId(), System.currentTimeMillis());
                mc.dropMessage("Done, please wait for a reply.");
            }
        } else if (splitted[0].equalsIgnoreCase("~help") || splitted[0].equalsIgnoreCase("!help")) {
            mc.dropMessage("==HurricaneMS Commands==");
            for (CommandDefinition cd : getDefinition()) {
                if (!cd.getCommand().equalsIgnoreCase("help")) {
                    mc.dropMessage("~" + cd.getCommand() + " - " + cd.getHelp());
                }
            }
        } else if (splitted[0].equalsIgnoreCase("~battleshiphp") || splitted[0].equalsIgnoreCase("!battleshiphp")) {
            if (c.getPlayer().getBattleShipHp() > 0) {
                mc.dropMessage("Your battleship currently has " + c.getPlayer().getBattleShipHp() + " HP.");
            } else {
                mc.dropMessage("You are not on a battleship.");
            }
        } else if (splitted[0].equalsIgnoreCase("~rates") || splitted[0].equalsIgnoreCase("!rates")) {
            mc.dropMessage("EXP Rate: " + c.getChannelServer().getExpRate());
            mc.dropMessage("Meso Rate: " + c.getChannelServer().getMesoRate());
            mc.dropMessage("Drop Rate: " + c.getChannelServer().getDropRate());
        } else if (splitted[0].equalsIgnoreCase("~donator") || splitted[0].equalsIgnoreCase("!donator")) {
            NPCScriptManager.getInstance().start(c, 9201001, "donatorshop", null);
            mc.dropMessage("Donator shop opened!");
        } else if (splitted[0].equalsIgnoreCase("~freemarket") || splitted[0].equalsIgnoreCase("!freemarket")) {
            if (c.getPlayer().getMapId() == 910000000) {
                mc.dropMessage("You are already in Free Market.");
                return;
            } else {
                c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET);
                MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
                c.getPlayer().changeMap(map, map.getPortal("st00"));
            }
        } else if (splitted[0].equalsIgnoreCase("~bosshp") || splitted[0].equalsIgnoreCase("!bosshp")) {
            List<MapleMapObject> mobs = c.getPlayer().getMap().getMapObjectsInRange(new Point(0,0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject mob : mobs) {
                MapleMonster m = (MapleMonster) mob;
                if (m.isBoss()) {
                    mc.dropMessage(m.getName() + " with " + m.getHp() + "/" + m.getMaxHp() + " HP");
                }
            }
        } else if (splitted[0].equalsIgnoreCase("~foj") || splitted[0].equalsIgnoreCase("!foj")) {
            NPCScriptManager.getInstance().start(c, 9010003, null, null);
        }
    }

    public int itemQuantity(MapleClient c, int itemid) {
        MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
        MapleInventory iv = c.getPlayer().getInventory(type);
        int possesed = iv.countById(itemid);
        return possesed;
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
			new CommandDefinition("storage", "", "Opens storage from anywhere", 0),
			new CommandDefinition("str", "", "Add your stats very fast", 0),
			new CommandDefinition("int", "", "Add your stats very fast", 0),
			new CommandDefinition("dex", "", "Add your stats very fast", 0),
			new CommandDefinition("luk", "", "Add your stats very fast", 0),
			new CommandDefinition("online", "", "Check who is online", 0),
			new CommandDefinition("clearinv", "", "clear your slots", 0),
			new CommandDefinition("switch", "", "Switches characters without relogging.", 0),
			new CommandDefinition("dropall", "", "Drops all items in your inventory.", 0),
			new CommandDefinition("gm", "", "Sends all online GameMasters a message.", 0),
			new CommandDefinition("help", "", "Shows help.", 0),
			new CommandDefinition("battleshiphp", "", "Shows your battleship HP.", 0),
			new CommandDefinition("rates", "", "Shows the current rates.", 0),
			new CommandDefinition("donator", "", "Opens the donator shop.", 0),
			new CommandDefinition("freemarket", "", "Warps you to free market.", 0),
			new CommandDefinition("bosshp", "", "Shows all bosses HP.", 0),
			new CommandDefinition("foj", "", "Opens the FOJ (Field of judgement) NPC.", 0),
		};
    }
}