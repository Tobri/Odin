package com.tobri.first;

import android.content.Context;

/**
 * Created by mkn on 30.01.14.
 */
public interface ISender {
    abstract void callback(String result);
    abstract Context getContext();
}
