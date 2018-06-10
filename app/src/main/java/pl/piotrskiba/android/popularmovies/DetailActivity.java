package pl.piotrskiba.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import pl.piotrskiba.android.popularmovies.AsyncTasks.DeleteMovieTask;
import pl.piotrskiba.android.popularmovies.AsyncTasks.InsertMovieTask;
import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;

public class DetailActivity extends AppCompatActivity {

    private LinearLayout mDefaultLayout;
    private LinearLayout mErrorLayout;
    private ProgressBar mLoadingIndicator;

    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mMovieOriginalTitle;
    private TextView mMovieDate;
    private TextView mMovieDescription;
    private TextView mMovieRating;
    private TextView mMovieLanguage;
    private TextView mMovieStatus;

    private DetailedMovie mMovie;
    MovieEntry mMovieEntry;

    Boolean isFavorite = false;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        mDefaultLayout = findViewById(R.id.target_layout);
        mErrorLayout = findViewById(R.id.error_layout);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mMoviePoster = findViewById(R.id.iv_poster);
        mMovieTitle = findViewById(R.id.tv_title);
        mMovieOriginalTitle = findViewById(R.id.tv_original_title);
        mMovieDate = findViewById(R.id.tv_date);
        mMovieDescription = findViewById(R.id.tv_description);
        mMovieRating = findViewById(R.id.tv_rating);
        mMovieLanguage = findViewById(R.id.tv_language);
        mMovieStatus = findViewById(R.id.tv_status);

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(Intent.EXTRA_UID)) {
            String movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);

            DetailsViewModelFactory factory = new DetailsViewModelFactory(this, movieId);
            DetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);

            viewModel.getMovie().observe(this, new Observer<DetailedMovie>() {
                @Override
                public void onChanged(@Nullable DetailedMovie detailedMovie) {
                    if(detailedMovie != null) {
                        populateUi(detailedMovie);
                    }
                    else{
                        showErrorLayout();
                    }
                }
            });
        }
    }

    void onVideosButtonClick(View view){
        Intent intent = new Intent(this, VideosActivity.class);
        intent.putExtra(Intent.EXTRA_UID, String.valueOf(mMovie.getId()));
        startActivity(intent);
    }

    void onReviewsButtonClick(View view){
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra(Intent.EXTRA_UID, String.valueOf(mMovie.getId()));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_detailed, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isFavorite) {
            menu.findItem(R.id.action_favorite).setVisible(false);
            menu.findItem(R.id.action_unfavorite).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_favorite).setVisible(true);
            menu.findItem(R.id.action_unfavorite).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_favorite){
            new InsertMovieTask(this, new InsertMovieTaskCompleteListener()).execute(mMovie);
        }
        else if(item.getItemId() == R.id.action_unfavorite){
            new DeleteMovieTask(this, new DeleteMovieTaskCompleteListener()).execute(mMovieEntry);
        }
        return super.onOptionsItemSelected(item);
    }

    public class InsertMovieTaskCompleteListener implements AsyncTaskCompleteListener<MovieEntry> {
        @Override
        public void onTaskComplete(MovieEntry result) {
            mMovieEntry = result;
            isFavorite = true;
            invalidateOptionsMenu();
            Toast.makeText(context, R.string.marked_as_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    public class DeleteMovieTaskCompleteListener implements AsyncTaskCompleteListener<Void> {
        @Override
        public void onTaskComplete(Void result) {
            isFavorite = false;
            invalidateOptionsMenu();
            Toast.makeText(context, R.string.unmarked_as_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    private void populateUi(DetailedMovie movie){
        mMovie = movie;

        String imageUrl = NetworkUtils.buildImageUrl(movie.getPosterPath()).toString();
        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.ic_broken_image_white_80dp)
                .into(mMoviePoster);

        mMovieTitle.setText(movie.getTitle());
        if (!movie.getTitle().equals(movie.getOriginalTitle())) {
            mMovieOriginalTitle.setText(movie.getOriginalTitle());
        } else {
            if(mMovieOriginalTitle != null) { // wasn't it previously removed?
                ViewGroup parent = (ViewGroup) mMovieOriginalTitle.getParent();
                parent.removeView(mMovieOriginalTitle);
                mMovieOriginalTitle = null;
            }
        }

        if(movie.getOverview().equals(""))
            mMovieDescription.setText(R.string.no_overview_available);
        else
            mMovieDescription.setText(movie.getOverview());

        if(!movie.getReleaseDate().equals(""))
            mMovieDate.setText(movie.getReleaseDate());
        else{
            if(mMovieDate != null) {
                ViewGroup parent = (ViewGroup) mMovieDate.getParent();
                parent.removeView(mMovieDate);
                mMovieDate = null;
            }
        }

        /*
            Convert 2-letter language code to language
            Inspiration: http://www.java2s.com/Code/Java/I18N/Getalistofcountrynames.htm
        */
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String langCode = locale.getLanguage();
            if (langCode.equals(movie.getOriginalLanguage())) {
                mMovieLanguage.setText(locale.getDisplayLanguage());
                break;
            }
        }
        if(mMovieLanguage.getText().equals("")){
            if(mMovieLanguage != null) {
                ViewGroup parent = (ViewGroup) mMovieLanguage.getParent();
                parent.removeView(mMovieLanguage);
                mMovieLanguage = null;
            }
        }

        String ratingText = getString(R.string.rating, movie.getVoteAverage());
        mMovieRating.setText(ratingText);

        if(movie.getVoteAverage() > 0) {
            if (movie.getVoteAverage() < 4) {
                mMovieRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_bad, 0, 0, 0);
            } else if (movie.getVoteAverage() >= 7) {
                mMovieRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_good, 0, 0, 0);
            } else {
                mMovieRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_neutral, 0, 0, 0);
            }
        }
        else{
            if(mMovieRating != null) {
                ViewGroup parent = (ViewGroup) mMovieRating.getParent();
                parent.removeView(mMovieRating);
                mMovieRating = null;
            }
        }

        if(movie.getStatus().equals(getResources().getString(R.string.api_state_rumored)))
            mMovieStatus.setText(R.string.state_rumored);
        else if(movie.getStatus().equals(getResources().getString(R.string.api_state_planned)))
            mMovieStatus.setText(R.string.state_planned);
        else if(movie.getStatus().equals(getResources().getString(R.string.api_state_inproduction)))
            mMovieStatus.setText(R.string.state_inproduction);
        else if(movie.getStatus().equals(getResources().getString(R.string.api_state_postproduction)))
            mMovieStatus.setText(R.string.state_postproduction);
        else if(movie.getStatus().equals(getResources().getString(R.string.api_state_canceled)))
            mMovieStatus.setText(R.string.state_canceled);
        else{
            // released
            if(mMovieStatus != null){
                ViewGroup parent = (ViewGroup) mMovieStatus.getParent();
                parent.removeView(mMovieStatus);
                mMovieStatus = null;
            }
        }

        showDefaultLayout();
    }

    private void showDefaultLayout(){
        mDefaultLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showErrorLayout(){
        mDefaultLayout.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }
}
