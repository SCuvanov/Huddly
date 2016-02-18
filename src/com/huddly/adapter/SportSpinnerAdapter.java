package com.huddly.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huddly.main.R;
import com.huddly.model.Sport;

public class SportSpinnerAdapter extends ArrayAdapter<Sport> {

	Context context;
	ArrayList<Sport> array;
	int resourceId;

	public SportSpinnerAdapter(Context context, int resourceId,
			ArrayList<Sport> array) {
		// TODO Auto-generated constructor stub

		super(context, resourceId, array);
		this.context = context;
		this.resourceId = resourceId;
		this.array = array;
	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvSportName;
		ImageView ivSportImage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Sport sport = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.sport_list_item, null);
			viewHolder.tvSportName = (TextView) convertView
					.findViewById(R.id.tvSportName);
			viewHolder.ivSportImage = (ImageView) convertView
					.findViewById(R.id.ivSportImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Populate the data into the template view using the data object
		viewHolder.tvSportName.setText(sport.getSportName());
		viewHolder.ivSportImage.setImageResource(sport.getResourceId());

		// Return the completed view to render on screen
		return convertView;

	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		Sport sport = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.sport_list_item, null);
			viewHolder.tvSportName = (TextView) convertView
					.findViewById(R.id.tvSportName);
			viewHolder.ivSportImage = (ImageView) convertView
					.findViewById(R.id.ivSportImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Populate the data into the template view using the data object
		viewHolder.tvSportName.setText(sport.getSportName());
		viewHolder.ivSportImage.setImageResource(sport.getResourceId());

		// Return the completed view to render on screen
		return convertView;
	}

}
