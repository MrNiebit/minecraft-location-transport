package cn.lacknb.locationtransport.listener;

import cn.lacknb.locationtransport.LocationTransport;
import cn.lacknb.locationtransport.mode.LocationModel;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author gitsilence
 * @date 2023-09-17
 */
public class LocationTransportListener implements CommandExecutor, Listener {

    private static final Logger log = Logger.getLogger("locationTransport");

    public static final Map<Integer, LocationModel> locationMap = Maps.newConcurrentMap();
    private final Material waystoneMaterial = Material.PLAYER_HEAD; // 物品类型

    // 创建GUI界面
    public static Inventory gui = Bukkit.createInventory(null, 18, "传送页面");

    public LocationTransportListener() {
    }

    public LocationTransportListener(Object obj, Inventory g) {
        if (obj != null) {
            locationMap.putAll((Map<? extends Integer, ? extends LocationModel>) obj);
        }
        if (g != null) {
            gui = g;
        }
    }

    private ItemStack createItem(Player player) {
        // 创建一个新的物品堆栈
        ItemStack customItem = new ItemStack(Material.PLAYER_HEAD);

        // 设置物品的显示名称
        ItemMeta itemMeta = customItem.getItemMeta();
        itemMeta.setDisplayName("传送石");

        // 设置物品的展示素材
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwner(player.getName()); // 替换为你的用户名

        // 将修改后的物品元数据应用到物品堆栈中
        customItem.setItemMeta(skullMeta);
        return customItem;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            log.info("命令输入" + command.getName());
            if (args.length > 0 && "give".equals(args[0])) {
                Player player = (Player) sender;
                ItemStack item = createItem(player);
                player.getInventory().addItem(item);
                player.sendMessage("获得物品：" + item.getItemMeta().getDisplayName());
                return true;
            }
        }

        return false;
    }

    // /**
    //  * 水中方块变成掉落物
    //  * @param event
    //  */
    // @EventHandler
    // // BlockFadeEvent
    // public void onBlockSpread(BlockSpreadEvent event) {
    //     decreaseCount(event.getBlock());
    // }

    @EventHandler
    public void onBlockEntitySpawn(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        decreaseCount(block);
    }

    private boolean inWater(Block block) {
        // 检查方块周围是否有水
        for (BlockFace face : BlockFace.values()) {
            Block nearbyBlock = block.getRelative(face);
            Material nearbyType = nearbyBlock.getType();

            if (nearbyType == Material.WATER) {
                return true;
            }

            // 检查方块是否被水logged
            BlockData blockData = nearbyBlock.getBlockData();
            if (blockData instanceof Waterlogged && ((Waterlogged) blockData).isWaterlogged()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 放置方块事件
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // 在这里编写你的逻辑代码
        Block block = event.getBlockPlaced();
        if (block.getType() == waystoneMaterial) {
            // 判断放置方块的上面是否是水
            if (inWater(block)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("你不能在水中放置方块！");
                return;
            }

            int index = gui.firstEmpty();
            if (index == -1) {
                return;
            }
            ItemStack dia = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta meta = dia.getItemMeta();
            if (meta != null) {
                ItemMeta itemMeta = event.getItemInHand().getItemMeta();
                if (itemMeta != null && itemMeta.hasDisplayName()) {
                    meta.setDisplayName(getRandomChatColor() + itemMeta.getDisplayName());
                    dia.setItemMeta(meta);
                }
            }
            gui.addItem(dia);
            locationMap.put(index, LocationModel.build(block.hashCode(), block.getLocation().serialize()));
        }
    }

    private ChatColor getRandomChatColor() {
        ChatColor[] colors = ChatColor.values();
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }

    private String formatLocation(Location location) {
        return location.getWorld().getName() + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }

    /**
     * 对物品右键
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().name().contains("RIGHT_CLICK_BLOCK")) {
            Block clickedBlock = event.getClickedBlock();
            if (waystoneMaterial == clickedBlock.getType()) {
                player.openInventory(gui);
            }
        }
    }

    // 设置方块的防水属性
    public void setBlockWaterlogged(Block block, boolean waterlogged) {
        BlockState blockState = block.getState();
        if (blockState.getBlockData() instanceof Waterlogged) {
            Waterlogged waterloggedData = (Waterlogged) blockState.getBlockData();
            waterloggedData.setWaterlogged(waterlogged);
            blockState.setBlockData(waterloggedData);
            blockState.update(true);
        }
    }

    /**
     * 拆掉物品
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // 在这里编写你的逻辑代码
        Block block = event.getBlock();
        decreaseCount(block);
    }

    private void decreaseCount(Block block) {
        if (block.getType() != waystoneMaterial) {
            return;
        }
        // 阻止方块拆除
        // event.setCancelled(true);
        for (Map.Entry<Integer, LocationModel> entry : locationMap.entrySet()) {
            if (block.hashCode() == entry.getValue().getBlockHashCode()) {
                gui.clear(entry.getKey());
                locationMap.remove(entry.getKey());
            }
        }
    }

    /**
     * 右键 指定背包 里面的物品
     * @param event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查点击的是玩家的GUI界面
        if (event.getView().getTitle().equals("传送页面")) {
            // 防止玩家拖动物品
            event.setCancelled(true);
            // 检查点击的是选项物品
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // 获取点击的玩家
                Player player = (Player) event.getWhoClicked();

                if (locationMap.containsKey(event.getRawSlot())) {
                    log.info("点击了 " + event.getRawSlot());
                    Map<String, Object> loc = locationMap.get(event.getRawSlot()).getLocation();
                    Location location = Location.deserialize(loc);
                    player.teleport(location);
                    player.sendMessage(String.format("玩家%s传送到了 %s(x,y,z)", player.getName(),
                            formatLocation(location)));
                }

                // 关闭GUI界面
                player.closeInventory();
            }
        }
    }

}
