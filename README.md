# VideoView-Player
##1.VideoView-Player介绍
   此library主要是对Android原生的VideoView进行了封装播放格和原生的一样，开发者如果只需要简单的播放界面的话那么此library库是完全够用的，只需传入一个网络地址或者视频列表集合就可以播放了
##2.使用方法
	` /**
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
    }`
##3.其它的使用方法请下载源码后看里面的simple