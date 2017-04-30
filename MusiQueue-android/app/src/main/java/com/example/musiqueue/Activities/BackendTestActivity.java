package com.example.musiqueue.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.musiqueue.HelperClasses.BackgroundWorker;
import com.example.musiqueue.R;

public class BackendTestActivity extends AppCompatActivity {
    EditText nameET;
    EditText addrET;
    TextView result;
    Button submit;
    String data;
    static boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backend_test);

        nameET = (EditText) findViewById(R.id.name_edit);
        addrET = (EditText) findViewById(R.id.addr_edit);
        result = (TextView) findViewById(R.id.result_text);
        submit = (Button) findViewById(R.id.submit_button);
        if (!first) {
            data = getIntent().getStringExtra("result");
            result.setText(data);
        }
        first = false;
    }

    public void update(View view) {
        String name = nameET.getText().toString();
        String addr = addrET.getText().toString();
        String type = "test";

        // CREATE A BACKGROUND WORKER TO TELL THE SERVER TO UPDATE THE DATABASE
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, name, addr);

    }
}
