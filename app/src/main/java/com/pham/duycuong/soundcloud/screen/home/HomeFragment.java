package com.pham.duycuong.soundcloud.screen.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.RecyclerItemClickListener;
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

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.CategoryAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.EqualSpacingItemDecoration;
import com.pham.duycuong.soundcloud.custom.adapter.RecyclerItemClickListener;
import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.screen.category.CategoryActivity;
import com.pham.duycuong.soundcloud.screen.download.DownloadActivity;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistActivity;
import com.pham.duycuong.soundcloud.screen.search.SearchActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeContract.View, RecyclerItemClickListener.OnItemClickListener {

    private static final int GRID_COLUMN_NUMB = 2;
    private static final int GRID_SPACE = 20;
    private static final int REQUEST_PERMISSION = 1;

    private RecyclerView mRecyclerViewCategories;
    private CategoryAdapter mCategoryAdapter;
    private HomeContract.Presenter mHomePresenter;

    private TextView mTextViewRetry;
    private RelativeLayout mLayoutInternet;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextViewRetry = view.findViewById(R.id.textRetry);
        mLayoutInternet = view.findViewById(R.id.layoutInternet);
        mRecyclerViewCategories = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getActivity(), GRID_COLUMN_NUMB);
        EqualSpacingItemDecoration itemDecoration = new EqualSpacingItemDecoration(GRID_SPACE);
        mCategoryAdapter = new CategoryAdapter();
        mRecyclerViewCategories.setAdapter(mCategoryAdapter);
        mRecyclerViewCategories.setLayoutManager(layoutManager);
        mRecyclerViewCategories.addItemDecoration(itemDecoration);
        mRecyclerViewCategories.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRecyclerViewCategories, this));

        mHomePresenter = new HomePresenter(TracksRepository
                .getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(getActivity()))));

        mHomePresenter.setView(this);
        if(isInternetAvailable()){
            mHomePresenter.getCategories();
        }
        else{
            mLayoutInternet.setVisibility(View.VISIBLE);
        }

        mTextViewRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetAvailable()){
                    mHomePresenter.getCategories();
                }
            }
        });

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
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY,
                mCategoryAdapter.getCategories().get(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
