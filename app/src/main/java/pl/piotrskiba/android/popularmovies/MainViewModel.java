package pl.piotrskiba.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;
import pl.piotrskiba.android.popularmovies.models.Movie;
import pl.piotrskiba.android.popularmovies.models.MovieList;

public class MainViewModel extends AndroidViewModel {

    private final LiveData<List<MovieEntry>> favoriteMovies;
    private final MutableLiveData<MovieList> popularMovies = new MutableLiveData<>();
    private final MutableLiveData<MovieList> topRatedMovies = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase mDb = AppDatabase.getInstance(getApplication());
        favoriteMovies = mDb.movieDao().loadAllMovies();
    }

    public void appendPopularMovies(MovieList newMovieList){
        Movie[] newMovies = newMovieList.getMovies();

        if(popularMovies.getValue() == null){
            popularMovies.setValue(new MovieList(newMovies));
        }
        else {
            Movie[] oldMovies = popularMovies.getValue().getMovies();

            Movie[] allMovies = Arrays.copyOf(oldMovies, oldMovies.length + newMovies.length);
            System.arraycopy(newMovies, 0, allMovies, oldMovies.length, newMovies.length);

            popularMovies.setValue(new MovieList(allMovies));
        }
    }

    public void appendTopRatedMovies(MovieList newMovieList){
        Movie[] newMovies = newMovieList.getMovies();

        if(topRatedMovies.getValue() == null){
            topRatedMovies.setValue(new MovieList(newMovies));
        }
        else {
            Movie[] oldMovies = topRatedMovies.getValue().getMovies();

            Movie[] allMovies = Arrays.copyOf(oldMovies, oldMovies.length + newMovies.length);
            System.arraycopy(newMovies, 0, allMovies, oldMovies.length, newMovies.length);

            topRatedMovies.setValue(new MovieList(allMovies));
        }
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return favoriteMovies;
    }
    public MutableLiveData<MovieList> getPopularMovies() {
        return popularMovies;
    }
    public MutableLiveData<MovieList> getTopRatedMovies() {
        return topRatedMovies;
    }
}
