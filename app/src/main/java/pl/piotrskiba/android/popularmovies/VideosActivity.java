package pl.piotrskiba.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import pl.piotrskiba.android.popularmovies.AsyncTasks.FetchMovieVideosTask;
import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.Video;
import pl.piotrskiba.android.popularmovies.models.VideoList;

import static pl.piotrskiba.android.popularmovies.Utils.textUtils.getPhoneLanguage;

public class VideosActivity extends AppCompatActivity implements VideoListAdapter.VideoListAdapterOnClickHandler {

    RecyclerView mRecyclerView;
    VideoListAdapter mVideoListAdapter;
    LinearLayoutManager layoutManager;

    private String forcedLanguage = null;

    String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        mRecyclerView = findViewById(R.id.rv_video_list);
        mVideoListAdapter = new VideoListAdapter(this);
        layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mVideoListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(Intent.EXTRA_UID)){
            String movieId = parentIntent.getStringExtra(Intent.EXTRA_UID);
            this.movieId = movieId;
            loadMovieVideos();
        }
    }

    private void loadMovieVideos(){
        new FetchMovieVideosTask(new FetchMovieVideosTaskCompleteListener()).execute(movieId, forcedLanguage);
    }

    @Override
    public void onClick(Video clickedVideo) {
        String url = NetworkUtils.buildYoutubeVideoUrl(clickedVideo.getKey()).toString();
        Uri uri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if(intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    public class FetchMovieVideosTaskCompleteListener implements AsyncTaskCompleteListener<VideoList> {
        @Override
        public void onTaskComplete(VideoList result) {
            // load english videos, when not found in other language
            if(result.getMovies().length == 0 && forcedLanguage == null && getPhoneLanguage() != getString(R.string.default_language)){
                forcedLanguage = getString(R.string.default_language);
                new FetchMovieVideosTask(new FetchMovieVideosTaskCompleteListener()).execute(movieId, forcedLanguage);
            }
            else {
                mVideoListAdapter.setData(result);
            }
        }
    }
}