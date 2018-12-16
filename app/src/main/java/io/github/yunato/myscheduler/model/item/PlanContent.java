package io.github.yunato.myscheduler.model.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlanContent {
    public static final List<PlanItem> ITEMS = new ArrayList<>();
    //private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    static {
        final int COUNT = 24;
        for (int i = 0; i < COUNT; i++) {
            addItem(createPlanItem(i));
        }
    }

    //TODO: public に変更
    private static void addItem(PlanItem item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.calendarId, item);
    }

    public static PlanItem createPlanItem() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN);
        long time = Long.parseLong(sdf.format(date.getTime()));
        return new PlanItem("noNumber", "", "", time, time);
    }

    public static String convertMillToString(long time) {
        StringBuilder builder = new StringBuilder();
        if(time / 100 == 0){
            builder.append("00");
        }else if(time / 100 < 10){
            builder.append("0").append(Long.toString(time/100));
        }else{
            builder.append(Long.toString(time/100));
        }
        builder.append(" : 00");
        return builder.toString();
    }

    //TODO: 本来は引数として日付を受け取り、SQLiteから予定を取得する
    //TODO: このクラスを通さないと PlanItem のインスタンスを作成できない
    private static PlanItem createPlanItem(int index) {
        return new PlanItem(Integer.toString(index),
                "予定名", "予定の内容", index * 100, (index + 1) * 100);
    }


    public static class PlanItem implements Serializable{
        private final String planId;
        private final String title;
        private final String description;
        //public final String colorKey;
        private final long startMillis;
        private final long endMillis;

        /**
         * コンストラクタ
         * @param planId        プランID
         * @param title         予定名
         * @param description   予定の説明
         * @param startMillis   予定の開始時刻
         * @param endMillis     予定の終了時刻
         */
        private PlanItem(String planId, String title,
                         String description, long startMillis, long endMillis) {
            this.planId = planId;
            this.title = title;
            this.description = description;
            this.startMillis = startMillis;
            this.endMillis = endMillis;
        }

        public String getPlanId() {
            return planId;
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
