package com.fullwall.resources.sk89q.commands;

public class RequirementMissingException extends CommandException {

	private static final long serialVersionUID = -4299721983654504028L;

	public RequirementMissingException(String message) {
		super(message);
	}
}