package io.github.yunato.myscheduler.model.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanContent {
    public static final List<PlanItem> ITEMS = new ArrayList<>();
    private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 24;

    // item の初期化
    static {
        for (int i = 0; i < COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(PlanItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.calendarId, item);
    }

    private static PlanItem createDummyItem(int index) {
        return new PlanItem(Integer.toString(index), "予定名", "予定の内容", index * 100, (index + 1) * 100);
    }

    public static class PlanItem implements Serializable{
        public final String calendarId;
        public final String title;
        public final String description;
        //public final String colorKey;
        public final long startMillis;
        public final long endMillis;

        public PlanItem(String calendarId, String title, String description, long startMillis, long endMillis) {
            this.calendarId = calendarId;
            this.title = title;
            this.description = description;
            this.startMillis = startMillis;
            this.endMillis = endMillis;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
