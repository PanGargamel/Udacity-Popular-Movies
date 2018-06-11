package pl.piotrskiba.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.models.Movie;
import pl.piotrskiba.android.popularmovies.models.MovieList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder> {
    List<Movie> mMovies;
    public int loadedPagesPopular = 0;
    public int loadedPagesTopRated = 0;

    private final MovieListAdapterOnClickHandler clickHandler;

    public interface MovieListAdapterOnClickHandler{
        void onClick(Movie clickedMovie);
    }

    public MovieListAdapter(MovieListAdapterOnClickHandler clickHandler){
        this.clickHandler = clickHandler;
    }

    public class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView mMovieTitle;
        final ImageView mMoviePoster;

        MovieListAdapterViewHolder(View view){
            super(view);
            mMovieTitle = view.findViewById(R.id.tv_title);
            mMoviePoster = view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Movie movie = mMovies.get(pos);
            clickHandler.onClick(movie);
        }
    }

    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);

        return new MovieListAdapterViewHolder(view);
    }



    @Override
    public int getItemCount() {
        if(mMovies == null)
            return 0;
        else
            return mMovies.size();
    }

    @Override
    public void onBindViewHolder(MovieListAdapterViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.mMovieTitle.setText(movie.getTitle());

        String posterUrl = NetworkUtils.buildImageUrl(movie.getPosterPath()).toString();

        Picasso.get()
                .load(posterUrl)
                .error(R.drawable.ic_broken_image_white_80dp)
                .into(holder.mMoviePoster);
    }

    public void clearData(){
        mMovies = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void appendData(MovieList movies, String sorting){
        if(movies != null) {
            List<Movie> newMovies = Arrays.asList(movies.getMovies());
            mMovies.addAll(newMovies);

            if(sorting.equals(NetworkUtils.PATH_POPULAR))
                loadedPagesPopular += 1;
            else if(sorting.equals(NetworkUtils.PATH_TOP_RATED))
                loadedPagesTopRated += 1;

            notifyDataSetChanged();
        }
    }
}
