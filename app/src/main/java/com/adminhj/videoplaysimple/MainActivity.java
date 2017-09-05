package com.adminhj.videoplaysimple;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adminhj.videoplayer.activity.VideoActivity;
import com.adminhj.videoplayer.ben.LocalVideoInfo;
import com.adminhj.videoplayer.utils.GetMove;
import com.adminhj.videoplayer.utils.Utils;

import java.io.Serializable;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int SUCCESS = 1;
    private static final int ERROR = 0;
    private ListView mListView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR:
                    Toast.makeText(context, "没有发现视频...", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    mListView.setAdapter(adapter);
                    break;
            }
        }
    };

    private List<LocalVideoInfo> videoInfoList;
    private ListAdapter adapter;
    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPremission();
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setOnItemClickListener(new ItemOnclicklistener());
        context = this;
    }

    /**
     * 传入单个 url地址
     * 播放网络视频
     *
     * @param view
     */
    public void click1(View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        String bpath = "http://vfx.mtime.cn/Video/2017/05/09/mp4/170509071709934167.mp4";
        intent.setDataAndType(Uri.parse(bpath), "video/*");
        startActivity(intent);
    }

    /**
     * 播放本地视频
     *
     * @param view
     */
    public void click2(View view) {
        //获取本地视频
        GetMove.getLocalMove(context, new GetMove.OnFinishLinistener() {
            @Override
            public void onFinish(List<LocalVideoInfo> localVideoList) {
                if (localVideoList != null && localVideoList.size() > 0) {
                    videoInfoList = localVideoList;
                    adapter = new ListAdapter();
                    handler.sendEmptyMessage(SUCCESS);
                } else {
                    handler.sendEmptyMessage(ERROR);
                }
            }
        });
    }

    class ItemOnclicklistener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, VideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", (Serializable) videoInfoList);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }

    /**
     * 检测权限
     */
    private void checkPremission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                requestNeedPermission();
            }
        }
    }

    /**
     * 请求权限
     */
    private void requestNeedPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            Toast.makeText(this, "requested permission!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * listView 适配器
     */
    class ListAdapter extends BaseAdapter {

        Holder holder;

        @Override
        public int getCount() {
            return videoInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return videoInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.list_video_item, null);
                holder = new Holder(convertView);
            }
            holder = getHolder(convertView);
            holder.name.setText(videoInfoList.get(position).getName());
            holder.time.setText(Utils.durationFromat(videoInfoList.get(position).getDuration()));
            holder.size.setText(Utils.sdSizeFromat(context, videoInfoList.get(position).getSize()));
            return convertView;
        }

        private Holder getHolder(View convertView) {
            Holder holder = (Holder) convertView.getTag();
            if (holder == null) {
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }

        class Holder {
            ImageView more;
            TextView name;
            TextView time;
            TextView size;

            public Holder(View converView) {
                more = (ImageView) converView.findViewById(R.id.iv_video_more);
                name = (TextView) converView.findViewById(R.id.tv_video_name);
                time = (TextView) converView.findViewById(R.id.tv_video_duration);
                size = (TextView) converView.findViewById(R.id.tv_video_size);
            }
        }
    }
}
