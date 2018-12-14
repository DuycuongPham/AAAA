package com.pham.duycuong.soundcloud.custom.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.source.Genre;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> mCategories;

    public CategoryAdapter() {
        mCategories = new ArrayList<>();
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bindView(mCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategories == null ? 0 : mCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewCategory;
        private ImageView mImageCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mTextViewCategory = itemView.findViewById(R.id.text_view_category);
            mImageCategory = itemView.findViewById(R.id.image_category);
        }

        public void bindView(Category category) {
            if (category == null) {
                return;
            }
            mTextViewCategory.setText(category.getName());

            switch(category.getGenre()){

                case Genre.ALL_MUSIC:
                    mImageCategory.setImageResource(R.drawable.img_all_music);
                    break;

                case Genre.AMBIENT:
                    mImageCategory.setImageResource(R.drawable.img_ambient);
                    break;

                case Genre.CLASSICAL:
                    mImageCategory.setImageResource(R.drawable.img_classical);
                    break;

                case Genre.COUNTRY:
                    mImageCategory.setImageResource(R.drawable.country_music);
                    break;

                case Genre.DANCE_EDM:
                    mImageCategory.setImageResource(R.drawable.img_edm);
                    break;

                case Genre.DEEP_HOUSE:
                    mImageCategory.setImageResource(R.drawable.country_music);
                    break;

                case Genre.DISCO:
                    mImageCategory.setImageResource(R.drawable.img_disco);
                    break;

                case Genre.DRUM_BASS:
                    mImageCategory.setImageResource(R.drawable.img_drum);
                    break;

                case Genre.PIANO:
                    mImageCategory.setImageResource(R.drawable.img_piano);
                    break;

                case Genre.POP:
                    mImageCategory.setImageResource(R.drawable.img_pop);
                    break;

                case Genre.ROCK:
                    mImageCategory.setImageResource(R.drawable.img_rock);
                    break;

            }
//            Picasso.get().load(category.getImageUrl()).fit().centerCrop()
//                    .error(R.drawable.ic_playlist)
//                    .placeholder(R.drawable.ic_playlist)
//                    .into(mImageCategory);
        }
    }
}
