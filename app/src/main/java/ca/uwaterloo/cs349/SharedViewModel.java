package ca.uwaterloo.cs349;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<ArrayList<CustomGesture>> mGesturesLive;

    public SharedViewModel() {

        mGesturesLive = new MutableLiveData<>(new ArrayList<CustomGesture>());
    }

    public LiveData<ArrayList<CustomGesture>> getGestures() { return mGesturesLive; }

    // to add a gesture
    public boolean addGesture(CustomGesture gesture) {
        ArrayList<CustomGesture> mGestures = mGesturesLive.getValue();
        // check if gesture name already exists
        for(int i = 0; i < mGestures.size(); ++i) {
            if(mGestures.get(i).name.equals(gesture.name)) {
                return false;
            }
        }
        // only add if is unique
        mGesturesLive.getValue().add(gesture);
        return true;
    }
}