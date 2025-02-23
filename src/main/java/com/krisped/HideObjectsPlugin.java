package com.krisped;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import net.runelite.api.Client;
import net.runelite.api.Renderable;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.DecorativeObject;
import net.runelite.api.WallObject;

import net.runelite.client.callback.Hooks;
import net.runelite.client.callback.Hooks.RenderableDrawListener;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

/**
 * Plugin som "hider" (skjuler) GameObjects, GroundObjects, DecorativeObjects og WallObjects,
 * ved å returnere false i shouldDraw(...) hvis config tilsier at de ikke skal tegnes.
 *
 * Dette er samme prinsipp som Entity Hider bruker for NPC/Player/Projectile.
 */
@Slf4j
@PluginDescriptor(
        name = "Hide Objects",
        description = "Skjul objekter basert på ID og type (inspirert av Entity Hider)",
        tags = { "objects", "hide" }
)
public class HideObjectsPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private HideObjectsConfig config;

    @Inject
    private Hooks hooks;

    // Her lagrer vi ID‑ene som skal skjules (fra config) i et Set.
    private Set<Integer> hiddenIds = new HashSet<>();

    // Disse brukes for "hide all" av hver type.
    private boolean hideAllGameObjects;
    private boolean hideAllGroundObjects;
    private boolean hideAllWalls;
    private boolean hideAllDecorative;

    // RenderableDrawListener -> Kalles for alt av "Renderable" før tegning.
    private final RenderableDrawListener drawListener = this::shouldDrawRenderable;

    @Provides
    HideObjectsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HideObjectsConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        log.info("HideObjectsPlugin startet (Entity Hider style).");
        updateConfig();
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    protected void shutDown() throws Exception
    {
        hooks.unregisterRenderableDrawListener(drawListener);
        log.info("HideObjectsPlugin stoppet.");
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("hideobjects"))
        {
            log.debug("HideObjects config endret, oppdaterer...");
            updateConfig();
        }
    }

    /**
     * Oppdaterer local state basert på config. Kalles ved startUp og når config endres.
     */
    private void updateConfig()
    {
        hiddenIds.clear();

        String text = config.getIdsToHide();
        if (text != null && !text.trim().isEmpty())
        {
            for (String s : text.split(","))
            {
                try
                {
                    hiddenIds.add(Integer.parseInt(s.trim()));
                }
                catch (NumberFormatException e)
                {
                    log.warn("Ugyldig tall i config: {}", s.trim());
                }
            }
        }

        hideAllGameObjects = config.hideAllGameObjects();
        hideAllGroundObjects = config.hideAllGroundObjects();
        hideAllWalls = config.hideAllWalls();
        hideAllDecorative = config.hideAllDecorativeObjects();

        log.debug("Oppdatert config: hiddenIds={}, hideAllGameObjects={}, hideAllGroundObjects={}, hideAllWalls={}, hideAllDecorative={}",
                hiddenIds, hideAllGameObjects, hideAllGroundObjects, hideAllWalls, hideAllDecorative);
    }

    /**
     * Her bestemmer vi om et Renderable skal tegnes eller ei.
     * Returner false for å "hindre" tegning (altså "hide").
     */
    private boolean shouldDrawRenderable(Renderable renderable, boolean drawingUI)
    {
        // For debugging: se klassenavnet til renderable
        // log.debug("shouldDrawRenderable: class={}", renderable.getClass().getName());

        if (renderable instanceof GameObject)
        {
            GameObject go = (GameObject) renderable;
            int id = go.getId();

            if (hideAllGameObjects || hiddenIds.contains(id))
            {
                log.debug("Skjuler GameObject (id={})", id);
                return false;
            }
        }
        else if (renderable instanceof GroundObject)
        {
            GroundObject g = (GroundObject) renderable;
            int id = g.getId();

            if (hideAllGroundObjects || hiddenIds.contains(id))
            {
                log.debug("Skjuler GroundObject (id={})", id);
                return false;
            }
        }
        else if (renderable instanceof DecorativeObject)
        {
            DecorativeObject d = (DecorativeObject) renderable;
            int id = d.getId();

            if (hideAllDecorative || hiddenIds.contains(id))
            {
                log.debug("Skjuler DecorativeObject (id={})", id);
                return false;
            }
        }
        else if (renderable instanceof WallObject)
        {
            WallObject w = (WallObject) renderable;
            int id = w.getId();

            if (hideAllWalls || hiddenIds.contains(id))
            {
                log.debug("Skjuler WallObject (id={})", id);
                return false;
            }
        }

        // Hvis vi ikke matcher noen av de ovenfor, tegnes objektet som normalt.
        return true;
    }
}
