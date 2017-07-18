package com.example.zhangyang.ui_thread;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        bt= (Button) findViewById(R.id.bt);
        new Thread(new Runnable() {
            @Override
            public void run() {
                tv.setText("子线程可以更新UI吗");
            }
        }).start();



        //当点击会崩溃
        // 可通过handler发生消息、 handler.post(Runnable r)、view.post(Runnable r)、activity.runOnUIThread(Runnable r)等方法来通知主线程更新UI
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("子线程真的可以更新UI吗?");
                    }
                }).start();

                /**
                 * === handler.post(Runnable r) ===
                 */
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandler.sendEmptyMessage(100);
//                    }
//                }).start();

                /**
                 * === handler.post(Runnable r) ===
                 */
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTvTest.setText("handler.post");
//                            }
//                        });
//                    }
//                }).start();

                /**
                 * === view.post(Runnable r) ===
                 */
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                tv.setText("view.post");
//                            }
//                        });
//                    }
//                }).start();

                /**
                 * === activity.runOnUIThread(Runnable r) ===
                 */
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tv.setText("runOnUIThread");
//                            }
//                        });
//                    }
//                }).start();
            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                tv.setText("由Handler发送消息");
            }
        }
    };
}
