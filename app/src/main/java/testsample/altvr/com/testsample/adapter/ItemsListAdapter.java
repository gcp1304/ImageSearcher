package testsample.altvr.com.testsample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.util.ItemImageTransformation;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class ItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private LogUtil log = new LogUtil(ItemsListAdapter.class);
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 0;

    private static final int INVALID_DIMEN = -1;

    private final ItemListener mListener;
    private final int mImageWidth;
    private List<PhotoVo> mItems;
    private Context mContext;
    private DatabaseUtil mDbUtil;
    private PhotoVo mPhotoVo;

    @Override
    public void onClick(View view) {

        // When the text was save
        if (((TextView) view).getText().equals(mContext.getResources().getString(R.string.save))) {
            mDbUtil.insert(mPhotoVo);
            ((TextView) view).setText(mContext.getResources().getString(R.string.unsave));
            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_LONG).show();
            return;
        }

        // When the text was unsave
        if (((TextView) view).getText().equals(mContext.getResources().getString(R.string.unsave))) {
        // This function should never be called when the image is not present in DB hence no need to check for photo presence in device
            //if (mDbUtil.exists(mPhotoVo.id)) {
                mDbUtil.delete(mPhotoVo.id);
                Toast.makeText(mContext, R.string.unsaved, Toast.LENGTH_LONG).show();
                ((TextView) view).setText(mContext.getResources().getString(R.string.save));
                return;
            //}
        }
    }

    public interface ItemListener {
        void itemClicked(ItemViewHolder rowView, int position);
    }

    public ItemsListAdapter(List<PhotoVo> items, ItemListener listener, int imageWidth, Context context) {
        mItems = items;
        mListener = listener;
        mImageWidth = imageWidth;
        mContext = context;
        mDbUtil = new DatabaseUtil(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_photos_item, viewGroup, false);
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
        Context context = itemViewHolder.itemView.getContext();

        Bitmap createBitmap = Bitmap.createBitmap(16, 9, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(context.getColor(R.color.list_item_background));
        Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), createBitmap);

        //Load images from server

        if (isImageSizeGiven()) {
            Picasso.with(context)
                    .load(mPhotoVo.webformatURL)
                    .transform(new ItemImageTransformation(mImageWidth))
                    .error(bitmapDrawable)
                    .into(itemViewHolder.itemImage);
        } else {
            Picasso.with(context)
                    .load(mPhotoVo.webformatURL)
                    .error(bitmapDrawable)
                    .into(itemViewHolder.itemImage);
        }


        itemViewHolder.itemName.setText(mContext.getResources().getText(R.string.tags) + mPhotoVo.tags);
        if (mDbUtil.exists(mPhotoVo.id)) {
            itemViewHolder.saveText.setText(mContext.getResources().getString(R.string.unsave));
        } else {
            itemViewHolder.saveText.setText(mContext.getResources().getString(R.string.save));
        }

        itemViewHolder.saveText.setOnClickListener(this);
        //mListener.itemClicked(itemViewHolder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isImageSizeGiven() {
        return mImageWidth != INVALID_DIMEN;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView saveText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            saveText = (TextView) itemView.findViewById(R.id.saveText);
        }
    }
}
