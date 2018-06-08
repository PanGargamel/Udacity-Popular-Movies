package pl.piotrskiba.android.popularmovies.models;

public class Video {

    private final String key;
    private final String name;
    private final String site;
    private final String type;

    public Video(String key, String name, String site, String type){
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public String getKey() { return key; }

    public String getName() { return name; }

    public String getSite() { return site; }

    public String getType() { return type; }
}
