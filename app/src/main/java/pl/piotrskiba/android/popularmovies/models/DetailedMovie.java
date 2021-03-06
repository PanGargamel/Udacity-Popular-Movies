package pl.piotrskiba.android.popularmovies.models;


public class DetailedMovie {

    private final int id;
    private final String imdb_id;
    private final String original_language;
    private final String original_title;
    private final String overview;
    private final String poster_path;
    private final String release_date;
    private final String status;
    private final String title;
    private final double vote_average;

    public DetailedMovie(int id, String imdb_id, String original_language, String original_title, String overview, String poster_path, String release_date, String status, String title, double vote_average){
        this.id = id;
        this.imdb_id = imdb_id;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.status = status;
        this.title = title;
        this.vote_average = vote_average;
    }

    public int getId(){ return id; }

    public String getImdbId() { return imdb_id; }

    public String getOriginalLanguage() { return original_language; }

    public String getOriginalTitle() { return original_title; }

    public String getOverview() { return overview; }

    public String getPosterPath() { return poster_path; }

    public String getReleaseDate() { return release_date; }

    public String getStatus() { return status; }

    public String getTitle() { return title; }

    public double getVoteAverage(){ return vote_average; }
}
