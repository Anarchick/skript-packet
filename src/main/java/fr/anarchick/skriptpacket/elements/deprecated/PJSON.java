//package fr.anarchick.skriptpacket.elements.deprecated;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Nonnull;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.comphenix.protocol.utility.MinecraftReflection;
//import com.comphenix.protocol.wrappers.WrappedDataWatcher;
//import com.comphenix.protocol.wrappers.WrappedWatchableObject;
//import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
//import com.google.common.collect.ImmutableMap;
//
//import fr.anarchick.skriptpacket.util.converters.ConverterLogic.Auto;
//
//public class PJSON {
//
//    private Map<String, Object> map = new HashMap<>();
//    
//    public PJSON() {}
//    
//    public PJSON(Map<String, Object> map) {
//        this.map = map;
//    }
//    
//    public PJSON(String jsonString) {
//        if (jsonString != null) { 
//            try {
//                JSONObject json = new JSONObject(jsonString);
//                map = json.toMap();
//            } catch (JSONException ex) {}
//        }
//    }
//    @SuppressWarnings("unchecked")
//    public static PJSON create(Object obj) {
//        if (MinecraftReflection.isMinecraftObject(obj))
//            return fromNMS(obj);
//        if (obj instanceof String)
//            return new PJSON((String) obj);
//        if (obj instanceof WrappedWatchableObject)
//            return fromDataWatcher((WrappedWatchableObject) obj);
//        if (obj instanceof WrappedDataWatcher)
//            return fromDataWatcher((WrappedDataWatcher) obj);
//        if (obj instanceof Map)
//            return new PJSON((Map<String, Object>) obj);
//        return null;
//    }
//    
//    
//    public static PJSON fromNMS(Object nms) {
//        if (MinecraftReflection.isMinecraftObject(nms, "DataWatcher$Item")) {
//            return fromDataWatcher(new WrappedWatchableObject(nms));
//        } else if (MinecraftReflection.isMinecraftObject(nms, "DataWatcher")) {
//            return fromDataWatcher(new WrappedDataWatcher(nms));
//        }
//        return null;
//    }
//    
//    public static PJSON fromDataWatcher(@Nonnull WrappedWatchableObject datawatcher) {
//        Map<String, Object> map = new HashMap<>();
//        String index = String.valueOf(datawatcher.getIndex());
//        map.put(index, datawatcher.getValue());
//        return new PJSON(map);
//    }
//    
//    public static PJSON fromDataWatcher(@Nonnull WrappedDataWatcher datawatcher) {
//        Map<String, Object> map = new HashMap<>();
//        for (int i : datawatcher.getIndexes()) {
//            map.put(String.valueOf(i), datawatcher.getObject(i));
//        }
//        return new PJSON(map);
//    }
//    
//    public PJSON merge(PJSON from) {
//        Map<String, Object> map1 = getMap();
//        Map<String, Object> map2 = from.getMap();
//        Map<String, Object> map = new HashMap<>(map1);
//        map.putAll(map2);
//        return new PJSON(map);
//    }
//    
//    public void put(String key, Object value) {
//        map.put(key, value);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public <T> T get(String key) {
//        return (T) map.get(key);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public <T> T remove(String key) {
//        return (T) map.remove(key);
//    }
//    
//    public String toString() {
//        JSONObject json = toJSON();
//        return (json != null) ? json.toString() : null;
//        
//    }
//    
//    public Object toMojangson() {
//        return Auto.STRING_TO_MOJANGSON.convert(toString());
//    }
//    
//    public WrappedDataWatcher toDataWatcher() {
//        WrappedDataWatcher dw = new WrappedDataWatcher();
//        for (String str : map.keySet()) {
//            Integer index = Integer.valueOf(str);
//            if (index != null) {
//                Object value = map.get(str);
//                if (value != null) {
//                    Serializer serializer = WrappedDataWatcher.Registry.get(value.getClass());
//                    dw.setObject(index, serializer, value);
//                }
//            }
//        }
//        return dw;
//    }
//    
//    public List<WrappedWatchableObject> toDataWatcherItems() {
//        WrappedDataWatcher dw = toDataWatcher();
//        return dw.getWatchableObjects();
//    }
//    
//    public WrappedWatchableObject toDataWatcherItem(Integer index) {
//        String str = index.toString();
//        if (!map.containsKey(str)) return null;
//        return new WrappedWatchableObject(index, map.get(str));
//    }
//    
//    public JSONObject toJSON() {
//        try {
//            return new JSONObject(map);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//    
//    private Map<String, Object> getMap() {
//        return ImmutableMap.copyOf(map);
//    }
//    
//}
