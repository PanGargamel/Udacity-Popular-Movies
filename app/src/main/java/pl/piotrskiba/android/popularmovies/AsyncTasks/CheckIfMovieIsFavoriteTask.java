package pl.piotrskiba.android.popularmovies.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;

public class CheckIfMovieIsFavoriteTask extends AsyncTask<Integer, Void, List<MovieEntry>>{

    private Context context;
    private AsyncTaskCompleteListener<List<MovieEntry>> listener;

    public CheckIfMovieIsFavoriteTask(Context context, AsyncTaskCompleteListener<List<MovieEntry>> listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<MovieEntry> doInBackground(Integer... ids) {
        int id = ids[0];

        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());

        return db.movieDao().loadMoviesByMovieId(id);
    }

    @Override
    protected void onPostExecute(List<MovieEntry> movieEntries) {
        listener.onTaskComplete(movieEntries);
        super.onPostExecute(movieEntries);
    }
}
