package io.github.yunato.myscheduler.model.item;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.yunato.myscheduler.model.repository.EventItemRepository;

import static org.junit.Assert.assertEquals;

public class EventInfoTest {
    @Test
    public void convertDateToString() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN);
        String str = sdf.format(date);
        assertEquals(str, EventItemRepository.convertDateToString(date.getTime()));
    }

    @Test
    public void convertTimeToString() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.JAPAN);
        String str = sdf.format(date);
        assertEquals(str, EventItemRepository.convertTimeToString(date.getTime()));
    }

}
