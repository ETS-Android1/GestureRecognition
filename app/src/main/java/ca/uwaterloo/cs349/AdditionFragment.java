package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class AdditionFragment extends Fragment {

    // variables to create arraylist of lines
    private float startx = -1;
    private float starty = -1;

    // line
    public ArrayList<Point> drawing = new ArrayList<>();

    // paint
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    // two views in relative
    View root;
    DrawView drawView;

    // widgets
    Button okayButton;
    Button clearButton;
    EditText gestureName;

    // SVM for data
    private SharedViewModel mViewModel;

    // currGesture
    CustomGesture gesture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        root = inflater.inflate(R.layout.fragment_addition, container, false);

        // defining buttons from fragment_addition.xml
        okayButton = root.findViewById(R.id.okay_button);
        clearButton = root.findViewById(R.id.clear_button);
        gestureName = root.findViewById(R.id.gesture_name);

        // Relative layout to store root view and draw view to draw
        RelativeLayout relative = new RelativeLayout(getActivity());

        // creating draw view to draw
        drawView = new DrawView(getActivity());

        // Adding both views in order of back to front
        relative.addView(drawView);
        relative.addView(root);

        // set paint size
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);

        // clicking okay and registering a gesture with given line and name
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input gesture name;
                String toRegister = gestureName.getText().toString();

                // If gesture name is empty, make toast let user know
                if(toRegister.isEmpty()) {
                    Toast.makeText(getActivity(), "Please input gesture name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If no gesture is drawn, make toast let user know
                if(drawing.isEmpty()) {
                    Toast.makeText(getActivity(), "Please draw a gesture.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // bitmap of current drawing
                Bitmap bitmap = Bitmap.createBitmap(drawView.getWidth(), drawView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                drawView.draw(c);

                // create new gesture type
                gesture = new CustomGesture(drawing, bitmap, toRegister);
                if(!mViewModel.addGesture(gesture)) {
                    Toast.makeText(getActivity(), "Gesture name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    // clear board and text after added gesture
                    drawing.clear();
                    drawView.invalidate();
                    gestureName.setText("");
                }
        }
        });

        // clear drawing on click
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear lines and redraw when clicked
                drawing.clear();
                if(drawView != null) drawView.invalidate();
            }
        });

        return relative;
    }



    // A view to draw on
    private class DrawView extends View{

        public DrawView(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (startx < 0 || starty < 0) {
                return;
            }
            // add new point and draw line
            Point toAdd = new Point(startx, starty);
            drawing.add(toAdd);
            Point newPoint = null;
            Point oldPoint;
            for (Point p : drawing) {
                oldPoint = newPoint;
                newPoint = p;

                if (oldPoint != null)
                    canvas.drawLine(oldPoint.x, oldPoint.y, newPoint.x, newPoint.y, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawing.clear();
                    startx = event.getX();
                    starty = event.getY();
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    startx = event.getX();
                    starty = event.getY();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    startx = event.getX();
                    starty = event.getY();
                    invalidate();
                    break;
            }
            return true;
        }

    }



}