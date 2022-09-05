

Xposed模块配置
Project
┗ app
  ┗ lib					      //手动创建目录添加文件					 
	┗ XposedBridgeApi-82.jar  //右键 Add as library 添加到库

Project
┗ app
  ┗ build.gradle
        dependencies{ //在这里插入代码
		//注意 IDA自动填写的要去掉 implementation files('lib\\XposedBridgeApi-82.jar')
	  	compileOnly 'de.robv.android.xposed:api:82'	//设置只编译不打包
	    }

	
Project
┗ app
  ┗ src
    ┗ AndroidManifest.xml
		<application
		android:xxx = "" 
		.......... >
	
	     <meta-data android:name="xposedmodule" android:value="true" /> 			//让Xposed识别为模块
	     <meta-data android:name="xposedminversion" android:value="82" /> 		//最小支持Api版本
	     <meta-data android:name="xposeddescription" android:value="描述" />		//模块描述
		 
		</application>

Project
┗ app
  ┗ src
    ┗ main			//手动创建目录添加文件
	  ┗ assets
        ┗ xposed_init //里面写xposed启动函数入口



动态加载劫持
┗ 动态加载Dex
  ┗ XposedHelpers.findAndHookMethod - > Application.class, "attach", Context.class
┗ 动态加载Class
  ┗ XposedBridge.hookAllMethods -> ClassLoader.class, "loadClass"


//////第二节//////
自学推荐
http://c.biancheng.net/java/ Java基础教程（从入门到精通）
https://www.cnblogs.com/gordon0918/p/6732100.html 基本用法
https://skyhand.blog.csdn.net/article/details/52574650 插件开发之二: Xposed一些知识
https://skyhand.blog.csdn.net/article/details/52575900 插件开发之三: 编写广告去除插件
https://www.jianshu.com/p/616be7deec33 Xposed系列之微信屏蔽拍一拍(三)


ClassLoader (类加载器)
类加载器可以有很多个
也就是有很多个爸爸 
她们的孩子可能名字相同 
但是做着不同的事
所以要找对孩子才行

(我是第一个加载器加载的)
ClassLoader 
┗ Main
┗ OKhttp3
┗ MMKV
  ┗ MMKV$Thread1
  ┗ MMKV$Thread2
┗ Account
┗ 很多很多类

(我是第二个加载器加载的)
ClassLoader  
┗ MMKV               //如果被动态加载 ClassLoader = MMKV.getClassLoader() 
  ┗ MMKV$Thread1     //Clazz = findClass(MMKV.getClassName() + "$Thread1", ClassLoader)
  ┗ MMKV$Thread2     //findAndHookMethod(Clazz, "Method", Object... , CallBack)
┗ Account


动态加载劫持
  ┗ 动态加载Dex
	XposedHelpers.findAndHookMethod - > Application.class, "attach", Context.class
	beforeHookedMethod(){
	context = (Context)param.args[0];
	ClassLoader classLoader = context.getClassLoader();
	}
  
  ┗ 动态加载Class
	XposedBridge.hookAllMethods -> ClassLoader.class, "loadClass"
	afterHookedMethod(){
	if (param.hasThrowable()) return;
	if (param.args.length != 1 ) return;
	Class clszz = (Class) param.getResult();
	if (clszz.getName.equals("Class")){
		XposedHelpers.findAndHookMethod(clszz, "Method")
		}
	}	



//堆栈调用(StackTraceElement/堆栈跟踪元素) -> PrintStack
package com.god

class Test {
	OnCreate(){
	if (UserInfo != null){
		isVip()
		}
	}

	Stack(){
	print(Stack)
	}

	isVip(){
	print("我被执行了")
	Stack()
	}

	main(){ //首先被执行
	OnCreate()
	}
}

//main 首先被执行
打印结果是:
			我被执行了
			at com.god.Test.isVip(Test.java:?)
			at com.god.Test.OnCreate(Test.java:?)
			at com.god.Test.main(Test.java:?)
//这样的好处是方便分析是谁调用 更好的 去分析整个执行流程


关于 XposedBridge  基础功能
//注意！！！
Method 被 abstract || native 修饰不能HOOK

