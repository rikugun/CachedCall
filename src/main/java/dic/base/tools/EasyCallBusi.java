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
 * 方便地调用后台交易的方法
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
     * 对调用后台交易方法的再次封装
     *
     * @param servID    请求的交易号码
     * @param inPkg     请求的内容
     * @param errStruct 如果交易返回null,则errStruct内存储交易返回的错误消息
     * @return 如果调用交易成功，则返回outPkg, 否则返回null
     * @throws AppException 使用例子： ....... //inPkg已经构造成功 ErrorStruct errStruct=new
     *                      ErrorStruct(); DataPackage outPkg=null; try{
     *                      EasyCallBusi.callService("xxxx", inPkg); }catch( AppException
     *                      e) { //调用出现了错误，错误信息为： e.getMessage(); //进行抛出或返回处理 } if(
     *                      outPkg==null ) //交易返回错误信息 { //返回交易的错误信息 errStruct.message
     *                      //错误信息 errStruct.message_code //错误码 //进行返回或抛出处理 } //交易成功返回
     *                      对outPkg进行解析后续处理 ......
     */
    public static DataPackage callService(String servID, DataPackage inPkg, ErrorStruct errStruct) throws AppException {
        return callService(servID, inPkg, errStruct, 0);
    }

    public static DataPackage callService(String servID, DataPackage inPkg, ErrorStruct errStruct, int flag)
            throws AppException {
        DataPackage outPkg = null;
        if (errStruct == null)
            throw new AppException("程序错误：ErrorStruct参数为空!");
        /*
         * //System.out.println("\n***TransCode["+servID+"]'s InPkg Content:");
         * if( inPkg!=null ) //System.out.println(inPkg.toXml());
         * //System.out.println("\n");
         */

        // writeWatchFile(servID, inPkg, false); comments by rikugun 20110313
        // 每次调用都将读取 watch_trans_file.xml 文件,IO消耗比较大

        servID = TransConverter.convert(servID, inPkg);  //翻转交易号 @rikugun
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
            System.err.println("**非致命错误，写watch交易时失败!");
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
                // 设置失效时间
                Date expira = new Date(System.currentTimeMillis() + adapter.getDuration());
                if (mcc.set(transReqKey, inPkg, expira)) {
                    mcc.set(transRspKey, rst, expira);
                }
            }
            return rst;
        }
        if (adapter.compare(inPkg, cachedPkg) == 0) {
            // 获取缓存数据
            DataPackage rst = (DataPackage) mcc.get(transRspKey);
            if (rst != null) {
                return rst;
                //todo: 刷新缓存
            }
            return callService(transId, inPkg, errStruct);
        }

        return null;
    }
}