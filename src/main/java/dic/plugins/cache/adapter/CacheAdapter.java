package dic.plugins.cache.adapter;

import com.bss.connect.DataPackage;

/**
 * User: rikugun
 * Date: 9/5/13
 * Time: 3:49 PM
 */
public interface CacheAdapter {


    public int compare(DataPackage pkg1, DataPackage pkg2);

    public String getKey(DataPackage pkg);

    public long getDuration();
}
