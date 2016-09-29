package com.junqi.user.huanxin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText et_username, et_friend, et_content;
    TextView tv_content;
    EMMessageListener msgListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resever();
        init();
    }

    private void init() {
        et_username = (EditText) findViewById(R.id.et_use_name);
        et_friend = (EditText) findViewById(R.id.ed_friend);
        et_content = (EditText) findViewById(R.id.et_content);
        tv_content = (TextView) findViewById(R.id.tv_content);


        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });
    }

    private void send() {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(et_content.getText().toString(), et_friend.getText().toString());
//如果是群聊，设置chattype，默认是单聊
//        if (chatType == CHATTYPE_GROUP)
//            message.setChatType(EMMessage.ChatType.GroupChat);
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

    }

    private void add() {
        //参数为要添加的好友的username和添加理由
        try {
            EMClient.getInstance().contactManager().addContact(et_friend.getText().toString(), "");
            //        获取好友的 username list，开发者需要根据 username 去自己服务器获取好友的详情
            List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();

            for (int i = 0; i < usernames.size(); i++) {
                Log.i("add", "add" + usernames.get(i));
            }

        } catch (HyphenateException e) {
            e.printStackTrace();
        }


    }

    private void login() {

        EMClient.getInstance().login(et_username.getText().toString(), "1234", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {
//                 这个界面可以添加loading界面
            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }

    private void register() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                //注册失败会抛出HyphenateException
                try {
                    EMClient.getInstance().createAccount(et_username.getText().toString(), "1234");//同步方法
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }


        }.start();


    }
       private  void  resever(){
//           通过注册消息监听来接收消息。

           EMMessageListener msgListener = new EMMessageListener() {

               @Override
               public void onMessageReceived(List<EMMessage> messages) {
                   //收到消息
               }

               @Override
               public void onCmdMessageReceived(List<EMMessage> messages) {
                   //收到透传消息
               }

               @Override
               public void onMessageReadAckReceived(List<EMMessage> messages) {
                   //收到已读回执
               }

               @Override
               public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                   //收到已送达回执
               }

               @Override
               public void onMessageChanged(EMMessage message, Object change) {
                   //消息状态变动
               }
           };
           EMClient.getInstance().chatManager().addMessageListener(msgListener);
//           记得在不需要的时候移除listener，如在activity的onDestroy()时
           EMClient.getInstance().chatManager().removeMessageListener(msgListener);
       }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().logout(true);
        if (msgListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        }
    }}
