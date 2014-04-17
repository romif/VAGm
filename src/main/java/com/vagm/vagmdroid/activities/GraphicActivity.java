package com.vagm.vagmdroid.activities;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

public class GraphicActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = buildIntent();
		startActivity(intent); // шаг 7
	}

	public Intent buildIntent() {
		int[] values = new int[] { 5, 15, 25, 50, 75 }; // шаг 2
		String[] bars = new String[] { "Francesca's", "King of Clubs", "Zen Lounge", "Tied House", "Molly Magees" };
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };

		CategorySeries series = new CategorySeries("Pie Chart"); // шаг 3
		DefaultRenderer dr = new DefaultRenderer(); // шаг 4

		for (int v = 0; v < 5; v++) { // шаг 5
			series.add(bars[v], values[v]);
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[v]);
			dr.addSeriesRenderer(r);
		}
		dr.setZoomButtonsVisible(true);
		dr.setZoomEnabled(true);
		dr.setChartTitleTextSize(20);
		return ChartFactory.getPieChartIntent( // шаг 6
				this, series, dr, "Pie of bars");
	}

}
