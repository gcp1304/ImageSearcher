package testsample.altvr.com.testsample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.util.ItemImageTransformation;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class ItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
        itemViewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        //mListener.itemClicked(itemViewHolder, position);
    }

    /**
     * Showing pop up menu when tapping on 3 dots
     * @param view
     */
    private void showPopupMenu(View view) {
        // Inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo, popup.getMenu());
        Menu menu = popup.getMenu();
        if (mDbUtil.exists(mPhotoVo.id)) {
            menu.findItem(R.id.action_unsave).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(false);
        } else {
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_unsave).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class PopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_save:
                    mDbUtil.insert(mPhotoVo);
                    Toast.makeText(mContext, R.string.saved, Toast.LENGTH_LONG).show();
                    return true;
                case R.id.action_unsave:
                    mDbUtil.delete(mPhotoVo.id);
                    Toast.makeText(mContext, R.string.unsaved, Toast.LENGTH_LONG).show();
                    return true;
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Added to favourites", Toast.LENGTH_LONG).show();
                    return true;
                default:
            }
            return false;
        }
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
        public ImageView itemImage, overflow;
        public TextView itemName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }
}
