package com.example.administrator.myapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Administrator on 2015/8/12 0012.
 */
public class InitIMLUtils {
    private static final String IMAGE_CACHE = "imageloader/Cache";

    private static ImageLoaderConfiguration configuration;
    private static DisplayImageOptions.Builder options=null;


    public static  void initImageLoad(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, IMAGE_CACHE);
        configuration = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(720, 1280) // maxwidth, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024)) //你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                        //.discCacheFileNameGenerator(newMd5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context,5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for releaseapp
                .build();//开始构建
        ImageLoader.getInstance().init(configuration);



    }

    public static DisplayImageOptions.Builder initIMGoption(){
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.yy_nopicture)            //加载图片时的图片
//                .showImageForEmptyUri(R.drawable.yy_nopicture)         //没有图片资源时的默认图片
//                .showImageOnFail(R.drawable.yy_nopicture)             // 加载失败时的图片
                .cacheInMemory(true)                               //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .considerExifParams(true)                          //启用EXIF和JPEG图像格式
                        //.displayer(new RoundedBitmapDisplayer(20))         //设置显示风格这里是圆角矩形
                .bitmapConfig(Bitmap.Config.RGB_565)			   //色彩模式
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                ;
        return options;
    }
    public static DisplayImageOptions.Builder getOptions(){
        if(options==null){
            options=initIMGoption();
        }
        return options;
    }

}
