package com.example.fastrecord;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
    public String file_name = "FastRecords";
    public static final int RESULT_ENABLE = 11;
    MediaRecorder Callrecorder = new MediaRecorder();
    File audiofile;
    File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/FastRecord");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (arePermissionsEnabled()) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(i);

                Toast.makeText(getApplicationContext(), "Recorder Started", Toast.LENGTH_SHORT).show();
                if (!sampleDir.exists()) {
                    sampleDir.mkdirs();
                }
                try {
                    audiofile = File.createTempFile(file_name, ".amr", sampleDir);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Problem In Creating Directory", Toast.LENGTH_SHORT).show();
                }
                Callrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                Callrecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
                Callrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                Callrecorder.setOutputFile(audiofile.getAbsolutePath());
                try {
                    Callrecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Problem In Recording", Toast.LENGTH_SHORT).show();
                }
                Callrecorder.start();
                //notify
                //final String someLongText = "Recording Will Be Stored In 'FastRecord' Folder In Default Storage";
                final String someLongText = "";

                final Notification.Builder builder = new Notification.Builder(this);
                builder.setStyle(new Notification.BigTextStyle(builder)
                        .bigText(someLongText)
                        .setBigContentTitle("Recording Started"))
                        .setSmallIcon(R.drawable.mic2);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
                final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(1, builder.build());
            }
            else {
                requestMultiplePermissions();
                finish();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();

        final String someLongText = "Recording Stored In 'FastRecord' Folder";

        final Notification.Builder builder = new Notification.Builder(this);
        builder.setStyle(new Notification.BigTextStyle(builder)
                .bigText(someLongText)
                .setBigContentTitle("Recording Stopped"))
                .setSmallIcon(R.drawable.mic2);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);

    }
}
