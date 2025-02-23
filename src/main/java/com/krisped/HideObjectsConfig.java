package com.krisped;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("hideobjects")
public interface HideObjectsConfig extends Config
{
    @ConfigItem(
            keyName = "idsToHide",
            name = "IDs to hide",
            description = "Liste med objekt‑ID‑er som skal skjules (komma‑separert)",
            position = 1
    )
    default String getIdsToHide()
    {
        return "";
    }

    @ConfigSection(
            name = "Hide all",
            description = "Huk av for å skjule alle objekter av gitt type",
            position = 10,
            closedByDefault = false
    )
    String hideAllSection = "hideAllSection";

    @ConfigItem(
            keyName = "hideAllGameObjects",
            name = "Hide all GameObjects",
            description = "Skjul alle GameObjects i verden",
            position = 11,
            section = hideAllSection
    )
    default boolean hideAllGameObjects()
    {
        return false;
    }

    @ConfigItem(
            keyName = "hideAllGroundObjects",
            name = "Hide all GroundObjects",
            description = "Skjul alle GroundObjects i verden",
            position = 12,
            section = hideAllSection
    )
    default boolean hideAllGroundObjects()
    {
        return false;
    }

    @ConfigItem(
            keyName = "hideAllWalls",
            name = "Hide all WallObjects",
            description = "Skjul alle WallObjects i verden",
            position = 13,
            section = hideAllSection
    )
    default boolean hideAllWalls()
    {
        return false;
    }

    @ConfigItem(
            keyName = "hideAllDecorativeObjects",
            name = "Hide all DecorativeObjects",
            description = "Skjul alle DecorativeObjects i verden",
            position = 14,
            section = hideAllSection
    )
    default boolean hideAllDecorativeObjects()
    {
        return false;
    }
}
