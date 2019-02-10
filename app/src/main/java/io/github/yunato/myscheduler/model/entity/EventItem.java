package io.github.yunato.myscheduler.model.entity;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventItem implements Parcelable {
    private final String eventId;
    private final String title;
    private final String description;
    //public final String colorKey;
    private final long startMillis;
    private final long endMillis;

    /**
     * コンストラクタ
     *
     * @param eventId     プランID
     * @param title       予定名
     * @param description 予定の説明
     * @param startMillis 予定の開始時刻
     * @param endMillis   予定の終了時刻
     */
    public EventItem(String eventId, String title,
                      String description, long startMillis, long endMillis) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    private EventItem(Parcel in) {
        eventId = in.readString();
        title = in.readString();
        description = in.readString();
        startMillis = in.readLong();
        endMillis = in.readLong();
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public String getStrStartDate(){
        return convertDateToString(startMillis);
    }

    public String getStrStartTime(){
        return convertTimeToString(startMillis);
    }

    public String getStrEndDate(){
        return convertDateToString(endMillis);
    }

    public String getStrEndTime(){
        return convertTimeToString(endMillis);
    }

    private String convertDateToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPANESE);
        return sdf.format(date);
    }

    private String convertTimeToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.JAPANESE);
        return sdf.format(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(startMillis);
        dest.writeLong(endMillis);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new EventItem(source);
        }

        @Override
        public EventItem[] newArray(int size) {
            return new EventItem[size];
        }
    };
}
