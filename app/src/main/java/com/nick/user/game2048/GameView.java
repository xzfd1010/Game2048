package com.nick.user.game2048;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16/5/21.
 */
public class GameView extends GridLayout {

    //用2维数组记录卡片方阵
    private Card[][] cardsMap = new Card[4][4];

    //用List集合记录卡片坐标,Point是每个卡片的坐标
    private List<Point> emptyPoints = new ArrayList<>();

    public GameView(Context context) {
        super(context);

        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initGameView();
    }

    //在构造方法中添加game的入口方法,initGameView();
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initGameView();
    }

    private void initGameView() {
        //使卡片成四列
        setColumnCount(4);
        //设置整体背景
        setBackgroundColor(0xffbbada0);

        //监听手指的滑动
        setOnTouchListener(new OnTouchListener() {

            private float startX,startY,offsetX,offsetY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();//记录手指按下的x坐标
                        startY = motionEvent.getY();//记录手指按下的y坐标
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = motionEvent.getX() - startX;//记录手指抬起的x坐标偏移量
                        offsetY = motionEvent.getY() - startY;//记录手指抬起的y坐标偏移量
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            //如果x偏移量大于y偏移量,则意图为在水平方向上滑动
                            if (offsetX < -5) {//此时是左滑,-5是为了设定范围,避免太灵敏
                                swipeLeft();
                            } else if (offsetX > 5) {//此时是右滑
                                swipeRight();
                            }
                        } else {
                            if (offsetY < -5) {//手机坐标系中向下为正方向
                                swipeUp();
                            } else if (offsetY > 5) {
                                swipeDown();
                            }
                        }
                        break;
                }
                return true;//返回true才能触发事件
            }
        });

    }

    //动态的获取屏幕尺寸,根据屏幕尺寸设置卡片大小,方法只在被创建的时候执行一次,旋转的时候不会执行,已经设置为直立
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int cardWidth = (Math.min(w, h)-10)/4;//手机屏幕的宽高那个大不知道,取较小值,减10留出空隙,除4得到卡片宽度

        addCards(cardWidth, cardWidth);//正方形

        startGame();

    }

    private void addCards(int cardWidth,int cardHeight){

        Card c;
        //通过循环把16张卡片添加到GameView中
        for(int y=0;y<4;y++) {
            for(int x=0;x<4;x++) {
                c = new Card(getContext());
                c.setNum(0);//初始数字均设为0
                addView(c, cardWidth, cardHeight);//把卡片添加到父布局中

                cardsMap[x][y] = c;//记录单张卡片
            }
        }
    }

    private void startGame() {

        MainActivity.getMainActivity().clearScore();//开始游戏时清零分数

        //归零所有卡片
        for(int y=0;y<4;y++) {
            for(int x=0;x<4;x++) {
                cardsMap[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();//执行两次添加随机数的方法,添加两个随机数的值
    }

    //生成随机数的方法
    private void addRandomNum() {

        emptyPoints.clear();//清空集合

        for(int y=0;y<4;y++) {
            for(int x=0;x<4;x++) {
                if (cardsMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));//记录所有的卡片数字<=0的坐标点,初始List的size为16
                }
            }
        }

        //用随机数方法获取被移除的那个点,remove方法返回的是被移除的元素,Math.random()的值为0-1
        Point p = emptyPoints.remove((int)(Math.random() * emptyPoints.size()));
        //在移除的这张卡片上生成随机数,并且设置生成2和4的概率为9:1
        cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
    }

    //实现游戏逻辑
    private void swipeLeft() {
        boolean merge = false;//用于判断是否添加新卡片
        //左滑,数字能够往左移动,如果是相同数字,能够合并,而且这两个数字可以是不相邻的
        for(int y=0;y<4;y++) {
            for(int x=0;x<4;x++) {
                for(int x1=x+1;x1<4;x1++) {
                    //从当前位置往右遍历,看当前卡片右方的卡片
                    if (cardsMap[x1][y].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            //当前位置上是空的,把右方数字设置到原位置
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);//清空右方卡片
                            //还有一种情况,在上述状况下,继续往后遍历,后面是空的,右面又有一个数字,如果这两个数字相同,但并不会合并,所以需要再遍历一次
                            x--;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);
                            //有合并就添加分数
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }
                        break;//只要查找到一个数字有值,最里层的循环就可以直接break掉
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();//如果发生合并,添加新的随机数
            //只要添加新的随机数,就要判断是否结束游戏
            checkComplete();
        }
    }
    private void swipeRight(){
        boolean merge = false;
        //右滑,数字能够往右移动,如果是相同数字,能够合并,而且这两个数字可以是不相邻的
        for(int y=0;y<4;y++) {
            for(int x=3;x>=0;x--) {
                for(int x1=x-1;x1>=0;x1--) {
                    //从当前位置往左遍历,看当前卡片左方的卡片
                    if (cardsMap[x1][y].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            //当前位置上是空的,把右方数字设置到原位置
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);//清空右方卡片
                            //还有一种情况,在上述状况下,继续往后遍历,后面是空的,右面又有一个数字,如果这两个数字相同,但并不会合并,所以需要再遍历一次
                            x++;
                            merge=true;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge=true;
                        }
                        break;
                    }

                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }

    }
    private void swipeUp() {
        boolean merge=false;
        //上滑,数字能够往上移动,如果是相同数字,能够合并,而且这两个数字可以是不相邻的
        for(int x=0;x<4;x++) {
            for(int y=0;y<4;y++) {
                for(int y1=y+1;y1<4;y1++) {
                    //从当前位置往右遍历,看当前卡片右方的卡片
                    if (cardsMap[x][y1].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            //当前位置上是空的,把右方数字设置到原位置
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);//清空右方卡片
                            //还有一种情况,在上述状况下,继续往后遍历,后面是空的,右面又有一个数字,如果这两个数字相同,但并不会合并,所以需要再遍历一次
                            y--;
                            merge=true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge=true;
                        }
                        break;
                    }

                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }
    private void swipeDown() {
        boolean merge=false;
        //下滑,数字能够往下移动,如果是相同数字,能够合并,而且这两个数字可以是不相邻的
        for(int x=0;x<4;x++) {
            for(int y=3;y>=0;y--) {
                for(int y1=y-1;y1>=0;y1--) {
                    //从当前位置往右遍历,看当前卡片右方的卡片
                    if (cardsMap[x][y1].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            //当前位置上是空的,把右方数字设置到原位置
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);//清空右方卡片
                            //还有一种情况,在上述状况下,继续往后遍历,后面是空的,右面又有一个数字,如果这两个数字相同,但并不会合并,所以需要再遍历一次
                            y++;
                            merge=true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge=true;
                        }
                        break;
                    }

                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    //判断游戏结束的方法,条件:1,没有空的卡片;2,没有相邻的卡片数字相同
    private void checkComplete() {

        boolean complete =true;

        //利用标签跳出循环
        ALL:
        for(int y=0;y<4;y++) {
            for(int x=0;x<4;x++) {
                if (cardsMap[x][y].getNum() == 0||
                        (x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
                        (x<3&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
                        (y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<3&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {
                    //卡片为空或者相邻4个卡片有相同数字,都不结束游戏
                    complete=false;
                    break ALL;//此时跳出所有循环
                }
            }
        }

        //如果结束,弹出对话框,结束游戏,按钮是重新开始游戏.
        if (complete) {
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束")
                    .setNegativeButton("结束游戏", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.getMainActivity().finish();
                }
            }).setPositiveButton("重来", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGame();
                }
            }).show();
        }
    }

}
