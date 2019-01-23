package com.forasoft.androidopus;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

    public final static int AUDIO_REQUEST_CODE = 101;
    public static boolean isCalling = false;

    public int sampleRate = 24000;
    public int frameDuration = 20;
    public int numberOfChannels = 1;
    public int frameSize = sampleRate * frameDuration / 1000;
    public int maxFrameSize = 1276 * 3;

    private AudioRecord audioRecorder;
    private AudioTrack audioPlayer;
    private Opus opus;
    private WorkerThread audioRecordingThread = new WorkerThread();
    private WorkerThread playingThread = new WorkerThread();
    private short[] playBuffer;

    private Button startButton;
    private Button stopButton;
    private TextView callStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        callStatusTextView = findViewById(R.id.tv_call_status);

        initOpus();
        initializeAudioPlayer();

        audioRecordingThread.start();
        audioRecordingThread.prepareHandler();
        playingThread.start();
        playingThread.prepareHandler();

        playBuffer = new short[frameSize];

        if (checkPermission()) {
            initAudioRecorder();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermission()) {
                    requestPermission();
                    return;
                }

                startCall();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OPUSANDROID", "Button click");
                isCalling = false;
                callStatusTextView.setText("Call ended");

                audioPlayer.stop();
                audioRecorder.stop();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        isCalling = false;

        if (opus == null) {
            initOpus();
        }

        if (audioPlayer != null && audioPlayer.getState() == AudioTrack.PLAYSTATE_STOPPED) {
            audioPlayer.play();
        }

        if (audioRecorder != null && audioRecorder.getState() == AudioRecord.RECORDSTATE_STOPPED) {
            audioRecorder.startRecording();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isCalling = false;
        callStatusTextView.setText("Call ended.");

        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        if (audioRecorder != null) {
            audioRecorder.stop();
        }

        opus.releaseEncoder();
        opus.releaseDecoder();
        opus = null;
    }

    private void initOpus() {
        opus = new Opus();
        opus.initEncoder(sampleRate, numberOfChannels, frameSize, maxFrameSize);
        opus.initDecoder(sampleRate, numberOfChannels, frameSize);
        Log.d("OPUSINIT", "Opus initialized");
    }

    void initializeAudioPlayer() {
        int bufsize = AudioTrack.getMinBufferSize(24000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, 24000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufsize, AudioTrack.MODE_STREAM);
    }

    private void initAudioRecorder() {
        audioRecorder = findAudioRecord();

        if (audioRecorder == null) {
            Log.d("OPUSANDROID", "Can not initialize audio audioRecorder");
            Toast.makeText(getApplicationContext(), "Can not initialize audioRecorder", Toast.LENGTH_SHORT).show();
        }
    }

    public AudioRecord findAudioRecord() {
        short channelConfig = AudioFormat.CHANNEL_IN_MONO;
        short audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        try {
            Log.d("OPUSANDROID", "Attempting rate " + sampleRate + "Hz, bits: " + audioFormat);

            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

            int bufferSize = minBufferSize * 4;

            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);

            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {


                Log.d("OPUSANDROID", "Create audioRecorder with rate " + sampleRate + "Hz, audioFormat: " + audioFormat + ", channelConfig: "
                        + channelConfig + ", bufferSize: " + bufferSize);
                return recorder;
            }

        } catch (Exception e) {
            Log.e("OPUSANDROID", sampleRate + "Exception, keep trying.", e);
        }

        return null;
    }

    private void startCall() {

        isCalling = true;
        audioPlayer.play();
        callStatusTextView.setText("In call...");

        audioRecordingThread.postTask(new Runnable() {
            @Override
            public void run() {
                startAudioStream();
            }
        });

    }

    private void startAudioStream() {

        audioRecorder.startRecording();

        int encoded;
        short[] inBuf = new short[frameSize];
        byte[] outBuf = new byte[maxFrameSize];
        Log.d("OPUSANDROID", "Create inBuf size:" + inBuf.length + ", outBuf size:" + outBuf.length);

        while (isCalling) {

            audioRecorder.read(inBuf, 0, inBuf.length);
            Log.d("SOCKET", "inBuf size :" + inBuf.length);

            encoded = opus.encode(inBuf, outBuf);

            byte[] sendBuf = new byte[encoded];
            System.arraycopy(outBuf, 0, sendBuf, 0, encoded);
            Log.d("OPUSANDROID", "Encoded size:" + encoded + ", Encoded sendBuf:" + getOutBufferData(sendBuf));

            playWithDelay(sendBuf, 0);
        }
    }

    private void playWithDelay(final byte[] outBuf, long delayInMillis) {
        playingThread.postDelayTask(delayInMillis, new Runnable() {
            @Override
            public void run() {

                int numOfDecodedSamples = opus.decode(outBuf, playBuffer);
                Log.d("OPUSANDROID", "numOfDecodedSamples " + numOfDecodedSamples);

                audioPlayer.write(playBuffer, 0, playBuffer.length);
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, AUDIO_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AUDIO_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean audioAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (audioAccepted) {
                        initAudioRecorder();

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startCall();
                            }
                        }, 3000);

                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, can't start call", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private List<Byte> getOutBufferData(byte[] outBuf) {
        List<Byte> result = new ArrayList<>();
        for (int i = 0; i < outBuf.length; i++) {
            result.add(outBuf[i]);
        }
        return result;
    }
}
