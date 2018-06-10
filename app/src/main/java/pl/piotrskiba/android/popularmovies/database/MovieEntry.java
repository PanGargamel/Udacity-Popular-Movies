package pl.piotrskiba.android.popularmovies.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private final int movieId;
    private final String imdbId;
    private final String originalLanguage;
    private final String originalTitle;
    private final String overview;
    private final String posterPath;
    private final String releaseDate;
    private final String status;
    private final String title;
    private final double voteAverage;
    private final Date updatedAt;

    @Ignore
    public MovieEntry(int movieId, String imdbId, String originalLanguage, String originalTitle, String overview, String posterPath, String releaseDate, String status, String title, double voteAverage, Date updatedAt){
        this.movieId = movieId;
        this.imdbId = imdbId;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.status = status;
        this.title = title;
        this.voteAverage = voteAverage;
        this.updatedAt = updatedAt;
    }

    public MovieEntry(long id, int movieId, String imdbId, String originalLanguage, String originalTitle, String overview, String posterPath, String releaseDate, String status, String title, double voteAverage, Date updatedAt){
        this.id = id;
        this.movieId = movieId;
        this.imdbId = imdbId;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.status = status;
        this.title = title;
        this.voteAverage = voteAverage;
        this.updatedAt = updatedAt;
    }

    public long getId(){ return id; }

    public int getMovieId(){ return movieId; }

    public String getImdbId() { return imdbId; }

    public String getOriginalLanguage() { return originalLanguage; }

    public String getOriginalTitle() { return originalTitle; }

    public String getOverview() { return overview; }

    public String getPosterPath() { return posterPath; }

    public String getReleaseDate() { return releaseDate; }

    public String getStatus() { return status; }

    public String getTitle() { return title; }

    public double getVoteAverage(){ return voteAverage; }

    public Date getUpdatedAt(){ return updatedAt; }
}
