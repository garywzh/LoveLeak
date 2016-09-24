package xyz.garywzh.loveleak.network;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import xyz.garywzh.loveleak.model.CommentListBean;
import xyz.garywzh.loveleak.model.VideoItem;
import xyz.garywzh.loveleak.model.VideoListBean;

/**
 * Created by garywzh on 2016/9/12.
 */
public interface LiveLeakApi {

    @GET("mobile_app_api?a=show_items&featured=1")
    Observable<VideoListBean> getFeaturedItems(@Query("begin") int begin, @Query("limit") int limit);

    @GET("mobile_app_api?a=show_items&popular=1")
    Observable<VideoListBean> getPopularItems(@Query("begin") int begin, @Query("limit") int limit);

    @GET("mobile_app_api?a=show_items")
    Observable<VideoListBean> getRecentItems(@Query("begin") int begin, @Query("limit") int limit);

    @GET("mobile_app_api?a=show_items")
    Observable<VideoListBean> getItemsBySearch(@Query("search") String search, @Query("begin") int begin, @Query("limit") int limit);

    @GET("mobile_app_api?a=show_comments")
    Observable<CommentListBean> getComments(@Query("item_id") String item_id, @Query("from") int from, @Query("limit") int limit);
}
