/**
 * VoidPlots - https://github.com/mukulx/VoidPlots
 *
 * Copyright (C) 2026 mukulx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package dev.mukulx.voidplots.commands;

import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlotCommand implements CommandExecutor, TabCompleter {
    
    private final VoidPlots plugin;
    private final Map<String, SubCommand> subCommands;
    
    public PlotCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        
        registerSubCommands();
    }
    
    private void registerSubCommands() {
        subCommands.put("claim", new ClaimCommand(plugin));
        subCommands.put("auto", new AutoCommand(plugin));
        subCommands.put("home", new HomeCommand(plugin));
        subCommands.put("visit", new VisitCommand(plugin));
        subCommands.put("trust", new TrustCommand(plugin));
        subCommands.put("untrust", new UntrustCommand(plugin));
        subCommands.put("clear", new ClearCommand(plugin));
        subCommands.put("delete", new DeleteCommand(plugin));
        subCommands.put("info", new InfoCommand(plugin));
        subCommands.put("list", new ListCommand(plugin));
        subCommands.put("settings", new SettingsCommand(plugin));
        subCommands.put("flag", new FlagCommand(plugin));
        subCommands.put("kick", new KickCommand(plugin));
        subCommands.put("ban", new BanCommand(plugin));
        subCommands.put("unban", new UnbanCommand(plugin));
        subCommands.put("rate", new RateCommand(plugin));
        subCommands.put("top", new TopCommand(plugin));
        subCommands.put("comment", new CommentCommand(plugin));
        subCommands.put("comments", new CommentsCommand(plugin));
        subCommands.put("download", new DownloadCommand(plugin));
        subCommands.put("upload", new UploadCommand(plugin));
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);
        
        if (subCommand == null) {
            sendHelp(sender);
            return true;
        }
        
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        subCommand.execute(sender, subArgs);
        
        return true;
    }
    
    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage("§8§m----------§r §5VoidPlots Commands §8§m----------");
        sender.sendMessage("§e/plot claim §7- Claim the plot you're standing in");
        sender.sendMessage("§e/plot auto §7- Auto-claim a free plot");
        sender.sendMessage("§e/plot home §7- Teleport to your plot");
        sender.sendMessage("§e/plot visit <player> §7- Visit a player's plot");
        sender.sendMessage("§e/plot trust <player> §7- Trust a player on your plot");
        sender.sendMessage("§e/plot untrust <player> §7- Untrust a player");
        sender.sendMessage("§e/plot flag <flag> [value] §7- Set plot flags");
        sender.sendMessage("§e/plot kick <player> §7- Kick a player from your plot");
        sender.sendMessage("§e/plot ban <player> §7- Ban a player from your plot");
        sender.sendMessage("§e/plot unban <player> §7- Unban a player");
        sender.sendMessage("§e/plot rate [1-5] §7- Rate this plot");
        sender.sendMessage("§e/plot top [page] §7- View top rated plots");
        sender.sendMessage("§e/plot comment <message> §7- Leave a comment");
        sender.sendMessage("§e/plot comments [page] §7- View plot comments");
        sender.sendMessage("§e/plot download [name] §7- Download plot as schematic");
        sender.sendMessage("§e/plot upload <schematic> §7- Upload schematic to plot");
        sender.sendMessage("§e/plot clear §7- Clear your plot");
        sender.sendMessage("§e/plot delete §7- Delete your plot");
        sender.sendMessage("§e/plot info §7- View plot information");
        sender.sendMessage("§e/plot list §7- List all plots");
        sender.sendMessage("§e/plot settings §7- Open plot settings GUI");
        sender.sendMessage("§8§m--------------------------------");
    }
    
    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(subCommands.keySet());
            return filterCompletions(completions, args[0]);
        }
        
        if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(sender, subArgs);
            }
        }
        
        return Collections.emptyList();
    }
    
    @NotNull
    private List<String> filterCompletions(@NotNull List<String> completions, @NotNull String input) {
        List<String> filtered = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lowerInput)) {
                filtered.add(completion);
            }
        }
        
        return filtered;
    }
}
