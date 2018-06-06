package pl.piotrskiba.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieDao;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.models.Movie;
import pl.piotrskiba.android.popularmovies.models.MovieList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<MovieEntry>> {

    private LinearLayout mErrorLayout;

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private ProgressBar mProgressBar;

    private GridLayoutManager layoutManager;

    private String currentSorting = NetworkUtils.PATH_POPULAR;
    private final static String SORTING_FAVORITES = "favorites";

    private boolean isLoading = false;

    private AppDatabase mDb;

    private int FAVORITE_MOVIES_LOADER = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorLayout = findViewById(R.id.error_layout);

        mRecyclerView = findViewById(R.id.rv_movie_list);
        mMovieListAdapter = new MovieListAdapter(this);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        mRecyclerView.setAdapter(mMovieListAdapter);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOnScrollListener(onScrollListener);

        mMovieListAdapter.clearData();
        FetchMoviesTaskParams params = new FetchMoviesTaskParams(currentSorting, mMovieListAdapter.loadedPages + 1);
        new FetchMoviesTask().execute(params);

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<MovieEntry>> onCreateLoader(int id, Bundle args) {
        Log.d("loader", "creating new loader");
        return new AsyncTaskLoader<List<MovieEntry>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
                super.onStartLoading();
            }

            @Override
            public List<MovieEntry> loadInBackground() {
                Log.d("loader", "loading data in background");
                return mDb.movieDao().loadAllMovies();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<MovieEntry>> loader, List<MovieEntry> data) {

        Log.d("loader", "load finished");
        List<MovieEntry> favoriteMovies = data;
        Movie[] simpleFavoriteMovies = new Movie[favoriteMovies.size()];

        for(int i = 0; i < favoriteMovies.size(); i++){
            MovieEntry movieEntry = favoriteMovies.get(i);
            Movie movie = new Movie(movieEntry.getPosterPath(), movieEntry.getMovieId(), movieEntry.getTitle());
            simpleFavoriteMovies[i] = movie;
        }

        MovieList movieList = new MovieList(simpleFavoriteMovies);
        mMovieListAdapter.clearData();
        mMovieListAdapter.appendData(movieList);
        Log.d("loader", "loaded " + simpleFavoriteMovies.length + " favorite movies");
    }

    @Override
    public void onLoaderReset(Loader<List<MovieEntry>> loader) {

    }

    public class FetchMoviesTask extends AsyncTask<FetchMoviesTaskParams, Void, MovieList>{
        @Override
        protected void onPreExecute() {
            Log.d("loader", "fetching movies from Internet...");
            super.onPreExecute();
            isLoading = true;
        }

        @Override
        protected MovieList doInBackground(FetchMoviesTaskParams... params) {
            String path = params[0].path;
            int pageToLoad = params[0].page;

            URL url = NetworkUtils.buildUrl(path, pageToLoad);

            try {
                String response = NetworkUtils.getHttpResponse(url);

                Gson gson = new Gson();

                return gson.fromJson(response, MovieList.class);
            }
            catch(IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieList movies) {
            mProgressBar.setVisibility(View.INVISIBLE);
            isLoading = false;

            if(movies != null) {
                mMovieListAdapter.appendData(movies);
                showDefaultLayout();
            }
            else{
                // is there any movie on the screen?
                if(mMovieListAdapter.mMovies.size() == 0){
                    showErrorLayout();
                    // show movie list when the connection is back
                    FetchMoviesTaskParams params = new FetchMoviesTaskParams(currentSorting, mMovieListAdapter.loadedPages + 1);
                    new FetchMoviesTask().execute(params);
                }
                else{
                    showDefaultLayout();
                }
            }
        }
    }

    private static class FetchMoviesTaskParams{
        final String path;
        final int page;

        FetchMoviesTaskParams(String path, int page){
            this.path = path;
            this.page = page;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort_by_popular){
            Log.d("loader", "selected popular");
            mMovieListAdapter.clearData();

            currentSorting = NetworkUtils.PATH_POPULAR;
            FetchMoviesTaskParams params = new FetchMoviesTaskParams(currentSorting, mMovieListAdapter.loadedPages + 1);
            new FetchMoviesTask().execute(params);
            return true;
        }
        else if(item.getItemId() == R.id.action_sort_by_toprated){
            Log.d("loader", "selected top rated");
            mMovieListAdapter.clearData();

            currentSorting = NetworkUtils.PATH_TOP_RATED;
            FetchMoviesTaskParams params = new FetchMoviesTaskParams(currentSorting, mMovieListAdapter.loadedPages + 1);
            new FetchMoviesTask().execute(params);
            return true;
        }
        else if(item.getItemId() == R.id.action_sort_by_favorites){
            Log.d("loader", "selected favorites");
            currentSorting = SORTING_FAVORITES;
            mMovieListAdapter.clearData();

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<MovieEntry>> loader = loaderManager.getLoader(FAVORITE_MOVIES_LOADER);

            if(loader == null){
                loaderManager.initLoader(FAVORITE_MOVIES_LOADER, null, this);
            } else {
                loaderManager.restartLoader(FAVORITE_MOVIES_LOADER, null, this);
            }
            return true;
        }
        else if(item.getItemId() == R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Pagination handling
    Source:
        https://medium.com/@etiennelawlor/pagination-with-recyclerview-1cb7e66a502b
     */
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(currentSorting != SORTING_FAVORITES) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition();

                // load more data before reaching the end of list (10 elements earlier)
                if (firstVisibleItemPos + visibleItemCount >= totalItemCount - 10 && !isLoading) {
                    FetchMoviesTaskParams params = new FetchMoviesTaskParams(currentSorting, mMovieListAdapter.loadedPages + 1);
                    new FetchMoviesTask().execute(params);
                }
            }
        }
    };

    @Override
    public void onClick(Movie clickedMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_UID, String.valueOf(clickedMovie.getId()));
        startActivity(intent);
    }

    private void showErrorLayout(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    private void showDefaultLayout(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
