package ua.dp.michaellang.testlauncher;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Date: 21.08.2017
 *
 * @author Michael Lang
 */
public class ImageLoader<T> extends HandlerThread {
    private static final String TAG = "ImageLoader";
    private static final int MESSAGE_LOAD = 0;

    private PackageManager mPackageManager;
    private RequestHandler mRequestHandler;
    private ImageLoaderListener<T> mLoaderListener;
    private Handler mResponseHandler;

    public ImageLoader(PackageManager packageManager, Handler responseHandler) {
        super(TAG);
        mPackageManager = packageManager;
        mResponseHandler = responseHandler;
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_LOAD);
    }

    public void setLoaderListener(ImageLoaderListener<T> loaderListener) {
        mLoaderListener = loaderListener;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new RequestHandler();
    }

    public void loadImage(T target, ResolveInfo info) {
        mRequestHandler.mRequestMap.put(target, info);
        mRequestHandler.obtainMessage(MESSAGE_LOAD, target)
                .sendToTarget();
    }

    public interface ImageLoaderListener<T> {
        void onImageLoaded(T target, Drawable drawable);
    }

    @SuppressLint("HandlerLeak")
    private class RequestHandler extends Handler {
        private ConcurrentMap<T, ResolveInfo> mRequestMap;

        public RequestHandler() {
            mRequestMap = new ConcurrentHashMap<>();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_LOAD) {
                final T target = (T) msg.obj;
                final ResolveInfo info = mRequestMap.get(target);

                final Drawable drawable = info.loadIcon(mPackageManager);
                if (mLoaderListener != null) {
                    mResponseHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mRequestMap.get(target) != info) {
                                return;
                            }
                            mRequestMap.remove(target);
                            mLoaderListener.onImageLoaded(target, drawable);
                        }
                    });
                }
            }
        }
    }
}
