package com.csclab.hc.androidpratice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /** Init Variable for Page 1 **/
    EditText inputNumTxt1;
    EditText inputNumTxt2;

    Button btnAdd;
    Button btnSub;
    Button btnMult;
    Button btnDiv;

    /** Init Variable for Page 2 **/
    TextView textResult;

    Button return_button;

    /** Init Variable **/
    String oper = "";

    //Variables connection
    EditText inputIP;
    Button ipSend;
    String ipAdd = "";
    String resultToDisplay;
    String stringToSendToServer="";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Func() for setup page 1 **/
        //jumpToMainLayout();

        setContentView(R.layout.ip_page);
        inputIP = (EditText)findViewById(R.id.edIP);
        ipSend = (Button)findViewById(R.id.ipButton);

        ipSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Func() for setup page 1 **/
                ipAdd = inputIP.getText().toString();
                jumpToMainLayout();
            }
        });
    }

    /** Function for page 1 setup */
    public void jumpToMainLayout() {
        //TODO: Change layout to activity_main
        // HINT: setContentView()
        setContentView(R.layout.activity_main);

        //TODO: Find and bind all elements(4 buttons 2 EditTexts)
        // inputNumTxt1, inputNumTxt2
        // btnAdd, btnSub, btnMult, btnDiv
        inputNumTxt1 = (EditText) findViewById(R.id.etNum1);
        inputNumTxt2 = (EditText) findViewById(R.id.etNum2);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnMult = (Button) findViewById(R.id.btnMult);
        btnDiv = (Button) findViewById(R.id.btnDiv);

        //TODO: Set 4 buttons' listener
        // HINT: myButton.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMult.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
    }

    /** Function for onclick() implement */
    @Override
    public void onClick(View v) {
        float num1 = 0; // Store input num 1
        float num2 = 0; // Store input num 2

        // check if the fields are empty
        if (TextUtils.isEmpty(inputNumTxt1.getText().toString())
                || TextUtils.isEmpty(inputNumTxt2.getText().toString())) {
            return;
        }

        // read EditText and fill variables with numbers
        num1 = Float.parseFloat(inputNumTxt1.getText().toString());
        num2 = Float.parseFloat(inputNumTxt2.getText().toString());

        // defines the button that has been clicked and performs the corresponding operation
        // write operation into oper, we will use it later for output
        //TODO: caculate result
        switch (v.getId()) {
            case R.id.btnAdd:
                oper = "+";
                break;
            case R.id.btnSub:
                oper = "-";
                break;
            case R.id.btnMult:
                oper = "*";
                break;
            case R.id.btnDiv:
                oper = "/";
                break;
            default:
                break;
        }
        // HINT:Using log.d to check your answer is correct before implement page turning
        //Log.d("debug","ANS "+result);

        resultToDisplay =new String(num1 + " " + oper + " " + num2 + " = ");
        stringToSendToServer="["+num1+"]["+oper+"]["+num2+"]";
        Thread t = new thread();
        t.start();

    }

    public void jumpToResultLayout(String resultStr){
        //split resultStr
        resultToDisplay+=resultStr.substring(1,resultStr.indexOf("]"));
        System.out.println("IN RESULT PAGE");
        System.out.println(resultToDisplay);

        setContentView(R.layout.result_page);



        //TODO: Bind return_button and textResult form result view
        // HINT: findViewById()
        // HINT: Remember to give type
        return_button = (Button) findViewById(R.id.return_button);
        textResult = (TextView) findViewById(R.id.textResult);

        if (textResult != null) {
            //TODO: Set the result text
            textResult.setText(resultToDisplay);
        }

        if (return_button != null) {
            //TODO: prepare button listener for return button
            // HINT:
            // mybutton.setOnClickListener(new View.OnClickListener(){
            //      public void onClick(View v) {
            //          // Something to do..
            //      }
            // }
            return_button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // TODO
                    jumpToMainLayout();
                }

            });
        }
    }

    class thread extends Thread{
        public void run(){
            try{
                System.out.println("Client: Waiting to connect...");
                int serverPort = 2000;

                // Create socket connect server
                Socket socket = new Socket(ipAdd, serverPort);
                System.out.println("Connected!");

                // Create stream communicate with server
                OutputStream out = socket.getOutputStream();
                //String strToSend = "Hi I'm client";

                byte[] sendStrByte = new byte[1024];
                System.arraycopy(stringToSendToServer.getBytes(), 0, sendStrByte, 0, stringToSendToServer.length());
                out.write(sendStrByte);



                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = null;
                String resultFromServer="";
                while ((line = in.readLine()) != null) {
                    System.out.println("Server says: " + line); //here you process you line result
                    resultFromServer=line;

                }

                final String str=resultFromServer;

                //TODO: Pass the result String to jumpToResultLayout() and show the result at Result view
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        jumpToResultLayout(str);
                    }
                });



            }catch (Exception e){
                System.out.println("Error" + e.getMessage());
            }
        }
    }

}
