package com.example.musiqueue.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.musiqueue.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Brian on 6/24/2017.
 */

public class ClientSideCom extends AppCompatActivity {
    Socket socket;
    int port = 3636;
    // TODO: set ip to our server
    String ip = "";
    EditText sendEdit;
    Button sendButton;
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_demo);
        sendEdit = (EditText) findViewById(R.id.send_edit);
        sendButton = (Button) findViewById(R.id.send_button);
        responseText = (TextView) findViewById(R.id.response_text);

        new Thread(new ClientThread()).start();
    }

    public void sendMessage(View view) {
        try {
            PrintWriter serverWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            serverWriter.println(sendEdit.getText());
        } catch (Exception e) {
            System.out.println("sending exception: " + e);
        }
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("im running");
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(ip, port);
                System.out.println("socket inet address is " + socket.getInetAddress());
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = serverReader.readLine();
                if (response != null && !response.isEmpty())
                    responseText.append(response);
            } catch (Exception e) {
                System.out.println(" running exception " + e);
            }
            System.out.println("Im exiting");
        }
    }
}
