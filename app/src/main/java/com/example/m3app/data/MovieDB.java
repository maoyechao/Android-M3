package com.example.m3app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Movie.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MovieDB extends RoomDatabase {
    public abstract MovieDAO movieDAO();

    private static MovieDB INSTANCE;

    public static synchronized MovieDB getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDB.class, "MovieDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
