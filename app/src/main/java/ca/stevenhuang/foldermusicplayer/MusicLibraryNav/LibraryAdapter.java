package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

import ca.stevenhuang.foldermusicplayer.R;

/**
 * Created by Steven on 3/5/2015.
 */
// TODO: use base adapter
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryItemHolder> {
	Context mContext;
	ArrayList<LibraryItem> mData;
	private static ILibraryItemOnClickListener mListener;
	private SparseBooleanArray selectedItems;
	int lastPosition = -1;

	public LibraryAdapter(Context context, ArrayList<LibraryItem> data, ILibraryItemOnClickListener listener) {
		mContext = context;
		mData = data;
		mListener = listener;
		selectedItems = new SparseBooleanArray();
		setHasStableIds(true);
	}

	@Override
	public LibraryItemHolder onCreateViewHolder(ViewGroup viewGroup, int pos) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		View view = inflater.inflate(R.layout.library_row, viewGroup, false);
		return new LibraryItemHolder(view);
	}

	@Override
	public void onBindViewHolder(LibraryItemHolder itemViewHolder, int pos) {
		LibraryItem item = mData.get(pos);
		itemViewHolder.container.setSelected(isSelected(pos));
		itemViewHolder.icon.setImageDrawable(new IconicsDrawable(mContext, item.getIcon()).sizeDp(30).color(Color.WHITE));
		itemViewHolder.title.setText(item.getTitle());
		itemViewHolder.subtitle.setText(item.getSubtitle());
		itemViewHolder.container.setStateListAnimator(AnimatorInflater.loadStateListAnimator(mContext, R.anim.elevation));
		//setAnimation(itemViewHolder.container, pos);
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).hashCode();
	}

	public void setSelected(int position, boolean selected) {
		if(selected) {
			selectedItems.put(position, true);
		} else {
			selectedItems.delete(position);
		}
		notifyItemChanged(position);
	}
	public boolean isSelected(int position) {
		return selectedItems.get(position, false);
	}
	public void clearSelected() {
		for(int i = 0; i < selectedItems.size(); i++) {
			int index = selectedItems.keyAt(i);
			setSelected(index, false);
		}
		selectedItems.clear();
	}

	private void setAnimation(View viewToAnimate, int pos) {
		if (pos > lastPosition) {
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
			animation.setStartOffset(pos*30);
			viewToAnimate.startAnimation(animation);
			lastPosition = pos;
		}
	}

	public class LibraryItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		public RelativeLayout container;
		public ImageView icon;
		public TextView title;
		public TextView subtitle;

		public LibraryItemHolder(View itemView) {
			super(itemView);
			container = (RelativeLayout)itemView.findViewById(R.id.library_row_container);
			icon = (ImageView)itemView.findViewById(R.id.library_row_icon);
			title = (TextView)itemView.findViewById(R.id.library_row_title);
			subtitle = (TextView)itemView.findViewById(R.id.library_row_subtitle);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int vPos = getAdapterPosition();
//			if(isSelected(vPos)) {
//				setSelected(vPos, false);
//				container.setSelected(false);
//				//v.setElevation(0);
//			} else {
//				setSelected(vPos, true);
//				container.setSelected(true);
//				//v.setElevation(40);
//			}
			mListener.onRowClick(v, getLayoutPosition());
		}

		@Override
		public boolean onLongClick(View v) {
			return mListener.onRowLongClick(v, getLayoutPosition());
		}


	}

	public interface ILibraryItemOnClickListener {
		void onRowClick(View caller, int pos);
		boolean onRowLongClick(View caller, int pos);
	}
}
