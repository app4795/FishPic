package com.example.mac_paul.tackpicture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;


public class TakePictureActivity extends Activity {
    private String TAG = "TakePictureActivity";
    private SurfaceView mSurfaceview;
    private SurfaceHolder holder;
    private int screenWidth, screenHeight;
    private Camera camera;// 定義系統所用相機
    boolean isPreview = false; // 是否在預覽中
    private Handler mHandler = new Handler();
    private AudioManager mAudioManager = null;
    private static final byte START = 0x1;
    static int times = 0;
    private Uri imageUri;
    private Button button;
    private ArrayList<Bitmap> bitmapsArr=new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        keepQuite();
        initializationCamera();
        times=0;
        button = (Button) findViewById(R.id.button_take_picture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictures();
            }
        });
    }

    /**
     * 摄像头设备启用
     */
    private void initializationCamera() {
        // 绑定预览视图
        mSurfaceview = (SurfaceView) this.findViewById(R.id.arc_hf_video_view);
        holder = mSurfaceview.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stopCamera();
            }

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (FindFrontCamera() != -1) {
                    camera = Camera.open(FindFrontCamera());
                } else {
                    camera = Camera.open();
                }
                initCamera();
                holder = surfaceHolder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
                holder = surfaceHolder;
            }

        });
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }



    /**
     * 拍摄照片并保存
     */
    private void takePictures() {
        if (camera != null) {
            camera.autoFocus(null);
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    camera.startPreview();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bitmapsArr.add(bitmap);
                    //設置縮放
                    //Matrix matrix = new Matrix();
                    //matrix.postScale(5f, 4f);
                    //bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    File out = null;
                    //處理如果沒有SD卡則保存在內存中
                    if (checkSD()) {
                        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeviceManager/Camera";
                        out = new File(path);
                        Log.v("TAG","FileGOOGOOOOOOOOO");
                        if (!out.exists()) {
                            out.mkdirs();
                        }
                        Log.v("TAG", fileName);
                        out = new File(path, fileName);
                    }
                    else {
                        String fileName = TAG + ".jpg";
                        Log.v("TAG", fileName);
                        out = new File(getFilesDir(), fileName);

                    }

                    try {
                        /*
                        FileOutputStream outStream = new FileOutputStream(out);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                        //计数
                        */
                        Log.v("TAG", "ASD");
                        times = times + 1;
                        //拍照次數判斷
                        if (true) {
                            takePictures();
                            Log.v("TAG", "ASDASDASDASDASD");
                            Thread.sleep(1000);
                            if (times >= 3){//設定拍照次數
                                stopCamera();
                                Toast.makeText(TakePictureActivity.this,"自拍结束……", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(TakePictureActivity.this, MainActivity.class);
                                Bitmap tempbit;
                                tempbit=bitmapsArr.get(0);
                                intent.putExtra("bitimg0",bitmapsArr.get(0));
                                tempbit=bitmapsArr.get(1);
                                intent.putExtra("bitimg1",bitmapsArr.get(1));
                                //不能傳送 因檔案>40K 可讀取存在bitmapsArr
                                //startActivity(intent);
                                //tempbit=bitmapsArr.get(2);
                                //intent.putExtra("bitimg2", bitmapsArr.get(2));
                                //finish();
                            }
                        }

                    } catch (Exception e) {
                        Log.v("TAG", "nonononono");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /* 相机初始化的method */
    private void initCamera() {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewFrameRate(5); // 每秒5張
                parameters.setPictureFormat(PixelFormat.JPEG);// 設置照片輸出格式
                parameters.set("jpeg-quality", 85);// 照片質量
                camera.setParameters(parameters);
                camera.setPreviewDisplay(holder);
                //設置旋轉角度，注意在manifest的activity裡面去設置android:screenOrientation="portrait"
                camera.setDisplayOrientation(90);
                camera.startPreview();
                isPreview = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 停止相機的method */
    private void stopCamera() {
        if (camera != null) {
            if (isPreview) {
                camera.stopPreview();
                isPreview = false;
            }
            camera.release();
            camera = null; // 釋放記憶體
        }
    }


    /**
     * Camera prepare
     */
    private void prepareCamera() {
        if (FindFrontCamera() != -1) {
            camera = Camera.open(FindFrontCamera());
        } else {
            camera = Camera.open();
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            camera.release();
            camera = null;
            e.printStackTrace();
        }

    }

    /**
     * 重置
     * @param time
     */
    private void DelayTime(long time) {
        final Timer timer = new Timer();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START:
                        stopCamera();
                        prepareCamera();
                        initCamera();
                        timer.cancel();
                        break;
                }
            }
        };
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(START);
            }
        };
        timer.schedule(timerTask, time);
    }



    /**
     * 手機SD卡檢測
     */
    public boolean checkSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 查找前置相機
     */
    private int FindFrontCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表相機方位，CAMERA_FACING_FRONT前置 CAMERA_FACING_BACK後置
                return camIdx;
            }
        }
        return -1;
    }

    /**
     * 设备静默
     */

    private void keepQuite() {
        //消音
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
}
