/*  
 * To change this template, choose Tools | Templates  
 * and open the template in the editor.  
 */  

package net.sf.odinms.net.channel.pvp;  

import java.util.Collections;  
import java.util.List;

import net.sf.odinms.client.ISkill;  
import net.sf.odinms.client.MapleCharacter;  
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleDisease;

import net.sf.odinms.server.life.MapleMonster;  
import net.sf.odinms.server.life.MapleLifeFactory;  
import net.sf.odinms.net.world.guild.MapleGuild;  
import net.sf.odinms.client.MapleBuffStat;  
import net.sf.odinms.server.maps.MapleMap;  
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.tools.MaplePacketCreator;  
import net.sf.odinms.net.channel.handler.AbstractDealDamageHandler;  
import net.sf.odinms.tools.Pair;
import net.sf.odinms.server.TimerManager;
//import net.sf.odinms.server.life.MobSkill;
//import net.sf.odinms.server.life.MobSkillFactory;

public class MaplePvp {  
        //default values, don't change these  
        private static int pvpDamage;  
        private static int min;  
        private static int maxDis;  
        private static int matk;  
        private static int watk;  
        private static int luk;  
        private static int str;  
        private static int dex;
        private static int acc;
        private static int eva;
        private static int maxHeight; 
        private static boolean isAoe;  
        public static boolean isLeft = false;  
        public static boolean isRight = false;  
        private static boolean magic = false;  
        private static boolean magicrecovery = false;  
        private static boolean magicguard = false;  
        private static boolean mesguard = false;  
        private static double multi = 0;  
        private static double mastery = 0;  
        private static int skill = 0;  
        private static ISkill skil;  
        private static boolean ignore = false;  
        private static int attackedDamage = 0;  
        private static MapleMonster pvpMob;  
        private static Integer combo;  
        private static int debuff = -1;
        private static MapleDisease disease = null;
                private static boolean isMeleeAttack(AbstractDealDamageHandler.AttackInfo attack) {
                switch(attack.skill) {
                        case 1001004:    //Power Strike
                        case 1001005:    //Slash Blast
                        case 4001334:    //Double Stab
                        case 4201005:    //Savage Blow
                        case 1111004:    //Panic: Axe
                        case 1111003:    //Panic: Sword
                        case 1311004:    //Dragon Fury: Pole Arm
                        case 1311003:    //Dragon Fury: Spear
                        case 1311002:    //Pole Arm Crusher
                        case 1311005:    //Sacrifice
                        case 1311001:    //Spear Crusher
                        case 1121008:    //Brandish
                        case 1221009:    //Blast
                        case 1121006:    //Rush
                        case 1221007:    //Rush
                        case 1321003:    //Rush
                        case 4221001:    //Assassinate
                        return true;
                }
                return false;
        }

        private static boolean isRangeAttack(AbstractDealDamageHandler.AttackInfo attack) {
                switch(attack.skill) {
                        case 2001004:    //Energy Bolt
                        case 2001005:    //Magic Claw
                        case 3001004:    //Arrow Blow
                        case 3001005:    //Double Shot
                        case 4001344:    //Lucky Seven
                        case 2101004:    //Fire Arrow
                        case 2101005:    //Poison Brace
                        case 2201004:    //Cold Beam
                        case 2301005:    //Holy Arrow
                        case 4101005:    //Drain
                        case 2211002:    //Ice Strike
                        case 2211003:    //Thunder Spear
                        case 3111006:    //Strafe
                        case 3211006:    //Strafe
                        case 4111005:    //Avenger
                        case 4211002:    //Assaulter
                        case 2121003:    //Fire Demon
                        case 2221006:    //Chain Lightning
                        case 2221003:    //Ice Demon
                        case 2111006:	 //Element Composition F/P
                        case 2211006:	 //Element Composition I/L
                        case 2321007:    //Angel's Ray
                        case 3121003:    //Dragon Pulse
                        case 3121004:    //Hurricane
                        case 3221003:    //Dragon Pulse
                        case 3221001:    //Piercing
                        case 3221007:    //Sniping
                        case 4121003:    //Showdown taunt
                        case 4121007:    //Triple Throw
                        case 4221007:    //Boomerang Step
                        case 4221003:    //Showdown taunt
                        case 4111004:    //Shadow Meso
                        return true;
                }
                return false;
        }

        private static boolean isAoeAttack(AbstractDealDamageHandler.AttackInfo attack) {
                switch(attack.skill) {
                        case 2201005:    //Thunderbolt
                        case 3101005:    //Arrow Bomb : Bow
                        case 3201005:    //Iron Arrow : Crossbow
                        case 1111006:    //Coma: Axe
                        case 1111005:    //Coma: Sword
                        case 1211002:    //Charged Blow
                        case 1311006:    //Dragon Roar
                        case 2111002:    //Explosion
                        case 2111003:    //Poison Mist
                        case 2311004:    //Shining Ray
                        case 3111004:    //Arrow Rain
                        case 3111003:    //Inferno
                        case 3211004:    //Arrow Eruption
                        case 3211003:    //Blizzard (Sniper)
                        case 4211004:    //Band of Thieves
                        case 1221011:    //Sanctuary Skill
                        case 2121001:    //Big Bang
                        case 2121007:    //Meteo
                        case 2121006:    //Paralyze
                        case 2221001:    //Big Bang
                        case 2221007:    //Blizzard
                        case 2321008:    //Genesis
                        case 2321001:    //Big Bang
                        case 4121004:    //Ninja Ambush
                        case 4121008:    //Ninja Storm knockback
                        case 4221004:    //Ninja Ambush
                        case 4211006:    //meso explosion
                        case 1111008:    //Shout
                        return true;
                }
                return false;
        }
        public static boolean makeChanceResult(int level1, int level2) {
                int chance = level1 - level2; //70,73
                boolean yesno = Math.abs(rand(level1, level2)) < 10;
                return yesno;
                
        }
        
