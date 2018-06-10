package pl.piotrskiba.android.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp){
        if(timestamp == null)
            return null;
        else
            return new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        if(date == null)
            return null;
        else
            return date.getTime();
    }
}
