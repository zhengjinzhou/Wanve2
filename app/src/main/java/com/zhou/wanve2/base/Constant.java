package com.zhou.wanve2.base;

/**
 * Created by zhou on 2017/12/11.
 */

public class Constant {

    //vpn系统的登录地址
    //服务器地址
    public static final String BASE_URL = "http://121.15.203.82:9210";
    //登录路径
    public static final String HUAMBO_LOGIN_URL = "/DMS_Phone/Login/LoginHandler.ashx?Action=Login&cmd=";
    //主页地址
    public static final String HUAMBO_MAIN_URL = BASE_URL + "/DMS_Phone/Login/QuickLogin.aspx?cmd=";
    /*---------------------------------------------------------------------------------------------*/

    //登录地址
    public static final String LOGIN_URL = "http://121.15.203.82:9210/EIM_PIC//WebService/WebService.asmx";

    public static final String WEBSESSION = "WEBSESSION";
    public static final String NEW_URL = "new_url";
    public static final java.lang.String PSW = "vJo06/qsLDOK5p2FvLqujo8G9eCsjrLJGcg8TGN0QZexSchZjBfneZ1vL4h3BN/EEId5hEBxZWM=";

    //接入地址   拍照端.
    public static  String ssoUrl = "http://121.15.203.82:9210/EIM_PIC/Handlers/SingleSignOnHandler.ashx?Action=SingleSignOnByXML";//首次跳转，进行验证合法性.
    public static String iniUrl = "http://121.15.203.82:9210/EIM_PIC/Handlers/SingleSignOnHandler.ashx?Action=Redirect&SessionId=";//如果验证通过后，第二次跳转，携带验证后的SessionId执行页面跳转.
    public static String UserName ="username";
    public static String UserBean = "UserBean";
    //跳转 地址

    //正在地址？ http://121.15.203.82:9210/WAN_MPDA_Pic/
}
