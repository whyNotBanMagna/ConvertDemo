package com.we.convertdemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //采样率
    private static final int SAMPLE_RATE = 44100;
    //声道数
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    //返回音频数据的格式
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private Button record, stop, convert;
    private AudioRecord audioRecord = null; //声明AudioRecord对象
    private int recordBufSize = 0; //申明recordBuffer的大小
    private FileOutputStream os = null;
    private boolean isRecording = false;
    private File mp3File;
    private Convert convertUtil;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record = findViewById(R.id.record);
        stop = findViewById(R.id.stop);
        convert = findViewById(R.id.convert);
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
        convert.setOnClickListener(this);

        file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        mp3File = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.mp3");

        convertUtil = new Convert();
        String name = convertUtil.stringFromJNI();
        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                startRecord();
                break;
            case R.id.stop:
                stopRecord();
                break;
            case R.id.convert:
                if(isRecording==true) {
                    stopRecord();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        convertFile();
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private void convertFile() {
        if(!file.exists()) {
            Toast.makeText(MainActivity.this, "pcm文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mp3File.exists()) {
            mp3File.delete();
        }
        String pcmPath = file.toString();
        String mp3Path = mp3File.toString();
        convertUtil.init(pcmPath, CHANNEL, 128, SAMPLE_RATE, mp3Path);
        convertUtil.encode();
        convertUtil.destroy();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "转码成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void startRecord() {
        recordBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_FORMAT, recordBufSize);
        //初始化数据流
        final byte data[] = new byte[recordBufSize];

        if (!file.mkdirs()) {
            Log.d("TAG", "文件不存在");
        }
        if (file.exists()) {
            file.delete();
        }


        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    os = new FileOutputStream(file);
                    if (os != null) {
                        while (isRecording) {
                            int read = audioRecord.read(data, 0, recordBufSize);
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                os.write(data);
                            }
                        }
                    }
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public void stopRecord(){
        isRecording = false;
        if(null !=audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

}
