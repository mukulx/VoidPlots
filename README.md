# VoidPlots

> ⚠️ **Development Status**: This plugin is currently in active development. While all core features are implemented and tested, it may contain bugs or incomplete features. More features will be added over time, and the plugin will continue to be improved. Use in production at your own risk.

A powerful Minecraft plot management plugin for build events and creative servers. From the void, create your world.

## Features

- **Plot System**: Automatically generated plot world with customizable plot sizes and road widths
- **Plot Management**: Claim, auto-claim, delete, and clear plots
- **Trust System**: Trust players to build on your plots
- **Plot Protection**: Prevents unauthorized building and interaction
- **Teleportation**: Home and visit commands to navigate between plots
- **GUI System**: Interactive settings GUI for plot configuration
- **High-Performance Database**: HikariCP connection pooling for optimal SQLite performance
- **Plot Flags**: Control PvP, mob spawning, explosions, weather, and time
- **Rating System**: Rate plots with interactive star-based GUI
- **Kick/Ban System**: Kick players and ban them from your plots
- **Comments/Guest Book**: Leave comments on plots
- **Schematics**: Download/upload plots with WorldEdit integration
- **Notifications**: Get notified when players visit your plot or trusted players come online
- **PlaceholderAPI**: 14+ placeholders for use in other plugins

## Commands

### Player Commands
- `/plot claim` - Claim the plot you're standing in
- `/plot auto` - Auto-claim a free plot and teleport to it
- `/plot home` - Teleport to your plot
- `/plot visit <player>` - Visit another player's plot
- `/plot trust <player>` - Trust a player on your plot
- `/plot untrust <player>` - Remove trust from a player
- `/plot kick <player>` - Kick a player from your plot
- `/plot ban <player>` - Ban a player from your plot
- `/plot unban <player>` - Unban a player from your plot
- `/plot flag <flag> [value]` - Set plot flags (pvp, mob-spawning, etc.)
- `/plot rate [1-5]` - Rate a plot (opens GUI if no rating specified)
- `/plot top [page]` - View top-rated plots
- `/plot comment <message>` - Leave a comment on a plot
- `/plot comments [page]` - View plot comments
- `/plot download [name]` - Download plot as schematic (requires WorldEdit)
- `/plot upload <schematic>` - Upload schematic to plot (requires WorldEdit)
- `/plot clear [confirm]` - Clear your plot (requires confirmation)
- `/plot delete [confirm]` - Delete your plot (requires confirmation)
- `/plot info` - View information about the current plot
- `/plot list [page]` - List all plots
- `/plot settings` - Open plot settings GUI

### Admin Commands
- `/voidplots reload` - Reload configuration
- `/voidplots createworld [name]` - Create a plot world
- `/voidplots deleteworld <name>` - Delete a world and all its data
- `/voidplots tp [world]` - Teleport to plot world
- `/voidplots setowner <player>` - Set the owner of a plot

## Requirements

- Minecraft 1.21.1+
- Paper, Spigot, or Bukkit server (Folia support planned for future releases)
- Java 21
- (Optional) WorldEdit/FastAsyncWorldEdit for schematic features
- (Optional) PlaceholderAPI for placeholder support

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Support

Found a bug or have a feature suggestion? Please [open an issue](https://github.com/mukulx/VoidPlots/issues) on the GitHub repository.
