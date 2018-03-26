package com.framgia.tungvd.soundcloud.data.source;

import android.support.annotation.StringDef;

import static com.framgia.tungvd.soundcloud.data.source.Genre.*;

@StringDef({ALL_MUSIC, AMBIENT, CLASSICAL, COUNTRY, DANCE_EDM, DANCEHALL, DEEP_HOUSE,
        DISCO, DRUM_BASS, DUBSTEP, ELECTRONIC, INDIE, HOUSE, METAL, PIANO, POP, ROCK,
        TRAP, AUDIOBOOKS, COMEDY, LEARNING, SCIENCE, SPORTS, TECHNOLOGY})
public @interface Genre {
    String ALL_MUSIC = "all-music";
    String AMBIENT = "ambient";
    String CLASSICAL = "classical";
    String COUNTRY = "country";
    String DANCE_EDM = "dance-edm";
    String DANCEHALL = "dancehall";
    String DEEP_HOUSE = "deep-house";
    String DISCO = "disco";
    String DRUM_BASS = "drum-bass";
    String DUBSTEP = "dubstep";
    String ELECTRONIC = "electronic";
    String INDIE = "indie";
    String HOUSE = "house";
    String METAL = "metal";
    String PIANO = "piano";
    String POP = "pop";
    String ROCK = "rock";
    String TRAP = "trap";
    String AUDIOBOOKS = "audiobooks";
    String COMEDY = "comedy";
    String LEARNING = "learning";
    String SCIENCE = "science";
    String SPORTS = "sports";
    String TECHNOLOGY = "technology";
}
