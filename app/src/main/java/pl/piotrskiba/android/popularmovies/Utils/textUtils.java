package pl.piotrskiba.android.popularmovies.Utils;

import java.util.Locale;

public class textUtils {
    public static String getPhoneLanguage(){
        if(Locale.getDefault().getLanguage().equals("pl")){
            return "pl-PL";
        }
        else{
            return "en-US";
        }
    }
}
