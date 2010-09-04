package org.fedora.fedobot;

import java.io.IOException;
import java.util.HashMap;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Fedobot extends PircBot {

	public static void main(String[] args){
		Fedobot bot = new Fedobot();
		// Enable debugging output.
		bot.setVerbose(false);
	}
	HashMap<String,String> monitor = new HashMap<String,String>();
	public Fedobot() {
		boolean connected = false;
		while(!connected)
			try {

				this.setName("Fedobot");
				this.setAutoNickChange(true);
				this.setLogin("Dexter");
				this.connect("irc.freenode.net");
				joinChannel("#fedora-latam");
				connected=true;
			} catch (NickAlreadyInUseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IrcException e) {
				e.printStackTrace();
			}

	}

	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
			if (message.startsWith("!join")) {
				message = message.replace("!join ","");
				joinChannel(message);
			}else
				onMessage(sender,sender,login,hostname,message);
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		Translate.setHttpReferrer("RAWK");
		String lang = monitor.get(channel);
		try{
			if(lang!=null&&!sender.equals("Fedobot")){
				String transText = "";
				if(lang.equals("en"))
					transText = Translate.execute(message,Language.AUTO_DETECT,Language.ENGLISH);
				else if(lang.equals("es"))
					transText = Translate.execute(message,Language.AUTO_DETECT,Language.SPANISH);
				else if(lang.equals("pt"))
					transText = Translate.execute(message,Language.AUTO_DETECT,Language.PORTUGUESE);
				if(!transText.equals(""))
					if(channel.contains("##")){
						sendMessage(channel.substring(1,channel.lastIndexOf("-")), "("+sender+"): "+transText);
					}else
						sendMessage("#"+channel+"-"+lang, "("+sender+"): "+transText);
			}
		}catch(Exception e){}

		if(message.startsWith("!translate")){
			try{
				String translatedText = Translate.execute(message.replace("!translate ",""), Language.AUTO_DETECT, Language.ENGLISH);
				sendMessage(channel, translatedText);
			}catch(Exception e){
			}
		}
		else if(message.startsWith("!traduzir")){
			try{
				String translatedText = Translate.execute(message.replace("!traduzir ",""), Language.AUTO_DETECT, Language.PORTUGUESE);
				sendMessage(channel, translatedText);
			}catch(Exception e){
			}
		}
		else if(message.startsWith("!traducir")){
			try{
				String translatedText = Translate.execute(message.replace("!traducir ",""), Language.AUTO_DETECT, Language.SPANISH);
				sendMessage(channel, translatedText);
			}catch(Exception e){
			}
		}
		else if(message.startsWith("!traduce")){
			try{
				String translatedText = Translate.execute(message.replace("!traduce ",""), Language.AUTO_DETECT, Language.SPANISH);
				sendMessage(channel, translatedText);
			}catch(Exception e){
			}
		}
		else if(message.startsWith("!help")){
			sendMessage(channel, "Commands are: !traducir, !traduzir, !translate");
		}
		else 
			if(message.startsWith("!monitor")){
			try{
				String[] commandWord = message.split(" ");
				String chan = commandWord[1];
				String language = commandWord[2];
				String languageTwo = commandWord[3];
				monitor.put(chan,language);
				monitor.put("#"+chan+"-"+language,languageTwo);
				this.joinChannel("#"+chan+"-"+language);
				sendMessage(sender, "Esta sala esta siendo traducida. Unete a #"+chan+"-"+language+" para la traducción");
				sendMessage(sender, "Got it. Join #"+chan+"-"+language);
			}catch(Exception e){
				sendMessage(sender, "Usage: !monitor #channel-name en|es|pt en|es|pt");    			
			}
		}else if(message.startsWith("!demonitor")){
			try{
				String[] commandWord = message.split(" ");
				String chan = commandWord[1];
				String language = commandWord[2];
				monitor.remove(chan);
				monitor.remove("#"+chan+"-"+language);
			}catch(Exception e){
				sendMessage(sender, "Usage: !monitor #channel-name en|es|pt en|es|pt");    			
			}
		}else if(message.startsWith("!clear")){
			try{
				monitor.clear();
			}catch(Exception e){
				sendMessage(sender, "Usage: !monitor #channel-name en|es|pt en|es|pt");    			
			}
		}
	}
}