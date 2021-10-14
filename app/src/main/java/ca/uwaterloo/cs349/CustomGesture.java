package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class CustomGesture {

    ArrayList<Point> resamplePoints;
    Bitmap bitmap;
    String name;

    public CustomGesture(ArrayList<Point> drawing, Bitmap bitmap, String name) {
        this.bitmap = bitmap;
        this.name = name;

        // resample points
        resamplePoints = resampleDrawing(drawing);

        // Rotating to zero degrees
        // First calculate the centroid
        float totalX = 0;
        float totalY = 0;
        for(Point p : resamplePoints) {
            totalX += p.x;
            totalY += p.y;
        }

        float centroidX = totalX / resamplePoints.size();
        float centroidY = totalY / resamplePoints.size();

        // get first point (reference for rotation)
        Point startPoint = resamplePoints.get(0);

        // calculate x value of new arm of startpoint
        float yDist = Math.abs(startPoint.y - centroidY);
        float xDist = Math.abs(startPoint.x - centroidX);
        float dist = (float) Math.sqrt(((yDist) * (yDist)) + ((xDist) * (xDist)));

        Point newStart = new Point(centroidX + dist, centroidY);

        // calculate angle between arms relative to centroid
        double angle1 = Math.atan2(startPoint.y - centroidY, startPoint.x - centroidX);
        double angle2 = Math.atan2(newStart.y -centroidY, newStart.x - centroidX);
        double toRotate = angle2 - angle1;

        ArrayList<Point> newPoints = new ArrayList<>();

        // apply same rotation to all points in resamplePoints and then replace resamplePoints
        for(Point p : resamplePoints) {
            float oldX = p.x;
            float oldY = p.y;

            float xNew = (float) (centroidX + (oldX - centroidX) * Math.cos(toRotate) - (oldY - centroidY) * Math.sin(toRotate));
            float yNew = (float) (centroidY + (oldX - centroidX) * Math.sin(toRotate) + (oldY - centroidY) * Math.cos(toRotate));

            newPoints.add(new Point(xNew, yNew));
        }

        resamplePoints.clear();
        for(Point p : newPoints) {
            resamplePoints.add(p);
        }

        // Now to scale, find centroid first (should've probably made methods for each of these but i was tired) :(
        totalX = 0;
        totalY = 0;
        for(Point p : resamplePoints) {
            totalX += p.x;
            totalY += p.y;
        }

        centroidX = totalX / resamplePoints.size();
        centroidY = totalY / resamplePoints.size();

        // find max/min values
        Point maxX;
        Point maxY;
        Point minX;
        Point minY;
        minX = resamplePoints.get(0);
        maxX = resamplePoints.get(0);
        maxY = resamplePoints.get(0);
        minY = resamplePoints.get(0);

        for(Point p : resamplePoints) {
            if(p.x < minX.x) {
                minX = p;
            }
            if(p.x > maxX.x) {
                maxX = p;
            }
            if(p.y < minY.y) {
                minY = p;
            }
            if(p.y > maxY.y) {
                maxY = p;
            }
        }

        // get bound size of current draw
        float xBoundLength = maxX.x - minX.x;
        float yBoundLength = maxY.y - minY.y;

        // get rescaled points, apply x and y scale factors to each x and y point
        newPoints.clear();
        for(Point p : resamplePoints) {
            float newX = centroidX + ((p.x - centroidX) * (100 / xBoundLength));
            float newY = centroidY + ((p.y - centroidY) * (100 / yBoundLength));
            newPoints.add(new Point(newX, newY));
        }

        resamplePoints.clear();
        for(Point p : newPoints) {
            resamplePoints.add(p);
        }

        // translation ; define new centroid
        float newCentroidX = 500;
        float newCentroidY = 500;

        newPoints.clear();
        for(Point p : resamplePoints) {
            float newX = newCentroidX + (p.x - centroidX);
            float newY = newCentroidY + (p.y - centroidY);
            newPoints.add(new Point(newX, newY));
        }

        // finally, replace resamplePoints with the fully resampled, rotated, scale, and translated points
        resamplePoints.clear();
        for(Point p : newPoints) {
            resamplePoints.add(p);
        }

    }

    // resample algo
    ArrayList<Point> resampleDrawing(ArrayList<Point> drawing) {
        // new array to return
        ArrayList<Point> toReturn = new ArrayList<>();

        // total length of drawing
        float pathLength = drawing.size() - 1;

        // sampling interval of drawing
        float samplingInterval = pathLength / 128;

        // tracker to get 128 points
        float tracker = samplingInterval;

        while(tracker <= pathLength) {
            float floor = (float) Math.floor(tracker);
            float ceiling = (float) Math.ceil(tracker);

            Point p1 = drawing.get((int) floor);
            Point p2 = drawing.get((int) ceiling);
            float xdist = p2.x - p1.x;
            float ydist = p2.y - p1.y;

            Point toAdd = new Point(p1.x + ((tracker - floor) * xdist), p1.y + ((tracker - floor) * ydist));
            toReturn.add(toAdd);

            tracker += samplingInterval;
        }
        return toReturn;
    }
}
