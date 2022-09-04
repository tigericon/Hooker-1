package top.niunaijun.HookHelper;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.*;

public class QYCamera {
	public static void HookVIP(XC_LoadPackage.LoadPackageParam lpparam) {


		XposedHelpers.findAndHookMethod(
			"com.lm.components.subscribe.config.UserVipInfo",
			lpparam.classLoader,
			"isVipUser",
			new XC_MethodHook() {

				protected void afterHookedMethod(MethodHookParam param) throws Throwable {

					param.setResult(true);
				}
			});




	}
}
