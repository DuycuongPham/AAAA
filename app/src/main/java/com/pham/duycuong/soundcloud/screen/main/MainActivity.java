package com.pham.duycuong.soundcloud.screen.main;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.MyPagerAdapter;
import com.pham.duycuong.soundcloud.data.model.MusicService;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.screen.InfoAppActivity;
import com.pham.duycuong.soundcloud.screen.home.HomeFragment;
import com.pham.duycuong.soundcloud.screen.personal.PersonalFragment;
import com.pham.duycuong.soundcloud.screen.search.SearchActivity;

import com.pham.duycuong.soundcloud.screen.sync.SyncActivity;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.ArrayList;

import static com.pham.duycuong.soundcloud.util.Constant.RECREATE_SYNC__ACTIVITY;
import static com.pham.duycuong.soundcloud.util.Constant.RESETPADDING_BROADCAST;

public class MainActivity extends BaseActivity
        implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    private static final int GRID_COLUMN_NUMB = 2;
    private static final int GRID_SPACE = 20;
    private static final int REQUEST_PERMISSION = 1;
    public static final int SYNC_REQUEST_CODE = 15;

    private MainContract.Presenter mMainPresenter;
    private HomeFragment mHomeFragment;
    private PersonalFragment mPersonalFragment;
    private ViewPager mViewPager;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()== Constant.RESETPADDING_BROADCAST){
                mViewPager.setPadding(0,0,0, 80);
            }
            else if(intent.getAction() == RECREATE_SYNC__ACTIVITY){
                startActivity(new Intent(MainActivity.this, SyncActivity.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        getSupportActionBar().setTitle(Html.fromHtml(
        //                "<font color='#000000'>" + getString(R.string.title_app_name) + "
        // </font>"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_black));
//        toolbar.setTitle(Html.fromHtml(
//                "<font color='#000000'>" + getString(R.string.title_app_name) + " </font>"));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(RESETPADDING_BROADCAST);
        filter.addAction(RECREATE_SYNC__ACTIVITY);
        registerReceiver(mBroadcastReceiver, filter);

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
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments, names));
        mMainPresenter = new MainPresenter();
        mMainPresenter.setView(this);
        mMainPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
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
            case R.id.item_search:
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

    public MusicService getMusicService() {
        return mMusicService;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.info) {
            startActivity(new Intent(MainActivity.this, InfoAppActivity.class));
        }

        else if(id == R.id.feedback) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Music-Cloud-Vietnam-208638056743399/"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        else if (id == R.id.policy) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://quachaapp.blogspot.com/2017/06/chinh-sach-cua-anh-ta-la-mot-phan-cua.html"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SYNC_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
                startActivity(new Intent(MainActivity.this, SyncActivity.class));
//            }
//        }
    }
}
