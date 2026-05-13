package com.NoTrollsInParty;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NoTrollsInRaidsPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(NoTrollsInRaidsPlugin.class);
		RuneLite.main(args);
	}
}