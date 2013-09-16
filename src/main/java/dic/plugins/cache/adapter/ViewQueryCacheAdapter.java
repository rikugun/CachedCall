package dic.plugins.cache.adapter;

import com.bss.connect.DataPackage;

public class ViewQueryCacheAdapter implements CacheAdapter {

	public int compare(DataPackage pkg1, DataPackage pkg2) {
		if (pkg1.GetColContent(37, 1, "device_number").equals(
				pkg2.GetColContent(37, 1, "device_number"))
				&& pkg1.GetColContent(37, 1, "tele_type").equals(
						pkg2.GetColContent(37, 1, "tele_type"))) {
			return 0;
		}
		return -1;
	}

	public String getKey(DataPackage pkg) {
		return pkg.GetColContent(37, 1, "device_number")
				+ pkg.GetColContent(37, 1, "tele_type");
	}

	public long getDuration() {
		return 1000*60*5;
	}

	// private String

}
