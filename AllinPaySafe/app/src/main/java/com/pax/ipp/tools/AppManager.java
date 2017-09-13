package com.pax.ipp.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.LinkedList;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * <p/>
 * Created by __Berial___
 */
public class AppManager {

	private static LinkedList<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}


	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			synchronized (AppManager.class){
				if (instance==null)
				instance = new AppManager();
			}
		}
		if (activityStack == null) {
			activityStack = new LinkedList<>();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new LinkedList<>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		return activityStack.getLast();
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.getLast();
		if (activity != null) {
			finishActivity(activity);
		}

	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
		}
	}
	public void removeActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
		}
	}
	/**
	 * 指定类名的Activity 是否存在
	 * @param cls
     */
	public boolean isActivity(Class<?> cls) {
		boolean flag= false;
		if(activityStack==null||activityStack.size()==0)return false;
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 *
	 * @param cls
	 * @return
     */
	public Activity getActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		if (!isActivity(cls))return;
		try {
			for (Activity activity : activityStack) {
				if (activity.getClass().equals(cls)) {
					finishActivity(activity);
				}
			}
		}catch (Exception e){

		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(),UserInfoConfig.,"");
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(),UserInfoConfig.,"");

		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.killBackgroundProcesses(context.getPackageName());
			System.exit(0);
		} catch (Exception ignored) {
		}
	}

	/**
	 * 退出登录
	 */
	public void ExitLogin() {
//		Api.uid="";
//		Api.token="";
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(),UserInfoConfig.spToken,"");
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(),UserInfoConfig.spUid,"");
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(),UserInfoConfig.UserInfo,"");
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(),UserInfoConfig.UserRegisterStatus,"");
//		SharedPreUtils.getInstanse().putKeyValue(HXXCApplication.getInstance(), UserInfoConfig.FlagLogin,false);
	}

}