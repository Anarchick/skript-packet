package fr.anarchick.skriptpacket.elements.expressions.datawatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comphenix.protocol.wrappers.Vector3F;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class DataWatcher extends WrappedDataWatcher {
    
    public DataWatcher() {
        super();
    }
    
    public DataWatcher(Object handle) {
        super(handle);
    }
    
    public DataWatcher(Entity entity) {
        super(entity);
    }
    
    public DataWatcher(List<WrappedWatchableObject> objects) {
        super(objects);
    }

    public void set(Number index, Object value) {
        if (index == null || value == null) return;
        Serializer serializer;
        if (value instanceof Vector vector) {
            float x = (float) vector.getX();
            float y = (float) vector.getY();
            float z = (float) vector.getZ();
            value = new Vector3F(x, y, z);
            serializer = WrappedDataWatcher.Registry.getVectorSerializer();
        } else {
            serializer = WrappedDataWatcher.Registry.get(value.getClass());
        }
        this.setObject(index.intValue(), serializer, value);
    }

    public JSONObject toJSON() {
        Map<Integer, Object> map = new HashMap<>();
        for (WrappedWatchableObject wwo : this.getWatchableObjects()) {
            map.put(wwo.getIndex(), wwo.getValue());
        }
        try {
            return new JSONObject(map);
        } catch (Exception ex) {
            return new JSONObject();
        }
        
    }
}
