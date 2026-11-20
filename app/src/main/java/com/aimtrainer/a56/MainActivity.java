
package com.aimtrainer.a56;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.Random;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(14, 20, 32));

        TextView title = new TextView(this);
        title.setText("AIM TRAINER A56 - v15.0\nToca los objetivos para entrenar");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setPadding(20, 22, 12, 14);
        root.addView(title);

        TrainingView view = new TrainingView();
        root.addView(view,
            new LinearLayout.LayoutParams(-1, 0, 1));

        TextView score = new TextView(this);
        score.setTextColor(Color.WHITE);
        score.setTextSize(16);
        score.setPadding(20, 12, 12, 18);
        root.addView(score);

        view.score = score;
        view.updateScore();
        setContentView(root);
    }

    class TrainingView extends View {
        Paint p = new Paint(3);
        Random rand = new Random();

        float[] x = new float[4];
        float[] y = new float[4];
        float[] vx = new float[4];
        float[] vy = new float[4];

        int hits = 0, shots = 0, heads = 0;
        long last = System.nanoTime();
        TextView score;

        TrainingView() {
            super(MainActivity.this);

            for (int i = 0; i < 4; i++) {
                x[i] = .2f + i * .19f;
                y[i] = .2f + (i % 2) * .36f;
                vx[i] = (.09f + i * .018f)
                    * (i % 2 == 0 ? 1 : -1);
                vy[i] = (.07f + i * .01f)
                    * (i % 2 == 0 ? -1 : 1);
            }
        }

        void updateScore() {
            if (score != null) {
                int accuracy = shots == 0
                    ? 0 : hits * 100 / shots;

                score.setText(
                    "Disparos: " + shots +
                    "   Aciertos: " + hits +
                    "   Cabeza: " + heads +
                    "   Precision: " + accuracy + "%"
                );
            }
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            c.drawColor(Color.rgb(17, 27, 42));

            long now = System.nanoTime();
            float dt = Math.min(
                .04f, (now - last) / 1e9f);
            last = now;

            float w = getWidth();
            float h = getHeight();
            float r = Math.min(w, h) * .065f;

            for (int i = 0; i < 4; i++) {
                x[i] += vx[i] * dt;
                y[i] += vy[i] * dt;

                float margin = r / w;
                if (x[i] < margin || x[i] > 1 - margin) {
                    vx[i] = -vx[i];
                    x[i] = Math.max(margin,
                        Math.min(1 - margin, x[i]));
                }

                margin = r / h;
                if (y[i] < margin || y[i] > 1 - margin) {
                    vy[i] = -vy[i];
                    y[i] = Math.max(margin,
                        Math.min(1 - margin, y[i]));
                }

                float cx = x[i] * w;
                float cy = y[i] * h;

                p.setColor(Color.rgb(244, 85, 95));
                c.drawCircle(cx, cy, r, p);

                p.setColor(Color.rgb(255, 206, 74));
                c.drawCircle(cx, cy - r * .48f,
                    r * .34f, p);

                p.setColor(Color.WHITE);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(3);
                c.drawCircle(cx, cy, r, p);
                p.setStyle(Paint.Style.FILL);
            }

            postInvalidateDelayed(16);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() != MotionEvent.ACTION_DOWN)
                return true;

            shots++;

            float tx = e.getX();
            float ty = e.getY();
            float r = Math.min(
                getWidth(), getHeight()) * .065f;

            for (int i = 0; i < 4; i++) {
                float cx = x[i] * getWidth();
                float cy = y[i] * getHeight();

                if (Math.hypot(tx - cx,
                    ty - (cy - r * .48f)) <= r * .34f) {
                    heads++;
                    hits++;
                    relocate(i);
                    break;
                }

                if (Math.hypot(tx - cx, ty - cy) <= r) {
                    hits++;
                    relocate(i);
                    break;
                }
            }

            updateScore();
            invalidate();
            return true;
        }

        void relocate(int i) {
            x[i] = .16f + rand.nextFloat() * .68f;
            y[i] = .14f + rand.nextFloat() * .72f;
        }
    }
}
