package com.adminhj.videoplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.adminhj.videoplayer.ben.LocalVideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdminHeJun on 2017/2/22.
 * </p>
 * Content:Get the local video utility class
 * </p>
 * Modified:
 * </p>
 * Version:
 * </p>
 */

public class GetMove {
    private static OnFinishLinistener finishLinistener;

    /**
     * get local video
     *
     * @param context  you need to pass in the context
     * @param linistener CallBack this interface
     */
    public static void getLocalMove(final Context context, final OnFinishLinistener linistener) {
        finishLinistener = linistener;
        new Thread() {
            @Override
            public void run() {
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA
                };
                Cursor query = contentResolver.query(uri, objs, null, null, null);
                List<LocalVideoInfo> localVideoInfos = new ArrayList<>();
                if (query != null) {
                    while (query.moveToNext()) {
                        LocalVideoInfo localAudioInfo = new LocalVideoInfo();
                        localAudioInfo.setName(query.getString(0));
                        localAudioInfo.setDuration(query.getLong(1));
                        localAudioInfo.setSize(query.getLong(2));
                        localAudioInfo.setData(query.getString(3));
                        localVideoInfos.add(localAudioInfo);
                    }
                }
                query.close();
                finishLinistener.onFinish(localVideoInfos);
            }
        }.start();

    }


    /**
     * CallBack this interface when the local video is obtained
     */
    public interface OnFinishLinistener {
        void onFinish(List<LocalVideoInfo> musicInfoList);
    }

}
