package de.DiscordBot.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import de.DiscordBot.Config.OptionType;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class TranslateCommand extends DiscordCommand {

  Process                          p;
  volatile BufferedReader          br;
  volatile PrintWriter             pw;
  volatile static HashMap<String, String> langs = new HashMap<String, String>();

  public TranslateCommand() {
    super(
            "translate", new String[] { "trans" },
            "Translates a text to a certain Language (default: english)",
            "Normal \\translate Command translates to English! Use alternatives like \\german for German Translation");
    langs.put("english", "en");
    langs.put("german", "de");
    langs.put("afrikaans", "af");
    langs.put("albanian", "sq");
    langs.put("amharic", "am");
    langs.put("arabic", "ar");
    langs.put("armenian", "hy");
    langs.put("azerbaijani", "az");
    langs.put("basque", "eu");
    langs.put("belarusian", "be");
    langs.put("bengali", "bn");
    langs.put("bosnian", "bs");
    langs.put("bulgarian", "bg");
    langs.put("catalan", "ca");
    langs.put("cebuano", "ceb");
    langs.put("chichewa", "ny");
    langs.put("zh-cn", "zh-cn");
    langs.put("zh-tw", "zh-tw");
    langs.put("corsican", "co");
    langs.put("croatian", "hr");
    langs.put("czech", "cs");
    langs.put("danish", "da");
    langs.put("dutch", "nl");
    langs.put("esperanto", "eo");
    langs.put("estonian", "et");
    langs.put("filipino", "tl");
    langs.put("finnish", "fi");
    langs.put("french", "fr");
    langs.put("frisian", "fy");
    langs.put("galician", "gl");
    langs.put("georgian", "ka");
    langs.put("greek", "el");
    langs.put("gujarati", "gu");
    langs.put("ht", "ht");
    langs.put("hausa", "ha");
    langs.put("hawaiian", "haw");
    langs.put("hebrew", "iw");
    langs.put("hindi", "hi");
    langs.put("hmong", "hmn");
    langs.put("hungarian", "hu");
    langs.put("icelandic", "is");
    langs.put("igbo", "ig");
    langs.put("indonesian", "id");
    langs.put("irish", "ga");
    langs.put("italian", "it");
    langs.put("japanese", "ja");
    langs.put("javanese", "jw");
    langs.put("kannada", "kn");
    langs.put("kazakh", "kk");
    langs.put("khmer", "km");
    langs.put("korean", "ko");
    langs.put("ku", "ku");
    langs.put("kyrgyz", "ky");
    langs.put("lao", "lo");
    langs.put("latin", "la");
    langs.put("latvian", "lv");
    langs.put("lithuanian", "lt");
    langs.put("luxembourgish", "lb");
    langs.put("macedonian", "mk");
    langs.put("malagasy", "mg");
    langs.put("malay", "ms");
    langs.put("malayalam", "ml");
    langs.put("maltese", "mt");
    langs.put("maori", "mi");
    langs.put("marathi", "mr");
    langs.put("mongolian", "mn");
    langs.put("my", "my");
    langs.put("nepali", "ne");
    langs.put("norwegian", "no");
    langs.put("pashto", "ps");
    langs.put("persian", "fa");
    langs.put("polish", "pl");
    langs.put("portuguese", "pt");
    langs.put("punjabi", "ma");
    langs.put("romanian", "ro");
    langs.put("russian", "ru");
    langs.put("samoan", "sm");
    langs.put("gd", "gd");
    langs.put("serbian", "sr");
    langs.put("sesotho", "st");
    langs.put("shona", "sn");
    langs.put("sindhi", "sd");
    langs.put("sinhala", "si");
    langs.put("slovak", "sk");
    langs.put("slovenian", "sl");
    langs.put("somali", "so");
    langs.put("spanish", "es");
    langs.put("sundanese", "su");
    langs.put("swahili", "sw");
    langs.put("swedish", "sv");
    langs.put("tajik", "tg");
    langs.put("tamil", "ta");
    langs.put("telugu", "te");
    langs.put("thai", "th");
    langs.put("turkish", "tr");
    langs.put("ukrainian", "uk");
    langs.put("urdu", "ur");
    langs.put("uzbek", "uz");
    langs.put("vietnamese", "vi");
    langs.put("welsh", "cy");
    langs.put("xhosa", "xh");
    langs.put("yiddish", "yi");
    langs.put("yoruba", "yo");
    langs.put("zulu", "zu");

    for(String s : langs.keySet()) {
      addCommandAlias(s);
    }
    try {
      p = new ProcessBuilder("nodejs", "./translateTest.js").redirectErrorStream(true).start();
      br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      pw = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    if (args.length == 0) {
      return new MessageBuilder().append("You need to specify a message to translate!");
    }
    String text = m.getContent().split(" ", 2)[1];
    String lang = "en";
    if(langs.containsKey(command.toLowerCase())) {
      lang = langs.get(command.toLowerCase());
    }else {
      lang = getConfig(m.getGuild()).getValue("defaultLanguage", "en");
    }
    synchronized (br) {
      synchronized (pw) {
        pw.println(lang);
        try {
          pw.println(Base64.getEncoder().encodeToString(text.replace("\n", " ").getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        pw.flush();
        String ret = "";
        String line = "";
        String origLang = "unknown";
        try {
          origLang = br.readLine();
          line = br.readLine();
          while (line != null && !line.equalsIgnoreCase("$FINISHED$")) {
            System.out.println(line);
            ret += new String(Base64.getDecoder().decode(line), "UTF-8") + "\n";
            line = br.readLine();
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          return Integer.valueOf(4);
        }
        String resolved = "unknown";
        for(String s : langs.keySet()) {
          if(langs.get(s).equalsIgnoreCase(origLang)) {
            resolved = s;
            break;
          }
        }
        return new MessageBuilder().append("Translated from the language " + resolved + ":").appendCodeBlock(ret, "").build();
      }
    }
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    cfg.setValue("defaultLanguage", "en");
  }

  @Override
  public ConfigPage createRemoteConfigurable() {
    LinkedList<ConfigurableOption> conf = new LinkedList<ConfigurableOption>();
    conf.add(new ConfigurableOption(this, "Language", "To what Language should the \\translate Command translate to", OptionType.MAP, "language", langs));
    return new ConfigPage("TranslateCommand", this, conf);
  }

  @Override
  public boolean isRemoteConfigurable() {
    // TODO Auto-generated method stub
    return true;
  }

}
