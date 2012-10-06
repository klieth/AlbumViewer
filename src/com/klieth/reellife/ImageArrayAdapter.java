package com.klieth.reellife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageArrayAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final List<String> names = new ArrayList<String>();
	private final List<Bitmap> pics = new ArrayList<Bitmap>();
	
	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}
	
	public ImageArrayAdapter(Activity context, String[] names, Bitmap[] bms) {
		super(context, R.layout.rowlayout, new ArrayList<String>(Arrays.asList(names)));
		this.context = context;
		this.names.addAll(Arrays.asList(names));
		this.pics.addAll(Arrays.asList(bms));
	}
	
//	public ImageArrayAdapter(Context context, String[] names) {
//		super(context, R.layout.rowlayout, new ArrayList(Arrays.asList(names)));
//		this.context = context;
//		this.names.addAll(Arrays.asList(names));
//	}
	
	public void add(Bitmap bm, String object) {
		add(object);
		pics.add(bm);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.rowlayout, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView)rowView.findViewById(R.id.TextView01);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.ImageView01);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		if (position >= names.size()) {
			holder.text.setText("empty");
		} else {
			holder.text.setText(names.get(position));
		}
		holder.image.setImageBitmap(pics.get(position));
		return rowView;
	}
}
