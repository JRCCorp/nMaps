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
		canvas.drawLine(x, y, (float) (x + radio * Math.sin((double) (-posicion) / 180 * 3.143)),
				(float) (y - radio * Math.cos((double) (-posicion) / 180 * 3.143)), brujula);
		canvas.drawText(String.valueOf(posicion), x, y, brujula);

	}

	public void updateData(float position) {
		this.posicion = position;
		invalidate();
	}

}
