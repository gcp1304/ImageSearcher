package testsample.altvr.com.testsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import testsample.altvr.com.testsample.Constants;
import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.adapter.ItemsListAdapter;
import testsample.altvr.com.testsample.events.SaveOrFavChangeEvent;
import testsample.altvr.com.testsample.helper.EmptyRecyclerView;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class FavoritesFragment extends Fragment{
    private EmptyRecyclerView mRecyclerView;
    private ArrayList<PhotoVo> mFavoritePhotosList;
    private DatabaseUtil mDbUtil;
    private ItemsListAdapter mAdapter;
    private ViewPager mViewPager;


    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    public FavoritesFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_photos, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.favoritePhotosRecyclerView);
        mDbUtil = new DatabaseUtil(getActivity());
        mFavoritePhotosList = (ArrayList<PhotoVo>) mDbUtil.getAllPhotos(Constants.ADD_FAVORITE);
        mViewPager = (ViewPager) getActivity().findViewById(R.id.tabs_viewpager);
        view.findViewById(R.id.favPhotosButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        View emptyView = view.findViewById(R.id.favoritesEmptyView);
        mRecyclerView.setEmptyView(emptyView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViews();
    }

    private void setUpViews() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ItemsListAdapter(mFavoritePhotosList, getResources().getDisplayMetrics().widthPixels, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new ItemsListAdapter.RecyclerTouchListener(
                getActivity(), mRecyclerView, new ItemsListAdapter.OnPhotoClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("photos", mFavoritePhotosList);
                bundle.putInt("position", position);

                SlideShowDialogFragment newFragment = SlideShowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "slideShow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }
        ));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFavoritePhotosList.clear();
        mAdapter = null;
    }

    @Subscribe
    public void onAddFavOrRemoveFavPhotoEvent(SaveOrFavChangeEvent event) {
        if (event.eventType == Constants.ADD_FAVORITE || event.eventType == Constants.REMOVE_FAVORITE) {
            mFavoritePhotosList.clear();
            mFavoritePhotosList.addAll(mDbUtil.getAllPhotos(Constants.ADD_FAVORITE));
            mAdapter.notifyDataSetChanged();
        }
    }
}
