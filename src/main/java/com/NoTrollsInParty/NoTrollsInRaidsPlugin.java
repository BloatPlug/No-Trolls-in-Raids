package com.NoTrollsInParty;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Ignore;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;

@PluginDescriptor(
        name = "No Trolls In Raids"
)
public class NoTrollsInRaidsPlugin extends Plugin
{
    @Inject
    private Client client;

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getIgnoreContainer() == null) return;

        int[] groups = {28, 50, 161, 162, 364, 772, 773, 774};

        for (int groupId : groups)
        {
            for (int i = 0; i < 5; i++)
            {
                Widget root = client.getWidget(groupId, i);
                if (root != null && !root.isHidden())
                {
                    processWidgetTree(root);
                }
            }
        }
    }

    private void processWidgetTree(Widget w)
    {
        if (w == null || w.isHidden()) return;

        String text = w.getText();
        if (text != null && !text.isEmpty() && !text.equals("-"))
        {
            String newText = injectColorTags(text);
            if (!newText.equals(text))
            {
                w.setText(newText);
            }
        }

        Widget[] children;
        if ((children = w.getStaticChildren()) != null)
        {
            for (Widget c : children) processWidgetTree(c);
        }

        if ((children = w.getDynamicChildren()) != null)
        {
            for (Widget c : children) processWidgetTree(c);
        }

        if ((children = w.getNestedChildren()) != null)
        {
            for (Widget c : children) processWidgetTree(c);
        }
    }

    private String injectColorTags(String text)
    {
        String result = text;
        for (Ignore ignoreEntry : client.getIgnoreContainer().getMembers())
        {
            if (ignoreEntry != null && ignoreEntry.getName() != null)
            {
                String ignoredName = ignoreEntry.getName().replace("\u00A0", " ").trim();
                if (!ignoredName.isEmpty())
                {
                    result = colorize(result, ignoredName);
                }
            }
        }
        return result;
    }

    private String colorize(String fullText, String name)
    {
        if (fullText.toLowerCase().contains(name.toLowerCase()) && !fullText.contains("<col=ff0000>"))
        {
            return fullText.replaceAll(
                    "(?i)" + java.util.regex.Pattern.quote(name),
                    "<col=ff0000>$0</col>"
            );
        }
        return fullText;
    }
}