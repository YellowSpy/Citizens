package com.citizens.npctypes.questers.rewards;

import org.bukkit.entity.Player;

import com.citizens.Permission;
import com.citizens.npctypes.questers.Reward;
import com.citizens.npctypes.questers.quests.QuestManager.RewardType;

public class PermissionReward implements Reward {
	private final String reward;
	private final boolean take;

	public PermissionReward(String reward, boolean take) {
		this.reward = reward;
		this.take = take;
	}

	@Override
	public void grant(Player player) {
		if (take)
			Permission.givePermission(player, reward, true);
		else
			Permission.givePermission(player, reward, false);
	}

	@Override
	public RewardType getType() {
		return RewardType.PERMISSION;
	}

	@Override
	public Object getReward() {
		return reward;
	}

	@Override
	public boolean isTake() {
		return take;
	}

	@Override
	public boolean canTake(Player player) {
		return take ? Permission.generic(player, reward) : true;
	}
}