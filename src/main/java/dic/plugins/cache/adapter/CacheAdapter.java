package dic.plugins.cache.adapter;

import com.bss.connect.DataPackage;

/**
 * User: rikugun
 * Date: 9/5/13
 * Time: 3:49 PM
 */
public interface CacheAdapter {
	//比较2个数据包请求是否相同,如果相同则缓存/获取缓存内容
    public int compare(DataPackage pkg1, DataPackage pkg2);
	//获取请求包的标识key,唯一
    public String getKey(DataPackage pkg);
	//获取缓存的时间
    public long getDuration();
}
