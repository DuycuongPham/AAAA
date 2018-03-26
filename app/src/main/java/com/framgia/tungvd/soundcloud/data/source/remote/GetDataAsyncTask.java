package com.framgia.tungvd.soundcloud.data.source.remote;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.framgia.tungvd.soundcloud.BuildConfig;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.Genre;
import com.framgia.tungvd.soundcloud.data.source.TracksDataSource.LoadTracksCallback;
import com.framgia.tungvd.soundcloud.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GetDataAsyncTask extends AsyncTask<String, Void, Void> {

    private static final int READ_TIMEOUT = 10000; /*millisecond*/
    private static final int CONNECT_TIMEOUT = 15000; /*millisecond*/
    private static final String ENCODE = "utf-8";

    private LoadTracksCallback mCallback;
    private ArrayList<Track> mTracks;
    private String mGenre;
    private int mPage;
    private String mQuery;

    public GetDataAsyncTask(@Genre String genre, int page,
                            @NonNull LoadTracksCallback callback) {
        mCallback = callback;
        mPage = page;
        mGenre = genre;
        mQuery = "";
    }

    public GetDataAsyncTask(String query, @NonNull LoadTracksCallback callback) {
        mQuery = query;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(@Genre String... strings) {

        String url;
        if (!mQuery.isEmpty()) {
            try {
                String query = URLEncoder.encode(mQuery, ENCODE);
                url = String.format(
                        Constant.SoundCloud.SOUND_CLOUD_SEARCH,
                        BuildConfig.SOUND_CLOUD_KEY, query);
                String data = getJSONStringFromURL(url);
                mTracks = getTracksFromJson(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = String.format(
                        Constant.SoundCloud.SOUND_CLOUD_GENRE,
                        BuildConfig.SOUND_CLOUD_KEY,
                        Constant.SoundCloud.DOT_ENCODE,
                        Constant.SoundCloud.DOT_ENCODE,
                        mGenre,
                        mPage);
                String data = getJSONStringFromURL(url);
                mTracks = getTracksFromJson(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mTracks == null || mTracks.isEmpty()) {
            mCallback.onDataNotAvailable();
        } else {
            mCallback.onTracksLoaded(mTracks);
        }
    }

    private String getJSONStringFromURL(String urlString) throws IOException {

        HttpURLConnection urlConnection;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();

        urlConnection.disconnect();
        return jsonString;
    }

    private ArrayList<Track> getTracksFromJson(String jsonData) {
        ArrayList<Track> tracks = new ArrayList<>();
        int length = 0;
        JSONArray jsa = null;
        try {
            jsa = new JSONArray(jsonData);
            length = jsa.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsa == null || length == 0) {
            return tracks;
        }
        for (int i = 0; i < length; i++) {
            try {
                JSONObject jso = new JSONObject(jsa.getString(i));

                String kind = jso.getString(Constant.JsonProperties.KIND);
                long id = jso.getLong(Constant.JsonProperties.ID);
                String createAt = jso.getString(Constant.JsonProperties.CREATED_AT);
                long duration = jso.getLong(Constant.JsonProperties.DURATION);
                String state = jso.getString(Constant.JsonProperties.STATE);
                String tagList = jso.getString(Constant.JsonProperties.TAG_LIST);
                boolean downloadable = jso.getBoolean(Constant.JsonProperties.DOWNLOADABLE);
                String genre = jso.getString(Constant.JsonProperties.GENRE);
                String title = jso.getString(Constant.JsonProperties.TITLE);
                String description = jso.getString(Constant.JsonProperties.DESCRIPTION);
                String labelName = jso.getString(Constant.JsonProperties.LABEL_NAME);
                String streamUrl = jso.getString(Constant.JsonProperties.STREAM_URL);
                String downloadUrl = "";
                String artworkUrl = jso.getString(Constant.JsonProperties.ARTWORK_URL);
                if (downloadable) {
                    downloadUrl = jso.getString(Constant.JsonProperties.DOWNLOAD_URL);
                }

                JSONObject userJso = jso.getJSONObject(Constant.JsonProperties.USER);

                long userId = userJso.getLong(Constant.JsonProperties.USER_ID);
                String userName = userJso.getString(Constant.JsonProperties.USER_NAME);
                String avatarUrl = userJso.getString(Constant.JsonProperties.AVATAR_URL);

                Track track = new Track.Builder()
                        .kind(kind)
                        .id(id)
                        .createAt(createAt)
                        .duration(duration)
                        .state(state)
                        .tagList(tagList)
                        .downloadable(downloadable)
                        .genre(genre)
                        .title(title)
                        .description(description)
                        .labelName(labelName)
                        .streamUrl(streamUrl)
                        .user(userId, userName, avatarUrl)
                        .downloadUrl(downloadUrl)
                        .artworkUrl(artworkUrl)
                        .build();

                tracks.add(track);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tracks;
    }
}
