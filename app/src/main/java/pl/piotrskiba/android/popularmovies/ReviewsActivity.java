package pl.piotrskiba.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import pl.piotrskiba.android.popularmovies.AsyncTasks.FetchMovieReviewsTask;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.ReviewList;

import static pl.piotrskiba.android.popularmovies.Utils.textUtils.getPhoneLanguage;

public class ReviewsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private LinearLayout mErrorLayout;
    private TextView mNoDataTextView;

    private ReviewListAdapter mReviewListAdapter;

    private String movieId;

    private String forcedLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        mRecyclerView = findViewById(R.id.rv_review_list);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorLayout = findViewById(R.id.error_layout);
        mNoDataTextView = findViewById(R.id.tv_no_data);

        mReviewListAdapter = new ReviewListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mReviewListAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(Intent.EXTRA_UID)){
            this.movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);
            loadMovieReviews();
        }
    }

    private void loadMovieReviews(){
        new FetchMovieReviewsTask(new FetchMovieReviewsTaskCompleteListener()).execute(movieId, forcedLanguage);
    }

    public class FetchMovieReviewsTaskCompleteListener implements AsyncTaskCompleteListener<ReviewList> {
        @Override
        public void onTaskComplete(ReviewList result) {
            // load english reviews, when not found in other language
            if(result != null) {
                if (result.getReviews().length == 0 && forcedLanguage == null && !getPhoneLanguage().equals(getString(R.string.default_language))) {
                    forcedLanguage = getString(R.string.default_language);
                    new FetchMovieReviewsTask(new FetchMovieReviewsTaskCompleteListener()).execute(movieId, forcedLanguage);
                } else {
                    if(result.getReviews().length == 0){
                        showNoDataLayout();
                    }
                    else {
                        mReviewListAdapter.setData(result);
                        showDefaultLayout();
                    }
                }
            }
            else{
                showErrorLayout();
            }
        }
    }

    private void showDefaultLayout(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoDataTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorLayout(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoDataTextView.setVisibility(View.INVISIBLE);
    }

    private void showNoDataLayout(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoDataTextView.setVisibility(View.VISIBLE);
    }
}
