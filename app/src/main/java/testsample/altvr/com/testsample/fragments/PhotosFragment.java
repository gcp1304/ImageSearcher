package testsample.altvr.com.testsample.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import testsample.altvr.com.testsample.Constants;
import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.activities.MainActivity;
import testsample.altvr.com.testsample.adapter.ItemsListAdapter;
import testsample.altvr.com.testsample.events.ApiErrorEvent;
import testsample.altvr.com.testsample.events.PhotosEvent;
import testsample.altvr.com.testsample.service.ApiService;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class PhotosFragment extends Fragment{
    private LogUtil log = new LogUtil(PhotosFragment.class);
    private LinearLayout fetchingItems;
    private RecyclerView itemsListRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApiService mService;

    private ArrayList<PhotoVo> mItemsData = new ArrayList<>();
    private ItemsListAdapter mListAdapter;
    private DatabaseUtil mDatabaseUtil;

    private OnEventListener mCallback;


    public static PhotosFragment newInstance() {
        return new PhotosFragment();
    }

    public PhotosFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnEventListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = new ApiService(getActivity());
        mDatabaseUtil = new DatabaseUtil(getActivity());
        setupViews();
    }

    private void initViews(View view) {
        fetchingItems = (LinearLayout) view.findViewById(R.id.listEmptyView);
        itemsListRecyclerView = (RecyclerView) view.findViewById(R.id.photosListRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
    }

    private void setupViews() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mService.getDefaultPhotos();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        fetchingItems.setVisibility(View.VISIBLE);
        setupItemsList();
        EventBus.getDefault().register(this);
    }


    private void setupItemsList() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        itemsListRecyclerView.setLayoutManager(mLayoutManager);
        itemsListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mListAdapter = new ItemsListAdapter(mItemsData, getResources().getDisplayMetrics().widthPixels, getActivity());
        itemsListRecyclerView.setAdapter(mListAdapter);

        itemsListRecyclerView.addOnItemTouchListener(new ItemsListAdapter.RecyclerTouchListener(
                getActivity(), itemsListRecyclerView, new ItemsListAdapter.OnPhotoClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("photos", mItemsData);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mService.getDefaultPhotos();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mItemsData.clear();
        mListAdapter = null;
    }

    @Subscribe
    public void onEvent(PhotosEvent event) {
        /**
         * YOUR CODE HERE
         *
         * This will be the event posted via the EventBus when a photo has been fetched for display.
         *
         * For part 1a you should update the data for this fragment (or notify the user no results
         * were found) and redraw the list.
         *
         * For part 2b you should update this to handle the case where the user has saved photos.
         */
        fetchingItems.setVisibility(View.GONE);
        mItemsData.clear();
        if (event.data != null) {
            mItemsData.addAll(event.data);
        } else {
            mCallback.onEventOccurred(getResources().getString(R.string.no_results_found));

        }
        mItemsData.addAll(mDatabaseUtil.getAllPhotos(Constants.ALL));
        mListAdapter.notifyDataSetChanged();

    }

    @Subscribe
    public void onEvent(ApiErrorEvent event) {
        /**
         * YOUR CODE HERE
         *
         * This will be the event posted via the EventBus when an API error has occured.
         *
         * For part 1a you should clear the fragment and notify the user of the error.
         */
        mCallback.onEventOccurred(event.errorDescription);
        if (event.errorDescription.equals(getString(R.string.network_error))) {
            fetchingItems.setVisibility(View.GONE);
            mItemsData.clear();
            mItemsData.addAll(mDatabaseUtil.getAllPhotos(Constants.ALL));
            mListAdapter.notifyDataSetChanged();
        }
    }

    // Container Activity must implement this interface
    public interface OnEventListener {
        public void onEventOccurred(String message);
    }

}
