package me.snowznz.simplelumberjack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getPluginManager;

public class SimpleLumberjack extends JavaPlugin implements Listener {
    FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        // Setup config
        config.addDefault("block-break-duration", 2);

        config.options().copyDefaults(true);
        saveConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean isLogBlock(Material material) {
        return material.toString().endsWith("_LOG");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Get the block broken
        Block blockBroken = event.getBlock();

        // Check if the block broken is a log
        if (isLogBlock(blockBroken.getType())) {
            // Get a list of the blocks around the broken block
            List<Block> surroundingLogs = getSurroundingLogs(blockBroken.getLocation());

            for (Block block : surroundingLogs) {
                if (isLogBlock(block.getType())) {
                    // Break block after 0.5 seconds (in ticks)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Create a new BlockBreakEvent
                            BlockBreakEvent breakEvent = new BlockBreakEvent(block, event.getPlayer());

                            // Call the appropriate method to trigger the event
                            getPluginManager().callEvent(breakEvent);

                            // Check if the event was not cancelled
                            if (!breakEvent.isCancelled()) {
                                // Break the block naturally
                                block.breakNaturally();
                            }
                        }
                    }.runTaskLater(this, config.getInt("block-break-duration"));

                }
            }
        }
    }

    private List<Block> getSurroundingLogs(Location location) {
        List<Block> surroundingLogs = new ArrayList<>();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Location tempLocation = new Location(location.getWorld(), x, y, z);

        // Iterate over the neighboring blocks within a 1-block radius
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetY = -1; offsetY <= 1; offsetY++) {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                    // Skip the central block and blocks below
                    if ((offsetX == 0 && offsetY == 0 && offsetZ == 0) || offsetY == -1) {
                        continue;
                    }

                    tempLocation.setX(x + offsetX);
                    tempLocation.setY(y + offsetY);
                    tempLocation.setZ(z + offsetZ);

                    Block surroundingBlock = tempLocation.getBlock();
                    if (isLogBlock(surroundingBlock.getType())) {
                        surroundingLogs.add(surroundingBlock);
                    }
                }
            }
        }

        return surroundingLogs;
    }
}
