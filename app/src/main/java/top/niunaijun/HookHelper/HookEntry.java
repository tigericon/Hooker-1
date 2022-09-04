package top.niunaijun.HookHelper;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.*;
import android.app.Dialog;
import android.os.StrictMode;

public class HookEntry implements IXposedHookLoadPackage {
    public static final String TAG = "HookEntry";
    private static final String 云记 = "com.jideos.jnotes";
    private static final String 剪映 = "com.lemon.lv";    
    private static final String 冰箱 = "com.catchingnow.icebox";
    private static final String VMOS = "com.vmos.pro";
    private static final String 激活状态 = "top.niunaijun.HookHelper";
    private static final String 神奇脑波 = "imoblife.brainwavestus";
    private static final String 小熊录屏 = "com.duapps.recorder";
    private static final String 番茄小说 = "com.dragon.read";
    private static final String 番茄畅听 = "com.xs.fm";
    private static final String 嘀嗒清单 = "cn.ticktick.task";
    private static final String 讯飞语记 = "com.iflytek.vflynote";
    private static final String 腾讯微云 = "com.qq.qcloud";
    private static final String 酷我音乐 = "cn.kuwo.player";
    private static final String 轻颜相机 = "com.gorgeous.lite";
    private static final String 有道云笔记 = "com.youdao.note"; 
    private static final String 万能小组件 = "com.growing.topwidgets";    
    private static final String 宇宙工具箱 = "com.cosmos.tools";  
    private static final String 小组件盒子 = "io.iftech.android.box";
	private static final String ES文件浏览器 ="com.estrongs.android.pop";
    private static final String 表格Excel手机版 = "com.yipeinet.excel";   
    private static final String FakeLocation虚拟定位 = "com.lerist.fakelocation";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }


        if (lpparam.packageName.equals(番茄小说)) {
            FQXS.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(VMOS)) {
            VMOSPro.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(剪映)) {
            JY.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(讯飞语记)) {
            XFYL.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(番茄畅听)) {
            FQCT.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(冰箱)) {
            Freezeit.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(FakeLocation虚拟定位)) {
            FakeLocation.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(腾讯微云)) {
            QQCloud.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(表格Excel手机版)) {
            PhoneExcel.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(有道云笔记)) {
            YDCloud.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(酷我音乐)) {
            KwMusic.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(万能小组件)) {
            XZJ.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(激活状态)) {
            CheckMK.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(宇宙工具箱)) {
            YZTools.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(轻颜相机)) {
            QYCamera.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(小组件盒子)) {
            XZJBox.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(嘀嗒清单)) {
            TickList.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(神奇脑波)) {
            MagicNB.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(云记)) {
            CloudNote.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(小熊录屏)) {
			XXVideo.HookVIP(lpparam);
        } else if (lpparam.packageName.equals(ES文件浏览器)) {
			ESManager.HookVIP(lpparam);
        } else
			FuckDialog.HookVIP(lpparam);
        {


        }

    }


}
