package brg.com.doitalready;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by jmo on 4/10/2015.
 */
public class AdvancedHashMap extends LinkedHashMap<String, List<Object>> {

    public int numberOfElements() {
        int size = 0;

        for (String key : keySet()) {
            size += get(key).size();
        }

        return size;
    }

    public int indexOfElement(Object object) {
        int index = 0;

        for (Entry<String, List<Object>> entry : entrySet()) {
            List<Object> list = entry.getValue();
            for (Object currObj : list) {
                if (currObj.equals(object)) {
                    return index;
                }
                index++;
            }
        }

        return -1;
    }

    public Object getItemAtIndex(int index) {
        int currIndex = 0;

        for (Entry<String, List<Object>> entry : entrySet()) {
            List<Object> list = entry.getValue();
            for (Object currObj : list) {
                if (currIndex == index) {
                    return currObj;
                }
                currIndex++;
            }
        }

        return null;
    }

    public List<Object> getListForKey(String key) {
        List<Object> list = get(key);
        if (list == null) {
            list = new ArrayList<>();
            list.add(key);
        }
        return list;
    }

    public void putItem(String key, Object object) {
        List<Object> keySet = getListForKey(key);
        keySet.add(object);
        put(key, keySet);
    }

    public void removeItem(String key, Object object) {
        List<Object> keySet = getListForKey(key);
        keySet.remove(object);
    }
}
