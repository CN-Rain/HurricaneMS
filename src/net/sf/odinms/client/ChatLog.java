/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. Then suck a cock.
 */

package net.sf.odinms.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ChatLog {
	private String fuck;
	
	public static class ChatEntry {
		private Date time;
		private String msg;
		
		public ChatEntry(String msg) {
			this.time = new Date();
			this.msg = msg;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public Date getTime() {
			return time;
		}

		public void setTime(Date time) {
			this.time = time;
		}
	}
	
	private List<String> chatLog = new LinkedList<String>();
	
	private ChatLog() {}
	
	public static ChatLog load(String charName) {
		ChatLog ret = new ChatLog();
		ret.fuck = charName;
		
		try {
			File root = new File("ChatLog/");
			if (!root.exists() || !root.isDirectory()) {
				root.mkdir();
			}
			File fl = new File("ChatLog/" + charName + ".log");
			if (!fl.exists()) {
				fl.createNewFile();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fl)));
			String ln = null;
			while ((ln = br.readLine()) != null) {
				ret.chatLog.add(ln);
			}
			br.close();
		} catch (IOException ex) {
			Logger.getLogger(ChatLog.class.getName()).log(Level.SEVERE, null, ex);
		}
		return ret;
	}
	
	public void save() {
		BufferedWriter out = null;
		try {
			File flog = new File("ChatLog/" + fuck + ".log");
			out = new BufferedWriter(new FileWriter(flog));
			PrintWriter pw = new PrintWriter(out);
			for (String s : chatLog) {
				pw.println(s);
			}
		} catch (IOException ex) {
			Logger.getLogger(ChatLog.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
				Logger.getLogger(ChatLog.class.getName()).log(Level.SEVERE, null, ex);
			}
		}		
	}
	
	public void log(ChatEntry ce) {
		chatLog.add("[" + DateFormat.getInstance().format(ce.getTime()) + "] " + ce.getMsg());
	}
}