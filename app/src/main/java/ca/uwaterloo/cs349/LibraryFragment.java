package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class LibraryFragment extends Fragment {

    private SharedViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);

        // get recy view
        RecyclerView gesturesList = (RecyclerView) root.findViewById(R.id.list_view);

        // instantiate adapter for recycler view with the array list gestures in SVM
        GestureAdapter gestureAdapter = new GestureAdapter(mViewModel.getGestures().getValue());

        // set adapter for recycler view to be custom gesture adapter
        gesturesList.setAdapter(gestureAdapter);

        gesturesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}