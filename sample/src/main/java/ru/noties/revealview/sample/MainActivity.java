package ru.noties.revealview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.revealview.RevealColorView;
import ru.noties.revealview.RevealOperation;

public class MainActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RevealColorView revealColorView = (RevealColorView) findViewById(R.id.reveal_view);
        revealColorView.setOnTouchListener(new View.OnTouchListener() {

            private boolean hide;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {

                    final RevealOperation operation;
                    final int start;
                    final int end;
                    if (hide) {
                        operation = revealColorView.newHide();
                        start = 0;
                        end = 0x40333333;
                    } else {
                        operation = revealColorView.newReveal();
                        start = 0x80333333;
                        end = 0;
                    }
                    hide = !hide;

                    final int x = (int) (event.getX() + .5F);
                    final int y = (int) (event.getY() + .5F);

                    operation
                            .coordinates(x, y)
                            .duration(750L)
                            .colors(start, end)
                            .start();

                    return true;
                }
                return MotionEvent.ACTION_DOWN == event.getAction();
            }
        });
    }
}
