package de.DiscordBot.ChatLog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javautils.Html.HtmlAttribute;
import javautils.Html.HtmlDocument;
import javautils.Html.HtmlTag;
import javautils.Html.Extended.Tags.ListTag;
import javautils.Html.Extended.Tags.TableTag;
import javautils.Html.Extended.Tags.TitleTag;
import javautils.RestAPI.HtmlAction;
import javautils.RestAPI.RestAPI;
import javautils.RestAPI.RestAPIActionSet;
import javautils.tcpmanager.TcpServerMode;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

public class ChatLogInterface {

	ChatLog cl;
	
	public ChatLogInterface(ChatLog cl, final JDA bot) {
		this.cl = cl;
		RestAPIActionSet raas = new RestAPIActionSet();
		HtmlDocument listServers = new HtmlDocument();
		listServers.addTag(new TitleTag("Servers:", 1));
		listServers.addTag(new HtmlTag("br"));
		TableTag servers = new TableTag();
		servers.addColumn("Name");
		servers.addColumn("Channels");
		servers.addColumn("Messages");
		listServers.addTag(servers.addAttribute("id", new HtmlAttribute("list")));
		
		HtmlDocument listChannels = new HtmlDocument();
		
		
		HtmlDocument listMessages = new HtmlDocument();
		
		raas.addAction("/", new HtmlAction(listServers) {

			@Override
			public HtmlDocument createModifiedHtmlDocument(HtmlDocument clone, HashMap<String, String> conf,
					HashMap<String, String> vars) {
				TableTag tt = (TableTag) clone.getTag("table").get(0);
				for(File s : cl.logFolder.listFiles()) {
					if(s.isDirectory()) {
						String sName = s.getName();
						int channels = cl.listChannels(sName).size();
						long messages = cl.countMessages(sName);
						tt.addRow(sName, channels +"", messages+"");
					}
				}
				return clone;
			}
			
		});
		raas.addAction("/*/*", new HtmlAction(listMessages) {

			@Override
			public HtmlDocument createModifiedHtmlDocument(HtmlDocument clone, HashMap<String, String> conf,
					HashMap<String, String> vars) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		raas.addAction("/*", new HtmlAction(listChannels) {
			
			@Override
			public HtmlDocument createModifiedHtmlDocument(HtmlDocument clone, HashMap<String, String> conf,
					HashMap<String, String> vars) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		RestAPI.startRestAPIServer(raas, TcpServerMode.NO_ENCRYPTION.setPort(7070));
	}
	
}
