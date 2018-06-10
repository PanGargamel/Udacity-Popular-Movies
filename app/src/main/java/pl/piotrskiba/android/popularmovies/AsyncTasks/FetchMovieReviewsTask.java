package pl.piotrskiba.android.popularmovies.AsyncTasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.ReviewList;

public class FetchMovieReviewsTask extends AsyncTask<String, Void, ReviewList> {

    private final AsyncTaskCompleteListener<ReviewList> listener;

    public FetchMovieReviewsTask(AsyncTaskCompleteListener<ReviewList> listener){
        this.listener = listener;
    }

    @Override
    protected ReviewList doInBackground(String... params) {
        String movieId = params[0];
        String forcedLanguage = params[1];

        URL url = NetworkUtils.buildReviewsUrl(movieId, forcedLanguage);

        try {
            String response = NetworkUtils.getHttpResponse(url);

            Gson gson = new Gson();

            return gson.fromJson(response, ReviewList.class);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ReviewList reviewList) {
        listener.onTaskComplete(reviewList);
        super.onPostExecute(reviewList);
    }
}
