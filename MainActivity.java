package com.example.batikjavalow;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button btnPen, btnLine, btnRect, btnCircle, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);
        btnPen = findViewById(R.id.btnPen);
        btnLine = findViewById(R.id.btnLine);
        btnRect = findViewById(R.id.btnRect);
        btnCircle = findViewById(R.id.btnCircle);
        btnClear = findViewById(R.id.btnClear);

        btnPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setMode(DrawingView.PEN_MODE);
            }
        });

        btnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setMode(DrawingView.LINE_MODE);
            }
        });

        btnRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setMode(DrawingView.RECT_MODE);
            }
        });

        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setMode(DrawingView.CIRCLE_MODE);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearCanvas();
            }
        });
    }
}