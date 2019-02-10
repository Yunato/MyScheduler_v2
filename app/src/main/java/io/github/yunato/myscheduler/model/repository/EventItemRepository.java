package io.github.yunato.myscheduler.model.repository;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.model.usecase.GenerateEventUsecase;

public class EventItemRepository {
    private Calendar calendar = Calendar.getInstance();
    public static List<EventItem> ITEMS = new ArrayList<>();
    //private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void createList(Calendar calendar) {
        GenerateEventUsecase generator = new GenerateEventUsecase(calendar);
        ITEMS = generator.generate();
    }

    public static EventItem createEmpty() {
        Calendar nowTime = Calendar.getInstance();
        final long time = nowTime.getTimeInMillis();
        return new EventItem("NoNumber", "", "", time, time);
    }

    @Nullable
    public static EventItem create(
            String eventId,
            String title,
            String description,
            long startMillis,
            long endMillis) {
        //TODO: 不適切なら null を返す (このメソッドを呼び出すusecaseが担うべきか)
        return new EventItem(eventId, title, description, startMillis, endMillis);
    }

    public static void add(EventItem item) {
        //TODO: 日付が本日のものであれば追加を行えるようにEventItem のフィールドを比較する
        ITEMS.add(item);
        //ITEM_MAP.put(item.calendarId, item);
    }
}
