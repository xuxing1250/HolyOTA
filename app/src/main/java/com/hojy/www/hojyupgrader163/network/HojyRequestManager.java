package com.hojy.www.hojyupgrader163.network;

import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;

import java.io.File;

/**
 * Created by Ron on 2016/10/25.
 */

public class HojyRequestManager {

    private RequestQueue requestQueue = NoHttp.getRequestQueueInstance();
    //
    private static class HojyRequestManagerHolder{
        private static final HojyRequestManager requestRequestManager = new HojyRequestManager();
    }

    private HojyRequestManager(){}

    public static HojyRequestManager getInstalce(){
        return HojyRequestManagerHolder.requestRequestManager;
    }

    public void requestCheckUpgradeInfoFromServer(OnResponseListener<String> responseListener){
        Request<String> request = NoHttp.createStringRequest(
                "http://192.168.0.1/xml_action.cgi?method=set&module=duster&file=upgrade_info"
                 , RequestMethod.POST);
        request.setDefineRequestBodyForXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<RGW>\n" +
                "  <upgrade_info>\n" +
                "    <Action>1</Action>\n" +
                "  </upgrade_info>\n" +
                "</RGW>");
        requestQueue.add(1,request,responseListener);
    }

    public void requestDownloadUpgradePackgeFromServer(OnResponseListener<String> responseListener){
        Request<String> request = NoHttp.createStringRequest(
                "http://192.168.0.1/xml_action.cgi?method=set&module=duster&file=upgrade_info"
                , RequestMethod.POST);
        request.setDefineRequestBodyForXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "  <RGW>\n" +
                "    <upgrade_info>\n" +
                "      <Action>10</Action>\n" +
                "    </upgrade_info>\n" +
                "  </RGW>");
        requestQueue.add(10,request,responseListener);
    }

    public void requestApplyUpgradePackage(OnResponseListener<String> responseListener){
        Request<String> request = NoHttp.createStringRequest(
                "http://192.168.0.1/xml_action.cgi?method=set&module=duster&file=upgrade_info"
                , RequestMethod.POST);
        request.setDefineRequestBodyForXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "  <RGW>\n" +
                "    <upgrade_info>\n" +
                "      <Action>20</Action>\n" +
                "    </upgrade_info>\n" +
                "  </RGW>");
        requestQueue.add(20,request,responseListener);
    }

    public void requestUploadFirmwareAndApply(OnResponseListener<String> listener, File file){
        Request<String> request = NoHttp.createStringRequest("http://192.168.0.1/xml_action.cgi?Action=Upload&file=upgrade&command",RequestMethod.POST);
        request.add("file",new FileBinary(file));
        requestQueue.add(30,request,listener);
    }

    public void requestUploadFirmwareState(OnResponseListener<String> listener){
        Request<String> request = NoHttp.createStringRequest("http://192.168.0.1/xml_action.cgi?method=get&module=duster&file=download_local_upgrade",RequestMethod.POST);
        requestQueue.add(33,request,listener);
    }

    public void requestStatus1(OnResponseListener<String> status1Listener){
        Request<String> request = NoHttp.createStringRequest("http://192.168.0.1/xml_action.cgi?method=get&module=duster&file=status1",RequestMethod.POST);
        requestQueue.add(34,request,status1Listener);
    }
}
