package org.vorobjev.tripsdiary.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.DoubleSummaryStatistics;

/**
 * Created by pc on 24.12.2016.
 */
@DatabaseTable(tableName = "trip", daoClass = TripsDao.class)
public class TripEntity {

    @DatabaseField(columnName = "id", id = false, generatedId = true, allowGeneratedIdInsert = true)
    protected int id;
    @DatabaseField(columnName = "description")
    String description;
    @DatabaseField(columnName = "photo_bytes", dataType = DataType.BYTE_ARRAY)
    byte[] imageBytes;
    @DatabaseField(columnName = "lat_start")
    Double latStart;
    @DatabaseField(columnName = "lon_start")
    Double lonStart;
    @DatabaseField(columnName = "lat_end")
    Double latEnd;
    @DatabaseField(columnName = "lon_end")
    Double lonEnd;

    public Double getLatStart() {
        return latStart;
    }

    public void setLatStart(Double latStart) {
        this.latStart = latStart;
    }

    public Double getLonStart() {
        return lonStart;
    }

    public void setLonStart(Double lonStart) {
        this.lonStart = lonStart;
    }

    public Double getLatEnd() {
        return latEnd;
    }

    public void setLatEnd(Double latEnd) {
        this.latEnd = latEnd;
    }

    public Double getLonEnd() {
        return lonEnd;
    }

    public void setLonEnd(Double lonEnd) {
        this.lonEnd = lonEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
