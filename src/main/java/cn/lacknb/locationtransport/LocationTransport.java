package cn.lacknb.locationtransport;

import cn.lacknb.locationtransport.listener.LocationTransportListener;
import cn.lacknb.locationtransport.utils.InventoryHelper;
import cn.lacknb.locationtransport.utils.SerializationUtils;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public final class LocationTransport extends JavaPlugin  {

    private static final Logger log = Logger.getLogger("locationTransport");

    @Override
    public void onEnable() {
        // 加载数据
        Object location = SerializationUtils.deserializeObject("location_serializable.dat");
        Inventory gui = InventoryHelper.loadFromFile("gui_serializable.dat");

        // 使用BukkitRunnable的runTaskTimer方法来执行定时任务
        new BukkitRunnable() {
            @Override
            public void run() {
                // 在这里编写定时任务的逻辑
                SerializationUtils.serializeObject(LocationTransportListener.locationMap, "location_serializable.dat");
                InventoryHelper.saveToFile(LocationTransportListener.gui, "gui_serializable.dat");
                log.info(" ~~~~ serializeObject!!");
            }
        }.runTaskTimer(this, 0L, 7200L);

        LocationTransportListener transportListener = new LocationTransportListener(location, gui);
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(transportListener, this);
        // 注册指令
        log.info("插件加载中...");
        PluginCommand command = getCommand("waystone");
        command.setExecutor(transportListener);
        log.info("插件加载完成");
        // 设置定时任务，自动序列化数据, 36000个游戏时刻 = 30分钟, 1秒钟=20个游戏时刻

    }

}
