/*
 *@author Jvlaple
 */


function act() {
    rm.mapMessage(6, "Horntail has appeared from the depths of his cave.");
    rm.changeMusic("Bgm14/HonTale");
    rm.spawnMonster(8810026, 76, 260);
    rm.getReactor().getMap().addMapTimer(12 * 60 * 60, 240000000);
}  