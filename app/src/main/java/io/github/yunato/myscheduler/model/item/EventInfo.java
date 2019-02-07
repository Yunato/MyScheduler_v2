package io.github.yunato.myscheduler.model.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.yunato.myscheduler.model.usecase.EventGenerator;

//TODO: EventInfo を repository パッケージへ移す
//TODO: EventItem を entity パッケージへ移す
public class EventInfo {
    private Calendar targetDay = Calendar.getInstance();
    public static List<EventItem> ITEMS = new ArrayList<>();
    //private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    public Calendar getTargetDay() {
        return targetDay;
    }

    public void setTargetDay(Calendar targetDay) {
        this.targetDay = targetDay;
    }

    /**
     * @param time 時間(ミリ秒)
     */
    public static String convertDateToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPANESE);
        return sdf.format(date);
    }

    /**
     * 引数 time から時刻を文字列として取得する
     *
     * @param time 時間(ミリ秒)
     */
    public static String convertTimeToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.JAPANESE);
        return sdf.format(date);
    }

    //TODO: 本来は引数として日付を受け取り予定一覧を作成する
    public static void createEventList() {
        EventGenerator generator = new EventGenerator();
        ITEMS = generator.generate();
    }

    public static EventItem createEmptyEventItem() {
        Calendar calendar = Calendar.getInstance();
        final long time = calendar.getTimeInMillis();
        return new EventItem("NoNumber", "", "", time, time);
    }

    @Nullable
    public static EventItem createEventItem(
            String eventId,
            String title,
            String description,
            long startMillis,
            long endMillis) {
        //TODO: 不適切なら null を返す (usecaseが担うべきか)
        return new EventItem(eventId, title, description, startMillis, endMillis);
    }

    public static void addItem(EventItem item) {
        //TODO: 日付が本日のものであれば追加を行えるようにEventItem のフィールドを比較する
        ITEMS.add(item);
        //ITEM_MAP.put(item.calendarId, item);
    }

    public static class EventItem implements Parcelable {
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
        private EventItem(String eventId, String title,
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
}
