package me.dave.followers.commands;

import me.dave.chatcolorhandler.ChatColorHandler;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import me.dave.followers.FollowerCreator;
import me.dave.followers.Followers;
import me.dave.followers.FollowerGUI;
import me.dave.followers.datamanager.FollowerHandler;

import java.util.*;

public class FollowerCmd implements CommandExecutor, TabCompleter {
    private final HashSet<UUID> openInvPlayerSet;

    public FollowerCmd(HashSet<UUID> openInvPlayerSet) {
        this.openInvPlayerSet = openInvPlayerSet;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("follower.admin.reload")) {
                    ChatColorHandler.sendMessage(sender, Followers.configManager.getLangMessage("no-permissions"));
                    return true;
                }
                Followers.configManager.reloadConfig();
                Followers.followerManager.reloadFollowers();
                Followers.dataManager.reloadFollowerInventories();
                sender.sendMessage(Followers.configManager.getLangMessage("reloaded"));
                return true;
            } else if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Console cannot run this command!");
                    return true;
                }
                if (!player.hasPermission("follower.admin.create")) {
                    ChatColorHandler.sendMessage(player, Followers.configManager.getLangMessage("no-permissions"));
                    return true;
                }
                ItemStack creator = new FollowerCreator().getCreatorItem();
                player.getInventory().addItem(creator);
                ChatColorHandler.sendMessage(player, Followers.configManager.getLangMessage("get-follower-creator"));
                return true;
            }  else if (args[0].equalsIgnoreCase("delete")) {
                if (!sender.hasPermission("follower.admin.delete")) {
                    ChatColorHandler.sendMessage(sender, Followers.configManager.getLangMessage("no-permissions"));
                    return true;
                }
                ChatColorHandler.sendMessage(sender, Followers.configManager.getLangMessage("incorrect-usage").replaceAll("%command-usage%", "/follower delete <follower_name>"));
                return true;
            }
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                if (!sender.hasPermission("follower.admin.delete")) {
                    ChatColorHandler.sendMessage(sender, Followers.configManager.getLangMessage("no-permissions"));
                    return true;
                }
                String[] temp = Arrays.copyOfRange(args, 1, args.length);
                StringBuilder followerName = new StringBuilder();
                for (String currString : temp) {
                    followerName.append(currString).append(" ");
                }
                String followerNameFinal = followerName.substring(0, followerName.length() - 1);
                FollowerHandler follower = Followers.followerManager.getFollower(followerNameFinal);
                if (follower == null) ChatColorHandler.sendMessage(sender, Followers.configManager.getLangMessage("follower-doesnt-exist").replaceAll("%follower%", followerNameFinal));
                else {
                    Followers.followerManager.removeFollower(followerNameFinal);
                    ChatColorHandler.sendMessage(sender, Followers.configManager.getLangMessage("follower-deleted").replaceAll("%follower%", followerNameFinal));
                }
                return true;
            }
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot run this command!");
            return true;
        }
        FollowerGUI followerInv = new FollowerGUI(player, 1, openInvPlayerSet);
        followerInv.openInventory(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {

        List<String> tabComplete = new ArrayList<>();
        List<String> wordCompletion = new ArrayList<>();
        boolean wordCompletionSuccess = false;

        if (args.length == 1) {
            if (commandSender.hasPermission("follower.admin.reload")) tabComplete.add("reload");
            if (commandSender.hasPermission("follower.admin.create")) tabComplete.add("create");
            if (commandSender.hasPermission("follower.admin.delete")) tabComplete.add("delete");
        } else if (args.length == 2) {
            if (commandSender.hasPermission("follower.admin.delete")) {
                tabComplete.addAll(Followers.followerManager.getFollowers().keySet());
            }
        }

        for (String currTab : tabComplete) {
            int currArg = args.length - 1;
            if (currTab.startsWith(args[currArg])) {
                wordCompletion.add(currTab);
                wordCompletionSuccess = true;
            }
        }
        if (wordCompletionSuccess) return wordCompletion;
        return tabComplete;
    }
}