package com.example.archerlei.googlevrdemo.Util;

import android.content.Context;

import com.example.archerlei.googlevrdemo.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.list;

/**
 * Created by archerlei on 2016/10/19.
 */

public class LyricUtil {
    public static List<LyricInfo> parse(Context context, int sourceId) {
        List<LyricInfo> list = new ArrayList<>();

        String encoding = "GBK";
        InputStream in = context.getResources().openRawResource(sourceId);
        Reader reader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(reader);

        String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]";
        Pattern pattern = Pattern.compile(regex);
        String lineStr = null;

        try {
            while ((lineStr = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(lineStr);
                while (matcher.find()) {
                    LyricInfo lyricInfo = new LyricInfo();
                    String min = matcher.group(1); // 分钟
                    String sec = matcher.group(2); // 秒
                    String mill = matcher.group(3); // 毫秒，注意这里其实还要乘以10
                    lyricInfo.lyricTime = getLongTime(min, sec, mill + "0");
                    // 获取当前时间的歌词信息
                    lyricInfo.lyricText = lineStr.substring(matcher.end());
                    list.add(lyricInfo);
                }
            }
            reader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static long getLongTime(String min, String sec, String mill) {
        // 转成整型
        int m = Integer.parseInt(min);
        int s = Integer.parseInt(sec);
        int ms = Integer.parseInt(mill);
        
        if (s >= 60) {
            System.out.println("警告: 出现了一个时间不正确的项 --> [" + min + ":" + sec + "."
                            + mill.substring(0, 2) + "]");
        }
        // 组合成一个长整型表示的以毫秒为单位的时间
        return (long) (m * 60 * 1000 + s * 1000 + ms);
    }
}
