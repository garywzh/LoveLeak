package xyz.garywzh.loveleak.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xyz.garywzh.loveleak.R;
import xyz.garywzh.loveleak.model.VideoItem;
import xyz.garywzh.loveleak.model.VideoListBean;
import xyz.garywzh.loveleak.network.NetworkHelper;
import xyz.garywzh.loveleak.ui.VideoActivity;
import xyz.garywzh.loveleak.ui.adapter.VideoItemAdapter;
import xyz.garywzh.loveleak.util.ListUtils;
import xyz.garywzh.loveleak.util.LogUtils;

/**
 * Created by garywzh on 2016/9/12.
 */
public class ItemListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, VideoItemAdapter.OnItemActionListener {
    private static final String TAG = ItemListFragment.class.getSimpleName();
    private static final String ARG_TYPE = "type";
    private static final String ARG_SEARCH = "search";

    public static final int TYPE_FEATURED = 0;
    public static final int TYPE_POPULAR = 1;
    public static final int TYPE_RECENT = 2;
    public static final int TYPE_SEARCH = 3;

    private static final int DUPLICATE_CHECK_LENGTH = 3;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private VideoItemAdapter mAdapter;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private boolean onLoading;
    private boolean noMore = false;
    private int mCount;
    private List<VideoItem> mItems;
    private int mType;
    private String mQueryString;
    private boolean firstLoad;
    private Subscription mSubscription;

    public static ItemListFragment newInstance(int type, String queryString) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        if (queryString != null) {
            args.putString(ARG_SEARCH, queryString);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public ItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(ARG_TYPE);
            mQueryString = arguments.getString(ARG_SEARCH);
        }
        mItems = new ArrayList<>();
        mCount = 0;
        firstLoad = true;

        mAdapter = new VideoItemAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_item_list, container, false);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initRecyclerView();
        return mSwipeRefreshLayout;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.recycler_view);

        linearLayoutManager = new LinearLayoutManager(mSwipeRefreshLayout.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !onLoading && !noMore) {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if ((pastItems + visibleItemCount) >= (totalItemCount - 1)) {

                        LogUtils.d(TAG, "scrolled to bottom, loading more");
                        loadData();
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firstLoad) {
            loadData();
        }
    }

    private void loadData() {
        Observable<VideoListBean> observable;
        switch (mType) {
            case ItemListFragment.TYPE_FEATURED:
                observable = NetworkHelper.getApi()
                        .getFeaturedItems(mCount * NetworkHelper.VIDEO_ONCE_LOAD_COUNT, NetworkHelper.VIDEO_ONCE_LOAD_COUNT);
                break;
            case ItemListFragment.TYPE_POPULAR:
                observable = NetworkHelper.getApi()
                        .getPopularItems(mCount * NetworkHelper.VIDEO_ONCE_LOAD_COUNT, NetworkHelper.VIDEO_ONCE_LOAD_COUNT);
                break;
            case ItemListFragment.TYPE_RECENT:
                observable = NetworkHelper.getApi()
                        .getRecentItems(mCount * NetworkHelper.VIDEO_ONCE_LOAD_COUNT, NetworkHelper.VIDEO_ONCE_LOAD_COUNT);
                break;
            case ItemListFragment.TYPE_SEARCH:
                observable = NetworkHelper.getApi()
                        .getItemsBySearch(mQueryString, mCount * NetworkHelper.VIDEO_ONCE_LOAD_COUNT, NetworkHelper.VIDEO_ONCE_LOAD_COUNT);
                break;
            default:
                throw new RuntimeException("error type");
        }
        mSubscription = observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        onLoading = true;
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                        });
                    }
                })
                .map(new Func1<VideoListBean, List<VideoItem>>() {
                    @Override
                    public List<VideoItem> call(VideoListBean videoListBean) {
                        return videoListBean.result;
                    }
                })
                .doOnNext(new Action1<List<VideoItem>>() {
                    @Override
                    public void call(List<VideoItem> videoItems) {
                        if (mCount == 0) {
                            mItems.clear();
                        }
                        if (videoItems.size() > 0) {
                            ListUtils.mergeListWithoutDuplicates(mItems, videoItems, DUPLICATE_CHECK_LENGTH);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VideoItem>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        onLoading = false;
                    }

                    @Override
                    public void onNext(List<VideoItem> items) {
                        if (mCount == 0) {
                            linearLayoutManager.scrollToPositionWithOffset(0, 0);
                        }
                        mAdapter.setDataSource(mItems);
                        firstLoad = false;
                        mCount++;
                        mSwipeRefreshLayout.setRefreshing(false);
                        onLoading = false;
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        mSwipeRefreshLayout.setRefreshing(false);
        onLoading = false;
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onRefresh() {
        if (!onLoading) {
            mCount = 0;
            noMore = false;
            loadData();
        }
    }

    @Override
    public boolean onItemOpen(VideoItem item) {
        Intent intent = new Intent(mContext, VideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", item);
        intent.putExtras(bundle);

        startActivity(intent);
        return false;
    }
}
