package com.example.draft1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class SignUp extends AppCompatActivity {

    private static final String SERVER_IP = "192.168.57.209";
    private static final int SERVER_PORT = 12345;
    private static final int pic_id = 123;
    private static ImageButton ib;
    private static Button b3;
    private static byte[] byteArray;
    private static ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE); // or View.INVISIBLE
        b3 = findViewById(R.id.b3);
        EditText t1 = findViewById(R.id.e3);
        EditText t2 = findViewById(R.id.e4);
        ib=findViewById(R.id.ib);
        b3.setEnabled(false);


        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                String username=t1.getText().toString();
                String pass=t2.getText().toString();
                String title="";
                String response="";
                boolean check=true;
                if(username.length()>=4 && username.length()<=32 && username.endsWith("@sastra.ac.in") && pass.length()>=4 && pass.length()<=8) {

                    check=false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String ackk = null;
                            try {
                                Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                                // Send data to server
                                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                                try {
                                    OneWayHashing owh = new OneWayHashing();
                                    out.println("signup/" + username.substring(0,username.length()-13) + "/" + owh.hash(pass));
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                                // Receive data from server
                                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                ackk = in.readLine();


                                OutputStream outputStream = socket.getOutputStream();
                                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                                dataOutputStream.writeInt(byteArray.length); // Send the length of the byte array
                                dataOutputStream.write(byteArray); // Send the byte array

                                dataOutputStream.close();


                                // Close the socket
                                socket.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final String finalAckk = ackk.split("/")[2];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleServerResponse(finalAckk);
                                }
                            });
                        }
                    }).start();
                }
                else if(username.length() >= 32 || username.length() <= 13 )
                {
                    title="username";
                    response="username should between 13-52 characters and endswith @sastra.ac.in";
                }
                else if(pass.length()<=4 || pass.length() >=8)
                {
                    title="password";
                    response="password should be between 4-12 characters";
                }
                else {
                    title="username/password";
                    response="username/password is very short/very big";
                }
                if(check) {
                    new AlertDialog.Builder(SignUp.this)
                            .setTitle(title)
                            .setMessage(response)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if (response != null && response.equalsIgnoreCase("")) {
                                    //Intent intent = new Intent(SignUp.this, MainActivity.class);
                                    //intent.putExtra("message_key", "Registered Successfully!!");
                                    //startActivity(intent);
                                    //finish();
                                    //}
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id' with requestCode
        if (requestCode == pic_id && resultCode == RESULT_OK) {
            // BitMap is data structure of image file which store the image in memory

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ib.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            imageView.setVisibility(View.VISIBLE);
            b3.setEnabled(true);


        }
    }


    private void handleServerResponse(String response) {
        String title = "";
        String message = "";

        if (response != null && response.equalsIgnoreCase("success")) {
            title = "Registration Successful";
            message = "Login to continue";
        } else if(response != null && response.equalsIgnoreCase("username"))
        {
            title="Registration failed";
            message="username:exists";
        }
        else
        {
            title="Registration Failed";
            message="Check Again";
        }

        new AlertDialog.Builder(SignUp.this)
                .setTitle(title)
                .setMessage(response)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (response != null && response.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            intent.putExtra("message_key", "Registered Successfully!!");
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }
}
