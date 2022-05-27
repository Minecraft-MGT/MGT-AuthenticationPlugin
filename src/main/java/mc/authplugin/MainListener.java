package mc.authplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        showAuthToken(p);
    }

    public static void showAuthToken(Player toShow){
        String name = toShow.getName();

        String token = NetworkAdapter.getTokenForPlayer(name);
        if(token!=null){//check if player tried to authenticate
            toShow.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"Dein AuthenticationToken lautet: \""+token+"\"");
        }
    }

}
