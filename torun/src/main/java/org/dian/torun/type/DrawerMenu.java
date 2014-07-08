package org.dian.torun.type;

import org.dian.torun.AppData;
import org.dian.torun.R;

/**
 * Created by BaiYa on 2014/4/23.
 */
public enum DrawerMenu {

    neighbor(R.string.drawer_title_neighbor, R.drawable.ic_drawer_mine),
    friends(R.string.drawer_title_friends, R.drawable.ic_drawer_friends),
    arrangement(R.string.drawer_title_invitation, R.drawable.ic_drawer_invitation);

    private int mDisplayName;

    private int mDisplayIcon;

    DrawerMenu(int displayName, int displayIcon) {
        mDisplayName = displayName;
        mDisplayIcon = displayIcon;
    }

    public String getDisplayName() {
        return AppData.getContext().getString(mDisplayName);
    }

    public int getDisplayIconId() {
        return mDisplayIcon;
    }
}
