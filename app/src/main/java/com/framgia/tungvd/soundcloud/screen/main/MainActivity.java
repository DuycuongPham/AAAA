package com.framgia.tungvd.soundcloud.screen.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.custom.adapter.CategoryAdapter;
import com.framgia.tungvd.soundcloud.custom.adapter.EqualSpacingItemDecoration;
import com.framgia.tungvd.soundcloud.custom.adapter.MyPagerAdapter;
import com.framgia.tungvd.soundcloud.custom.adapter.RecyclerItemClickListener;
import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.source.TracksRepository;
import com.framgia.tungvd.soundcloud.data.source.local.MyDBHelper;
import com.framgia.tungvd.soundcloud.data.source.local.TracksLocalDataSource;
import com.framgia.tungvd.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.framgia.tungvd.soundcloud.screen.BaseActivity;
import com.framgia.tungvd.soundcloud.screen.category.CategoryActivity;
import com.framgia.tungvd.soundcloud.screen.download.DownloadActivity;
import com.framgia.tungvd.soundcloud.screen.home.HomeFragment;
import com.framgia.tungvd.soundcloud.screen.personal.PersonalFragment;
import com.framgia.tungvd.soundcloud.screen.playlist.PlaylistActivity;
import com.framgia.tungvd.soundcloud.screen.search.SearchActivity;
import com.framgia.tungvd.soundcloud.util.AppExecutors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MainContract.View {

    private static final int GRID_COLUMN_NUMB = 2;
    private static final int GRID_SPACE = 20;
    private static final int REQUEST_PERMISSION = 1;

    private MainContract.Presenter mMainPresenter;
    private HomeFragment mHomeFragment;
    private PersonalFragment mPersonalFragment;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initMusicService();
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { Manifest.permission.READ_PHONE_STATE }, REQUEST_PERMISSION);
        }
    }

    private void initView() {
        initBaseView();
        mHomeFragment = new HomeFragment();
        mPersonalFragment = new PersonalFragment();
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(mHomeFragment);
        fragments.add(mPersonalFragment);
        ArrayList<String> names = new ArrayList<>();
        names.add(getString(R.string.title_home));
        names.add(getString(R.string.title_personal));

//        TabLayout tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments, names));
//        tabLayout.setupWithViewPager(mViewPager);

        mMainPresenter = new MainPresenter();
        mMainPresenter.setView(this);
        mMainPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search_main:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
