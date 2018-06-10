package pl.piotrskiba.android.popularmovies.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;

import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;

public class InsertMovieTask extends AsyncTask<DetailedMovie, Void, MovieEntry>{

    private Context context;
    private AsyncTaskCompleteListener<MovieEntry> listener;

    public InsertMovieTask(Context context, AsyncTaskCompleteListener<MovieEntry> listener){
        this.context = context;
        this.listener = listener;
    }


    @Override
    protected MovieEntry doInBackground(DetailedMovie... detailedMovies) {
        DetailedMovie movie = detailedMovies[0];

        Date date = new Date();
        MovieEntry movieEntry = new MovieEntry(movie.getId(), movie.getImdbId(), movie.getOriginalLanguage(),
                movie.getOriginalTitle(), movie.getOverview(), movie.getPosterPath(),
                movie.getReleaseDate(), movie.getStatus(), movie.getTitle(),
                movie.getVoteAverage(), date);

        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        long id = db.movieDao().insertMovie(movieEntry);

        return new MovieEntry(id, movie.getId(), movie.getImdbId(), movie.getOriginalLanguage(),
                movie.getOriginalTitle(), movie.getOverview(), movie.getPosterPath(),
                movie.getReleaseDate(), movie.getStatus(), movie.getTitle(),
                movie.getVoteAverage(), date);
    }

    @Override
    protected void onPostExecute(MovieEntry movieEntry) {
        listener.onTaskComplete(movieEntry);
        super.onPostExecute(movieEntry);
    }
}
