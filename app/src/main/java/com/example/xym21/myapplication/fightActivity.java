package com.example.xym21.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import android.os.Handler;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.os.CountDownTimer;




public class fightActivity extends AppCompatActivity {
    private long time;//用于检测按两次 "再按一次退出游戏"
    ItemInfo info3;

    private CountDownTimer mTimer;
    public static boolean clickedd=false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        info3 = new ItemInfo("盔甲", -10, 10, -10);

        hua.flag_norun=true;
       hj.flag_hj=true;
        setContentView(R.layout.activity_fight); getSupportActionBar().hide();//隐藏标题栏
        setContentView(new hua(this));

       if(!clickedd){ }
    initView();
        //setContentView(new hua());跟swing的 add(new hua());好像一个意思，把hua添加进来
    }

    /**
     * 销毁线程方法
     */


    public boolean onKeyDown(int keyCode,KeyEvent event) { //返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){

            long t=System.currentTimeMillis();//获取系统时间
            if(t-time<=500){
                exit(); //如果500毫秒内按下两次返回键则退出游戏
            }else{
                time=t;
                Toast.makeText(getApplicationContext(),"再按一次退出游戏",Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return false;



    }
    private void initView() {
        if (mTimer == null) {
            mTimer = new CountDownTimer((long) (10 * 1000), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (!fightActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);

                    }
                }

                @Override
                public void onFinish() {
                exit();
                }
            };
            mTimer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }



    public void exit() {

        /*try {
            hua.wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        clickedd=true;
        Toast.makeText(this, "挑战结束，培养宠物激发能力吧", Toast.LENGTH_SHORT).show();
        info3.setLife(my.js);
        Intent intent1 = new Intent();
        intent1.putExtra("info", info3);
        //(2)把结果返回给调用者(mainActivity) 通过 onActivityResult 方法返回
        setResult(10, intent1);
        //finish();
        fightActivity.this.finish();
        hua.flag_norun=false;
        hj.flag_hj=false;
        //  my.b=null;
   //     my.list=null;
        // my.drlist=null;
     //   my.my=null;


      //  my.myhj=null;
       // my.myzd=null;
        //  hua.p=null;

    }
}
