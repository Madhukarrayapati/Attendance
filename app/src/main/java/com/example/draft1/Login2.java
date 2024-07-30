package com.example.draft1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Login2 extends AppCompatActivity {
    private static final int pic_id = 123;
    private ImageView click_image_id;
    private Button b;
    private Bitmap receivedBitmap;
    private byte[] byteArray;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        b = findViewById(R.id.submit);
        click_image_id = findViewById(R.id.image);
        byteArray = getIntent().getByteArrayExtra("image");
        if (byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            click_image_id.setImageBitmap(bitmap);
        }
        b.setEnabled(true);
        b.setOnClickListener(new View.OnClickListener() {
            private static final String SERVER_IP = "192.168.57.209";
            private static final int SERVER_PORT = 12345;
            private boolean check=false;

            @Override
            public void onClick(View v) {
            b.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String id=getIntent().getStringExtra("user_id");
                            socket = new Socket(SERVER_IP, SERVER_PORT);
                            OutputStream outputStream = socket.getOutputStream();
                            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                            out.println("image/" + id );
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String[] ackk = in.readLine().split("/");
                            String user_id=ackk[0];
                            String status=ackk[1];

                            if (user_id.equalsIgnoreCase(id) && status.equalsIgnoreCase("image_waiting")) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                outputStream.write(intToByteArray(byteArray.length));
                                outputStream.write(byteArray);
                                outputStream.flush();

                                String[] ackk2 = in.readLine().split("/");
                                String title, message;
                                if ( ackk2[0].equalsIgnoreCase(id) && ackk2[1].equalsIgnoreCase("image_success")) {
                                    title = "Image matched successfully";
                                    message = "Click OK";
                                    check=true;
                                } else {
                                    title = "Image not matched";
                                    message = "Try Again!!";
                                    b.setEnabled(true);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(Login2.this)
                                                .setTitle(title)
                                                .setMessage(message)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        if(check) {
                                                            Intent intent = new Intent(Login2.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish(); // Finish Login2 activity
                                                        }
                                                        else {
                                                            Intent intent = new Intent(Login2.this, login1.class);
                                                            startActivity(intent);
                                                            finish();

                                                        }

                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(Login2.this)
                                                .setTitle("Failed To Send Image")
                                                .setMessage("Click OK")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Do nothing, remain in the current activity
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                });
                            }
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Login2", "Error sending image: " + e.getMessage());
                        }
                    }
                }).start();
            }

            private byte[] intToByteArray(int value) {
                return new byte[]{
                        (byte) (value >>> 24),
                        (byte) (value >>> 16),
                        (byte) (value >>> 8),
                        (byte) value
                };
            }
        });
    }
}
