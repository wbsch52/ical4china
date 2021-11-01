package com.simon.ical.service;

import com.simon.ical.commons.GlobalConstant;
import com.simon.ical.commons.IpHolder;
import com.simon.ical.domain.Holiday;
import com.simon.ical.domain.SimpleHoliday;
import com.simon.ical.properties.AppleCalendarColorProperty;
import com.simon.ical.properties.AppleCalendarNameProperty;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Slf4j
@Service
public class ICalendarService {

    private static final String CALENDAR_NAME = "中国法定节假日";
    private static final String CALENDAR_COLOR = "#8DEEEE";
    private static final VTimeZone V_TIME_ZONE;
    private static final TimeZone TIME_ZONE;
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+8");
    private static final RandomUidGenerator RANDOM_UID_GENERATOR = new RandomUidGenerator();

    private static final AtomicInteger count = new AtomicInteger();

    static {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TIME_ZONE = registry.getTimeZone("Asia/Shanghai");
        V_TIME_ZONE = TIME_ZONE.getVTimeZone();
    }

    @Autowired
    private JuheApiService juheApiService;

    public File getIcsFile() {
        File file = new File(GlobalConstant.ICS_FULL_PATH);
        if (!file.exists()) {
            throw new RuntimeException("the ics file doesn't exists");
        }
        int count = ICalendarService.count.incrementAndGet();
        String ip = IpHolder.get();
        log.info("returning ics file to {}, times:{}", ip, count);
        return file;
    }

    public void generateIcsFile() throws IOException {
        File file = new File(GlobalConstant.ICS_FULL_PATH);
        // remove old ics file
        file.deleteOnExit();
        // make cache dir
        File cacheDir = new File(GlobalConstant.ICS_FILE_PATH);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        Year year = Year.now();
        Calendar calendar = buildCalendar(year);
        file.createNewFile();
        @Cleanup FileOutputStream out = new FileOutputStream(file);
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, out);
    }

    public String write(String path) throws IOException {
        if (!path.endsWith("/")) {
            path += "/";
        }
        Year year = Year.now();
        Calendar calendar = buildCalendar(year);
        File file = new File(path + GlobalConstant.ICS_FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        @Cleanup FileOutputStream out = new FileOutputStream(file);
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, out);
        return file.getAbsolutePath();
    }

    private VEvent buildVocationEvent(String name, String desc, String tips, List<Holiday.Item> items) {
        List<Holiday.Item> vacation = items.stream().filter(Holiday.Item::getStatus).sorted().collect(Collectors.toList());
        LocalDate startDate = vacation.get(0).getDate();
        // ical4j的bug输出的时间会比指定时间小，所以以当前时间基础上加1天来覆盖掉这个问题.
        long start = startDate.atStartOfDay().plusDays(1L).withHour(0).withMinute(0).withSecond(0).toInstant(ZONE_OFFSET).toEpochMilli();
        long end = vacation.get(vacation.size() - 1).getDate().atStartOfDay().plusDays(1L).withHour(23).withMinute(59).withSecond(59).withNano(59).toInstant(ZONE_OFFSET).toEpochMilli();
        VEvent event = new VEvent(new Date(start), new Date(end), name + "假期");
        event.getProperties().add(V_TIME_ZONE.getTimeZoneId());
        event.getProperties().add(RANDOM_UID_GENERATOR.generateUid());
        event.getProperties().add(new Description(name + ":" + desc + "; " + tips));
        return event;
    }

    private List<VEvent> buildWorkEvent(String name, List<Holiday.Item> items) {
        return items.stream()
                .filter(item -> Boolean.FALSE.equals(item.getStatus()))
                .sorted()
                .map(item -> {
                    LocalDateTime dateTime = item.getDate().atStartOfDay();
                    long start = dateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).toInstant(ZONE_OFFSET).toEpochMilli();
                    long end = dateTime.plusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(59).toInstant(ZONE_OFFSET).toEpochMilli();
                    VEvent event = new VEvent(new Date(start), new Date(end), name + ": 补班，记得定闹钟噢");
                    event.getProperties().add(V_TIME_ZONE.getTimeZoneId());
                    event.getProperties().add(RANDOM_UID_GENERATOR.generateUid());
                    return event;
                }).collect(Collectors.toList());
    }

    private Calendar buildCalendar(Year year) {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(new AppleCalendarNameProperty(CALENDAR_NAME));
        calendar.getProperties().add(new AppleCalendarColorProperty(CALENDAR_COLOR));
        calendar.getProperties().add(new ProdId("xhh52ch@gmail.com"));
        List<SimpleHoliday> simpleHolidays = juheApiService.fetchHolidaysByYear(year);
        while (simpleHolidays != null && !simpleHolidays.isEmpty()) {
            // merge same month
            TreeSet<YearMonth> yearMonths = new TreeSet<>();
            for (SimpleHoliday simpleHoliday : simpleHolidays) {
                LocalDate start = simpleHoliday.getStart();
                yearMonths.add(YearMonth.of(start.getYear(), start.getMonth()));
            }

            for (YearMonth yearMonth : yearMonths) {
                List<Holiday> holidays = juheApiService.fetchHolidaysByYearMonth(yearMonth);
                if (holidays != null && !holidays.isEmpty()) {
                    for (Holiday holiday : holidays) {
                        String name = holiday.getName();
                        String desc = holiday.getDesc();
                        String tips = holiday.getTips();
                        List<Holiday.Item> items = holiday.getItems();
                        VEvent event = buildVocationEvent(name, desc, tips, items);
                        calendar.getComponents().add(event);
                        List<VEvent> workEvents = buildWorkEvent(name, items);
                        for (VEvent workEvent : workEvents) {
                            calendar.getComponents().add(workEvent);
                        }

                    }
                }
            }
            year = year.plusYears(1);
            simpleHolidays = juheApiService.fetchHolidaysByYear(year);
        }
        return calendar;
    }

    public void setJuheApiService(JuheApiService juheApiService) {
        this.juheApiService = juheApiService;
    }
}
