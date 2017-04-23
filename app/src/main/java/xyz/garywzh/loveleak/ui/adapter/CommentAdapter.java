package xyz.garywzh.loveleak.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import xyz.garywzh.loveleak.R;
import xyz.garywzh.loveleak.model.Comment;
import xyz.garywzh.loveleak.model.VideoItem;
import xyz.garywzh.loveleak.util.LogUtils;
import xyz.garywzh.loveleak.util.TextStyleUtil;

/**
 * Created by garywzh on 2016/9/17.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COMMENT = 1;
    private static final int TYPE_FOOTER = 2;

    private VideoItem mVideoItem;
    private List<Comment> mComments;
    private boolean showProgressBar = true;
    private OnTitleClickListener mListener;

    public CommentAdapter(OnTitleClickListener listener) {
        mListener = listener;
        setHasStableIds(true);
    }

    public void showProgressBar(boolean show) {
        showProgressBar = show;
    }

    public void setVideoItem(VideoItem videoItem) {
        mVideoItem = videoItem;
        notifyDataSetChanged();
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (mComments == null ? 1 : mComments.size() + 1) + (showProgressBar ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (showProgressBar && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_COMMENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
            return new HeaderViewHolder(view, mListener);
        } else if (viewType == TYPE_COMMENT) {
            final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_comment, parent, false);
            return new CommentViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_footer, parent, false);
            return new FooterViewHolder(view);
        } else {
            throw new RuntimeException("wrong view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).fillData(mVideoItem);
        } else if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).fillData(mComments.get(position - 1));
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return mVideoItem.title.hashCode();
        } else if (showProgressBar && position == getItemCount() - 1) {
            return "progressbar".hashCode();
        } else {
            return mComments.get(position - 1).hashCode();
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder implements
        OnClickListener {

        private final TextView title;
        private final TextView userName;
        private final TextView time;
        private final TextView views;
        private final TextView vote;
        private final TextView reply;
        private final TextView description;
        private OnTitleClickListener mListener;
        private String mWebUrl;

        HeaderViewHolder(View itemView, OnTitleClickListener listener) {
            super(itemView);
            mListener = listener;
            title = (TextView) itemView.findViewById(R.id.tv_title);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            views = (TextView) itemView.findViewById(R.id.tv_views);
            vote = (TextView) itemView.findViewById(R.id.tv_thumbup_count);
            reply = (TextView) itemView.findViewById(R.id.tv_reply_count);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            description.setMovementMethod(LinkMovementMethod.getInstance());
        }

        void fillData(VideoItem item) {
            if (item.title != null) {
                title.setText(TextStyleUtil.clearStyle(item.title));
            }

            LogUtils.d("viewholder", item.videourl);
            mWebUrl = item.webUrl();

            if (mWebUrl != null) {
                LogUtils.d("viewholder", mWebUrl);
                title.setOnClickListener(this);
            }
            userName.setText(item.user_name);
            time.setText(item.addedon);
            views.setText(item.times_viewed + " views");
            vote.setText(item.number_of_votes);
            reply.setText(item.number_of_comments);
            if (item.description != null) {
                description.setText(TextStyleUtil.clearStyle(item.description));
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onTitleClicked(mWebUrl);
        }
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mAvatar;
        private final TextView mUsername;
        private final TextView mContent;
        private final TextView mReplyTime;

        CommentViewHolder(View view) {
            super(view);
            mAvatar = ((ImageView) view.findViewById(R.id.avatar_img));
            mUsername = (TextView) view.findViewById(R.id.tv_username);
            mContent = (TextView) view.findViewById(R.id.content_tv);
            mContent.setMovementMethod(LinkMovementMethod.getInstance());
            mReplyTime = ((TextView) view.findViewById(R.id.tv_time));
        }

        void fillData(Comment comment) {
            mUsername.setText(comment.user_name);
            if (comment.text != null) {
                mContent.setText(TextStyleUtil.clearStyle(comment.text));
            }
            mReplyTime.setText(comment.addedon);

            Glide.with(mAvatar.getContext()).load(comment.user_profile_image_url)
                .placeholder(R.drawable.avatar_default).crossFade()
                .into(mAvatar);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View footerView) {
            super(footerView);
        }
    }

    public interface OnTitleClickListener {

        void onTitleClicked(String url);
    }
}
