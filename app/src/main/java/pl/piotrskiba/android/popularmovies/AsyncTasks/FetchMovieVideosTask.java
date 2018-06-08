package pl.piotrskiba.android.popularmovies.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;
import pl.piotrskiba.android.popularmovies.models.MovieList;
import pl.piotrskiba.android.popularmovies.models.VideoList;

public class FetchMovieVideosTask extends AsyncTask<String, Void, VideoList> {

    private AsyncTaskCompleteListener<VideoList> listener;

    public FetchMovieVideosTask(AsyncTaskCompleteListener<VideoList> listener){
        this.listener = listener;
    }

    @Override
    protected VideoList doInBackground(String... params) {
        String movieId = params[0];
        String forcedLanguage = params[1];

        URL url = NetworkUtils.buildVideosUrl(movieId, forcedLanguage);

        try {
            String response = NetworkUtils.getHttpResponse(url);

            Gson gson = new Gson();

            return gson.fromJson(response, VideoList.class);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(VideoList videoList) {
        listener.onTaskComplete(videoList);
        super.onPostExecute(videoList);
    }
}
