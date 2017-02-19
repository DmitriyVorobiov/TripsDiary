package org.vorobjev.tripsdiary.db;

import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class TripsDao extends AbstractDao {

    public TripsDao(ConnectionSource connectionSource, Class<TripEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<TripEntity> getRecords() throws SQLException {
        return queryBuilder().query();
    }

    public void add(final TripEntity category) throws SQLException {
        callBatchTasks(new Callable<TripEntity>() {
            public TripEntity call() throws Exception {
                createOrUpdate(category);
                return null;
            }
        });
    }

    public List<TripEntity> findRecord(int id) throws SQLException {
        return queryBuilder().where().eq("id", new SelectArg(id)).query();
    }

    public void remove(final TripEntity category) throws SQLException {
        callBatchTasks(new Callable<TripEntity>() {
            public TripEntity call() throws Exception {
                delete(category);
                return null;
            }
        });
    }

    public void update(final TripEntity cats) throws SQLException {
        callBatchTasks(new Callable<TripEntity>() {
            public TripEntity call() throws Exception {
                createOrUpdate(cats);
                return null;
            }
        });
    }
}
