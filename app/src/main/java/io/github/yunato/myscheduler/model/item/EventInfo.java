package io.github.yunato.myscheduler.model.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        if(10000000000000L > time) {
            throw new RuntimeException("argument isn't appropriate   # PlanContent::40");
        }
        time /= 1000000;

        StringBuilder builder = new StringBuilder();
        builder.append(Long.toString(time / 10000));
        builder.append("年");
        time %= 10000;
        builder.append(Long.toString(time / 100));
        builder.append("月");
        time %= 100;
        builder.append(Long.toString(time));
        builder.append("日");
        return builder.toString();
    }

    /**
     * 引数 time から時刻を文字列として取得する
     * @param time 時間(ミリ秒)
     */
    public static String convertTimeToString(long time) {
        if(10000000000000L > time) {
            throw new RuntimeException("argument isn't appropriate   # PlanContent::62");
        }
        time = (time % 1000000) / 100;

        StringBuilder builder = new StringBuilder();
        builder.append(getDoubleDigit(time / 100));
        time %= 100;
        builder.append(":").append(getDoubleDigit(time));
        return builder.toString();
    }

    /**
     * 引数 value から 2桁 の数値を表す文字列を取得する
     * @param value 数値
     */
    private static String getDoubleDigit(long value){
        if(value < 0 || 100 <= value) {
            throw new RuntimeException("argument isn't appropriate");
        }
        if(10 <= value){
            return Long.toString(value);
        }else if(0 < value){
            return "0" + value;
        }else{
            return "00";
        }
    }

    public static EventItem createPlanItem() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN);
        long time = Long.parseLong(sdf.format(date.getTime()));
        return new EventItem("noNumber", "", "", time, time);
    }

    //TODO: 本来は引数として日付を受け取り、SQLiteから予定を取得する
    //TODO: このクラスを通さないと PlanItem のインスタンスを作成できない
    private static EventItem createPlanItem(int index) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
        String startTime, endTime;
        startTime = sdf.format(date.getTime()) + getDoubleDigit(index) + "0000";
        endTime = sdf.format(date.getTime()) + getDoubleDigit(index + 1) + "0000";
        return new EventItem(Integer.toString(index), "予定名", "予定の内容"
                                    , Long.parseLong(startTime), Long.parseLong(endTime));
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
