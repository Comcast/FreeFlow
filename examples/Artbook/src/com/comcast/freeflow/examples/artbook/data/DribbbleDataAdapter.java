package com.comcast.freeflow.examples.artbook.data;

import java.util.ArrayList;

import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.core.SectionedAdapter;

import com.comcast.freeflow.examples.artbook.DribbbleFeed;
import com.comcast.freeflow.examples.artbook.R;
import com.comcast.freeflow.examples.artbook.Shot;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DribbbleDataAdapter implements SectionedAdapter {

	private Context context;
	private DribbbleFeed feed;
	private Section section;

	private int[] colors = new int[] { 0xcc152431, 0xff264C58, 0xffF5C543,
			0xffE0952C, 0xff9A5325, 0xaaE0952C, 0xaa9A5325, 0xaa152431,
			0xaa264C58, 0xaaF5C543, 0x44264C58, 0x44F5C543, 0x44152431 };
	
	private boolean hideImages = false;

	public DribbbleDataAdapter(Context context, DribbbleFeed feed) {
		this.feed = feed;
		this.context = context;
		section = new Section();
		section.setSectionTitle("Pics");
		section.setData(new ArrayList<Object>(feed.getShots()));
	}

	@Override
	public long getItemId(int section, int position) {
		return section * 1000 + position;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.pic_view, parent, false);
		}
		ImageView img = (ImageView) convertView.findViewById(R.id.pic);
		if (hideImages) {
			int idx = position % colors.length;
			img.setBackgroundColor(colors[idx]);

		} else {
			Picasso.with(context)
					.load(feed.getShots().get(position).getImage_teaser_url())
					.into(img);
		}

		return convertView;
	}

	@Override
	public View getHeaderViewForSection(int section, View convertView,
			ViewGroup parent) {
		return null;
	}

	@Override
	public int getNumberOfSections() {
		return 1;
	}

	@Override
	public Section getSection(int index) {
		return section;
	}

	@Override
	public Class[] getViewTypes() {
		return new Class[] { LinearLayout.class };
	}

	@Override
	public Class getViewType(ItemProxy proxy) {
		return LinearLayout.class;
	}

	@Override
	public boolean shouldDisplaySectionHeaders() {
		return false;
	}

}
