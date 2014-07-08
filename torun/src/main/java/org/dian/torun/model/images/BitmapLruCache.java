package org.dian.torun.model.images;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by BaiYa on 2014/4/22.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache{

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(createKey(url));
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(createKey(url), bitmap);
    }

    private String createKey(String url) {
        return String.valueOf(url.hashCode());
    }
}
