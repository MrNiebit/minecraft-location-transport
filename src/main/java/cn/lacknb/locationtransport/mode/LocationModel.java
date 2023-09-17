package cn.lacknb.locationtransport.mode;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.util.Map;

/**
 * @author gitsilence
 * @date 2023-09-17
 */
public class LocationModel implements Serializable {
    
    private int blockHashCode;
    
    private Map<String, Object> location;
    
    public static LocationModel build(int block, Map<String, Object> location) {
        LocationModel model = new LocationModel();
        model.setLocation(location);
        model.setBlockHashCode(block);
        return model;
    }

    public int getBlockHashCode() {
        return blockHashCode;
    }

    public void setBlockHashCode(int blockHashCode) {
        this.blockHashCode = blockHashCode;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }
}
