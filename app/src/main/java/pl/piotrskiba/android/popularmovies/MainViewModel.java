package pl.piotrskiba.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import pl.piotrskiba.android.popularmovies.database.AppDatabase;
import pl.piotrskiba.android.popularmovies.database.MovieEntry;

class MainViewModel extends AndroidViewModel {

    private final LiveData<List<MovieEntry>> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase mDb = AppDatabase.getInstance(getApplication());
        favoriteMovies = mDb.movieDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
