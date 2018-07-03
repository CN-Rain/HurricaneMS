package net.sf.odinms.net.channel.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
//import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Bassoe
 */
public class OXHandler {
    private static MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
	
        public static String getOXQuestion(int imgdir, int id){
            List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
            MapleData itemsData;
            itemsData = stringData.getData("OXQuiz.img").getChildByPath("" + imgdir + "");
	MapleData itemFolder = itemsData.getChildByPath("" + id + "");
	    int itemId = Integer.parseInt(itemFolder.getName());
	    String itemName = MapleDataTool.getString("q", itemFolder, "NO-NAME");
	    itemPairs.add(new Pair<Integer, String>(itemId, itemName));
	
        
        return itemPairs.toString();
        }
        
        public static int getOXAnswer(int imgdir, int id){
            List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
            MapleData itemsData;
            itemsData = stringData.getData("OXQuiz.img").getChildByPath("" + imgdir + "");
            MapleData itemFolder = itemsData.getChildByPath("" + id + "");
            int bla = MapleDataTool.getInt(itemFolder.getChildByPath("a"));
	
        
        return bla;
        }
        
        public static String getOXExplain(int imgdir, int id){
            List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
            MapleData itemsData;
            itemsData = stringData.getData("OXQuiz.img").getChildByPath("" + imgdir + "");
	MapleData itemFolder = itemsData.getChildByPath("" + id + "");
	    int itemId = Integer.parseInt(itemFolder.getName());
	    String itemName = MapleDataTool.getString("d", itemFolder, "NO-NAME");
	    itemPairs.add(new Pair<Integer, String>(itemId, itemName));
	
        
        return itemPairs.toString();
        }
}