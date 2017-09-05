## VideoView-Player简介
* player目前兼容到Android7.0
* player是基于android原生VideoView控件封装的一个通用性播放界面
* 只支持android原生videoview所支持的视频格式
* 若你需要支持更多的视频格式，可下载源码自行增加第三方的os库进行修改
* 开发者如果只需要简单的播放界面的话那么此library库是完全够用的，只需传入一个网络地址或者视频列表集合就可以播放了
* 这里给大家推荐一个国内比较知名的播放性能强大的库 [Vitamio](https://www.vitamio.org/)
## player中有很常用的工具方法
* utils包中的GetMove.java类
  > *  此类是获取本地视频的帮助类，为了避免延时操作尽量在子线程中执行

---

* utils包中的Utils.java类
  > * public static String getNetSpeed(Context context) 获取网速
  > * public static void hideBottomUIMenu(Activity activity) 隐藏底部虚拟按键
  > * public static String durationFromat(long time) 将毫秒转化为 00:00 格式
  > * public static String getSystemTime() 获取系统时间
  > * public static int getScreenHeight(Context context) 获取屏幕高度
  > * public static int getScreenWidth(Context context) 获取屏幕宽度
  > * public static boolean isWifi(Context mContext) 判断是否连接到WIFI
  > *  public static String sdSizeFromat(Context context, long size) 容量转换 b kb mb gb
  
---

* wedigt包中的CustomToast类是自定义的toast用法请参考源码

## 使用方法
* 依赖库
  > `compile 'com.videoplay:VideoView-Player:1.0.0'`
  
* 播放本地视频的时候需要申请的权限
  > * Manifest.permission.WRITE_EXTERNAL_STORAGE
  
* 请求权限示例


```java
//检查是否有该项权限 
private void checkPremission() {
     if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                requestNeedPermission();
            }
        }
    }

//请求权限
private void requestNeedPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

 ```

* 简单的使用方法

```java


//传入单个 url地址播放网络视频
public void click1(View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        String bpath = "http://vfx.mtime.cn/Video/2017/05/09/mp4/170509071709934167.mp4";
        intent.setDataAndType(Uri.parse(bpath), "video/*");
        startActivity(intent);
    }


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


//listview条目的监听事件
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


```

## 其它的使用方法请下载源码后看里面的simple
![image](https://github.com/hejun1270/VideoPlay/blob/master/%E6%88%AA%E5%9B%BE.jpg)