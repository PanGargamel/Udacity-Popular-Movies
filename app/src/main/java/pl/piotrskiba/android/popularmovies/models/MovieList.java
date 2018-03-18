package pl.piotrskiba.android.popularmovies.models;

public class MovieList {
    private final Movie[] results;

    public MovieList(Movie[] results){
        this.results = results;
    }

    public Movie[] getMovies() { return results; }
}
