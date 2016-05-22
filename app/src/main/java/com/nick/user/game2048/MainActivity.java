package com.nick.user.game2048;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int score=0;//设置初试分数为0

    private TextView tvScore;//用于显示分数的TextView

    private static MainActivity mainActivity = null;


    public  MainActivity() {
        mainActivity=this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    //完成计分功能
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore = (TextView) findViewById(R.id.tvScore);
    }

    //清空分数
    public void clearScore() {
        score = 0;
        showScore();
    }

    //添加分数
    public void addScore(int s) {
        score += s;
        showScore();
    }

    //显示分数
    public void showScore(){
        tvScore.setText(score + "");
    }

}
