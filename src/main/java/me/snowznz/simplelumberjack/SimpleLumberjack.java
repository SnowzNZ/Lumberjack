package me.snowznz.simplelumberjack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SimpleLumberjack extends JavaPlugin implements Listener {
    private FileConfiguration config;

    @Override
    public void onEnable() {
        config = getConfig();
        config.addDefault("fist-break-speed", 8);
        config.addDefault("wooden-axe-break-speed", 7);
        config.addDefault("stone-axe-break-speed", 6);
        config.addDefault("iron-axe-break-speed", 5);
        config.addDefault("diamond-axe-break-speed", 4);
        config.addDefault("netherite-axe-break-speed", 3);
        config.addDefault("golden-axe-break-speed", 2);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean isLogBlock(Material material) {
        return material.toString().endsWith("_LOG");
    }

    private int getBreakSpeed(Material axeType) {
        return switch (axeType) {
            case WOODEN_AXE -> config.getInt("wooden-axe-break-speed");
            case STONE_AXE -> config.getInt("stone-axe-break-speed");
            case IRON_AXE -> config.getInt("iron-axe-break-speed");
            case DIAMOND_AXE -> config.getInt("diamond-axe-break-speed");
            case NETHERITE_AXE -> config.getInt("netherite-axe-break-speed");
            case GOLDEN_AXE -> config.getInt("golden-axe-break-speed");
            default -> config.getInt("fist-break-speed");
        };
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block blockBroken = event.getBlock();
        Material axeType = event.getPlayer().getInventory().getItemInMainHand().getType();
        int breakSpeed = getBreakSpeed(axeType);

        if (isLogBlock(blockBroken.getType())) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                List<Block> surroundingLogs = getSurroundingLogs(blockBroken.getLocation());
                for (Block block : surroundingLogs) {
                    BlockBreakEvent breakEvent = new BlockBreakEvent(block, event.getPlayer());
                    getServer().getPluginManager().callEvent(breakEvent);

                    if (!breakEvent.isCancelled()) {
                        block.breakNaturally();
                    }
                }
            }, breakSpeed);


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
