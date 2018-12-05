package io.github.yunato.myscheduler.model.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanContent {
    public static final List<PlanItem> ITEMS = new ArrayList<>();
    private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    // item の初期化
    static {
        for (int i = 0; i < COUNT; i++) {
            addItem(createDummyItem(i * 100));
        }
    }

    private static void addItem(PlanItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.calendarId, item);
    }

    private static PlanItem createDummyItem(int time) {
        return new PlanItem(makeStartMill(time), "予定名", "予定の内容");
    }

    private static String makeStartMill(int time) {
        StringBuilder builder = new StringBuilder();
        if(time / 100 == 0){
            builder.append("0");
        }else if(time / 100 < 10){
            builder.append("0").append(Integer.toString(time/100));
        }else{
            builder.append(Integer.toString(time/100));
        }
        builder.append(" : 00");
        return builder.toString();
    }

    public static class PlanItem {
        public final String calendarId;
        public final String title;
        public final String description;
        //public final String colorKey;
        public final long startMillis;
        public final long endMillis;

        public PlanItem(String calendarId, String title, String description) {
            this.calendarId = calendarId;
            this.title = title;
            this.description = description;
            this.startMillis = 0L;
            this.endMillis = 0L;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
