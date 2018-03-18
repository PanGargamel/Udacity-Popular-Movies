package pl.piotrskiba.android.popularmovies.Utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    // PUT YOUR API KEY HERE
    private final static String API_KEY = "";

    private final static String LANGUAGE = "en-US";

    private final static String BASE_URL = "https://api.themoviedb.org/";
    private final static String BASE_PATH = "/3/movie/";

    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/";
    private final static String BASE_IMAGE_PATH = "/t/p/";
    private final static String BASE_IMAGE_SIZE = "w342";

    public final static String PATH_POPULAR = "popular";
    public final static String PATH_TOP_RATED = "top_rated";

    private final static String PARAM_APIKEY = "api_key";
    private final static String PARAM_LANGUAGE = "language";
    private final static String PARAM_PAGE = "page";


    public static URL buildUrl(String path, int page){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .path(BASE_PATH + path)
                .appendQueryParameter(PARAM_APIKEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL builtUrl = null;
        try{
            builtUrl = new URL(uri.toString());
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }

        return builtUrl;
    }

    public static URL buildDetailsUrl(String id){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .path(BASE_PATH + id)
                .appendQueryParameter(PARAM_APIKEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE)
                .build();

        URL builtUrl = null;
        try{
            builtUrl = new URL(uri.toString());
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }

        return builtUrl;
    }

    public static URL buildImageUrl(String imagePath){
        Uri uri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                .path(BASE_IMAGE_PATH + BASE_IMAGE_SIZE + imagePath)
                .build();

        URL builtUrl = null;
        try{
            builtUrl = new URL(uri.toString());
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }

        return builtUrl;
    }

    public static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream icStream = connection.getInputStream();

            Scanner scanner = new Scanner(icStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally{
            connection.disconnect();
        }
    }
}
