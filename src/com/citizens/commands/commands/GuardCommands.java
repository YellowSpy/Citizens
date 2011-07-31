package com.citizens.commands.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.citizens.Permission;
import com.citizens.commands.CommandHandler;
import com.citizens.npctypes.guards.FlagInfo;
import com.citizens.npctypes.guards.FlagList.FlagType;
import com.citizens.npctypes.guards.Guard;
import com.citizens.resources.npclib.HumanNPC;
import com.citizens.resources.sk89q.Command;
import com.citizens.resources.sk89q.CommandContext;
import com.citizens.resources.sk89q.CommandPermissions;
import com.citizens.resources.sk89q.CommandRequirements;
import com.citizens.resources.sk89q.ServerCommand;
import com.citizens.utils.EntityUtils;
import com.citizens.utils.HelpUtils;
import com.citizens.utils.Messaging;
import com.citizens.utils.PathUtils;
import com.citizens.utils.StringUtils;
import com.platymuus.bukkit.permissions.Group;

@CommandRequirements(
		requireSelected = true,
		requireOwnership = true,
		requiredType = "guard")
public class GuardCommands implements CommandHandler {

	@CommandRequirements()
	@ServerCommand()
	@Command(
			aliases = "guard",
			usage = "help",
			desc = "view the guard help page",
			modifiers = "help",
			min = 1,
			max = 1)
	@CommandPermissions("guard.use.help")
	public static void guardHelp(CommandContext args, CommandSender sender,
			HumanNPC npc) {
		HelpUtils.sendGuardHelp(sender);
	}

	@Command(
			aliases = "guard",
			usage = "[type]",
			desc = "change a guard's type",
			modifiers = { "bodyguard", "bouncer" },
			min = 1,
			max = 1)
	@CommandPermissions("guard.modify.type")
	public static void type(CommandContext args, Player player, HumanNPC npc) {
		Guard guard = npc.getType("guard");
		PathUtils.cancelTarget(npc);
		if (args.getString(0).equalsIgnoreCase("bodyguard")) {
			if (!guard.isBodyguard()) {
				guard.setBodyguard();
				player.sendMessage(StringUtils.wrap(npc.getStrippedName())
						+ " is now a bodyguard.");
			} else {
				guard.clear();
				player.sendMessage(StringUtils.wrap(npc.getStrippedName())
						+ " has stopped being a bodyguard.");
			}
		} else if (args.getString(0).equalsIgnoreCase("bouncer")) {
			if (!guard.isBouncer()) {
				guard.setBouncer();
				player.sendMessage(StringUtils.wrap(npc.getStrippedName())
						+ " is now a bouncer.");
			} else {
				guard.clear();
				player.sendMessage(StringUtils.wrap(npc.getStrippedName())
						+ " has stopped being a bouncer.");
			}
		} else {
			Messaging.sendError(player, "That is not a valid guard type.");
		}
	}

	@Command(
			aliases = "guard",
			usage = "flags",
			desc = "view a guard's flags",
			modifiers = "flags",
			min = 1,
			max = 1)
	@CommandPermissions("guard.use.flags")
	public static void flags(CommandContext args, Player player, HumanNPC npc) {
		// TODO display a guard's current flags
	}

	@Command(
			aliases = "guard",
			usage = "addflag [target] (-a -g -m (-p [priority]))",
			desc = "add a flag to a guard",
			modifiers = { "addflag", "addf", "addfl" },
			flags = "agmp",
			min = 2,
			max = 3)
	@CommandPermissions("guard.modify.flags")
	public static void addFlag(CommandContext args, Player player, HumanNPC npc) {
		if (!args.hasFlag('a') && !args.hasFlag('g') && !args.hasFlag('m')) {
			player.sendMessage("No type flags specified.");
			return;
		}

		Guard guard = npc.getType("guard");
		if (args.hasFlag('p') && args.argsLength() != 3) {
			player.sendMessage(ChatColor.GRAY
					+ "Priority flag given without specifying a priority.");
			return;
		}

		boolean isSafe = args.getString(1).charAt(0) == '-';
		int priorityOffset = args.argsLength() == 2 ? 1
				: args.argsLength() == 3 ? 2 : -1;
		int priority = 1;
		if (args.hasFlag('p')) {
			if (priorityOffset == -1) {
				player.sendMessage(ChatColor.GRAY
						+ "Priority flag given without specifying a priority.");
				return;
			}
			priority = args.getInteger(priorityOffset);
		}
		if (args.hasFlag('a')) {
			guard.getFlags().addToAll(
					FlagInfo.newInstance("all", priority, isSafe));
		} else if (args.argsLength() == 1) {
			player.sendMessage(ChatColor.GRAY + "No name given.");
			return;
		}

		String name = isSafe ? args.getString(1).replaceFirst("-", "") : args
				.getString(1);
		name = name.toLowerCase();

		if (args.hasFlag('g')) {
			if (!Permission.useSuperPerms()) {
				player.sendMessage(ChatColor.GRAY
						+ "Group flags require bukkit's permission system to be used.");
				return;
			}
			Group group = Permission.getGroup(name);
			if (group == null) {
				player.sendMessage(ChatColor.GRAY + "Group not recognised.");
				return;
			}
		}
		if (args.hasFlag('m')) {
			if (!EntityUtils.validType(name, true)) {
				player.sendMessage(ChatColor.GRAY + "Mob type not recognised.");
				return;
			}
		}
		for (Character character : args.getFlags()) {
			FlagType type = character == 'g' ? FlagType.GROUP
					: character == 'p' ? FlagType.PLAYER : FlagType.MOB;
			guard.getFlags().addFlag(type,
					FlagInfo.newInstance(name, priority, isSafe));
		}
	}

	@Command(
			aliases = "guard",
			usage = "radius [radius]",
			desc = "change the protection radius of a bouncer",
			modifiers = "radius",
			min = 2,
			max = 2)
	@CommandPermissions("guard.modify.radius")
	public static void radius(CommandContext args, Player player, HumanNPC npc) {
		Guard guard = npc.getType("guard");
		if (guard.isBouncer()) {
			guard.setProtectionRadius(Double.parseDouble(args.getString(1)));
			player.sendMessage(StringUtils.wrap(npc.getStrippedName() + "'s")
					+ " protection radius has been set to "
					+ StringUtils.wrap(args.getString(1)) + ".");
		} else {
			Messaging.sendError(player, npc.getStrippedName()
					+ " must be a bouncer first.");
		}
	}

	@Command(
			aliases = "guard",
			usage = "aggro",
			desc = "set a guard to be aggressive",
			modifiers = "aggro",
			min = 1,
			max = 1)
	@CommandPermissions("guard.modify.aggro")
	public static void aggro(CommandContext args, Player player, HumanNPC npc) {
		Guard guard = npc.getType("guard");
		guard.setAggressive(!guard.isAggressive());
		if (guard.isAggressive()) {
			player.sendMessage(StringUtils.wrap(npc.getName())
					+ " is now aggressive.");
		} else {
			player.sendMessage(StringUtils.wrap(npc.getName())
					+ " has stopped being aggressive.");
		}
	}
}