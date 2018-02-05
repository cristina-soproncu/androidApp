package mape3.project;

/**
 * Created by crist on 12.06.2017.
 */

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class Swipe implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private Context context;
    public Swipe (Context ctx){
        context = ctx;
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            SwipeRight();
                        } else {
                            SwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        SwipeBottom();
                    } else {
                        SwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void SwipeRight() {
        Toast toast = Toast.makeText(context,"Right",Toast.LENGTH_SHORT);
        toast.show();
    }

    public void SwipeLeft() {
        Toast toast = Toast.makeText(context,"Left",Toast.LENGTH_SHORT);
        toast.show();
    }

    public void SwipeTop() {
        Toast toast = Toast.makeText(context,"Top",Toast.LENGTH_SHORT);
        toast.show();
    }

    public void SwipeBottom() {
        Toast toast = Toast.makeText(context,"Buttom",Toast.LENGTH_SHORT);
        toast.show();
    }
}