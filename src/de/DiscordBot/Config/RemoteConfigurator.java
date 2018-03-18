package de.DiscordBot.Config;

import java.util.HashMap;

import javautils.RestAPI.RawAction;
import javautils.RestAPI.RestAPI;
import javautils.RestAPI.RestAPIActionSet;
import javautils.tcpmanager.TcpServerMode;

public class RemoteConfigurator {

  RestAPIActionSet raas;

  HashMap<String, ConfigPage> configSites = new HashMap<String, ConfigPage>();
  HashMap<String, String> users = new HashMap<String, String>();


  public RemoteConfigurator() {
    raas = new RestAPIActionSet();

    raas.addAction("/pages/list", new RawAction() {

      @Override
      public String execute(HashMap<String, String> conf, HashMap<String, String> vars) {
        if(vars.containsKey("api")) {
          if(users.containsKey(vars.get("api"))) {

          }
        }
        return "{status: {code: 404, error: \"authorization\"}}";
      }

      @Override
      public String getContentType() {
        return "text/json";
      }
    });

    RestAPI.startRestAPIServer(raas, TcpServerMode.NO_ENCRYPTION.setPort(9889));

  }

}
