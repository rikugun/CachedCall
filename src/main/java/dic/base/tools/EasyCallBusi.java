package dic.base.tools;

import com.bss.backcall.CallBusi;
import com.bss.common.app.AppException;
import com.bss.connect.DataPackage;
import com.danga.MemCached.MemCachedClient;
import dic.base.structs.ErrorStruct;
import dic.plugins.cache.adapter.CacheAdapter;
import dic.plugins.memcached.service.MemcachedClientFactory;

import java.util.Date;
import java.util.HashMap;

/**
 * ����ص��ú�̨���׵ķ���
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author WISEKING
 * @version 1.0
 */
public class EasyCallBusi {
    /**
     * �Ե��ú�̨���׷������ٴη�װ
     *
     * @param servID    ����Ľ��׺���
     * @param inPkg     ���������
     * @param errStruct ������׷���null,��errStruct�ڴ洢���׷��صĴ�����Ϣ
     * @return ������ý��׳ɹ����򷵻�outPkg, ���򷵻�null
     * @throws AppException ʹ�����ӣ� ....... //inPkg�Ѿ�����ɹ� ErrorStruct errStruct=new
     *                      ErrorStruct(); DataPackage outPkg=null; try{
     *                      EasyCallBusi.callService("xxxx", inPkg); }catch( AppException
     *                      e) { //���ó����˴��󣬴�����ϢΪ�� e.getMessage(); //�����׳��򷵻ش��� } if(
     *                      outPkg==null ) //���׷��ش�����Ϣ { //���ؽ��׵Ĵ�����Ϣ errStruct.message
     *                      //������Ϣ errStruct.message_code //������ //���з��ػ��׳����� } //���׳ɹ�����
     *                      ��outPkg���н����������� ......
     */
    public static DataPackage callService(String servID, DataPackage inPkg, ErrorStruct errStruct) throws AppException {
        return callService(servID, inPkg, errStruct, 0);
    }

    public static DataPackage callService(String servID, DataPackage inPkg, ErrorStruct errStruct, int flag)
            throws AppException {
        DataPackage outPkg = null;
        if (errStruct == null)
            throw new AppException("�������ErrorStruct����Ϊ��!");
        /*
         * //System.out.println("\n***TransCode["+servID+"]'s InPkg Content:");
         * if( inPkg!=null ) //System.out.println(inPkg.toXml());
         * //System.out.println("\n");
         */

        // writeWatchFile(servID, inPkg, false); comments by rikugun 20110313
        // ÿ�ε��ö�����ȡ watch_trans_file.xml �ļ�,IO���ıȽϴ�

        servID = TransConverter.convert(servID, inPkg);  //��ת���׺� @rikugun
        outPkg = CallBusi.callService(CallBusi.BS_TUXEDO, servID, inPkg, flag);
        int iTranRet = ParaTool.isSuccess(outPkg, errStruct);
        // writeWatchFile(servID, outPkg, true);
        /*
         * //System.out.println("\n***TransCode["+servID+"]'s OutPkg Content:");
         * if( outPkg!=null ) //System.out.println(outPkg.toXml());
         * //System.out.println("\n");
         */
        if (iTranRet == 0) {
            return outPkg;
        } else {
            return null;
        }
    }

    private static void writeWatchFile(String servID, DataPackage pkg, boolean inFlag) {
        try {
            StringBuffer fileDir = new StringBuffer();
            HashMap hm = com.bss.common.sysconfig.GetSystemConfig.parseWatchTrans(fileDir);
            if (hm.containsKey(servID)) {
                String file = fileDir + servID;
                if (inFlag)
                    file += "_in.xml";
                else
                    file += "_out.xml";
                pkg.saveToXml(file);
            }
        } catch (Exception e) {
            System.err.println("**����������дwatch����ʱʧ��!");
            ;
        }
    }

    public static DataPackage callServiceCache(String transId,
                                               DataPackage inPkg, ErrorStruct errStruct, CacheAdapter adapter)
            throws AppException {
        String transReqKey = "trans_req:" + transId + ":"
                + adapter.getKey(inPkg);
        String transRspKey = "trans_rsp:" + transId + ":"
                + adapter.getKey(inPkg);

        MemCachedClient mcc = MemcachedClientFactory.getInstance();
        DataPackage cachedPkg = (DataPackage) mcc.get(transReqKey);
        if (cachedPkg == null) {

            DataPackage rst = callService(transId, inPkg, errStruct);
            if (rst != null) {
                // ����ʧЧʱ��
                Date expira = new Date(System.currentTimeMillis() + adapter.getDuration());
                if (mcc.set(transReqKey, inPkg, expira)) {
                    mcc.set(transRspKey, rst, expira);
                }
            }
            return rst;
        }
        if (adapter.compare(inPkg, cachedPkg) == 0) {
            // ��ȡ��������
            DataPackage rst = (DataPackage) mcc.get(transRspKey);
            if (rst != null) {
                return rst;
                //todo: ˢ�»���
            }
            return callService(transId, inPkg, errStruct);
        }

        return null;
    }
}