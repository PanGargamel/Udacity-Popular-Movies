package pl.piotrskiba.android.popularmovies.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;

import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;

public class DeleteMovieTask extends AsyncTask<MovieEntry, Void, Void>{

    private Context context;
    private AsyncTaskCompleteListener<Void> listener;

    public DeleteMovieTask(Context context, AsyncTaskCompleteListener<Void> listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(MovieEntry... movieEntries) {
        MovieEntry movieEntry = movieEntries[0];

        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        db.movieDao().deleteMovie(movieEntry);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onTaskComplete(aVoid);
        super.onPostExecute(aVoid);
    }
}
