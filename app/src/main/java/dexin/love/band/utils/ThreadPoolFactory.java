package dexin.love.band.utils;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ThreadPoolFactory {
    public static ThreadPoolProxy commonThreadPool;
    public static ThreadPoolProxy downThreadPool;
    public static int COMMON_THREAD_POOL_COREPOOL_SIZE=5;
    public static int COMMON_THREAD_POOL_MAX_SIZE=5;
    public static int KEEP_TIME=1;
    public static int DOWNLOAD_THREAD_POOL_COREPOOL_SIZE=5;
    public static int DOWNLOAD_THREAD_POOL_MAX_SIZE=5;
    public static ThreadPoolProxy getCommonThreadPool(){
        if(commonThreadPool==null){
            synchronized (ThreadPoolFactory.class){
                if(commonThreadPool==null){
                    commonThreadPool=new ThreadPoolProxy(COMMON_THREAD_POOL_COREPOOL_SIZE,COMMON_THREAD_POOL_MAX_SIZE,KEEP_TIME);
                }
            }
        }
        return commonThreadPool;
    }
    public static ThreadPoolProxy getDownThreadPool(){
        if(downThreadPool==null){
            synchronized (ThreadPoolFactory.class){
                if(downThreadPool==null){
                    downThreadPool=new ThreadPoolProxy(DOWNLOAD_THREAD_POOL_COREPOOL_SIZE,DOWNLOAD_THREAD_POOL_MAX_SIZE,KEEP_TIME);
                }
            }
        }
        return downThreadPool;
    }
}
