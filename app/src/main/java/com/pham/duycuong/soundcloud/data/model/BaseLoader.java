package com.pham.duycuong.soundcloud.data.model;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;

public abstract class BaseLoader<Result>  extends AsyncTaskLoader<Result> {

    private Result mResult;
    protected TracksDataSource.LoadTracksCallback mCallback;

    public BaseLoader(Context context){//, TracksDataSource.LoadTracksCallback callback){
        super(context);
//        mCallback = callback;
    }

    @Override
    public void deliverResult(Result result){
        if(isReset()){
            if(result!=null){
                onReleaseResources(result);
            }
        }

        Result oldResult = mResult;
        mResult = result;

        if(oldResult!=null){
            onReleaseResources(oldResult);
        }

        if(isStarted()){
            super.deliverResult(result);
        }
    }

    @Override
    public void onStartLoading(){
        if(mResult!=null){
            deliverResult(mResult);
        }

        if(takeContentChanged() || mResult == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading(){
        cancelLoad();
    }

    @Override
    public void onCanceled(Result result){
        super.onCanceled(result);
        onReleaseResources(result);
    }

    @Override
    protected void onReset(){
        super.onReset();
        onStopLoading();

        if(mResult != null){
            onReleaseResources(mResult);
            mResult = null;
        }

    }

    protected void onReleaseResources(Result result) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}