package testsample.altvr.com.testsample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.util.ItemImageTransformation;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class ItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LogUtil log = new LogUtil(ItemsListAdapter.class);

    private static final int INVALID_DIMEN = -1;

    private final int mImageWidth;
    private List<PhotoVo> mItems;
    private Context mContext;
    private PhotoVo mPhotoVo;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    public ItemsListAdapter(List<PhotoVo> items, int imageWidth, Context context) {
        mItems = items;
        mImageWidth = imageWidth;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.thumbnail, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      	/*
         * YOUR CODE HERE
         *
         * For Part 1a, you should get the proper PhotoVo instance from the mItems collection,
         * image, text, etc, into the ViewHolder (which will be an ItemViewHolder.)
         *
         * For part 1b, you should attach a click listener to the save label so users can save
         * or delete photos from their local db.
         */
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        mPhotoVo = mItems.get(position);

        Bitmap createBitmap = Bitmap.createBitmap(16, 9, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(mContext.getColor(R.color.list_item_background));
        Drawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), createBitmap);

        //Load images from server
        if (isImageSizeGiven()) {
            Picasso.with(mContext)
                    .load(mPhotoVo.webformatURL)
                    .transform(new ItemImageTransformation(mImageWidth))
                    .error(bitmapDrawable)
                    .into(itemViewHolder.thumbnail);
        } else {
            Picasso.with(mContext)
                    .load(mPhotoVo.webformatURL)
                    .error(bitmapDrawable)
                    .into(itemViewHolder.thumbnail);
        }
    }

    private boolean isImageSizeGiven() {
        return mImageWidth != INVALID_DIMEN;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface OnPhotoClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ItemsListAdapter.OnPhotoClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ItemsListAdapter.OnPhotoClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
