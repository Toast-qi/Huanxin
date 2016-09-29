package com.junqi.user.huanxin;

import android.app.Application;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/**
 * Created by user on 2016/9/29.
 */
public class MyApplication extends Application {
//接下来按照开发文档基础功能来写 将oncreate初始化
    @Override
    public void onCreate() {
        super.onCreate();
        initShare();
        initHX();
    }



    private  void initShare(){

    }

    private void initHX(){
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }
}
