package io.spaceport.Touchdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AndroidActivity extends Activity implements View.OnTouchListener {
	
	private TextView lbl_min;
	private TextView lbl_mean;
	private TextView lbl_max;
	private TextView lbl_stdDev;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        
        View view = this.findViewById(R.id.layout);
        this.lbl_min = (TextView) view.findViewById(R.id.lbl_min);
        this.lbl_mean = (TextView) view.findViewById(R.id.lbl_mean);
        this.lbl_max = (TextView) view.findViewById(R.id.lbl_max);
        this.lbl_stdDev = (TextView) view.findViewById(R.id.lbl_stdDev);
        view.setOnTouchListener(this);
    }
    
    // 16k should be enough for anybody...
    private static int BUFSIZ = 1 << 14;
    private long[] touchTimes = new long[BUFSIZ];
    private long[] deltas = new long[BUFSIZ];
    
    private int curTouch;
    private long startTime;
    private boolean started = false;
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_MOVE:
			this.move();
			break;
			
		case MotionEvent.ACTION_UP:
			this.up();
			break;
			
		default:
			// Do nothing
			break;
		}
		
		return true;
	}
	
	private void move() {
        long now = System.currentTimeMillis();
        if (!this.started) {
        	this.curTouch = 0;
        	this.startTime = now;
        	this.started = true;
        	return;
        }
        
        this.touchTimes[this.curTouch] = now - this.startTime;
        ++this.curTouch;
	}
	
	private void up() {
		this.started = false;
		
		if (this.curTouch < 2) {
			return;
		}
		
		int deltaCount = this.curTouch - 1;
		for (int i = 0; i < deltaCount; ++i) {
			this.deltas[i] = this.touchTimes[i + 1] - this.touchTimes[i];
		}
		
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		long sum = 0;
		for (int i = 0; i < deltaCount; ++i) {
			long dt = this.deltas[i];
			min = Math.min(min, dt);
			max = Math.max(max, dt);
			sum += dt;
		}
		
		double mean = sum / deltaCount;
		
		double stdSum = 0;
		for (int i = 0; i < deltaCount; ++i) {
			double x = this.deltas[i] - mean;
			stdSum += x * x;
		}
		
		double stdDev = Math.sqrt(stdSum / deltaCount);
		
		this.lbl_min.setText(Long.toString(min));
		this.lbl_mean.setText(Double.toString(mean));
		this.lbl_max.setText(Long.toString(max));
		this.lbl_stdDev.setText(Double.toString(stdDev));
	}
    
}
