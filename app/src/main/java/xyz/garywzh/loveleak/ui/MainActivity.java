package xyz.garywzh.loveleak.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import xyz.garywzh.loveleak.R;
import xyz.garywzh.loveleak.ui.adapter.VideoListPagerAdapter;
import xyz.garywzh.loveleak.ui.widget.SearchBoxLayout;

public class MainActivity extends AppCompatActivity {

    private SearchBoxLayout mSearchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new VideoListPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        initSearchBox();
    }

    private void initSearchBox() {
        mSearchBox = (SearchBoxLayout) findViewById(R.id.search_box);
        mSearchBox.setOnActionListener(new SearchBoxLayout.Listener() {
            @Override
            public void onQueryTextSubmit(final String query) {
                mSearchBox.hide();
                if (query.trim().length() == 0) {
                    return;
                } else {
/*
                    leave some time for closing inputmethod, otherwise there will be half screen
                    being white when returning from the searchresultactivity
*/
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                            intent.putExtra("search", query);
                            startActivity(intent);
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            mSearchBox.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
