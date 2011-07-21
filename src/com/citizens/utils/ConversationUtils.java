package com.citizens.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class ConversationUtils {
	private static Map<String, Converser> conversations = new ConcurrentHashMap<String, Converser>();

	public static void addConverser(Player player, Converser converser) {
		if (conversations.get(player.getName()) != null) {
			player.sendMessage(ChatColor.GRAY
					+ "You can only have one chat editor open at a time.");
			return;
		}
		conversations.put(player.getName(), converser);
		converser.begin(player);
	}

	public static void onChat(PlayerChatEvent event) {
		if (conversations.get(event.getPlayer().getName()) != null) {
			boolean finished = conversations.get(event.getPlayer().getName())
					.converse(
							new ConversationMessage(event.getPlayer(), event
									.getMessage()));
			if (finished)
				remove(event.getPlayer());
		}
	}

	public static void remove(Player player) {
		Converser converser = conversations.get(player.getName());
		if (converser != null)
			converser.end(player);
		conversations.remove(player.getName());
	}

	public static void verify() {
		for (String name : conversations.keySet()) {
			if (Bukkit.getServer().getPlayer(name) == null) {
				conversations.remove(name);
			}
		}
	}

	public interface Converser {
		public void begin(Player player);

		public boolean converse(ConversationMessage message);

		public void end(Player player);
	}

	public static class ConversationMessage {
		private final Player player;
		private final String message;
		private final String[] split;

		public ConversationMessage(Player player, String message) {
			this.player = player;
			this.message = message;
			this.split = message.split(" ");
		}

		public String getString(int index) {
			return split[index];
		}

		public String getJoinedStrings(int initialIndex) {
			StringBuilder buffer = new StringBuilder(split[initialIndex]);
			for (int i = initialIndex + 1; i < split.length; ++i) {
				buffer.append(" ").append(split[i]);
			}
			return buffer.toString();
		}

		public int getInteger(int index) throws NumberFormatException {
			return Integer.parseInt(split[index]);
		}

		public double getDouble(int index) throws NumberFormatException {
			return Double.parseDouble(split[index]);
		}

		public Player getPlayer() {
			return player;
		}

		public String getMessage() {
			return message;
		}
	}
}
