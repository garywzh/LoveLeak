package xyz.garywzh.loveleak.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import xyz.garywzh.loveleak.R;
import xyz.garywzh.loveleak.model.VideoItem;
import xyz.garywzh.loveleak.util.TextStyleUtil;

/**
 * Created by garywzh on 2016/9/13.
 */
public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.VideoViewHolder> {
    private final View.OnClickListener mClickListener;
    private List<VideoItem> mData;

    public VideoItemAdapter(final OnItemActionListener listener) {
        mClickListener = new OnViewHolderClickListener(listener);
        setHasStableIds(true);
    }

    public void setDataSource(List<VideoItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_video_item, parent, false);
        view.setOnClickListener(mClickListener);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.fillData(mData.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mData == null ? RecyclerView.NO_ID : Integer.parseInt(mData.get(position).vid);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        final View mRoot;
        final ImageView mCoverPic;
        final TextView mTitle;
        final TextView mUserName;
        final TextView mViewCount;
        final TextView mThumbUpCount;
        final TextView mReplyCount;

        VideoViewHolder(View view) {
            super(view);

            mRoot = view;
            mCoverPic = ((ImageView) view.findViewById(R.id.cover_img));
            mTitle = ((TextView) view.findViewById(R.id.tv_title));
            mUserName = ((TextView) view.findViewById(R.id.tv_user_name));
            mViewCount = (TextView) view.findViewById(R.id.tv_view_count);
            mThumbUpCount = (TextView) view.findViewById(R.id.tv_thumbup_count);
            mReplyCount = (TextView) view.findViewById(R.id.tv_reply_count);
        }

        void fillData(VideoItem item) {
            mRoot.setTag(item);
            mTitle.setText(TextStyleUtil.clearStyle(item.title));
            mUserName.setText(item.user_name);
            mViewCount.setText(item.times_viewed + " views");
            mThumbUpCount.setText(item.number_of_votes);
            mReplyCount.setText(item.number_of_comments);

            setCoverPic(item);
        }

        private void setCoverPic(VideoItem item) {
            final String url = item.thumburl;
            Glide.with(mCoverPic.getContext()).load(url)
                    .placeholder(R.drawable.coverpic_default).crossFade()
                    .into(mCoverPic);
        }
    }

    private static class OnViewHolderClickListener implements View.OnClickListener {
        private OnItemActionListener mListener;

        OnViewHolderClickListener(OnItemActionListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mListener == null)
                return;
            mListener.onItemOpen((VideoItem) (v.getTag()));
        }
    }

    public interface OnItemActionListener {
        boolean onItemOpen(VideoItem item);
    }
}
