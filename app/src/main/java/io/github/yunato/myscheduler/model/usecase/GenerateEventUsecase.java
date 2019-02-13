package io.github.yunato.myscheduler.model.usecase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.model.repository.EventItemRepository;

import static java.util.Calendar.getInstance;

public class GenerateEventUseCase {

    private final Calendar calendar;

    public GenerateEventUseCase(Calendar calendar) {
        this.calendar = calendar;
    }

    // TODO: 自動生成における各工程のメソッドを追加
    // TODO: calendar のフィールドを基にリストを作成する
    public List<EventItem> generate() {
        List<EventItem> newList = new ArrayList<>();

        Calendar calendar = getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth - 3);

        final int HOUR = 24;
        int count = 0;
        for (int index = dayOfMonth - 3; index < dayOfMonth + 3; index++) {
            for (int i = 0; i < HOUR; i++) {
                String name = "テスト" + count;
                long start = calendar.getTimeInMillis();
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                long end = calendar.getTimeInMillis();
                newList.add(EventItemRepository.create("NO_ID", name, name, start, end));
                count++;
            }
        }
        return newList;
    }
}
