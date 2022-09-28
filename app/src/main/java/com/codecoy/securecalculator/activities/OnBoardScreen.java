package com.codecoy.securecalculator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.codecoy.securecalculator.R;

public class OnBoardScreen extends AppCompatActivity {


    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board_screen);

        next=findViewById(R.id.next);

        next.setOnClickListener(v->{
            Intent mainIntent = new Intent(OnBoardScreen.this, CalcActivity.class);
            OnBoardScreen.this.startActivity(mainIntent);
            OnBoardScreen.this.finish();
        });
    }
}