package com.eksler.vadim.pulsinggame;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements GameView.GameViewListener, View.OnClickListener {

    private Button startButton;
    private TextView countTxt;
    private int currentCount = 0;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.gameView);
        countTxt = findViewById(R.id.countTxt);
        startButton = findViewById(R.id.startButton);
        gameView.setListener(this);
        startButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startButton:
                countTxt.setText("0");
                currentCount = 0;
                gameView.start();
                startButton.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onUpdateCount() {
        currentCount++;
        countTxt.setText(String.valueOf(currentCount));
    }

    @Override
    public void onFinish() {
        Log.d("TAG", "onFinish: ");
        gameView.stop();
        new AlertDialog.Builder(this)
                .setMessage(R.string.game_over)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startButton.setVisibility(View.VISIBLE);
                    }
                })
                .create()
                .show();
    }
}
