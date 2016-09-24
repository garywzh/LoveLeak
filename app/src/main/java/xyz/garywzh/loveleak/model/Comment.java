package xyz.garywzh.loveleak.model;

/**
 * Created by garywzh on 2016/9/12.
 */
public class Comment {

    /**
     * item_id : 34749851
     * user_name : RealityLeak
     * user_profile_image_url : http://edge.liveleak.com/80281E/s/s/18/media18/2015/Nov/27/ee4c2eb49de9_profile_image_1448676949.png?d5e8cc8eccfb6039332f41f6249e92b06c91b4db65f5e99818bdd0954b4cd8d01ae3&ec_rate=230
     * text : &quot;Hey Cindy? Yeah sorry I hung up on you. So anyway, like, I was saying, he's such a jerk, y'know? Like whatever!&quot;
     * addedon : 2016-9-11 22:04:20
     */

    public String item_id;
    public String user_name;
    public String user_profile_image_url;
    public String text;
    public String addedon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (user_name != null ? !user_name.equals(comment.user_name) : comment.user_name != null)
            return false;
        if (text != null ? !text.equals(comment.text) : comment.text != null) return false;
        return addedon != null ? addedon.equals(comment.addedon) : comment.addedon == null;

    }

    @Override
    public int hashCode() {
        int result = user_name != null ? user_name.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (addedon != null ? addedon.hashCode() : 0);
        return result;
    }
}
