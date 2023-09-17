package cn.lacknb.locationtransport.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

/**
 * @author gitsilence
 * @date 2023-09-17
 */
public class InventoryHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private ItemStack[] items;

    public InventoryHelper(Inventory inventory) {
        this.items = inventory.getContents();
    }

    public ItemStack[] getItems() {
        return items;
    }

    public static void saveToFile(Inventory inventory, String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             BukkitObjectOutputStream objectOut = new BukkitObjectOutputStream(fileOut)) {

            objectOut.writeInt(inventory.getSize()); // 写入Inventory的大小

            for (ItemStack item : inventory.getContents()) {
                objectOut.writeObject(item); // 依次写入每个ItemStack对象
            }

            System.out.println("Inventory saved to file: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Inventory loadFromFile(String filePath) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             BukkitObjectInputStream objectIn = new BukkitObjectInputStream(fileIn)) {

            int size = objectIn.readInt(); // 读取Inventory的大小
            Inventory inventory = Bukkit.createInventory(null, size, "传送页面");

            for (int i = 0; i < size; i++) {
                ItemStack item = (ItemStack) objectIn.readObject(); // 依次读取每个ItemStack对象
                inventory.setItem(i, item);
            }

            return inventory;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static InventoryHelper deserializeFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            Object obj = objectIn.readObject();
            if (obj instanceof InventoryHelper) {
                return (InventoryHelper) obj;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
