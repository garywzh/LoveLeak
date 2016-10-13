package xyz.garywzh.loveleak.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by garywzh on 2016/9/12.
 */
public class VideoItem implements Parcelable {

    /**
     * vid : 34749899
     * memberid : 2470706
     * user_name : bigern666
     * addedon : 2016-9-11 22:34:00
     * title : BIGERN666 - 9/11 Wedding
     * description : We got married today
     * tags : 9/11, wedding , bigern666, ERNEST acosta, Janna acosta,
     * category : Other Items from Liveleakers
     * catid : 24
     * published : 1
     * featured : 1
     * times_viewed : 32007
     * number_of_votes : 118
     * number_of_comments : 718
     * thumburl : https://cdn.liveleak.com/80281E/ll_a_u/thumbs/2016/Sep/11/a3d4db4e4f53_thumb_1.jpg
     * videourl : https://cdn.liveleak.com/80281E/ll_a_s/2016/Sep/11/LiveLeak-dot-com-27d_1473647436-trimE9CA57E0-8176-439C-B9D2-B17EF06C1A76_1473647472.mov.h264_270p.mp4?d5e8cc8eccfb6039332f41f6249e92b06c91b4db65f5e99818bdd0954b4cd8d4a4ad&ec_rate=230&.mp4
     */

    public String vid;
    public String memberid;
    public String user_name;
    public String addedon;
    public String title;
    public String description;
    public String tags;
    public String category;
    public String catid;
    public int published;
    public String featured;
    public String times_viewed;
    public String number_of_votes;
    public String number_of_comments;
    public String thumburl;
    public String videourl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoItem videoItem = (VideoItem) o;

        return vid.equals(videoItem.vid);
    }

    @Override
    public int hashCode() {
        return vid.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vid);
        dest.writeString(this.memberid);
        dest.writeString(this.user_name);
        dest.writeString(this.addedon);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.tags);
        dest.writeString(this.category);
        dest.writeString(this.catid);
        dest.writeInt(this.published);
        dest.writeString(this.featured);
        dest.writeString(this.times_viewed);
        dest.writeString(this.number_of_votes);
        dest.writeString(this.number_of_comments);
        dest.writeString(this.thumburl);
        dest.writeString(this.videourl);
    }

    public VideoItem() {
    }

    protected VideoItem(Parcel in) {
        this.vid = in.readString();
        this.memberid = in.readString();
        this.user_name = in.readString();
        this.addedon = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.tags = in.readString();
        this.category = in.readString();
        this.catid = in.readString();
        this.published = in.readInt();
        this.featured = in.readString();
        this.times_viewed = in.readString();
        this.number_of_votes = in.readString();
        this.number_of_comments = in.readString();
        this.thumburl = in.readString();
        this.videourl = in.readString();
    }

    public static final Parcelable.Creator<VideoItem> CREATOR = new Parcelable.Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel source) {
            return new VideoItem(source);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };
}
