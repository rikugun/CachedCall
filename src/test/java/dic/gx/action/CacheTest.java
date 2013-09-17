package dic.gx.action;

import com.bss.common.app.AppException;
import com.bss.common.event.utils.DataPackUtil;
import com.bss.connect.DataPackage;

import dic.acct.structs.AcctReqStruct;
import dic.base.structs.ErrorStruct;
import dic.base.structs.ViewQueryStruct;
import dic.base.tools.EasyCallBusi;
import dic.busi.structs.BusiQryStruct;
import dic.busi.structs.BusiReqStruct;
import dic.plugins.cache.adapter.CacheAdapter;
import dic.plugins.cache.adapter.ViewQueryCacheAdapter;
import waf.controller.web.action.HTMLActionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Vector;

/**
 * User: rikugun
 * Date: 9/16/13
 * Time: 10:45 AM
 */
public class CacheTest extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DataPackage outPkg;
        BusiReqStruct busiReq = new BusiReqStruct();
        busiReq.oper_no = "LJF";
        busiReq.oper_local_net = "591";
        busiReq.local_net = "591";
        busiReq.role_code = "R0012";
        busiReq.serv_code = "XB014";
        busiReq.dept_no = "GXNN";

        ViewQueryStruct busiQry = new ViewQueryStruct();
//        BusiQryStruct busiQry = new BusiQryStruct();

        busiQry.device_number = "13211346986";
        busiQry.tele_type = "GSM";
        busiQry.id_type="ID001";
        busiQry.any_dog1="0";
        
       

        
        Vector v = new Vector();
        v.add(busiReq);
        v.add(busiQry);

        CacheAdapter adapter = new ViewQueryCacheAdapter();
        ErrorStruct errStruct = new ErrorStruct();
        try {
            DataPackage inPkg = DataPackUtil.tabs2DP(v);
            inPkg.AddModule(703, "device_number,tele_type,local_net,begin_date,end_date,cx_flag");
            inPkg.AddRow();
            inPkg.SetColContent(0, "13211346986");
            inPkg.SetColContent(1, "GSM");
            inPkg.SetColContent(2, "591");
            inPkg.SetColContent(3, "20130501");
            inPkg.SetColContent(4, "20130531");
            inPkg.SetColContent(5, "4");            
            inPkg.SaveRow();
            
            if (request.getParameter("cache") != null) {
                System.out.println("缓存调用");

            	//客户信息查询
                outPkg = EasyCallBusi.callServiceCache("ITXQ9100", inPkg, errStruct, adapter);
                //详单查询
                outPkg = EasyCallBusi.callServiceCache("IIXJ0290", inPkg, errStruct, adapter);

            } else {
                System.out.println("无--缓存调用");

                outPkg = EasyCallBusi.callService("ITXQ9100", inPkg, errStruct);
                outPkg = EasyCallBusi.callService("IIXJ0290", inPkg, errStruct);

            }
        } catch (AppException e) {
            e.printStackTrace();
        }


    }
}
