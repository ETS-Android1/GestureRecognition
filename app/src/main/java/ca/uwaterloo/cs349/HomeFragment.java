package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

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

    private SharedViewModel mViewModel;

    // currGesture
    CustomGesture gesture;

    // widgets
    ImageView gestureOne;
    ImageView gestureTwo;
    ImageView gestureThree;
    TextView gestureOneName;
    TextView gestureTwoName;
    TextView gestureThreeName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        gestureOne = (ImageView) root.findViewById(R.id.gesture_one);
        gestureTwo = (ImageView) root.findViewById(R.id.gesture_two);
        gestureThree = (ImageView) root.findViewById(R.id.gesture_three);

        gestureOneName = (TextView) root.findViewById(R.id.gesture_one_name);
        gestureTwoName = (TextView) root.findViewById(R.id.gesture_two_name);
        gestureThreeName = (TextView) root.findViewById(R.id.gesture_three_name);

        // Relative layout to store root view and draw view to draw
        RelativeLayout relative2 = new RelativeLayout(getActivity());

        // creating draw view to draw
        drawView = new DrawView(getActivity());

        // Adding both views in order of back to front
        relative2.addView(drawView);
        relative2.addView(root);

        // set paint size
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);

        return relative2;
    }

    // check all gestures for first, second, third smallest d_i values using algo in assignment
    public void checkGestures() {
        Pair<CustomGesture, Float> one = new Pair<>(null, Float.MAX_VALUE);
        Pair<CustomGesture, Float> two = new Pair<>(null, Float.MAX_VALUE);
        Pair<CustomGesture, Float> three = new Pair<>(null, Float.MAX_VALUE);

        ArrayList<CustomGesture> gestures = mViewModel.getGestures().getValue();
        for(CustomGesture ges : gestures) {
            float total = 0;
            ArrayList<Point> thisGes = gesture.resamplePoints;
            ArrayList<Point> compGes = ges.resamplePoints;
            for(int i = 0; i < 128; ++i){
                total += Math.sqrt(Math.pow((thisGes.get(i).x - compGes.get(i).x), 2)+ Math.pow((thisGes.get(i).y - compGes.get(i).y), 2));
            }
            float d_i = total / 128;
            if(d_i < one.second) {
                three = two;
                two = one;
                one = new Pair<>(ges, d_i);
            } else if (d_i < two.second) {
                three = two;
                two = new Pair<>(ges, d_i);
            } else if (d_i < three.second) {
                three = new Pair<>(ges, d_i);
            }
        }

        if(one.first != null) {
            CustomGesture g = one.first;
            Bitmap toCopy = Bitmap.createBitmap(g.bitmap);
            gestureOne.setImageBitmap(g.bitmap);
            g.bitmap = toCopy;
            gestureOneName.setText(g.name);
        }

        if(two.first != null) {
            CustomGesture g = two.first;
            Bitmap toCopy = Bitmap.createBitmap(g.bitmap);
            gestureTwo.setImageBitmap(g.bitmap);
            g.bitmap = toCopy;
            gestureTwoName.setText(g.name);
        }

        if(three.first != null) {
            CustomGesture g = three.first;
            Bitmap toCopy = Bitmap.createBitmap(g.bitmap);
            gestureThree.setImageBitmap(g.bitmap);
            g.bitmap = toCopy;
            gestureThreeName.setText(g.name);
        }
    }


    private class DrawView extends View{

        public DrawView(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (startx < 0 || starty < 0) {
                return;
            }
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
                    // everytime user done drawing one stroke gesture, automatically check for similar gestures
                    startx = event.getX();
                    starty = event.getY();
                    invalidate();
                    gesture = new CustomGesture(drawing, null, "");
                    checkGestures();
                    break;
            }
            return true;
        }
    }



}