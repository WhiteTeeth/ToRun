package org.dian.torun.model.images;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.dian.torun.AppData;
import org.dian.torun.model.RequestManager;

/**
 * Created by BaiYa on 2014/4/22.
 */
public class ImageManager {

    // 取运行内存阈值的1/3作为图片缓存
    private static final int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) AppData.getContext()
            .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 4;

    private static ImageManager mInstance;

    private static BitmapLruCache mCache;

    /**
     * Volley image loader
     */
    private ImageLoader mImageLoader;

    private ImageManager() {
        mImageLoader = new ImageLoader(
                RequestManager.getRequestQueue(), new BitmapLruCache(MEM_CACHE_SIZE));
    }

    /**
     * @return instance of the cache manager
     */
    public static ImageManager getInstance() {
        if (mInstance == null) {
            mInstance = new ImageManager();
        }
        return mInstance;
    }

    public ImageLoader.ImageContainer loadImage(String url, ImageLoader.ImageListener listener) {
        return loadImage(url, listener, 0, 0);
    }

    public ImageLoader.ImageContainer loadImage(String url,
            ImageLoader.ImageListener listener, int maxWidth, int maxHeight) {
        return mImageLoader.get(url,listener, maxWidth, maxHeight);
    }

    public static ImageLoader.ImageListener getImageListener(
            final ImageView view,
            final Drawable defaultImageDrawable,
            final Drawable errorImageDrawable) {

        return new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorImageDrawable != null) {
                    view.setImageDrawable(errorImageDrawable);
                }
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    if (!isImmediate && defaultImageDrawable != null) {
                        TransitionDrawable transitionDrawable = new TransitionDrawable(
                                new Drawable[] {
                                        defaultImageDrawable,
                                        new BitmapDrawable(AppData.getContext().getResources(),
                                                response.getBitmap())
                                });
                        transitionDrawable.setCrossFadeEnabled(true);
                        view.setImageDrawable(transitionDrawable);
                        transitionDrawable.startTransition(100);
                    } else {
                        view.setImageBitmap(response.getBitmap());
                    }
                } else if (defaultImageDrawable != null) {
                    view.setImageDrawable(defaultImageDrawable);
                }
            }
        };
    }

}
