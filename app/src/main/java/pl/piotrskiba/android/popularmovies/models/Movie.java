package pl.piotrskiba.android.popularmovies.models;

public class Movie {

    private final String poster_path;
    private final int id;
    private final String title;

    public Movie(String poster_path, int id, String title){
        this.poster_path = poster_path;
        this.id = id;
        this.title = title;
    }

    public String getPosterPath() { return poster_path; }

    public int getId() { return id; }

    public String getTitle() { return title; }
}
