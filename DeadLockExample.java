package com.example.nguyenhuutu.convenientmenu;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG ="Main2Activity";
    /*
    Bước 1: Tạo một thread lặp (không chết khi run xong)
    Bước 2: Tạo hai thể hiện của thread đó, main sẽ chờ tới khi cả 2 thread sẵn sàng thì bắt đầu thực hiện bước 3
    Bước 3: Mỗi thread sẽ được post 1 runnable thông qua handler của mỗi cái để thực hiện một công việc.
      - Thread A tiến hành lock value1 và sau 50ms sẽ chờ value2 sẵn sàng, khi o2 sẵng sàng thì in ra value1 - value2 kết thúc công việc.
      - Thread B tiến hành lock value2 và sau 50ms sẽ chờ value1 sẵng sàng, khi value1 sẵng sàng thì in ra value2 - value1 kết thúc công việc.

     Do Thread A đã lock value1 và Thread B đã lock value2 nên cả 2 thread sẽ chờ nhau mà không bao giờ kết thúc công việc.
     Main vẫn hoạt động bình thường do cả hai công việc đều thực hiện ở thread khác.
     */

    /*
    Một số component của Android:
    - Activity
    - Service
    - BroadcastReceiver
    - ContentProvider
    - Intent
    - Notification
    - Widget
    ...
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        start();
    }

    private void sleep50Ms() {
        try {
            Thread.sleep(50);
        } catch (Exception ignore) { }
    }

    private void start() {
        final Integer value1 = 10;
        final Integer value2 = 5;
       LoopThread A = new LoopThread();
       A.setName("A");
       LoopThread B = new LoopThread();
       B.setName("B");
       A.start();
       B.start();

       Handler handlerOfA;
       Handler handlerOfB;

       do {
            handlerOfA = A.getHandler();
            handlerOfB = B.getHandler();
       } while (handlerOfA==null||handlerOfB==null);

        Log.d(TAG, "start: A and B are ready now");

        A.getHandler().post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: A");
                synchronized (value1) {
                   sleep50Ms();
                    Log.d(TAG, "run: A is trying to use value2");
                    synchronized (value2) {
                        Log.d(TAG, "run: value 1 - value 2 = "+(value1 -value2));
                    }
                }

                Looper.myLooper().quit();
                Log.d(TAG, "run: Quit A");

            }
        });

        B.getHandler().post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: B");
                synchronized (value2) {
                    sleep50Ms();
                    Log.d(TAG, "run: B is trying to use value1");
                    synchronized (value1) {
                        Log.d(TAG, "run: B value 2 - value 1 = "+(value2 - value1));
                    }
                }

                Looper.myLooper().quit();
                Log.d(TAG, "run: Quit B");
            }
        });
        /*
        Message mA =  A.getHandler().obtainMessage();
        Message mB = B.getHandler().obtainMessage();
        A.getHandler().sendMessage(mA);
        B.getHandler().sendMessage(mB);
        */
    }

    static class LoopThread extends Thread {
        Handler mHandler;

        public Handler getHandler() {
            return mHandler;
        }

        @Override
        public void run() {
          //  super.run();
            Looper.prepare();
            mHandler = new Handler();
            Looper.loop();
        }
    }
}

