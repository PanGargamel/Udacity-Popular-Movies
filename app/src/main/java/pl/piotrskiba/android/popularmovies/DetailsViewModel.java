package pl.piotrskiba.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import pl.piotrskiba.android.popularmovies.AsyncTasks.CheckIfMovieIsFavoriteTask;
import pl.piotrskiba.android.popularmovies.AsyncTasks.FetchMovieDetailsTask;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.android.popularmovies.models.DetailedMovie;

public class DetailsViewModel extends ViewModel {

    private final MutableLiveData<DetailedMovie> movie = new MutableLiveData<>();
    private final DetailActivity detailActivity;
    private final String movieId;

    public DetailsViewModel(DetailActivity detailActivity, String movieId){
        this.detailActivity = detailActivity;
        this.movieId = movieId;

        new CheckIfMovieIsFavoriteTask(detailActivity, new CheckIfMovieIsFavoriteTaskCompleteListener()).execute(Integer.valueOf(movieId));
    }

    public LiveData<DetailedMovie> getMovie() {
        return movie;
    }

    public class CheckIfMovieIsFavoriteTaskCompleteListener implements AsyncTaskCompleteListener<List<MovieEntry>> {

        @Override
        public void onTaskComplete(List<MovieEntry> moviesFromDb) {

            if(moviesFromDb.size() > 0){
                detailActivity.isFavorite = true;

                // movie in db, load it
                MovieEntry movieFromDb = moviesFromDb.get(0);
                detailActivity.mMovieEntry = movieFromDb;

                DetailedMovie detailedMovie = new DetailedMovie(movieFromDb.getMovieId(), movieFromDb.getImdbId(), movieFromDb.getOriginalLanguage(),
                        movieFromDb.getOriginalTitle(), movieFromDb.getOverview(), movieFromDb.getPosterPath(), movieFromDb.getReleaseDate(),
                        movieFromDb.getStatus(), movieFromDb.getTitle(), movieFromDb.getVoteAverage());

                movie.setValue(detailedMovie);
            }
            else{
                detailActivity.isFavorite = false;

                // movie not in db, make an API call
                new FetchMovieDetailsTask(new FetchMovieDetailsTaskCompleteListener()).execute(movieId);
            }
        }
    }

    public class FetchMovieDetailsTaskCompleteListener implements AsyncTaskCompleteListener<DetailedMovie> {
        @Override
        public void onTaskComplete(DetailedMovie result) {
            movie.setValue(result);
        }
    }
}
