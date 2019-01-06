package com.example.daniel.mvvmexample;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    public void setSound(Sound sound) {
        mSound = sound;
        notifyChange();
        // Or call notifyPropertyChange(BR.title) if only single items change multiple times
    }

    public Sound getSound() {
        return mSound;
    }

    @Bindable
    public String getTitle() {
        return mSound.getName();
    }

    public BeatBox getBeatBox() {
        return mBeatBox;
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }
}
