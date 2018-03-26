package com.framgia.tungvd.soundcloud.screen.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.framgia.tungvd.soundcloud.custom.adapter.RecyclerItemClickListener;
import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.source.TracksRepository;
import com.framgia.tungvd.soundcloud.data.source.local.MyDBHelper;
import com.framgia.tungvd.soundcloud.data.source.local.TracksLocalDataSource;
import com.framgia.tungvd.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.framgia.tungvd.soundcloud.screen.BaseActivity;
import com.framgia.tungvd.soundcloud.screen.category.CategoryActivity;
import com.framgia.tungvd.soundcloud.screen.download.DownloadActivity;
import com.framgia.tungvd.soundcloud.screen.playlist.PlaylistActivity;
import com.framgia.tungvd.soundcloud.screen.search.SearchActivity;
import com.framgia.tungvd.soundcloud.util.AppExecutors;

import java.util.List;

public class MainActivity extends BaseActivity
        implements MainContract.View, RecyclerItemClickListener.OnItemClickListener {

    private static final int GRID_COLUMN_NUMB = 2;
    private static final int GRID_SPACE = 20;
    private static final int REQUEST_PERMISSION = 1;

    private RecyclerView mRecyclerViewCategories;
    private CategoryAdapter mCategoryAdapter;
    private MainContract.Presenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initMusicService();
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSION);
        }
    }

    private void initView() {
        initBaseView();
        mRecyclerViewCategories = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, GRID_COLUMN_NUMB);
        EqualSpacingItemDecoration itemDecoration = new EqualSpacingItemDecoration(GRID_SPACE);
        mCategoryAdapter = new CategoryAdapter();
        mRecyclerViewCategories.setAdapter(mCategoryAdapter);
        mRecyclerViewCategories.setLayoutManager(layoutManager);
        mRecyclerViewCategories.addItemDecoration(itemDecoration);
        mRecyclerViewCategories.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerViewCategories, this));

        mMainPresenter = new MainPresenter(TracksRepository
                .getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this))));
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
            case R.id.item_download:
                startActivity(new Intent(this, DownloadActivity.class));
                break;
            case R.id.item_play_list:
                startActivity(new Intent(this, PlaylistActivity.class));
                break;
            case R.id.item_search_main:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showCategories(List<Category> categories) {
        mCategoryAdapter.setCategories(categories);
    }

    @Override
    public void showImageCategory(int position, String imageUrl) {
        mCategoryAdapter.getCategories().get(position).setImageUrl(imageUrl);
        mCategoryAdapter.notifyItemChanged(position);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY,
                mCategoryAdapter.getCategories().get(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

}
