package testsample.altvr.com.testsample.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class SlideShowDialogFragment extends DialogFragment {
    private LogUtil mLog = new LogUtil(SlideShowDialogFragment.class);
    private ArrayList<PhotoVo> mPhotosList;
    private ViewPager mViewPager;
    private int selectedPosition = 0;
    private TextView mCountTV, mTagsTV, mPhotographerTV;
    private ImageView mOverFlow;
    private PhotosViewPagerAdapter mPagerAdapter;
    private DatabaseUtil mDbUtil;

    public static SlideShowDialogFragment newInstance() {
        return new SlideShowDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_slider, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mCountTV = (TextView) view.findViewById(R.id.count);
        mTagsTV = (TextView) view.findViewById(R.id.tags);
        mOverFlow = (ImageView) view.findViewById(R.id.overflow);
        mOverFlow.setColorFilter(Color.parseColor("#FFFFFF"));
        mPhotographerTV = (TextView) view.findViewById(R.id.photographer);
        mDbUtil = new DatabaseUtil(getActivity());

        mPhotosList = (ArrayList<PhotoVo>) getArguments().getSerializable("photos");
        selectedPosition = getArguments().getInt("position");

        mLog.d("position : " + selectedPosition);
        mLog.d("Number of photos : " + mPhotosList.size());

        mPagerAdapter = new PhotosViewPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerChangeListener);
        setCurrentItem(selectedPosition);
        return view;
    }

    private void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, false);
        displayMetaInfo(position);
    }

    private void displayMetaInfo(int position) {
        mCountTV.setText((position + 1) + " of " + mPhotosList.size());

        final PhotoVo photo = mPhotosList.get(position);
        mTagsTV.setText(getString(R.string.tags) + photo.tags);
        mPhotographerTV.setText(getString(R.string.credits) + photo.user);
        mOverFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, photo);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    private void showPopupMenu(View view, final PhotoVo photoVo) {
        //Inflate Menu
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo, popup.getMenu());
        Menu menu = popup.getMenu();
        if (mDbUtil.exists(photoVo.id)) {
            menu.findItem(R.id.action_unsave).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(false);
        } else {
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_unsave).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        mDbUtil.insert(photoVo);
                        Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.action_unsave:
                        mDbUtil.delete(photoVo.id);
                        Toast.makeText(getActivity(), R.string.unsaved, Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.action_add_favourite:
                        Toast.makeText(getActivity(), "Added to favourites", Toast.LENGTH_LONG).show();
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }

    // View Page Change Listener
    ViewPager.OnPageChangeListener viewPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    // Custom View Pager Adapter
    public class PhotosViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public PhotosViewPagerAdapter() {}

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fullscreen_preview, container, false);

            ImageView photoPreview = (ImageView) view.findViewById(R.id.photo_preview);

            PhotoVo photo = mPhotosList.get(position);

            Bitmap createBitmap = Bitmap.createBitmap(16, 9, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(getContext().getColor(R.color.list_item_background));
            Drawable bitmapDrawable = new BitmapDrawable(getContext().getResources(), createBitmap);

            Picasso.with(getActivity())
                    .load(photo.webformatURL)
                    .error(bitmapDrawable)
                    .into(photoPreview);

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mPhotosList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View)object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
