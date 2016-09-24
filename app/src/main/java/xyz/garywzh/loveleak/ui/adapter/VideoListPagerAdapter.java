package xyz.garywzh.loveleak.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xyz.garywzh.loveleak.ui.fragment.ItemListFragment;

/**
 * Created by garywzh on 2016/9/15.
 */
public class VideoListPagerAdapter extends FragmentPagerAdapter {
    private String[] tabTitles = new String[]{"Featured", "Popular", "Recent"};

    public VideoListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ItemListFragment.newInstance(ItemListFragment.TYPE_FEATURED, null);
            case 1:
                return ItemListFragment.newInstance(ItemListFragment.TYPE_POPULAR, null);
            case 2:
                return ItemListFragment.newInstance(ItemListFragment.TYPE_RECENT, null);
            default:
                throw new RuntimeException("wrong position");
        }
    }

}
