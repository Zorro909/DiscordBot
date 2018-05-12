package de.DiscordBot.ChatLog;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javautils.Html.HtmlAttribute;
import javautils.Html.HtmlDocument;
import javautils.Html.HtmlTag;
import javautils.Html.Extended.Tags.CustomHtmlTag;
import javautils.Html.Extended.Tags.LinkTag;
import javautils.Html.Extended.Tags.ListTag;
import javautils.Html.Extended.Tags.TableTag;
import javautils.Html.Extended.Tags.TitleTag;
import javautils.Parser.ParseObject;
import javautils.RestAPI.Action;
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
		listServers.setTitle("Server List");

		HtmlDocument listChannels = new HtmlDocument();
		CustomHtmlTag title = new CustomHtmlTag("h1", "title");
		listChannels.addTag(title);
		listChannels.registerCustomTag(title);
		listChannels.addTag(new HtmlTag("br"));
		TableTag channels = new TableTag();
		channels.addColumn("Name");
		channels.addColumn("Messages");
		listChannels.addTag(channels.addAttribute("id", new HtmlAttribute("list")));

		HtmlDocument listMessages = new HtmlDocument();
		CustomHtmlTag title_2 = new CustomHtmlTag("h1", "title");
		listMessages.addTag(title_2);
		listMessages.registerCustomTag(title_2);
		listMessages.addTag(new HtmlTag("br"));
		TableTag msgs = new TableTag();
		msgs.addColumn("Time");
		msgs.addColumn("Author");
		msgs.addColumn("Message");
		listMessages.addTag(msgs.addAttribute("id", new HtmlAttribute("list")));
		
		raas.addAction("/", new HtmlAction(listServers) {

			@Override
			public HtmlDocument createModifiedHtmlDocument(HtmlDocument clone, HashMap<String, String> conf,
					HashMap<String, String> vars) {
				TableTag tt = (TableTag) clone.getTag("table").get(0);
				for (File s : cl.logFolder.listFiles()) {
					if (s.isDirectory()) {
						String sName = s.getName();
						int channels = cl.listChannels(sName).size();
						long messages = cl.countMessages(sName);
						tt.addRow("<a href='/" + URLEncoder.encode(sName) + "'>" + sName + "</a>", channels + "",
								messages + "");
					}
				}
				return clone;
			}

		});
		raas.addAction("/[^/]*/[^/]*", new HtmlAction(listMessages) {

			@Override
			public HtmlDocument createModifiedHtmlDocument(HtmlDocument clone, HashMap<String, String> conf,
					HashMap<String, String> vars) {
				String url = conf.get("Request-URL");
				String guild = url.split("/")[0];
				String chan = url.split("/")[1];
				int page = 1;
				try {
					page = Integer.parseInt(url.split("/")[2]);
				}catch(Exception e) {}
				clone.setTitle(chan);
				clone.setCustomTag("title", "Logs for Channel " + chan + " of " + guild);
				TableTag tt = (TableTag) clone.getTag("table").get(0);
				cl.getChannel(guild, chan).clm.stream().sequential().sorted((msg1, msg2) -> {
					if(msg1.time<msg2.time) {
						return 1;
					}
					return -1;
				}).skip((page-1)*50).limit(50).forEach((clm) -> {
					tt.addRow(Instant.ofEpochMilli(clm.time).atZone(ZoneId.systemDefault()).toLocalDateTime().toString(), clm.user, (clm.content.isEmpty() ? "[IMAGE NOT LOGGED]" : clm.content));
				});
				return clone;
			}

		});
		raas.addAction("/[^/]*", new HtmlAction(listChannels) {

			@Override
			public HtmlDocument createModifiedHtmlDocument(HtmlDocument clone, HashMap<String, String> conf,
					HashMap<String, String> vars) {
				String url = conf.get("Request-URL");
				String guild = URLDecoder.decode(url.substring(1));
				clone.setTitle(guild);
				clone.setCustomTag("title", "Channels of " + guild);
				TableTag tt = (TableTag) clone.getTag("table").get(0);
				for (String chan : cl.listChannels(guild)) {
					ChatLogChannel s = cl.getChannel(guild, chan);
					String sName = s.name;
					s.clm.clear();
					s.load();
					long messages = s.clm.size();
					s.clm.clear();
					tt.addRow("<a href='" + url + "/" + URLEncoder.encode(sName) + "'>" + sName + "</a>",
							messages + "");
				}
				return clone;
			}
		});
		raas.addAction("*", new Action() {
			
			@Override
			public boolean isRaw() {
				
				return false;
			}
			
			@Override
			public ParseObject executeRequest(HashMap<String, String> conf, HashMap<String, String> vars) {
				System.out.println("Got false request for URL: " + conf.get("Request-URL"));
				return new ParseObject("html");
			}
		});
		RestAPI.startRestAPIServer(raas, TcpServerMode.NO_ENCRYPTION.setPort(7070));
	}

}
