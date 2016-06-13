package com.example;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Created by Gary on 16/5/28.
 */
public class Server implements Runnable{
    private Thread thread;
    private ServerSocket servSock;

    JFrame notification;
    JPanel main;
    JLabel notifyline1;

    String operation = "";
    public Server(){

        notification = new JFrame();
        main = new JPanel();
        notifyline1 = new JLabel();

        notifyline1.setText("Waiting");
        main.add(notifyline1);
        notification.add(main);
        notification.setSize(new Dimension(320, 64));
        notification.setLocationRelativeTo(null);
        //notification.setUndecorated(true);
        notification.setVisible(true);


        try {
            // Detect server ip
            InetAddress IP = InetAddress.getLocalHost();
            System.out.println("IP of my system is := "+IP.getHostAddress());
            System.out.println("Waitting to connect......");

            // Create server socket
            servSock = new ServerSocket(2000);

            // Create socket thread
            thread = new Thread(this);
            thread.start();
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        } finally{

        }
    }

    @Override
    public void run(){
        // Running for waitting multiple client
        while(true){
            try{
                // After client connected, create client socket connect with client
                Socket clntSock = servSock.accept();
                InputStream in = clntSock.getInputStream();

                System.out.println("Connected in server!!");

                // Transfer data
                byte[] b = new byte[1024];
                int length;

                length = in.read(b);
                String s = new String(b);
                //String s is the obtained s with format [num1][oper][num2]. We have to obtain the numbers and operation
                float num1 = 0; // Store input num 1
                float num2 = 0; // Store input num 2
                float result = 0;

                System.out.println(s);

                try{
                    System.out.println("num1:"+s.substring(1, s.indexOf("]")));
                    num1=Float.valueOf(s.substring(1, s.indexOf("]")));
                }catch(Exception e){
                    e.printStackTrace();
                }

                int index1=s.indexOf("]")+2;
                s=s.substring(index1);

                try{
                    System.out.println("operation:"+s.substring(0, s.indexOf("]")));
                    operation=s.substring(0, s.indexOf("]"));
                }catch(Exception e){
                    e.printStackTrace();
                }

                int index2=s.indexOf("]")+2;
                s=s.substring(index2);

                try{
                    System.out.println("num2:"+s.substring(0,s.indexOf("]")));
                    num2=Float.valueOf(s.substring(0,s.indexOf("]")));
                }catch(Exception e){
                    e.printStackTrace();
                }


                System.out.println("[Server Said]" + s);
                System.out.println(num1+operation+num2);

                switch (operation) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "*":
                        result = num1 * num2;
                        break;
                    case "/":
                        result = num1 / num2;
                        break;
                    default:
                        break;
                }

                notifyline1.setText("The result from App is "+num1+" "+operation+" "+num2 + "= "+result);

                OutputStream out = clntSock.getOutputStream();
                String strToSend = String.valueOf("["+result+"]");

                byte[] sendStrByte = new byte[1024];
                System.arraycopy(strToSend.getBytes(), 0, sendStrByte, 0, strToSend.length());
                out.write(sendStrByte);
                out.close();
                /*OutputStream os = clntSock.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                pw.println("Hello");*/

                /*ObjectOutputStream sOutput = new ObjectOutputStream(clntSock.getOutputStream());
                sOutput.writeObject("TEST123");
                sOutput.close();*/



            }
            catch(Exception e){
                System.out.println("Error: "+e.getMessage());
            }
        }
    }
}
