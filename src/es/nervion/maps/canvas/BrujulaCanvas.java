package es.nervion.maps.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BrujulaCanvas extends View {

	private Paint brujula;
	private float posicion = 0;

	public BrujulaCanvas(Context context) {
		super(context);
		init();
	}

	private void init() {
		brujula = new Paint();
		brujula.setColor(Color.BLACK);
		brujula.setStrokeWidth(2);
		brujula.setTextSize(22);
		brujula.setStyle(Paint.Style.STROKE);
		brujula.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int x = getMeasuredWidth() / 2;
		int y = getMeasuredHeight() / 2;

		float radio = (float) (Math.max(x, y) * 0.5);
		canvas.drawCircle(x, y, radio, brujula);
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), brujula);

		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		int r;
		if(w > h){
			r = h/2;
		}else{
			r = w/2;
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);

		canvas.drawCircle(w/2, h/2, r, paint);

		paint.setColor(Color.RED);
		canvas.drawLine(
				w/2,
				h/2,
				(float)(w/2 + r * Math.sin(-posicion)),
				(float)(h/2 - r * Math.cos(-posicion)),
				paint);

	}

	public void updateData(float position) {
		this.posicion = position;
		invalidate();
	}

}
