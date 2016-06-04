package com.ljd.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ljd.annotation.inject.ContentView;
import com.ljd.annotation.inject.Inject;
import com.ljd.annotation.inject.OnClick;
import com.ljd.annotation.inject.ViewInject;


@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Inject(R.id.test_text)
    TextView textView;

    @Inject(R.id.test_btn)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInject.inject(this);
        textView.setText("hello word");
        button.setText("test");
    }

    @OnClick({R.id.test_btn,R.id.test_text})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.test_btn:
                Toast.makeText(this,"test onClick",Toast.LENGTH_SHORT).show();
                break;
            case R.id.test_text:
                Toast.makeText(this,"hello word",Toast.LENGTH_SHORT).show();
                break;
            
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewInject.unInject();
    }
}
