package mc.authplugin.commands;

import mc.authplugin.MainListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokenCMD implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MainListener.showAuthToken((Player) sender);

        return true;
    }
}
