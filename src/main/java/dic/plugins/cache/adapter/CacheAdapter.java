package dic.plugins.cache.adapter;

import com.bss.connect.DataPackage;

/**
 * User: rikugun
 * Date: 9/5/13
 * Time: 3:49 PM
 */
public interface CacheAdapter {
	//�Ƚ�2�����ݰ������Ƿ���ͬ,�����ͬ�򻺴�/��ȡ��������
    public int compare(DataPackage pkg1, DataPackage pkg2);
	//��ȡ������ı�ʶkey,Ψһ
    public String getKey(DataPackage pkg);
	//��ȡ�����ʱ��
    public long getDuration();
}