//这是最基本Hook
public static XC_MethodHook.Unhook hookMethod(Member hookMethod, XC_MethodHook callback)
┗ 什么是Hook 
//hookMethod 的第二个参数是回调 但她有两种类型 这里先简单介绍 后面会详细说
	┗ a.XC_MethodHook   	  执行回调 beforeHookedMethod(MethodHookParam param) 调用前回调 和 afterHookedMethod(MethodHookParam param) 调用后回调
	//原本执行流程
	调用函数 -> A函数
	//被Hook后
	调用函数 -> beforeHookedMethod(MethodHookParam param) -> A函数 -> afterHookedMethod(MethodHookParam param)
	//如果
	beforeHookedMethod(MethodHookParam param) 中调用 param.setResult(); 不会继续执行 !≠-> A函数 -> afterHookedMethod(MethodHookParam param)
	
	
	┗ b.XC_MethodReplacement	  替换方法 replaceHookedMethod
	//原本执行流程
	调用函数 -> A函数
	//被Hook后
	调用函数 -> replaceHookedMethod(MethodHookParam param) !≠-> A函数  不会继续调用 A函数

//给大家看个两个 XposedApi 提供的源代码 大家看看就好
┗ Hook全部 相同名称 的方法 返回 XC_MethodHook.Unhook 的集合
public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
	Set<XC_MethodHook.Unhook> unhooks = new HashSet<XC_MethodHook.Unhook>();
	for (Member method : hookClass.getDeclaredMethods())
		if (method.getName().equals(methodName))
			unhooks.add(hookMethod(method, callback));
	return unhooks;
}


┗ Hook全部 构造函数 的方法 返回 XC_MethodHook.Unhook 的集合
public static Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
	Set<XC_MethodHook.Unhook> unhooks = new HashSet<XC_MethodHook.Unhook>();
	for (Member constructor : hookClass.getDeclaredConstructors())
		unhooks.add(hookMethod(constructor, callback));
	return unhooks;
}
	
//其实 'XposedBridge' 和 'XposedHelpers' 提供的Hook会都调用 hookMethod 这个函数来实现


