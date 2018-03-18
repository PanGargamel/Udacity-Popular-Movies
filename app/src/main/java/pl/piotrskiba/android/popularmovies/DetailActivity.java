package pl.piotrskiba.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
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

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(Intent.EXTRA_UID)){
            String movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);

            new FetchMovieDetailsTask().execute(movieId);
        }
    }

    public class FetchMovieDetailsTask extends AsyncTask<String, Void, DetailedMovie>{

        @Override
        protected DetailedMovie doInBackground(String... params) {
            String movieId = params[0];

            URL url = NetworkUtils.buildDetailsUrl(movieId);
            Log.i("url", url.toString());

            try {
                String response = NetworkUtils.getHttpResponse(url);

                Gson gson = new Gson();

                return gson.fromJson(response, DetailedMovie.class);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DetailedMovie movie) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(movie != null) {
                String imageUrl = NetworkUtils.buildImageUrl(movie.getPosterPath()).toString();
                Picasso.get()
                        .load(imageUrl)
                        .error(R.drawable.ic_broken_image_white_80dp)
                        .into(mMoviePoster);

                mMovieTitle.setText(movie.getTitle());
                if (!movie.getTitle().equals(movie.getOriginalTitle())) {
                    mMovieOriginalTitle.setText(movie.getOriginalTitle());
                } else {
                    ViewGroup parent = (ViewGroup) mMovieOriginalTitle.getParent();
                    parent.removeView(mMovieOriginalTitle);
                }

                mMovieDescription.setText(movie.getOverview());
                mMovieDate.setText(movie.getReleaseDate());

            /*
                Convert 2-letter language code to language
                Inspiration: http://www.java2s.com/Code/Java/I18N/Getalistofcountrynames.htm
            */
                Locale[] locales = Locale.getAvailableLocales();
                for (Locale locale : locales) {
                    String langCode = locale.getLanguage();
                    String lang = locale.getDisplayLanguage();
                    if (langCode.equals(movie.getOriginalLanguage())) {
                        mMovieLanguage.setText(lang);
                        break;
                    }
                }


                String ratingText = getString(R.string.rating, movie.getVoteAverage());
                mMovieRating.setText(ratingText);

                if (movie.getVoteAverage() < 4) {
                    mMovieRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_bad, 0, 0, 0);
                } else if (movie.getVoteAverage() >= 7) {
                    mMovieRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_good, 0, 0, 0);
                } else {
                    mMovieRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_neutral, 0, 0, 0);
                }

                showDefaultLayout();
            }
            else{
                showErrorLayout();

                // show movie details when the connection is back
                Intent parentIntent = getIntent();
                if(parentIntent.hasExtra(Intent.EXTRA_UID)){
                    String movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);

                    new FetchMovieDetailsTask().execute(movieId);
                }
            }
        }
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
