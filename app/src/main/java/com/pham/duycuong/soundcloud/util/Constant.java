package com.pham.duycuong.soundcloud.util;

public class Constant {

    public static final String PLAY_LIST = "PLAY_LIST";
    public static final String BUNDLE ="BUNDLE";
    public static final String FIREBASE_SONG = "song";
    public static final String FIREBASE_PLAYLIST = "playlist";

    private Constant() {
    }

    public class SoundCloud {

        private SoundCloud() {
        }

        public static final String SOUND_CLOUD_GENRE =
                "https://api.soundcloud.com/tracks?client_id=%s&genre=soundcloud%sgenres%s%s&page=%d";
        public static final String SOUND_CLOUD_SEARCH =
                "https://api.soundcloud.com/tracks?client_id=%s&q=%s";
        public static final String PARAM_CLIENT = "?client_id=";
        public static final String DOT_ENCODE = "%3A";
        public static final String EXTENSION = ".mp4";
        public static final String NULL_VALUE = "null";
    }

    public class JsonProperties {

        private JsonProperties() {
        }

        public static final String KIND = "kind";
        public static final String ID = "id";
        public static final String CREATED_AT = "created_at";
        public static final String DURATION = "duration";
        public static final String STATE = "state";
        public static final String TAG_LIST = "tag_list";
        public static final String DOWNLOADABLE = "downloadable";
        public static final String GENRE = "genre";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String LABEL_NAME = "label_name";
        public static final String STREAM_URL = "stream_url";
        public static final String USER_ID = "id";
        public static final String USER_NAME = "username";
        public static final String AVATAR_URL = "avatar_url";
        public static final String USER = "user";
        public static final String DOWNLOAD_URL = "download_url";
        public static final String ARTWORK_URL = "artwork_url";
    }

    public class TrackEntry {

        private TrackEntry() {
        }

        public static final String TABLE_NAME = "track";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_KIND = "track_kind";
        public static final String COLUMN_CREATED_AT = "track_created_at";
        public static final String COLUMN_DURATION = "track_duration";
        public static final String COLUMN_STATE = "track_state";
        public static final String COLUMN_TAG_LIST = "track_tag_list";
        public static final String COLUMN_DOWNLOADABLE = "track_downloadable";
        public static final String COLUMN_GENRE = "track_genre";
        public static final String COLUMN_TITLE = "track_title";
        public static final String COLUMN_DESCRIPTION = "track_description";
        public static final String COLUMN_LABEL_NAME = "track_label_name";
        public static final String COLUMN_STREAM_URL = "track_stream_url";
        public static final String COLUMN_USER_ID = "track_user_id";
        public static final String COLUMN_USER_NAME = "track_user_name";
        public static final String COLUMN_AVATAR_URL = "track_avatar_url";
        public static final String COLUMN_DOWNLOAD_URL = "track_download_url";
        public static final String COLUMN_ARTWORK_URL = "track_artwork_url";
        public static final String COLUMN_DOWNLOADED = "track_downloaded";
        public static final String COLUMN_LOCAL_PATH = "track_local_path";
    }

    public class PlaylistEntry {

        private PlaylistEntry() {
        }

        public static final String TABLE_NAME = "playlist";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "playlist_name";
    }

    public class TrackPlaylistEntry {

        private TrackPlaylistEntry() {
        }

        public static final String TABLE_NAME = "track_playlist";
        public static final String COLUMN_ID_TRACK = "track_id";
        public static final String COLUMN_ID_PLAYLIST = "playlist_id";
    }

    public class SharedConstant {

        private SharedConstant() {
        }

        public static final String PREF_FILE = "PREF_FILE";
        public static final String PREF_SHUFFLE_MODE = "PREF_SHUFFLE_MODE";
        public static final String PREF_LOOP_MODE = "PREF_LOOP_MODE";
    }

}