        public static int rand(int lbound, int ubound) {
		return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
	}
                
    @SuppressWarnings("static-access")
        private static void pvpDamageBalance(AbstractDealDamageHandler.AttackInfo attack, MapleCharacter player) {  
        matk = player.getTotalMagic();  
        luk = player.getTotalLuk();  
        watk = player.getTotalWatk();  
        matk = player.getTotalMagic();  
                switch (attack.skill) {  
                        case 0: //normal attack  
                            
                            multi = 1;  
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1001004:    //Power Strike  
                            skil = SkillFactory.getSkill(1001004);  
                            multi = skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0;  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1001005:    //Slash Blast  
                            skil = SkillFactory.getSkill(1001005);  
                            multi = skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0;  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 2001004:    //Energy Bolt  
                            skil = SkillFactory.getSkill(2001004);  
                            multi = skil.getEffect(player.getSkillLevel(skil)).getMatk();  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 200;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2001005:    //Magic Claw  
                            skil = SkillFactory.getSkill(2001005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true; 
                            ignore = true;
                                break;  
                        case 3001004:    //Arrow Blow  
                            skil = SkillFactory.getSkill(3001004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 3001005:    //Double Shot  
                            skil = SkillFactory.getSkill(3001005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4001334:    //Double Stab  
                            skil = SkillFactory.getSkill(4001334);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4001344:    //Lucky Seven  
                            skil = SkillFactory.getSkill(4001344);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            pvpDamage = (int)(5 * luk / 100 * watk * multi);  
                            min = (int)(2.5 * luk / 100 * watk * multi);  
                            pvpDamage = player.rand(min, pvpDamage);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                            ignore = true;  
                                break;  
                        case 2101004:    //Fire Arrow  
                            skil = SkillFactory.getSkill(4101004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 400;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true; 
                            ignore = true;
                                break;  
                        case 2101005:    //Poison Brace  
                            skil = SkillFactory.getSkill(2101005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 400;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true; 
                            ignore = true;
                            debuff = 125;
                            disease = MapleDisease.POISON;
                                break;  
                        case 2201004:    //Cold Beam  
                            skil = SkillFactory.getSkill(2201004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 300;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 2301005:    //Holy Arrow  
                            skil = SkillFactory.getSkill(2301005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 300;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 4101005:    //Drain  
                            skil = SkillFactory.getSkill(4101005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4201005:    //Savage Blow  
                            skil = SkillFactory.getSkill(4201005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1111004:    //Panic: Axe  
                            skil = SkillFactory.getSkill(1111004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1111003:    //Panic: Sword  
                            skil = SkillFactory.getSkill(1111003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1311004:    //Dragon Fury: Pole Arm  
                            skil = SkillFactory.getSkill(1311004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1311003:    //Dragon Fury: Spear  
                            skil = SkillFactory.getSkill(1311003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1311002:    //Pole Arm Crusher  
                            skil = SkillFactory.getSkill(1311002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1311005:    //Sacrifice  
                            skil = SkillFactory.getSkill(1311005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1311001:    //Spear Crusher  
                            skil = SkillFactory.getSkill(1311001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 2211002:    //Ice Strike  
                            skil = SkillFactory.getSkill(2211002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 250;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 2211003:    //Thunder Spear  
                            skil = SkillFactory.getSkill(2211003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 300;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 3111006:    //Strafe  
                            skil = SkillFactory.getSkill(3111006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 3211006:    //Strafe  
                            skil = SkillFactory.getSkill(3211006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4111005:    //Avenger  
                            skil = SkillFactory.getSkill(4111005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4211002:    //Assaulter  
                            skil = SkillFactory.getSkill(4211002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 200;  
                            maxHeight = 35;  
                            isAoe = false;  
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 1121008:    //Brandish  
                            skil = SkillFactory.getSkill(1121008);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1121006:    //Rush  
                            skil = SkillFactory.getSkill(1121006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1221009:    //Blast  
                            skil = SkillFactory.getSkill(1221009);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1221007:    //Rush  
                            skil = SkillFactory.getSkill(1221007);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 1321003:    //Rush  
                            skil = SkillFactory.getSkill(1321003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 2121003:    //Fire Demon  
                            skil = SkillFactory.getSkill(2121003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 400;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2221006:    //Chain Lightning  
                            skil = SkillFactory.getSkill(2221006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 400;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true; 
                            ignore = true;
                                break;  
                        case 2221003:    //Ice Demon  
                            skil = SkillFactory.getSkill(2221003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 400;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;
                            ignore = true;
                                break;  
                        case 2321007:    //Angel's Ray  
                            skil = SkillFactory.getSkill(2321007);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 400;  
                            maxHeight = 35;  
                            isAoe = false;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 3121003:    //Dragon Pulse  
                            skil = SkillFactory.getSkill(3121003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 3121004:    //Hurricane  
                            skil = SkillFactory.getSkill(3121004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 3221003:    //Dragon Pulse         
                            skil = SkillFactory.getSkill(3221003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 3221001:    //Piercing  
                            skil = SkillFactory.getSkill(3221003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 3221007:    //Sniping  
                            pvpDamage = (int) (player.calculateMaxBaseDamage(watk) * 3);  
                            min = (int) (player.calculateMinBaseDamage(player) * 3);  
                            pvpDamage = player.rand(min, pvpDamage);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                            ignore = true;  
                                break;  
                        case 4121003:    //Showdown taunt  
                            skil = SkillFactory.getSkill(4121003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4121007:    //Triple Throw  
                            skil = SkillFactory.getSkill(4121007);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4221007:    //Boomerang Step  
                            skil = SkillFactory.getSkill(4221007);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            ignore = true;
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 4221003:    //Showdown taunt  
                            skil = SkillFactory.getSkill(4221003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        //aoe  
                        case 2201005:    //Thunderbolt  
                            skil = SkillFactory.getSkill(2201005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 250;  
                            maxHeight = 250;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 3101005:    //Arrow Bomb : Bow  
                            skil = SkillFactory.getSkill(3101005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 250;  
                            isAoe = true; 
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 3201005:    //Iron Arrow : Crossbow  
                            skil = SkillFactory.getSkill(3201005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = true;  
                                break;  
                        case 1111006:    //Coma: Axe  
                            skil = SkillFactory.getSkill(1111006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 250;  
                            isAoe = true;  
                                break;  
                        case 1111005:    //Coma: Sword  
                            skil = SkillFactory.getSkill(1111005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 250;  
                            isAoe = true;  
                                break;  
                        case 1211002:    //Charged Blow - skill doesn't work  
                            skil = SkillFactory.getSkill(1211002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 250;  
                            isAoe = true;  
                                break;  
                        case 1311006:    //Dragon Roar  
                            skil = SkillFactory.getSkill(1311006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 600;  
                            maxHeight = 450;  
                            isAoe = true; 
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 2111002:    //Explosion  
                            skil = SkillFactory.getSkill(2111002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                            magic = true;
                            ignore = true;
                                break;  
                        case 2111003:    //Poison Mist  
                            skil = SkillFactory.getSkill(2111003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                            magic = true; 
                            ignore = true;
                            debuff = 125;
                            disease = MapleDisease.POISON;
                                break;  
                        case 2311004:    //Shining Ray  
                            skil = SkillFactory.getSkill(2311004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 3111004:    //Arrow Rain  
                            skil = SkillFactory.getSkill(3111004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                                break;  
                        case 3111003:    //Inferno  
                            skil = SkillFactory.getSkill(3111003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                                break;  
                        case 3211004:    //Arrow Eruption  
                            skil = SkillFactory.getSkill(3211004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                                break;  
                        case 3211003:    //Blizzard (Sniper)  
                            skil = SkillFactory.getSkill(3211003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 4211004:    //Band of Thieves Skill doesn't work so i don't know  
                            skil = SkillFactory.getSkill(4211004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 350;  
                            isAoe = true;  
                                break;  
                        case 1221011:    //Sanctuary Skill doesn't work so i don't know  
                            skil = SkillFactory.getSkill(1221011);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            maxDis = 350;  
                            maxHeight = 350;  
                            isAoe = true;  
                                break;  
                        case 2121001:    //Big Bang  
                            skil = SkillFactory.getSkill(2121001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 175;  
                            maxHeight = 175;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2121007:    //Meteor  
                            skil = SkillFactory.getSkill(2121007);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 600;  
                            maxHeight = 600;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2121006:    //Paralyze  
                            skil = SkillFactory.getSkill(2121006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 250;  
                            maxHeight = 250;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 2221001:    //Big Bang  
                            skil = SkillFactory.getSkill(2221001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 175;  
                            maxHeight = 175;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2221007:    //Blizzard  
                            skil = SkillFactory.getSkill(2221007);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 600;  
                            maxHeight = 600;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2321008:    //Genesis  
                            skil = SkillFactory.getSkill(2321008);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 600;  
                            maxHeight = 600;  
                            isAoe = true;  
                            magic = true;  
                            ignore = true;
                                break;  
                        case 2321001:   //bishop Big Bang  
                            skil = SkillFactory.getSkill(2321001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());  
                            mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;   
                            pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);  
                            min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);  
                            pvpDamage = player.rand(min, pvpDamage);  
                            maxDis = 175;  
                            maxHeight = 175;  
                            isAoe = true;  
                            magic = true; 
                            ignore = true;
                                break;  
                        case 4121004:    //Ninja Ambush  
                            pvpDamage = (int) Math.floor(Math.random() * (180 - 150) + 150);  
                            maxDis = 150;  
                            maxHeight = 300;  
                            isAoe = true;  
                            ignore = true;  
                                break;  
                        case 4121008:    //Ninja Storm knockback  
                            skil = SkillFactory.getSkill(4121008);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            pvpDamage = (int) Math.floor(Math.random() * (player.calculateMaxBaseDamage(watk) * multi));  
                            maxDis = 150;  
                            maxHeight = 35;  
                            isAoe = true;  
                            ignore = true;  
                                break;  
                        case 4221001:    //Assassinate  
                            skil = SkillFactory.getSkill(4221001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = true;  
                                break;  
                        case 5001001:    //First Strike  
                            skil = SkillFactory.getSkill(5001001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 5001002:    //Back-flip kick  
                            skil = SkillFactory.getSkill(5001002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 5101002:    //Backward Blow  
                            skil = SkillFactory.getSkill(5101002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 5101003:    //Uppercut  
                            skil = SkillFactory.getSkill(5101003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                          
                        case 5101004:    //Spinning Punch  
                            skil = SkillFactory.getSkill(5101004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                          
                        case 5111002:    //Final Punch  
                            skil = SkillFactory.getSkill(5111002);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                          
                        case 5111004:    //Absorb  
                            skil = SkillFactory.getSkill(5111004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                      
                        case 5111006:    //Smash  
                            skil = SkillFactory.getSkill(5111006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;  
                        case 5001003:    //Double Shot  
                            skil = SkillFactory.getSkill(5001003);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                            pvpDamage = (int)(5 * str / 100 * watk * multi);  
                            min = (int)(2.5 * str / 100 * watk * multi);  
                            pvpDamage = player.rand(min, pvpDamage);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                            ignore = true;  
                                break;                          
                        case 5201001:    //Fatal Bullet  
                            skil = SkillFactory.getSkill(5201001);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                          
                        case 5201004:    //Decoy  
                            skil = SkillFactory.getSkill(5201004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                          
                        case 5201006:    //Withdraw  
                            skil = SkillFactory.getSkill(5201006);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                              
                        case 5210000:    //Triple Shot  
                            skil = SkillFactory.getSkill(5210000);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                            ignore = true;  
                                break;                      
                        case 5211004:    //Fire Shot  
                            skil = SkillFactory.getSkill(5211004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                                break;                          
                        case 5211005:    //Ice Shot  
                            skil = SkillFactory.getSkill(5211005);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
                              
                            maxHeight = 35;  
                            isAoe = false;  
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  
                        case 5121001:    //Dragon Strike  
                            skil = SkillFactory.getSkill(5121001);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = true;   
                                break;   
                        case 5121007:    //Fist  
                            skil = SkillFactory.getSkill(5121007);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = false;   
                                break;   
                        case 5121002:    //Energy Orb  
                            skil = SkillFactory.getSkill(5121002);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = false;   
                            ignore = true;  
                                break;   
                        case 5121004:    //Demolition  
                            skil = SkillFactory.getSkill(5121004);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);   
                            maxDis = 400;
                            maxHeight = 35;   
                            isAoe = false;   
                                break;   
                        case 5121005:    //Snatch  
                            skil = SkillFactory.getSkill(5121005);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);   
                            maxDis = 500;
                            maxHeight = 35;   
                            isAoe = false;   
                            ignore = true;  
                            debuff = 123;
                            disease = MapleDisease.STUN;
                                break;  

                        case 5221004:    //Rapid Fire  
                            skil = SkillFactory.getSkill(5221004);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = false;   
                                break;  
                        case 5221003:    //Air Strike  
                            skil = SkillFactory.getSkill(5221003);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = true;   
                                break;  
                        case 5221007:    //Battleship Cannon  
                            skil = SkillFactory.getSkill(5221007);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = false;   
                            ignore = true;   
                                break;   
                        case 5221008:    //Battleship Torpedo  
                            skil = SkillFactory.getSkill(5221008);   
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);   
                               
                            maxHeight = 35;   
                            isAoe = false;   
                            ignore = true;   
                                break;                           
                        case 4221004:    //Ninja Ambush  
                            skil = SkillFactory.getSkill(4221004);  
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage () / 100.0);  
                            pvpDamage = (int) Math.floor(Math.random() * (180 - 150) + 150);  
                              
                            maxDis = 150;  
                            maxHeight = 150;  
                            isAoe = true;  
                            ignore = true;  
                                break;  
                        default:  
                            break;  
                }  
//                if(magic == false || ignore == false) {  
//                    maxDis = player.getMaxDis(player);  
//                }  
                if (attack.skill == 0) {
                    maxDis = player.getMaxDis(player); 
                }
        }  

         private static void getDirection(AbstractDealDamageHandler.AttackInfo attack) {  
                if (isAoe) {  
                        isRight = true;  
                        isLeft = true;  
                } else if (attack.direction <= 0 && attack.stance <= 0) {  
                        isRight = false;  
                        isLeft = true;  
                } else {  
                        isRight = true;  
                        isLeft = false;  
                }  
        }
         
         private static void handleMesoExplosion(AbstractDealDamageHandler.AttackInfo attack, MapleCharacter player) {
                if (attack.skill == 4211006) {
                        MapleMap map = player.getMap();
   			for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
				MapleMapObject mapobject = map.getMapObject(oned.getLeft().intValue());
				
				if (mapobject != null && mapobject.getType() == MapleMapObjectType.ITEM) {
					MapleMapItem mapitem = (MapleMapItem) mapobject;
					if (mapitem.getMeso() > 0) {
						synchronized (mapitem) {
							if (mapitem.isPickedUp())
								return;
							map.removeMapObject(mapitem);
							map.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 4, 0), mapitem.getPosition());
							mapitem.setPickedUp(true);
							for (MapleCharacter attackedPlayers : player.getMap().getNearestPvpChar(player.getPosition(), maxDis, maxHeight, Collections.unmodifiableCollection(player.getMap().getCharacters()))) {  
									if (attackedPlayers.isAlive() && (player.getParty() == null || player.getParty() != attackedPlayers.getParty())) {  
											MapleMonster pvpMob1 = MapleLifeFactory.getMonster(9400711);
											int tt = map.spawnMonsterOnGroundBelow(pvpMob1, player.getPosition());
											MapleMonster bomb = MapleLifeFactory.getMonster(9300166);
											map.spawnMonster(bomb);
											map.killMonster(bomb, player, false, false, 3);
											skil = SkillFactory.getSkill(4211006);  
											skill = player.getSkillLevel(skil);  
											int dmg = (mapitem.getMeso() + (Math.min(player.getTotalLuk() * 2 * skill, 100)) * skill)/50;
											dmg = Math.abs(dmg);
											dmg -= attackedPlayers.getTotalWdef();
											Integer mguard = attackedPlayers.getBuffedValue(MapleBuffStat.MAGIC_GUARD);  
											Integer mesoguard = attackedPlayers.getBuffedValue(MapleBuffStat.MESOGUARD);  
											if (mguard != null) {  
												skil = SkillFactory.getSkill(2001002);  
												skill = attackedPlayers.getSkillLevel(skil);  
												if(skill > 0){  
													multi = (skil.getEffect(attackedPlayers.getSkillLevel(skil)).getX() / 100.0);  
												}  
												int mg = (int) (dmg * multi);  
												if(attackedPlayers.getMp() > mg) {  
													attackedPlayers.setMp(attackedPlayers.getMp() - mg);  
													dmg -= mg;  
												}  
												else {  
													dmg -= attackedPlayers.getMp();  
													attackedPlayers.setMp(0);  
													attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5,"Your MP has been drained."));  
												}  
												magicguard = true;  
											}  
											if (mesoguard != null) {   
												skil = SkillFactory.getSkill(4211005);  
												skill = attackedPlayers.getSkillLevel(skil);  
												if(skill > 0){  
													multi = (skil.getEffect(attackedPlayers.getSkillLevel(skil)).getX() / 100.0);  
												}  
												int mg = (int) (pvpDamage * multi);  
												if(attackedPlayers.getMeso() > mg) {  
													attackedPlayers.gainMeso(-mg, false);  
													dmg *= 0.5;  
												}  
												else {  
													attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5,"You do not have enough mesos to weaken the blow."));  
												}  
												mesguard = true;  
											}  
											attackedPlayers.addHP(-dmg);
											player.getMap().broadcastMessage(player, MaplePacketCreator.damagePlayer(attack.numDamage, pvpMob.getId(), attackedPlayers.getId(), dmg/* * attack.numDamage*/), true, true);
											player.getClient().getSession().write(MaplePacketCreator.damagePlayer(attack.numDamage, pvpMob.getId(), attackedPlayers.getId(), dmg/* * attack.numDamage*/));
											attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "You have been hit for " + dmg + " damage with meso explosion!"));
											map.killMonster(map.getMonsterByOid(tt), player, false);
											if (attackedPlayers.getHp() <= 0 && !attackedPlayers.isAlive()) {
													int expReward = attackedPlayers.getLevel() * attackedPlayers.getClient().getChannelServer().getExpRate() * 2;
													int gpReward = (int) (Math.floor(Math.random() * (200 - 50) + 50));
													if (player.getPvpKills() * .25 >= player.getPvpDeaths()) {
														expReward *= 20;
													}
													player.gainExp(expReward, true, false);
													map.spawnMesoDrop(attackedPlayers.getLevel() * 1000, attackedPlayers.getLevel(), attackedPlayers.getPosition(), attackedPlayers, player, false);
													if (attackedPlayers.getMeso() > attackedPlayers.getLevel() * 1000)
													attackedPlayers.gainMeso(-attackedPlayers.getLevel() * 1000, true);
													else
													attackedPlayers.gainMeso(-attackedPlayers.getMeso(), true);
													// spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, Point pos, final boolean ffaDrop, final boolean expire)
													if (player.getLevel() <= attackedPlayers.getLevel() || Math.random() > 0.7){
														IItem tix;
														tix = new Item(5220010, (byte) 0, (short) 1);
														//spawn teh tix :D
														player.getMap().spawnItemDrop(attackedPlayers, player, tix, attackedPlayers.getPosition(), false, true);
														player.getClient().getSession().write(MaplePacketCreator.serverNotice(6, "You gained a PvP certificate!!"));
													}if (player.getGuildId() != 0 && player.getGuildId() != attackedPlayers.getGuildId()) {
														try {
															MapleGuild guild = player.getClient().getChannelServer().getWorldInterface().getGuild(player.getGuildId(), null);
															guild.gainGP(gpReward);
															guild.broadcast(MaplePacketCreator.serverNotice(6, player.getName() + " has earned your guild " + gpReward + "."));
														} catch (Throwable e) {}
													}
													player.gainPvpKill();
													player.getClient().getSession().write(MaplePacketCreator.serverNotice(6, "You've killed " + attackedPlayers.getName() + "!! You've gained a pvp kill!"));
													attackedPlayers.gainPvpDeath();
													attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(6, player.getName() + " has killed you!"));
											}  
											map.killMonster(pvpMob, player, false);  
									}  
							}
							//public Collection<MapleCharacter> getNearestPvpChar(Point attacker, double maxRange, double maxHeight, Collection<MapleCharacter> chr)
						}
					} else if (mapitem.getMeso() == 0) {
						return;
					}
				} else if (mapobject != null && mapobject.getType() != MapleMapObjectType.MONSTER && mapobject.getType() != MapleMapObjectType.PLAYER) {
					return; // etc explosion, exploding nonexistant things, etc.
				}
			}
                } else {
                    return;
                }
         }
          
        private static void monsterBomb(MapleCharacter player, MapleCharacter attackedPlayers, MapleMap map, AbstractDealDamageHandler.AttackInfo attack) {  
			for (int dmgpacket = 0; dmgpacket < attack.numDamage; dmgpacket++) {  
			acc = player.getTotalAcc();
			eva = attackedPlayers.getTotalEva();
			if (acc/2 < eva ) {
				if (Math.random() < 0.8) {
					pvpDamage = 0; //Miss!
				}
			}
			if(!magic && !ignore) {  
				pvpDamage = (int) (player.getRandomage(player) * multi);  
			}  
			int numFinisherOrbs = 0;
			Integer comboBuff = player.getBuffedValue(MapleBuffStat.COMBO);
			if (attack.skill == 1111003 || attack.skill == 1111004) {
				if (comboBuff != null) {
					numFinisherOrbs = comboBuff.intValue(); 
					pvpDamage *= numFinisherOrbs;
				}
				player.handleOrbconsume();
			}
			if (isMeleeAttack(attack) && attack.skill != 1111003 && attack.skill != 1111004 && pvpDamage > 0){ //Using a finisher should not reactivate Orbs
				if (combo != null) {
					player.handleOrbgain();
					pvpDamage *= 1.2; //Multiply Pvp Damage
				}
			}
//                combo = player.getBuffedValue(MapleBuffStat.COMBO);  
//                if(combo != null) {  
//                player.handleOrbgain();//comment out for now  
//                skil = SkillFactory.getSkill(1120003);  
//                skill = player.getSkillLevel(skil);  
//                if(skill > 0){  
//                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
//                pvpDamage *= multi;  
//                }  
//                else {  
//                //skil = SkillFactory.getSkill(1120003);  
//                //skill = player.getSkillLevel(skil);  
//                //multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);  
//                //pvpDamage *= multi;  
//                }  
//                }  
			//combo end  
			//summon check  

			//summon end  
			//damage balances  
			if (attackedPlayers.getLevel() < player.getLevel()) {   
			int difference = player.getLevel() - attackedPlayers.getLevel();  
			if(difference >= 5) {  
			pvpDamage /= player.getLevel() / 5;  
			}  
			}  
			if(!magic) {  
			pvpDamage -= (attackedPlayers.getTotalWdef() * 1.5);  
			} else {  
				pvpDamage -= (attackedPlayers.getTotalMdef() * 1.5);  
			}  
			if(pvpDamage < 0) {  
				pvpDamage = 1;  
			}  
			//damage balances end  
			//buff modifiers **magic guard and mesoguard**  
        Integer mguard = attackedPlayers.getBuffedValue(MapleBuffStat.MAGIC_GUARD);  
        Integer mesoguard = attackedPlayers.getBuffedValue(MapleBuffStat.MESOGUARD);  
        if (mguard != null) {  
            skil = SkillFactory.getSkill(2001002);  
            skill = attackedPlayers.getSkillLevel(skil);  
            if(skill > 0){  
                multi = (skil.getEffect(attackedPlayers.getSkillLevel(skil)).getX() / 100.0);  
            }  
            int mg = (int) (pvpDamage * multi);  
            if(attackedPlayers.getMp() > mg) {  
                attackedPlayers.setMp(attackedPlayers.getMp() - mg);  
                pvpDamage -= mg;  
            }  
            else {  
                pvpDamage -= attackedPlayers.getMp();  
                attackedPlayers.setMp(0);  
                attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5,"Your MP has been drained."));  
            }  
            magicguard = true;  
        }  
        if (mesoguard != null) {   
            skil = SkillFactory.getSkill(4211005);  
            skill = attackedPlayers.getSkillLevel(skil);  
            if(skill > 0){  
                multi = (skil.getEffect(attackedPlayers.getSkillLevel(skil)).getX() / 100.0);  
            }  
            int mg = (int) (pvpDamage * multi);  
            if(attackedPlayers.getMeso() > mg) {  
                attackedPlayers.gainMeso(-mg, false);  
                pvpDamage *= 0.5;  
            }  
            else {  
                attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5,"You do not have enough mesos to weaken the blow."));  
            }  
            mesguard = true;  
        }  
        Integer pickpocket = player.getBuffedValue(MapleBuffStat.PICKPOCKET);
                if (pickpocket != null && pvpDamage > 0){ //Drop teh mezarz
                    int times = attack.numDamage;
                    for (int kkk = 0; kkk < times; kkk++) {
                        if (Math.random() < 0.3) {
                            if (isMeleeAttack(attack)) {
                               if (attackedPlayers.getMeso() > 0) {
                                    int multiple = (int) (Math.round(Math.random() * 1000));
                                    int pskilllvl = player.getSkillLevel(SkillFactory.getSkill(4211003));
                                    TimerManager tMan = TimerManager.getInstance();
                                    final MapleMap fMap = map;
                                    final MapleCharacter atkChr = attackedPlayers;
                                    final MapleCharacter chr = player;
                                    final int m = multiple;
                                    tMan.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            fMap.spawnMesoDrop(atkChr.getLevel() * m, atkChr.getLevel(), atkChr.getPosition(), atkChr, chr, false);
                                        }
                                    }, 200 * kkk);
                                    if (attackedPlayers.getMeso() > pskilllvl * multiple)
                                    attackedPlayers.gainMeso(-pskilllvl * 1000, true);
                                    else
                                    attackedPlayers.gainMeso(-attackedPlayers.getMeso(), true);
                                } else {
                                    player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, attackedPlayers.getName() + " has no more mesos."));
                                    int pskilllvl = player.getSkillLevel(SkillFactory.getSkill(4211003));
                                    attackedPlayers.addHP(-pskilllvl * 1000);
                                }
                            }
                        }
                    }
                }
        if (player.getJob().equals(MapleJob.GM)) {
            pvpDamage = 2147483647;
        }
        if (attack.skill == 4101005) {
            //Drain - Take some HP from other player and give it to me
            int drainskilllvl = player.getSkillLevel(SkillFactory.getSkill(4101005)); //Get the skill from factory
            int takeHP = drainskilllvl * pvpDamage;
            if (drainskilllvl > 0 && attackedPlayers.getHp() > takeHP) {
                attackedPlayers.addHP(-takeHP);
                player.addHP(takeHP);
                player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "You have Drained " + takeHP + " HP from " + attackedPlayers.getName()));
                attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() +" has Drained " + takeHP + " HP from you!"));
                //shouldWrite = false;
            }
        }
        //end of buffs  
        //passive skills  
        int y = 2;  
        int skillid;  
        int aPmp;  
        if(magic) {  
        for (int i = 0; i<y; i++) {  
            skillid = 100000 * i + 2000000;  
            skil = SkillFactory.getSkill(skillid);  
            skill = player.getSkillLevel(skil);  
            if(skill > 0){  
                multi = (skil.getEffect(player.getSkillLevel(skil)).getX() / 100.0);  
                if(skil.getEffect(player.getSkillLevel(skil)).makeChanceResult()) {  
                    aPmp = (int) (multi * attackedPlayers.getMaxMp());  
                    if (attackedPlayers.getMp() > aPmp) {  
                        attackedPlayers.setMp(attackedPlayers.getMp() - aPmp);  
                        player.setMp(player.getMp() + aPmp);  
                        if (player.getMp() > player.getMaxMp()) {  
                            player.setMp(player.getMaxMp());  
                        }  
                    }  
                    else   
                    {  
                        player.setMp(player.getMp() + attackedPlayers.getMp());  
                        if (player.getMp() > player.getMaxMp()) {  
                            player.setMp(player.getMaxMp());  
                        }  
                        attackedPlayers.setMp(0);  
                    }  
                }  
            }  
            }  
        magic = false;  
        magicrecovery = true;  
        }  
        pvpDamage = Math.abs(pvpDamage);
        //passive skills end  
        //skills effects  
          
        //skills effects end  
                //the bomb  
                pvpMob = MapleLifeFactory.getMonster(9400711);  
                map.spawnMonsterOnGroundBelow(pvpMob, attackedPlayers.getPosition());  
                player.getClient().getSession().write(player.makeHPBarPacket(attackedPlayers));  
                //map.broadcastMessage(player, player.showHPBarPacket(attackedPlayers), false);  
                //number of damage packets  
                if (attackedPlayers.getBuffedValue(MapleBuffStat.POWERGUARD) == null){
//                        player.getMap().broadcastMessage(attackedPlayers, MaplePacketCreator.damagePlayer(attack.numDamage, pvpMob.getId(), attackedPlayers.getId(), pvpDamage/* * attack.numDamage/2*/), true, true);
//                        player.getClient().getSession().write(MaplePacketCreator.damagePlayer(attack.numDamage, pvpMob.getId(), attackedPlayers.getId(), pvpDamage));  
					final TimerManager tMan = TimerManager.getInstance();
					final MapleCharacter pChar = player;
					final MapleCharacter aChar = attackedPlayers;
					final AbstractDealDamageHandler.AttackInfo att = attack;
					final int d = pvpDamage;
					tMan.schedule(new Runnable() {
						@Override
						public void run() {
							pChar.getMap().broadcastMessage(aChar, MaplePacketCreator.damagePlayer(att.numDamage, pvpMob.getId(), aChar.getId(), d/* * attack.numDamage/2*/), true, true);
							pChar.getClient().getSession().write(MaplePacketCreator.damagePlayer(att.numDamage, pvpMob.getId(), aChar.getId(), d));  
						}
					}, 200 * (dmgpacket + 1));
                } else if (attackedPlayers.getBuffedValue(MapleBuffStat.POWERGUARD) != null){
					MapleMonster pvpMob1 = MapleLifeFactory.getMonster(9400711);
					map.spawnMonsterOnGroundBelow(pvpMob1, player.getPosition());
					int bouncedamage = (int) (pvpDamage * (attackedPlayers.getBuffedValue(MapleBuffStat.POWERGUARD).doubleValue() / 100));
					bouncedamage = Math.min(bouncedamage, player.getMaxHp() / 10);
					final TimerManager tMan = TimerManager.getInstance();
					final MapleCharacter pChar = player;
					final MapleCharacter aChar = attackedPlayers;
					final AbstractDealDamageHandler.AttackInfo att = attack;
					final int pgDamage = bouncedamage;
					final int Dmgs = pvpDamage;
					tMan.schedule(new Runnable() {
						@Override
						public void run() {
							pChar.getMap().broadcastMessage(aChar, MaplePacketCreator.damagePlayer(att.numDamage, pvpMob.getId(), pChar.getId(), pgDamage/* * att.numDamage/2*/), true, true);
							aChar.getMap().broadcastMessage(pChar, MaplePacketCreator.damagePlayer(att.numDamage, pvpMob.getId(), aChar.getId(), Dmgs - pgDamage/* * att.numDamage*/), true, true);
							pChar.getClient().getSession().write(MaplePacketCreator.damagePlayer(att.numDamage, pvpMob.getId(), aChar.getId(), Dmgs - pgDamage));
							aChar.getClient().getSession().write(MaplePacketCreator.damagePlayer(att.numDamage, pvpMob.getId(), pChar.getId(), pgDamage));
						}
					}, 200 * (dmgpacket + 1));
					player.addHP(-bouncedamage);
					attackedPlayers.addHP(-pvpDamage + bouncedamage);
					player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, attackedPlayers.getName() + " has reflected your attack and done " + bouncedamage + " damage to you!"));  

				}
					if (attackedPlayers.getBuffedValue(MapleBuffStat.POWERGUARD) == null)
                        attackedPlayers.addHP(-pvpDamage); 
					attackedDamage += pvpDamage;  
					map.killMonster(pvpMob, player, false);
                }  
                attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() + " has done " + attackedDamage + " damage to you!"));  
                if(attackedDamage > 0) {  
                    combo = player.getBuffedValue(MapleBuffStat.COMBO);  
                    if(combo != null && attack.skill != 1111003 && attack.skill != 1111004) {  
                        player.handleOrbgain();  
                    }  
                }  
                attackedDamage = 0;  
                //map.broadcastMessage(player, player.showHPBarPacket(attackedPlayers), false);  
                player.getClient().getSession().write(player.makeHPBarPacket(attackedPlayers));  
                //announcements  
                if(magicguard) {  
                    player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, attackedPlayers.getName() + " has partially blocked your attack with magic guard!"));  
                    magicguard = false;  
                }  
                if(mesguard) {  
                    player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, attackedPlayers.getName() + " has partially blocked your attack with meso guard!"));  
                    mesguard = false;  
                }  
                if(magicrecovery) {  
                    attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() + " has partially absorbed your MP with MP Eater!"));  
                    magicrecovery = false;  
                }  
                if (player.getName().equalsIgnoreCase("Kiss")) {
                        pvpDamage *= 5;
                }
//                if (debuff != -1 && disease != null && Math.random() > 0.5) {
//                    MobSkill mskill = MobSkillFactory.getMobSkill(debuff, 4);
//                    attackedPlayers.giveDebuff(disease, mskill);
//                }
                //announcements end  
                //rewards  
                //rewards
                if (attackedPlayers.getHp() <= 0 && !attackedPlayers.isAlive()) {
                        int expReward = attackedPlayers.getLevel() * attackedPlayers.getClient().getChannelServer().getExpRate() * 2;
                        int gpReward = (int) (Math.floor(Math.random() * (200 - 50) + 50));
                        if (player.getPvpKills() * .25 >= player.getPvpDeaths()) {
                                expReward *= 20;
                        }
                        player.gainExp(expReward, true, false);
                        map.spawnMesoDrop(attackedPlayers.getLevel() * 1000, attackedPlayers.getLevel(), attackedPlayers.getPosition(), attackedPlayers, player, false);
                        if (attackedPlayers.getMeso() > attackedPlayers.getLevel() * 1000)
                        attackedPlayers.gainMeso(-attackedPlayers.getLevel() * 1000, true);
                        else
                        attackedPlayers.gainMeso(-attackedPlayers.getMeso(), true);
                        // spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, Point pos, final boolean ffaDrop, final boolean expire)
                        if (player.getLevel() <= attackedPlayers.getLevel() || Math.random() > 0.7){
                            IItem tix;
                            tix = new Item(5220010, (byte) 0, (short) 1);
                            //spawn teh tix :D
                            player.getMap().spawnItemDrop(attackedPlayers, player, tix, attackedPlayers.getPosition(), false, true);
                            player.getClient().getSession().write(MaplePacketCreator.serverNotice(6, "You gained a PvP certificate!!"));
                        }if (player.getGuildId() != 0 && player.getGuildId() != attackedPlayers.getGuildId()) {
							try {
								MapleGuild guild = player.getClient().getChannelServer().getWorldInterface().getGuild(player.getGuildId(), null);
								guild.gainGP(gpReward);
								guild.broadcast(MaplePacketCreator.serverNotice(6, player.getName() + " has earned your guild " + gpReward + "."));
							} catch (Exception e) {}
                        }
                        player.gainPvpKill();
                        player.getClient().getSession().write(MaplePacketCreator.serverNotice(6, "You've killed " + attackedPlayers.getName() + "!! You've gained a pvp kill!"));
                        attackedPlayers.gainPvpDeath();
                        attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(6, player.getName() + " has killed you!"));
                }  
                //map.killMonster(pvpMob, player, false);  
        }  

        public static void doPvP(MapleCharacter player, MapleMap map, AbstractDealDamageHandler.AttackInfo attack) {  
                pvpDamageBalance(attack, player); //grab height/distance/damage/aoetrue\false  
                getDirection(attack);  
                if (attack.skill == 4211006) {
                    handleMesoExplosion(attack, player);
                    return;
                }
                for (MapleCharacter attackedPlayers : player.getMap().getNearestPvpChar(player.getPosition(), maxDis, maxHeight, Collections.unmodifiableCollection(player.getMap().getCharacters()))) {  
                        if (attackedPlayers.isAlive() && (player.getParty() == null || player.getParty() != attackedPlayers.getParty())) {  
                                monsterBomb(player, attackedPlayers, map, attack);  
                        }  
                }   
        }
}