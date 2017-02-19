package org.vorobjev.tripsdiary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.vorobjev.tripsdiary.R;
import org.vorobjev.tripsdiary.TripsDiaryApplication;
import org.vorobjev.tripsdiary.db.TripEntity;
import org.vorobjev.tripsdiary.db.TripsDao;
import org.vorobjev.tripsdiary.db.TripsDiaryDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by pc on 24.12.2016.
 */
public class StopsActivity extends Activity {

    public static final String RECORD_ID = "item_id";

    @Bind(R.id.histoty_list)
    ListView listView;

    TripsDiaryDatabaseHelper dbHelper;

    @OnItemClick(R.id.histoty_list)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        startActivity(new Intent(RecordsActivity.this, RecordActivity.class).putExtra(RECORD_ID, ((String) parent.getItemAtPosition(position)).split(" ")[1]));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        ButterKnife.bind(this);
        dbHelper = TripsDiaryApplication.getInstance().getDbHelper();
    }

    protected void onResume() {
        super.onResume();
        fillOverallStatisticsView();
    }

    void fillOverallStatisticsView() {
        List<TripEntity> records = null;
        try {
            records = dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).getRecords();
        } catch (SQLException e) {
        }
        ArrayList<String> entries = new ArrayList<String>();
        for (TripEntity recordEntity : records) {
            String type = recordEntity.getLatEnd() == recordEntity.getLatStart() ? "Stop# " : "Trip# ";
            entries.add( "Stops# " + recordEntity.getId());
        }
        String[] items = entries.toArray(new String[entries.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, items);
        listView.setAdapter(adapter);
    }

}
