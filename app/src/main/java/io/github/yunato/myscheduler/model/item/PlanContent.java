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
        for (int i =1; i < COUNT; i++) {
            addItem(createDummyItem(i * 100));
        }
    }

    private static void addItem(PlanItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.calendarId, item);
    }

    private static PlanItem createDummyItem(int position) {
        return new PlanItem(String.valueOf(position), "予定名", makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
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
