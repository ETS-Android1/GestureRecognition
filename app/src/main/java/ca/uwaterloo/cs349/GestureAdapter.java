package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GestureAdapter extends RecyclerView.Adapter<GestureAdapter.ViewHolder> {

    // ViewHolder for each gesture
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ImageButton imageButton;

        public ViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.name);
            this.imageView = view.findViewById(R.id.gesture_image);
            this.imageButton = view.findViewById(R.id.img_button);
        }
    }

    private ArrayList<CustomGesture> gestures;
    ViewHolder viewHolder;

    public GestureAdapter(ArrayList<CustomGesture> gestures) {
        this.gestures = gestures;
    }


    @NonNull
    @Override
    public GestureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View gestureView = inflater.inflate(R.layout.list_item, parent, false);

        viewHolder = new ViewHolder(gestureView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final CustomGesture gesture = gestures.get(position);

        ImageView gestureImage = holder.imageView;
        TextView name = holder.textView;
        ImageButton bttn = holder.imageButton;

        // set click listener for x button and when clicked, remove item from gesture list and recycler view
        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestures.remove(gesture);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, gestures.size());
            }
        });

        // create a copy of bitmap and reassign since it gets recycled
        Bitmap toCopy = Bitmap.createBitmap(gesture.bitmap);
        gestureImage.setImageBitmap(gesture.bitmap);
        gesture.bitmap = toCopy;
        name.setText(gesture.name);
    }


    @Override
    public int getItemCount() {
        return gestures.size();
    }

    public ArrayList<CustomGesture> getGestures(){
        return gestures;
    }
}
