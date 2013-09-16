package dic.plugins.cache.adapter;

import com.bss.connect.DataPackage;

import dic.router.common.CodeKeys;

public class ServCodeAdapter implements CacheAdapter {

    public int compare(DataPackage pkg1, DataPackage pkg2) {
        if (getServCode(pkg1).equals(getServCode(pkg2))) {
            return 0;
        }
        return -1;
    }

    /**
     * �Ӱ��ڻ�ȡ BusiReq ��serv_code
     *
     * @param pkg
     * @return
     */
    private String getServCode(DataPackage pkg) {
        return pkg.GetColContent(CodeKeys.BUSI_REQ, 1, "serv_code");
    }

    /**
     *
     */
    public String getKey(DataPackage pkg) {
        return getServCode(pkg);
    }

    /**
     * Ĭ�ϻ��������
     */
    public long getDuration() {
        return 1000 * 60 * 5;
    }

}
