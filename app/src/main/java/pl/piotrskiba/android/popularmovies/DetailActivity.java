package pl.piotrskiba.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

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
    private MovieEntry mMovieEntry;

    private AppDatabase mDb;

    private int INSERT_MOVIE_LOADER = 21;
    private int CHECK_IF_FAVORITE_LOADER = 22;
    private int DELETE_MOVIE_LOADER = 23;

    private Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());

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

        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(CHECK_IF_FAVORITE_LOADER) == null)
            loaderManager.initLoader(CHECK_IF_FAVORITE_LOADER, null, this);
        else
            loaderManager.restartLoader(CHECK_IF_FAVORITE_LOADER, null, this);
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
            LoaderManager loaderManager = getSupportLoaderManager();

            Loader<Void> loader = loaderManager.getLoader(INSERT_MOVIE_LOADER);

            if(loader == null)
                loaderManager.initLoader(INSERT_MOVIE_LOADER, null, this);
            else
                loaderManager.restartLoader(INSERT_MOVIE_LOADER, null, this);
        }
        else if(item.getItemId() == R.id.action_unfavorite){
            LoaderManager loaderManager = getSupportLoaderManager();

            Loader<Void> loader = loaderManager.getLoader(DELETE_MOVIE_LOADER);

            if(loader == null)
                loaderManager.initLoader(DELETE_MOVIE_LOADER, null, this);
            else
                loaderManager.restartLoader(DELETE_MOVIE_LOADER, null, this);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == CHECK_IF_FAVORITE_LOADER){
            return new AsyncTaskLoader<List<MovieEntry>>(this) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                    super.onStartLoading();
                }

                @Override
                public List<MovieEntry> loadInBackground() {
                    Intent parentIntent = getIntent();
                    if(parentIntent.hasExtra(Intent.EXTRA_UID)){
                        String movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);
                        return mDb.movieDao().loadMoviesByMovieId(Integer.valueOf(movieId));
                    }
                    return null;
                }
            };
        }
        else if(id == INSERT_MOVIE_LOADER) {
            return new AsyncTaskLoader<Void>(this) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                    super.onStartLoading();
                }

                @Override
                public Void loadInBackground() {
                    Date date = new Date();
                    MovieEntry movieEntry = new MovieEntry(mMovie.getId(), mMovie.getOriginalLanguage(),
                            mMovie.getOriginalTitle(), mMovie.getOverview(), mMovie.getPosterPath(),
                            mMovie.getReleaseDate(), mMovie.getStatus(), mMovie.getTitle(),
                            mMovie.getVoteAverage(), date);

                    long id = mDb.movieDao().insertMovie(movieEntry);

                    mMovieEntry = new MovieEntry(id, mMovie.getId(), mMovie.getOriginalLanguage(),
                            mMovie.getOriginalTitle(), mMovie.getOverview(), mMovie.getPosterPath(),
                            mMovie.getReleaseDate(), mMovie.getStatus(), mMovie.getTitle(),
                            mMovie.getVoteAverage(), date);
                    return null;
                }
            };
        }
        else if(id == DELETE_MOVIE_LOADER) {
            return new AsyncTaskLoader<Void>(this) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                    super.onStartLoading();
                }

                @Override
                public Void loadInBackground() {
                    mDb.movieDao().deleteMovie(mMovieEntry);
                    return null;
                }
            };
        }
        else{
            return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        LoaderManager loaderManager = getSupportLoaderManager();

        if(loader == loaderManager.getLoader(CHECK_IF_FAVORITE_LOADER)){
            List<MovieEntry> moviesFromDb = (List<MovieEntry>) data;

            if(moviesFromDb.size() > 0){
                isFavorite = true;

                // movie in db, load it
                MovieEntry movie = moviesFromDb.get(0);
                mMovieEntry = movie;

                DetailedMovie detailedMovie = new DetailedMovie(movie.getMovieId(), movie.getOriginalLanguage(),
                        movie.getOriginalTitle(), movie.getOverview(), movie.getPosterPath(), movie.getReleaseDate(),
                        movie.getStatus(), movie.getTitle(), movie.getVoteAverage());
                populateUi(detailedMovie);
            }
            else{
                isFavorite = false;

                // movie not in db, make an API call
                Intent parentIntent = getIntent();
                if(parentIntent.hasExtra(Intent.EXTRA_UID)){
                    String movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);

                    new FetchMovieDetailsTask().execute(movieId);
                }
            }
        }
        else if(loader == loaderManager.getLoader(INSERT_MOVIE_LOADER)) {
            isFavorite = true;
            invalidateOptionsMenu();
            Toast.makeText(this, R.string.marked_as_favorite, Toast.LENGTH_SHORT).show();
        }
        else if(loader == loaderManager.getLoader(DELETE_MOVIE_LOADER)) {
            isFavorite = false;
            invalidateOptionsMenu();
            Toast.makeText(this, R.string.unmarked_as_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public class FetchMovieDetailsTask extends AsyncTask<String, Void, DetailedMovie>{

        @Override
        protected DetailedMovie doInBackground(String... params) {
            String movieId = params[0];

            URL url = NetworkUtils.buildDetailsUrl(movieId);

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
            populateUi(movie);
        }
    }

    private void populateUi(DetailedMovie movie){
        mMovie = movie;

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
