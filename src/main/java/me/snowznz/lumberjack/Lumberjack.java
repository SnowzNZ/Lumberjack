package me.snowznz.lumberjack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getScheduler;

public class Lumberjack extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Get block broken
        Block brokenBlock = event.getBlock();

        // Check if the block broken is a log
        if (isLogBlock(brokenBlock.getType())) {
            // Get a list of the blocks around the broken block
            List<Block> surroundingLogs = getSurroundingLogs(brokenBlock.getLocation());

            //
            breakSurroundingLogs(surroundingLogs);
        }
    }

    private boolean isLogBlock(Material material) {
        return material == Material.OAK_LOG ||
                material == Material.BIRCH_LOG ||
                material == Material.SPRUCE_LOG ||
                material == Material.JUNGLE_LOG ||
                material == Material.ACACIA_LOG ||
                material == Material.DARK_OAK_LOG ||
                material == Material.CHERRY_LOG ||
                material == Material.MANGROVE_LOG;
    }

    private List<Block> getSurroundingLogs(Location location) {
        List<Block> surroundingLogs = new ArrayList<>();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Iterate over the neighboring blocks within a 1-block radius
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetY = -1; offsetY <= 1; offsetY++) {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                    // Skip the central block
                    if (offsetX == 0 && offsetY == 0 && offsetZ == 0) {
                        continue;
                    }

                    // Calculate the location of the surrounding block
                    Location surroundingLocation = new Location(location.getWorld(), x + offsetX, y + offsetY, z + offsetZ);
                    Block surroundingBlock = surroundingLocation.getBlock();

                    if (isLogBlock(surroundingBlock.getType())) {
                        surroundingLogs.add(surroundingBlock);
                    }
                }
            }
        }

        return surroundingLogs;
    }

    private void breakSurroundingLogs(List<Block> blocks) {
        for (Block block : blocks) {
            // Break block after 0.5 seconds (in ticks)
            getScheduler().runTaskLater(this, () -> block.breakNaturally(), 10);
        }
    }
}
