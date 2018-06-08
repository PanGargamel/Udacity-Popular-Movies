package pl.piotrskiba.android.popularmovies.models;

public class VideoList {
    private final Video[] results;

    public VideoList(Video[] results){
        this.results = results;
    }

    public Video[] getMovies() { return results; }
}
