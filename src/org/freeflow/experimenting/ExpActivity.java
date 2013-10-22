package org.freeflow.experimenting;

import org.freeflow.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class ExpActivity extends Activity {

	DragExperiment experiment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_exp);
		experiment = (DragExperiment) findViewById(R.id.dragExperiment);

		for (int i = 0; i < 20; i++) {
			Button b = new Button(this);
			b.setText("" + i);
			experiment.addView(b);
		}

	}

}
