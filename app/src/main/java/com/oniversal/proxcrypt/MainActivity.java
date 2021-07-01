package com.oniversal.proxcrypt;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    static int gameType = 1;
    static int bit = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private boolean isDisplay = false;
    private boolean isMenuDis = false;
    Context ctx;
    private View menu;

    int mx, my;

    public static String socket;
    Process process;

    public String daemonPath;
    public String daemonPath64;

    static boolean isRoot = true;
    static boolean isNoroot = false;
    static boolean rootMod = false;
    static boolean ispubg = false;
    static boolean isproxy = false;

    static boolean is32 = false;
    static boolean is64 = false;
    static boolean bitMod = false;
    static boolean verMod = true;


    public boolean isDaemon = false;

    private RadioGroup radiogroup1, radiogroup2, radiogroup3, radiogroup4;

    public Button mbutton1, mbutton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CheckFloatViewPermission();
        ctx = this;
        mbutton1 = (Button) findViewById(R.id.button1);
        mbutton2 = (Button) findViewById(R.id.button2);
        mbutton1.setOnClickListener(this);
        mbutton2.setOnClickListener(this);

       // radiogroup1 = (RadioGroup) findViewById(R.id.radioInd);
        radiogroup2 = (RadioGroup) findViewById(R.id.radioMod);
        radiogroup3 = (RadioGroup) findViewById(R.id.radioBit);
        radiogroup4 = (RadioGroup) findViewById(R.id.pubg_radio);



        radiogroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.radiobutton3:
                        isRoot = true;
                        ExecuteElf("su -c");
                        isNoroot = false;
                        rootMod = true;
                        break;

                    case R.id.radiobutton4:
                        isNoroot = true;
                        isRoot = false;
                        rootMod = true;
                        break;
                }
            }
        });

        radiogroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.radiobutton5:
                        is32 = true;
                        is64 = false;
                        bitMod = true;
                        break;

                    case R.id.radiobutton6:
                        is64 = true;
                        is32 = false;
                        bitMod = true;
                        break;
                }
            }
        });
        radiogroup4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.radiopubg:
                        ispubg = true;
                        isproxy = false;
                        bitMod = true;
                        break;

                    case R.id.radioip:
                        isproxy = true;
                        ispubg = false;
                        bitMod = true;
                        break;
                }
            }
        });

        ExecuteElf("su -c");

        loadAssets();
        loadAssets64();

        socket = daemonPath;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                if (!isDisplay && !isMenuDis) {
                    if ( !isRoot ) {
                        Toast.makeText(MainActivity.this, "Please Rooting Device First !!", Toast.LENGTH_LONG).show();
                    } else {
                        if (isNoroot) {
                            if (is32) {
                                socket = daemonPath;
                            } else if (is64) {
                                socket = daemonPath64;
                            }
                        }
                        if (isRoot) {
                            if (is32) {
                                socket = "su -c " + daemonPath;
                            } else if (is64) {
                                socket = "su -c " + daemonPath64;
                            }
                        }
                        startPatcher();
                        startService(new Intent(this, Overlay.class));
                        // ShowFloatWindow();
                        isDisplay = true;
                        //startDaemon();
                        isDaemon = true;
                        //Toast.makeText(MainActivity.this,socket,Toast.LENGTH_LONG).show();
                    }

                    // FloatButton();
                    //  startPatcher();
                } else {
                    Toast.makeText(MainActivity.this, "Already Started !!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.button2:
                if (isDisplay) {
                    isDisplay = false;
                    isMenuDis = false;
                    isDaemon = false;

                    //process.destroy();
                    //Stop();
                    stopService(new Intent(this, FloatLogo.class));
                    stopService(new Intent(this, Overlay.class));
                }
                break;
        }
    }

    public void CheckFloatViewPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Enable Floating Permission ", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (FloatLogo.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    void startPatcher() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 123);
        } else {
            startFloater();
        }
    }

    private void startFloater() {
        if (!isServiceRunning()) {
            startService(new Intent(this, FloatLogo.class));
        } else {
            Toast.makeText(this, "Service Already Running!", Toast.LENGTH_SHORT).show();
        }
    }

    private void ExecuteElf(String shell) {
        String s = shell;
        try {
            Runtime.getRuntime().exec(s, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Shell(String str) {
        DataOutputStream dataOutputStream = null;
        try {
            process = Runtime.getRuntime().exec(str);
        } catch (IOException e) {
            e.printStackTrace();
            process = null;
        }
        if (process != null) {
            dataOutputStream = new DataOutputStream(process.getOutputStream());
        }
        try {
            dataOutputStream.flush();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            process.waitFor();
        } catch (InterruptedException e3) {
            e3.printStackTrace();
        }
    }

    public void startDaemon() {
        new Thread() {
            public void run() {
                Shell(socket);
            }
        }.start();
    }

    public void loadAssets() {
        String filepath = Environment.getExternalStorageDirectory() + "/Android/data/.tyb";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filepath);
            byte[] buffer = "DO NOT DELETE".getBytes();
            fos.write(buffer, 0, buffer.length);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String pathf = getFilesDir().toString() + "/sock";
        try {
            OutputStream myOutput = new FileOutputStream(pathf);
            byte[] buffer = new byte[1024];
            int length;
            InputStream myInput = getAssets().open("sock");
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myInput.close();
            myOutput.flush();
            myOutput.close();

        } catch (IOException e) {
        }

        daemonPath = getFilesDir().toString() + "/sock";

        try {
            Runtime.getRuntime().exec("chmod 777 " + daemonPath);
        } catch (IOException e) {
        }
    }

    public void loadAssets64() {
        String pathf = getFilesDir().toString() + "/sock64";
        try {
            OutputStream myOutput = new FileOutputStream(pathf);
            byte[] buffer = new byte[1024];
            int length;
            InputStream myInput = getAssets().open("sock64");
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myInput.close();
            myOutput.flush();
            myOutput.close();

        } catch (IOException e) {
        }

        daemonPath64 = getFilesDir().toString() + "/sock64";

        try {
            Runtime.getRuntime().exec("chmod 777 " + daemonPath64);
        } catch (IOException e) {
        }
    }

}