package pl.piotrskiba.android.popularmovies.models;

public class ReviewList {
    private final Review[] results;

    public ReviewList(Review[] results){
        this.results = results;
    }

    public Review[] getReviews() { return results; }
}
