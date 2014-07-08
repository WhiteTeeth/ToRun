package org.dian.torun.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.dian.torun.R;
import org.dian.torun.type.DrawerMenu;

public class DrawerAdapter extends BaseAdapter{


	private Context mContext;
	
	public DrawerAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return DrawerMenu.values().length;
	}

	@Override
	public DrawerMenu getItem(int position) {
		return DrawerMenu.values()[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.drawer_item, parent, false);
		}
        TextView textView = (TextView) convertView.findViewById(R.id.drawer_item);
		textView.setText(getItem(position).getDisplayName());
		textView.setCompoundDrawablesWithIntrinsicBounds(getItem(position).getDisplayIconId(), 0, 0, 0);
		return convertView;
	}

}
