package com.NoTrollsInParty;

import net.runelite.api.Client;
import net.runelite.api.Ignore;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;

@PluginDescriptor(
        name = "No Trolls In Raids",
        description = "Highlights ignored players in red on raid/party interfaces",
        tags = {"raid", "ignore", "tob", "cox", "party"}
)
public class NoTrollsInRaidsPlugin extends Plugin
{
    @Inject
    private Client client;

    private static final int[] TARGET_GROUPS = {28, 50, 161, 162, 364, 772, 773, 774};

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN || client.getIgnoreContainer() == null)
        {
            return;
        }

        for (int groupId : TARGET_GROUPS)
        {

            Widget root = client.getWidget(groupId, 0);

            if (root != null && !root.isHidden())
            {
                scanAndColor(root);
            }
        }
    }

    private void scanAndColor(Widget widget)
    {
        if (widget == null || widget.isHidden())
        {
            return;
        }

        String text = widget.getText();
        if (text != null && !text.isEmpty() && !text.equals("-"))
        {
            String newText = injectColorTags(text);

            if (!newText.equals(text))
            {
                widget.setText(newText);
            }
        }

        Widget[] staticChildren = widget.getStaticChildren();
        if (staticChildren != null)
        {
            for (Widget child : staticChildren) scanAndColor(child);
        }

        Widget[] dynamicChildren = widget.getDynamicChildren();
        if (dynamicChildren != null)
        {
            for (Widget child : dynamicChildren) scanAndColor(child);
        }

        Widget[] nestedChildren = widget.getNestedChildren();
        if (nestedChildren != null)
        {
            for (Widget child : nestedChildren) scanAndColor(child);
        }
    }

    private String injectColorTags(String text)
    {
        String result = text;
        if (result.contains("<col=ff0000>"))
        {
            return result;
        }

        for (Ignore ignoreEntry : client.getIgnoreContainer().getMembers())
        {
            if (ignoreEntry == null || ignoreEntry.getName() == null) continue;

            String ignoredName = ignoreEntry.getName().replace("\u00A0", " ").trim();
            if (ignoredName.isEmpty()) continue;

            if (result.toLowerCase().contains(ignoredName.toLowerCase()))
            {
                result = result.replaceAll(
                        "(?i)" + java.util.regex.Pattern.quote(ignoredName),
                        "<col=ff0000>$0</col>"
                );
            }
        }
        return result;
    }
}