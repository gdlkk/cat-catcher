package com.example.catcatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {

    public static final int BG = Color.parseColor("#397cdb");
    public static final long MAX_DELAY = 3000;
    public static final long MIN_DELAY = 1000;
    public static final int TRIES = 3;
    public int currTry = 1;
    private boolean gameStarted =false;
    private boolean disp;
    private long sumTime, startTime, avgTime, bestTime;
    @BindView(R.id.constrLayout)
    View constrLayout;
    @BindView(R.id.touchArea)
    View touchArea;
    @BindView(R.id.catToTap)
    View catToTap;
    @BindView(R.id.msgText)
    TextView msgText;
    private static Handler HANDLER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        HANDLER = new Handler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(HANDLER != null) {
            HANDLER.removeCallbacksAndMessages(null);
        }
    }

    private Runnable bgChange = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgText.setText("");
                    constrLayout.setBackgroundColor(getResources().getColor(R.color.redd));
                    touchArea.setVisibility(View.VISIBLE);
                    catToTap.setVisibility(View.VISIBLE);
                    disp = true;
                    startTime = System.currentTimeMillis();
                }
            });
        }
    };

    public static long randomDelay(long min, long max) {
        return min + (long) (Math.random() * (max - min));
    }

    @OnTouch(R.id.constrLayout)
    public boolean manageGame() {
        if (gameStarted) {
            if (disp) {
                long delay = System.currentTimeMillis() - startTime;
                if (delay < bestTime) {
                    bestTime = delay;
                }
                sumTime += delay;
                msgText.setText(getString(R.string.nextMsg));
                constrLayout.setBackgroundColor(BG);
                touchArea.setVisibility(View.GONE);
                catToTap.setVisibility(View.GONE);
                currTry++;
                if (currTry > TRIES) {
                    avgTime = sumTime/TRIES;
                    gameStarted = false;
                    msgText.setText(getString(R.string.endMsg).replace("#averageTime#", avgTime + "")
                                                                .replace("#bestTime#", bestTime + ""));
                } else {
                    disp = false;
                    HANDLER.postDelayed(bgChange, randomDelay(MIN_DELAY, MAX_DELAY));
                }
            } else {
                HANDLER.removeCallbacksAndMessages(bgChange);
                gameStarted = false;
                msgText.setText(R.string.tooFastMsg);
            }
        } else {
            gameStarted = true;
            disp = false;
            bestTime = Long.MAX_VALUE;
            sumTime = 0;
            currTry = 1;
            msgText.setText(R.string.readyMsg);
            HANDLER.postDelayed(bgChange, randomDelay(MIN_DELAY, MAX_DELAY));
        }
        return false;
    }
}