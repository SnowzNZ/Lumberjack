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

public class SimpleLumberjack extends JavaPlugin implements Listener {
    private FileConfiguration config;

    @Override
    public void onEnable() {
        config = getConfig();
        config.addDefault("block-break-duration", 2);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean isLogBlock(Material material) {
        return material.toString().endsWith("_LOG");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block blockBroken = event.getBlock();
        if (isLogBlock(blockBroken.getType())) {
            List<Block> surroundingLogs = getSurroundingLogs(blockBroken.getLocation());

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Block block : surroundingLogs) {
                        BlockBreakEvent breakEvent = new BlockBreakEvent(block, event.getPlayer());
                        getServer().getPluginManager().callEvent(breakEvent);

                        if (!breakEvent.isCancelled()) {
                            event.getPlayer().sendBlockDamage(block.getLocation(), 1);
                            block.breakNaturally();
                        }
                    }
                }
            }.runTaskLater(this, config.getInt("block-break-duration"));
        }
    }

    private List<Block> getSurroundingLogs(Location location) {
        List<Block> surroundingLogs = new ArrayList<>();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetY = 0; offsetY <= 1; offsetY++) {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                    if (offsetX == 0 && offsetY == 0 && offsetZ == 0) {
                        continue;
                    }

                    Location tempLocation = new Location(location.getWorld(), x + offsetX, y + offsetY, z + offsetZ);
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
