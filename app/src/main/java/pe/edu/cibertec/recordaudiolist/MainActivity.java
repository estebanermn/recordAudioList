package pe.edu.cibertec.recordaudiolist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_RECORD_AUDIO = 1;
    boolean permissionGranted = false;
    private String fileName = null;

    MediaRecorder recorder = null;
    MediaPlayer player = null;

    Toolbar toolbar;
    Chronometer chronometer;
    ImageView ivPlay, ivRecorder, ivStop;
    SeekBar seekBar;
    LinearLayout linearLayoutRecorder, linearLayoutPlay;

    private Handler mHandler = new Handler();
    private int lastProgress = 0;
    private boolean isPlaying = false;


    static final String LOG_TAG = "AudioRecorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Voice Recorder");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
        setSupportActionBar(toolbar);

        linearLayoutRecorder = findViewById(R.id.linearLayoutRecorder);
        chronometer = findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        ivPlay = findViewById(R.id.ivPlay);
        ivRecorder = findViewById(R.id.ivRecorder);
        ivStop = findViewById(R.id.ivStop);

        linearLayoutPlay = findViewById(R.id.linearLayoutPlay);
        seekBar = findViewById(R.id.seekBar);

        ivPlay.setOnClickListener(this);
        ivRecorder.setOnClickListener(this);
        ivStop.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkPermissionRecord();
        }

        initViews();

    }

    private void checkPermissionRecord() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO) {
            permissionGranted = grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED;


        }
        if (!permissionGranted) {
//            Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == ivRecorder) {
            prepareforRecording();
            startRecording();
        } else if (v == ivStop) {
            prepareforStop();
            stopRecording();
        } else if (v == ivPlay) {
            if (!isPlaying && fileName != null) {
                isPlaying = true;
                startPlaying();
            }else{
                isPlaying = false;
                stopPlaying();
            }
        }
    }

    private void initViews() {
    }

    private void prepareforRecording() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        ivRecorder.setVisibility(View.GONE);
        ivStop.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.GONE);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioEncoder(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios");
        if (!file.exists()) {
            file.mkdirs();
        }


//        fileName = getExternalCacheDir().getAbsolutePath();
//        fileName = fileName + "/audiorecorder.3gp";

        fileName = root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" +
                String.valueOf(System.currentTimeMillis() + ".mp3");

        Log.d("filename", fileName);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

        lastProgress = 0;
        seekBar.setProgress(0);
        stopPlaying();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void stopPlaying() {

        try {
            player.release();

        } catch (Exception e) {
            e.printStackTrace();
        }

        player = null;

        ivPlay.setImageResource(R.drawable.ic_play);
        chronometer.stop();
    }

    private void prepareforStop() {
        ivRecorder.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.GONE);
        linearLayoutPlay.setVisibility(View.VISIBLE);
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        recorder = null;

        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());

        Toast.makeText(this, "La grabación fue grabada exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        ivPlay.setImageResource(R.drawable.ic_pause);
        seekBar.setProgress(lastProgress);
        player.seekTo(lastProgress);
        seekUpdation();
        chronometer.start();


        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivPlay.setImageResource(R.drawable.ic_play);
                isPlaying = false;
                chronometer.stop();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null && fromUser) {
                    player.seekTo(progress);
                    chronometer.setBase(SystemClock.elapsedRealtime() - player.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if (player != null) {
            int mCurrentPosition = player.getCurrentPosition();
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }
}

