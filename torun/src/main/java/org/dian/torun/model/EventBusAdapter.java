package org.dian.torun.model;

import de.greenrobot.event.EventBus;

/**
 * Created by BaiYa on 2014/4/25.
 */
public class EventBusAdapter {

    public static void register(Object object) {
        EventBus.getDefault().register(object);
    }

    public static void unregister(Object object) {
        EventBus.getDefault().unregister(object);
    }

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public static void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }

}
