package pl.piotrskiba.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.piotrskiba.android.popularmovies.models.Review;
import pl.piotrskiba.android.popularmovies.models.ReviewList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    ReviewList mReviewList;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviewList.getReviews()[position];

        holder.mReviewAuthor.setText(review.getAuthor());
        holder.mReviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(mReviewList == null)
            return 0;
        else
            return mReviewList.getReviews().length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView mReviewAuthor;
        TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            mReviewAuthor = itemView.findViewById(R.id.tv_review_author);
            mReviewContent = itemView.findViewById(R.id.tv_review_content);

        }
    }

    public void setData(ReviewList reviewList){
        mReviewList = reviewList;
        notifyDataSetChanged();
    }
}
