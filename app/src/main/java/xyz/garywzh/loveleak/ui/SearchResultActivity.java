package xyz.garywzh.loveleak.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import xyz.garywzh.loveleak.R;
import xyz.garywzh.loveleak.ui.fragment.ItemListFragment;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String queryString = getIntent().getStringExtra("search");

        if (queryString != null) {
            setTitle(queryString);
            ItemListFragment fragment = ItemListFragment.newInstance(ItemListFragment.TYPE_SEARCH, queryString);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
