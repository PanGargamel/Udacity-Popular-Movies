package pl.piotrskiba.android.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final String mMovieId;
    private final DetailActivity mDetailActivity;

    public DetailsViewModelFactory(DetailActivity detailActivity, String movieId){
        mDetailActivity = detailActivity;
        mMovieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailsViewModel(mDetailActivity, mMovieId);
    }
}
