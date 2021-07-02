package com.oniversal.proxcrypt;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Rules {
    public static void Proxy() {
        runAsRoot("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -t nat -D OUTPUT -p tcp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354;iptables -t nat -D OUTPUT -p udp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354;iptables -t nat -A OUTPUT -p tcp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354;iptables -t nat -A OUTPUT -p udp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354");
    }
    public static void Reset() {
        runAsRoot("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -I INPUT -j ACCEPT;iptables -P OUTPUT ACCEPT;iptables -A INPUT -i lo -j ACCEPT -m comment --comment \"Allow all loopback traffic\";iptables -A INPUT ! -i lo -d 127.0.0.0/8 -j REJECT -m comment --comment \"Drop all traffic to 127 that doesn't use lo\";iptables -A OUTPUT -j ACCEPT -m comment --comment \"Accept all outgoing\";iptables -A INPUT -j ACCEPT -m comment --comment \"Accept all incoming\";iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT -m comment --comment \"Allow all incoming on established connections\"");
    }
    public static String runAsRoot(String cmd) {

        try {
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

    static Process execCommandLine(String command)
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

    public static void Pubg() {
        runAsRoot("iptables -I OUTPUT -p tcp --dport http-alt -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 10012 -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 20371 -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 15692 -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 18081 -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 8085 -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport https -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 49514 -j DROP\n" +
                "iptables -I OUTPUT -p tcp --dport 8086 -j DROP\n" +
                "\n" +
                "iptables -I INPUT -p tcp --dport http-alt -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 10012 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 20371 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 15692 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 18081 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 8085 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport https -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 49514 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 8086 -j DROP\n" +
                "iptables -I INPUT -p tcp --dport 17500 -j DROP");
    }

}
