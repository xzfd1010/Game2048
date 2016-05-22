package com.nick.user.game2048;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by user on 16/5/21.
 * Card类继承自FrameLayout,
 */
public class Card extends FrameLayout{

    private TextView label;//卡片显示数字的TextView

    private int num = 0;//卡片上的数字

    public Card(Context context) {
        super(context);

        //构造方法中初始化label
        label = new TextView(getContext());
        label.setTextSize(32);//设置文本大小
        label.setBackgroundColor(0x33ffffff);//设置文字背景
        label.setGravity(Gravity.CENTER);//把文本放置到中间

        //利用布局参数来控制布局,(-1,-1)代表填充整个父级容器,即Card的Layout只显示TextView一个控件
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);//设置卡片间隔为10像素
        addView(label, lp);//添加控件到父布局

        setNum(0);//设置初始数字为0
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        if (num <= 0) {
            label.setText("");//如果数字小于等于0,显示空
        }else{
            label.setText(num + "");//显示数字
        }
    }

    //判断两张卡片是否相同,即判断卡片上的数字是否相同
    public boolean equals(Card o) {
        return getNum() == o.getNum();//用来判断两张卡片上的数字是否相同
    }

}
