package com.oniversal.proxcrypt;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static java.lang.System.exit;

public class FloatLogo extends Service implements View.OnClickListener {

    private WindowManager mWindowManager;
    private View mFloatingView;
    static boolean baca = true;
    Switch isBox;
    public String daemonPath;
    public String binary;
    public String bin;
    TextView tv;
    @SuppressLint("StaticFieldLeak")
    private static FloatLogo Instance;

    public FloatLogo() {
    }

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    View espView, logoView;

    @SuppressLint("CutPasteId")
    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        createOver();

        logoView = mFloatingView.findViewById(R.id.relativeLayoutParent);
        espView = mFloatingView.findViewById(R.id.espView);
        loadAssets("pro32");
        loadAssets("pro64");
        loadAssets("setting.toml");
        loadAssets("forwarding-rules.txt");
        Init();
    }

    @SuppressLint("InflateParams")
    void createOver() {
        //Getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.float_logo, null);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //Setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);

        //Getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        final GestureDetector gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        //Window function
        TextView closeBtn = mFloatingView.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                espView.setVisibility(View.GONE);
                logoView.setVisibility(View.VISIBLE);
            }
        });

        final LinearLayout player = mFloatingView.findViewById(R.id.players);
        final TextView playerBtn = mFloatingView.findViewById(R.id.playerBtn);

        playerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerBtn.setBackgroundColor(Color.parseColor("#150432"));
                playerBtn.setTextColor(Color.parseColor("#5EE863"));
                player.setVisibility(View.VISIBLE);
            }
        });

        //Floating window setting
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    espView.setVisibility(View.VISIBLE);
                    logoView.setVisibility(View.GONE);
                    return true;
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            }
        });
        mFloatingView.findViewById(R.id.espView).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    espView.setVisibility(View.VISIBLE);
                    logoView.setVisibility(View.GONE);
                    return true;
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.floatLogo:
                //switching views
                espView.setVisibility(View.VISIBLE);
                logoView.setVisibility(View.GONE);
                break;

            case R.id.closeBtn:
                espView.setVisibility(View.GONE);
                logoView.setVisibility(View.VISIBLE);
                break;
        }*/
    }

    private String getType() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getString("type", "1");
    }

    private void setValue(String key, boolean b) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, b);
        ed.apply();
    }

    boolean getConfig(String key) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
        // return !key.equals("");
    }

    void setFps(int fps) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("fps", fps);
        ed.apply();
    }

    int getFps() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getInt("fps", 100);
    }

    public static void HideFloat() {
        if (Instance != null) {
            Instance.Hide();
        }
    }

    public void Hide() {
        stopSelf();
        exit(-1);
        /*Instance = null;
        try {
            mWindowManager.removeView(mFloatingView);
        }catch (Exception e){
            System.out.println("-->"+e);
        }
        try {
        stopSelf();
        }catch (Exception e){
            System.out.println("-->"+e);
        }
            try {
        this.onDestroy();
            }catch (Exception e) {
                System.out.println("-->" + e);
            }*/
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
	void Init() {
        tv = mFloatingView.findViewById(R.id.shelltxt);
		isBox = mFloatingView.findViewById(R.id.connect);
        isBox.setChecked(getConfig((String) isBox.getText()));
        SettingValue(1, getConfig((String) isBox.getText()));
        isBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isBox.isChecked() && MainActivity.isproxy == true) {
                    setValue(String.valueOf(isBox.getText()), isBox.isChecked());
                    baca = true;
                    runAsRoot("su -c " + binary + " --config " + " setting.toml" + " >/dev/null 2>&1 &");
                    tv.setText(runAsRoot("echo -e '" +
                            "░█████╗░███╗░░██╗██╗\n" +
                            "██╔══██╗████╗░██║██║\n" +
                            "██║░░██║██╔██╗██║██║\n" +
                            "██║░░██║██║╚████║██║\n" +
                            "╚█████╔╝██║░╚███║██║\n" +
                            "░╚════╝░╚═╝░░╚══╝╚═╝\n" +
                            "\n" +
                            "██╗░░██╗██╗░░██╗███████╗░█████╗░████████╗\n" +
                            "╚██╗██╔╝██║░░██║██╔════╝██╔══██╗╚══██╔══╝\n" +
                            "░╚███╔╝░███████║█████╗░░███████║░░░██║░░░\n" +
                            "░██╔██╗░██╔══██║██╔══╝░░██╔══██║░░░██║░░░\n" +
                            "██╔╝╚██╗██║░░██║███████╗██║░░██║░░░██║░░░\n" +
                            "╚═╝░░╚═╝╚═╝░░╚═╝╚══════╝╚═╝░░╚═╝░░░╚═╝░░░\n" +
                            "---------PROXY BYPASS ANONNIMOUS MODE--------\n" +
                            "  █▀▀ █▀█ █▄░█ █▄░█ █▀▀ █▀▀ ▀█▀ █▀▀ █▀▄\n" +
                            "  █▄▄ █▄█ █░▀█ █░▀█ ██▄ █▄▄ ░█░ ██▄ █▄▀\"'"));
                    Rules.Proxy();
                    SettingValue(0, isBox.isChecked());
                }else if (isBox.isChecked() && MainActivity.ispubg == true){
                    setValue(String.valueOf(isBox.getText()), isBox.isChecked());
                    baca = true;
                    runAsRoot("su -c " + binary + " --config " + " setting.toml" + " >/dev/null 2>&1 &");
                    tv.setText(runAsRoot( "echo -e '" +
                                    "██████╗░██╗░░░██╗██████╗░░██████╗░\n" +
                                    "██╔══██╗██║░░░██║██╔══██╗██╔════╝░\n" +
                                    "██████╔╝██║░░░██║██████╦╝██║░░██╗░\n" +
                                    "██╔═══╝░██║░░░██║██╔══██╗██║░░╚██╗\n" +
                                    "██║░░░░░╚██████╔╝██████╦╝╚██████╔╝\n" +
                                    "╚═╝░░░░░░╚═════╝░╚═════╝░░╚═════╝░\n"+
                            "-------------OniXheat bypass MODE-----------\n" +
                            "  █▀▀ █▀█ █▄░█ █▄░█ █▀▀ █▀▀ ▀█▀ █▀▀ █▀▄\n" +
                            "  █▄▄ █▄█ █░▀█ █░▀█ ██▄ █▄▄ ░█░ ██▄ █▄▀\"'"));
                    Rules.Pubg();
                } else {
                    setValue(String.valueOf(isBox.getText()), isBox.isChecked());
                    SettingValue(1, isBox.isChecked());
                    baca = false;
                    runAsRoot("pkill " + " pro"+bin );
                    tv.setText("----DͩIͥS͛CͨOͦNNEͤCͨᴛⷮEͤD-----\nͩ" +
                            "░▐█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█▄☆\n" +
                            "░███████████████████████\n" +
                            "░▓▓▓▓▓▓▓▓▓▓▓▓██▓▓▓▓▓▓▓▓◤\n" +
                            "╬▀░▐▓▓▓▓▓▓▌▀█░░░█▀░\n" +
                            "▒░░▓▓▓▓▓▓█▄▄▄▄▄█▀╬░\n" +
                            "░░█▓▓▓▓▓▌░▒▒▒▒▒▒▒▒▒\n" +
                            "░▐█▓▓▓▓▓░░▒▒▒▒▒▒▒▒▒\n" +
                            "░▐██████▌╬░▒▒▒▒▒▒▒▒\n");
                    Rules.Reset();
                }
            }
        });
	}
    public String runAsRoot(String cmd) {

        try {
            Runtime.getRuntime().exec("chmod 777 "+daemonPath);
            // Executes the command.
            Process process = execCommandLine(cmd);
            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            // process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Process execCommandLine(String command)
    {
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        OutputStreamWriter osw = null;

        try
        {
            proc = runtime.exec("su");
            osw = new OutputStreamWriter(proc.getOutputStream());
            osw.write(command);
            osw.flush();
            osw.close();
        }
        catch (IOException ex)
        {
            Log.e("execCommandLine()", "Command resulted in an IO Exception: " + command);
            return proc;
        }
        finally
        {
            if (osw != null)
            {
                try
                {
                    osw.close();
                }
                catch (IOException e){}
            }
        }

        try
        {
            proc.waitFor();
        }
        catch (InterruptedException e){}

        if (proc.exitValue() != 0)
        {
            Log.e("execCommandLine()", "Command returned error: " + command + "\n  Exit code: " + proc.exitValue());
        }
        return proc;
    }
    public void loadAssets(String script)
    {

        String pathf = getFilesDir().toString()+"/"+script;
        try
        {
            OutputStream myOutput = new FileOutputStream(pathf);
            byte[] buffer = new byte[1024];
            int length;
            InputStream myInput = getAssets().open(script);
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myInput.close();
            myOutput.flush();
            myOutput.close();

        }

        catch (IOException e)
        {
        }
        if ( MainActivity.is64) {
            binary = getFilesDir().toString()+"/"+"pro64";
            bin="64";
        }else if (MainActivity.is32){
            binary = getFilesDir().toString()+"/"+"pro32";
            bin="32";
        }
        daemonPath =getFilesDir().toString()+"/"+script;
        try{
                Runtime.getRuntime().exec("chmod 777 "+ daemonPath);
        }
        catch (IOException e)
        {
        }
    }

    public native void SettingValue(int setting_code, boolean value);
}

class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }
}