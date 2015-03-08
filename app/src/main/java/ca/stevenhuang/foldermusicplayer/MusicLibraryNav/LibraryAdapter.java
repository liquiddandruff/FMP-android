package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.util.ArrayList;

import ca.stevenhuang.foldermusicplayer.R;

/**
 * Created by Steven on 3/5/2015.
 */
// TODO: use base adapter
public class LibraryAdapter extends ArrayAdapter<LibraryItem> {
	Context mContext;
	int mLayoutID;
	ArrayList<LibraryItem> mData;

	public LibraryAdapter(Context context, int layoutID, ArrayList<LibraryItem> data) {
		super(context, layoutID, data);

		mLayoutID = layoutID;
		mContext = context;
		mData = data;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		if(view == null){
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			view = inflater.inflate(mLayoutID, parent, false);
			//Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
			//anim.setStartOffset(pos*100);
			//view.startAnimation(anim);
		}
		LibraryItem item = mData.get(pos);
		ImageView icon = (ImageView)view.findViewById(R.id.library_row_icon);
		TextView title = (TextView)view.findViewById(R.id.library_row_title);
		TextView subtitle = (TextView)view.findViewById(R.id.library_row_subtitle);

		icon.setImageDrawable(new IconicsDrawable(mContext, item.getIcon()).sizeDp(30).color(Color.WHITE));
		title.setText(item.getTitle());
		subtitle.setText(item.getSubtitle());

		return view;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public LibraryItem getItem(int pos) {
		return mData.get(pos);
	}

}
