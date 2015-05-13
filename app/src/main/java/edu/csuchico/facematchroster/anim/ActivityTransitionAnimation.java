package edu.csuchico.facematchroster.anim;

import android.annotation.TargetApi;
import android.app.Activity;

import edu.csuchico.facematchroster.R;

public class ActivityTransitionAnimation {

    public enum Direction {LEFT, RIGHT, FADE, UP, DOWN, NONE}

    @TargetApi(5)
    public static void slide(Activity activity, Direction direction) {
        if (direction == Direction.LEFT) {
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        } else if (direction == Direction.RIGHT) {
            activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        } else if (direction == Direction.FADE) {
            activity.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        } else if (direction == Direction.UP) {
            activity.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
        } else if (direction == Direction.DOWN) {
            activity.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
        } else if (direction == Direction.NONE) {
            activity.overridePendingTransition(R.anim.none, R.anim.none);
        }
    }
}