┗ 关于 hookMethod 的返回 XC_MethodHook.Unhook 类型
	//这个看看就好 主要是 用于 卸载Hook (unhook()) 获取Hook当前方法 (getHookedMethod()) 获取Hook回调XC_MethodHook (getCallback())
	public class Unhook implements IXUnhook<XC_MethodHook> {
		private final Member hookMethod;

		/*package*/ Unhook(Member hookMethod) {
			this.hookMethod = hookMethod;
		}

		/**
		 * Returns the method/constructor that has been hooked.
		 */
		public Member getHookedMethod() {
			return hookMethod;
		}

		@Override
		public XC_MethodHook getCallback() {
			return XC_MethodHook.this;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void unhook() {
			XposedBridge.unhookMethod(hookMethod, XC_MethodHook.this);
		}

	}
	
	
┗ 关于 XC_MethodHook 类型 

	┗ 普通Hook方法
	class MethodHook extends XC_MethodHook {
		
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			
			Object thisClassObject = param.thisObject; //当前类的hook方法所在类的对象
			Class<?> thisClass = thisClassObject.getClass(); //当前类的hook方法所在类
			
			Object[] args = param.args; //调用函数的全部参数
			
			//假设参数一是 Xcc 个类 
			//你需 获取这个类里面的 public String Key 数据
			//并且 调用该类里面的 boolean Pass(String Key) 函数
			//你应该这么做
			
			//所有类都属于 Object
			Class<?> c_Xcc = args[0].getClass();
			
			//获取类里面的 字段
			//clszz.getDeclaredField("FieldName"); 可以访问查找 全部字段 类型
			//clszz.getDeclaredFields(); 获取全部的数组
			
			//clszz.getField("FieldName"); 只能访问查找 没保护 的 public 修饰的之类的字段
			//clszz.getFields(); 获取全部 没保护 的 public 修饰的之类的字段 的数组
			
			//简单点区分 有 Declared 获取范围更广(即可以访问未公开字段)
			//加 s 是获取她的全部 即数组
			//说了那么多 肯定也是 getDeclaredField 这个函数好用 但是可能执行效率低下
			Field Key = c_Xcc.getDeclaredField("Key");
			
			//如果有保护会导致访问出错 抛出异常 所有保险起见都 设置为可访问
			Key.setAccessible(true);
			
			//所有类都属于 Object
			//public String Key 的对象是 o_Key(Object)
			//o_Key.getClass() = String 这是必然的
			Object o_Key = Key.get(c_Xcc);
			
			//获取类里面的 方法
			//Clazz.getDeclaredMethod("MethodName");
			//Clazz.getDeclaredMethods();
			
			//Clazz.getMethod("MethodName");
			//Clazz.getMethods();
			//这些都和上面同理
			
			//注意！！！
			//public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
			//获取方法时 不仅名称要对应 参数类型也要对应
			//我们这个方法第一个参数是 String 类型 就填 String.class
			//什么类就填 什么 class 有几个参数就得填几个 必须对应类型
			//没有的 类型 就用 XposedHelpers.findClass 函数 去获取填入
			
			Method Pass = c_Xcc.getDeclaredMethod("Pass", String.class);
			//如果有保护会导致访问出错 抛出异常 所有保险起见都 设置为可访问
			Pass.setAccessible(true);
			
			//public native Object invoke(Object obj, Object... args)
			//第一个填类的 Object 后面就是参数
			//她的返回类型是 boolean 就强转一下吧
			boolean issucceed = (boolean)Pass.invoke(c_Xcc, (String)o_Key);
			//这样就完成一个调用了
			if (!issucceed){
				//修改返回值
				//注意！！！
				//修改返回值后 执行完 当前函数后 不会执行 原函数
				param.setResult(true);
			}
			
			
		}

		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable {
			Object Result = param.getResult();
			//a.获取返回值 做想做的事
			//b.修改返回值
			//c.执行完后可以去调用某些函数(一般等执行完是为了初始化数据在调用)和上面一样的方法
			if (Result.toString.contains("AD广告Uir:")){
				param.setResult("没有广告再见");
			}
			
		}
		
	}

	┗ 重写/替换方法
	class MethodReplacement extends XC_MethodReplacement {
		
		@Override
		protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
			Object Result = null;//这是一个空的返回值 null
			if (param.args[0].toString().equals("送钱了")){
				//我现在反悔了 想调用一下原函数
				
				//原封不动的调用
				Result = XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
				//或 自己改参数的调用
				//XposedBridge.invokeOriginalMethod(param.method, param.thisObject, new Object[]{"送钱了",10000})
				
				//你都送钱了那我就不 Hook你了
				//卸载Hook
				XposedBridge.unhookMethod(param.method,this);
				//以上说的两个方法在 Hook 回调中均可使用不限于 replaceHookedMethod beforeHookedMethod afterHookedMethod
			}
			
		
			//注意 返回Object 一定要和 原函数类型相符 否则 程序容易出错
			return Result;
		}
		
	}

	┗ 构造概念 Constructor 和 Method 的区别
	//反编译中经常看到的 <init> 函数就是构造函数 Constructor 
	//注意!!!
	//a.在选择Hook时要使用 hookConstructor 而且不是 hookMethod
	//b.一般用Hook用 afterHookedMethod 的回调进行操作 这样可以避免一些数据未被初始化导致调用崩溃
	class a {
		final int a;
		final String b;
		final long c;
		a(int A,String B){ //构造函数 她可以被重写 ,当被 new a(1,"b") 会被执行
			a = A;
			b = B;
		}
		
		a(int A,String B,long C){ //构造函数的 重写 ,当被 new a(2,"bb",2L) 会被执行
			a = A;
			b = B;
			c = C;
		}
		
	}


关于 XposedHelpers 辅助功能
//她主要封装了很多查找功能 方便使用 大家会了基础在会这个没毛病吧

//findMethod 的底层实现是
clazz.getDeclaredMethod(methodName, parameterTypes)


Object... 代表可以多个参数 但在这些函数中最后一个会被当作 Callback(Hook回调/函数回调)
findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback)
                           clazz = findClass(className, classLoader)//中间加了个辅助
				            ↓
findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback)

//

findAndHookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback)
								clazz = findClass(className, classLoader)//中间加了个辅助
								 ↓
findAndHookConstructor(Class<?> clazz, Object... parameterTypesAndCallback)
//以上最终实现 Hook
XposedBridge.hookMethod()


clazz.newInstance()
//Java9之后推荐使用
clazz.getDeclaredConstructor().newInstance()
