package com.example.draft1;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1 = findViewById(R.id.b1);
        Button b2 = findViewById(R.id.b2);
        EditText t1 = findViewById(R.id.e1);
        EditText t2=findViewById(R.id.e2);

        //TextView tt=findViewById(R.id.t1);

        if (!hasPermissions())
        {
            showPermissionsRequiredDialog();
        }



        b1.setOnClickListener(new View.OnClickListener()
        {
            private static final String SERVER_IP = "192.168.57.209";
            private static final int SERVER_PORT = 12345;
            public void onClick(View v)
            {
                new Thread(new Runnable() {
                    String ackk=null;
                    @Override
                    public void run() {
                        try {
                            Socket socket =null;
                            //System.out.println("established");
                            // Send data to server
                            PrintWriter out =null;
                            BufferedReader in=null;

                                OneWayHashing owh=new OneWayHashing();
                                String user=t1.getText().toString();
                            if(user.length()<=4 || user.length()>=32 || !user.endsWith("@sastra.ac.in"))
                            {
                                throw new NoSuchAlgorithmException();
                            }
                                    socket=new Socket(SERVER_IP, SERVER_PORT);
                                    out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                                    out.println("login/" + user.substring(0, user.length() - 13) + "/" + owh.hash(t2.getText().toString()));
                                    in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    ackk = in.readLine();
                                    //tt.setText("Server response: " + response);
                                    // Close the socket
                                    socket.close();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String[] ack=ackk.split("/");
                                            //System.out.println("established");
                                            handleServerResponse(ack[0],ack[1]);
                                        }
                                    });


                            //System.out.println("text sent");
                            // Receive data from server


                            //System.out.println("got response");
                            //final String finalAckk = ackk.split(",")[1];

                        }
                        catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Not a valid user")
                                            .setMessage("Click OK")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing, just close the dialog
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                        }catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Not Connected To Server")
                                            .setMessage("Click OK")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing, just close the dialog
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                        }
                    }
                    }).start();




               // Intent inten = new Intent(MainActivity.this, login1.class);
               // startActivity(inten);
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent= new Intent(MainActivity.this, SignUp.class);

                startActivity(intent);
            }
        });
    }
    private void handleServerResponse(String response1,String response2) {
        if (response1 != null && response2.equalsIgnoreCase("success")) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Login Successful")
                    .setMessage("Click OK")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, login1.class);
                                intent.putExtra("user_id",response1);
                                startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        else
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Login Failed")
                    .setMessage("Try Again!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }



    private void showPermissionsRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("Permissions are must and should for the app to function properly. Please grant the required permissions.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions();
                    }
                })
                .setCancelable(false)
                .show();
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                }
            }
            if (!allPermissionsGranted) {
                showPermissionsRequiredDialog();
            }
        }
    }


}