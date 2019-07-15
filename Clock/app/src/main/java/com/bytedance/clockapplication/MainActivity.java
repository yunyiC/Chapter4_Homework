package com.bytedance.clockapplication;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bytedance.clockapplication.widget.Clock;

/**
 *
 * @author ?
 * @date ?
 *
 */
public class MainActivity extends AppCompatActivity {

    private View mRootView;
    private Clock mClockView;

    private HandlerThread mCheckMsgThread;
    private Handler mCheckMsgHandler;
    private boolean isUpdateInfo;

    private static final int MSG_UPDATE_INFO = 0x110;

    //UI线程管理的handler
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = findViewById(R.id.root);
        mClockView = findViewById(R.id.clock);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClockView.setShowAnalog(!mClockView.isShowAnalog());
            }
        });

        //创建后台线程
        initBackThread();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isUpdateInfo = true;
        mCheckMsgHandler.sendEmptyMessage(MSG_UPDATE_INFO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUpdateInfo = false;
        mCheckMsgHandler.removeMessages(MSG_UPDATE_INFO);
    }

    private void initBackThread() {
        mCheckMsgThread = new HandlerThread("clock_update");
        mCheckMsgThread.start();
        mCheckMsgHandler = new Handler(mCheckMsgThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
                checkForUpdate();
                if (isUpdateInfo) {
                    mCheckMsgHandler.sendEmptyMessageDelayed(MSG_UPDATE_INFO, 1000);
                }
            }
        };
    }

    /**
     * 刷新
     */
    private void checkForUpdate() {
        try {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //在这里刷新时钟
                    mClockView.invalidate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCheckMsgThread.quit();
    }
}
