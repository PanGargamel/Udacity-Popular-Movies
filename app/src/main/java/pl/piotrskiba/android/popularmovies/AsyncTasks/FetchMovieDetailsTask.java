package pl.piotrskiba.android.popularmovies.AsyncTasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;

public class FetchMovieDetailsTask extends AsyncTask<String, Void, DetailedMovie> {

    private final AsyncTaskCompleteListener<DetailedMovie> listener;

    public FetchMovieDetailsTask(AsyncTaskCompleteListener<DetailedMovie> listener){
        this.listener = listener;
    }

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
    protected void onPostExecute(DetailedMovie detailedMovie) {
        listener.onTaskComplete(detailedMovie);
        super.onPostExecute(detailedMovie);
    }
}
