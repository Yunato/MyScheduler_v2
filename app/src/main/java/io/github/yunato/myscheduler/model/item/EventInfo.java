package io.github.yunato.myscheduler.model.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventInfo {
    public static final List<EventItem> ITEMS = new ArrayList<>();
    //private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    static {
        final int COUNT = 24;
        for (int i = 0; i < COUNT; i++) {
            addItem(createPlanItem(i));
        }
    }

    //TODO: public に変更
    private static void addItem(EventItem item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.calendarId, item);
    }

    /**
     * 引数 time から日付を文字列として取得する
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
     * @param time 時間(ミリ秒)
     */
    public static String convertTimeToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.JAPANESE);
        return sdf.format(date);
    }

    public static EventItem createPlanItem() {
        Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN);
        //long time = Long.parseLong(sdf.format(date.getTime()));
        final long time = calendar.getTimeInMillis();
        return new EventItem("noNumber", "", "", time, time);
    }

    //TODO: 本来は引数として日付を受け取り、SQLiteから予定を取得する
    //TODO: このクラスを通さないと EventItem のインスタンスを作成できない
    private static EventItem createPlanItem(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, index);
        calendar.set(Calendar.MINUTE, 0);
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
        //String startTime, endTime;
        //startTime = sdf.format(calendar.getTime()) + getDoubleDigit(index) + "0000";
        //endTime = sdf.format(calendar.getTime()) + getDoubleDigit(index + 1) + "0000";
        final long startTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, index + 1);
        final long endTime = calendar.getTimeInMillis();
        return new EventItem(Integer.toString(index), "予定名", "予定の内容", startTime, endTime);
    }

    public static class EventItem implements Serializable{
        private final String eventId;
        private final String title;
        private final String description;
        //public final String colorKey;
        private final long startMillis;
        private final long endMillis;

        /**
         * コンストラクタ
         * @param eventId        プランID
         * @param title         予定名
         * @param description   予定の説明
         * @param startMillis   予定の開始時刻
         * @param endMillis     予定の終了時刻
         */
        private EventItem(String eventId, String title,
                          String description, long startMillis, long endMillis) {
            this.eventId = eventId;
            this.title = title;
            this.description = description;
            this.startMillis = startMillis;
            this.endMillis = endMillis;
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

        //TODO: 扱いやすい数値に変換する必要がある
        public long getStartMillis() {
            return startMillis;
        }

        //TODO: 扱いやすい数値に変換する必要がある
        public long getEndMillis() {
            return endMillis;
        }
    }
}
