package com.simon.ical.service;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.simon.ical.conf.JuheProperties;
import com.simon.ical.domain.Holiday;
import com.simon.ical.domain.SimpleHoliday;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Getter
@Setter
@Slf4j
@Service
public class JuheApiService {

    private static final String HOLIDAY_BY_MONTH_URL_PATTERN = "http://v.juhe.cn/calendar/month?key=%s&year-month=%s";
    private static final String HOLIDAY_BY_YEAR_URL_PATTERN = "http://v.juhe.cn/calendar/year?key=%s&year=%s";
    private static final HttpClient httpClient = HttpClients.createDefault();
    private static final DateTimeFormatter PARAMETER_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M");
    private static final DateTimeFormatter FESTIVAL_FORMATTER = new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-d"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toFormatter();

    @Autowired
    private JuheProperties juheProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public List<Holiday> fetchHolidaysByYearMonth(YearMonth yearMonth) {
        log.info("fetching recent holiday of the yearMonth({})", yearMonth);
        if (yearMonth == null) {
            log.warn("the yearMonth is null.");
            return Collections.emptyList();
        }
        String yearMonthAsString = PARAMETER_FORMATTER.format(yearMonth);
        String url = String.format(HOLIDAY_BY_MONTH_URL_PATTERN, juheProperties.getAppKey(), yearMonthAsString);
        HttpGet get = new HttpGet(url);
        List<Holiday> result = httpClient.execute(get, resp -> resolveResponse(yearMonth, resp));
        log.info("fetched {} holiday(s) from Juhe platform, result:{}", result.size(), result);
        return result;
    }

    @SneakyThrows
    public List<SimpleHoliday> fetchHolidaysByYear(Year year) {
        log.info("fetching all holidays of the year({})", year);
        if (year == null) {
            log.warn("the year is null.");
            return Collections.emptyList();
        }

        String url = String.format(HOLIDAY_BY_YEAR_URL_PATTERN, juheProperties.getAppKey(), year.toString());
        HttpGet get = new HttpGet(url);
        return httpClient.execute(get, resp -> {
            try {
                StatusLine statusLine = resp.getStatusLine();
                if (statusLine == null || statusLine.getStatusCode() != 200) {
                    log.error("cannot fetch holidays from Juhe platform.");
                    throw new RuntimeException("cannot fetch holidays from Juhe platform.");
                }
                String entity = EntityUtils.toString(resp.getEntity());
                JsonNode rootNode = objectMapper.readTree(entity);
                JsonNode errorCode = rootNode.get("error_code");
                if (errorCode.asInt() == 217701) {
                    log.warn("no data returned from Juhe platform maybe the holidays has not been scheduled yet. year:{}", year);
                    return Collections.emptyList();
                } else if (errorCode.asInt() != 0) {
                    log.error("cannot fetch holidays from Juhe platform. response:{}", rootNode);
                    if (errorCode.asInt() == 10001) {
                        throw new IllegalStateException("wrong key of Juhe platform.");
                    }
                    return Collections.emptyList();
                }

                ArrayNode holidayList = (ArrayNode) rootNode.get("result").get("data").get("holiday_list");
                if (holidayList.isEmpty()) {
                    log.info("current year({}) has no any holidays.", year);
                    return Collections.emptyList();
                }

                List<SimpleHoliday> holidays = Lists.newArrayList();
                for (JsonNode ele : holidayList) {
                    String startDateAsString = ele.get("startday").asText();
                    LocalDate start = LocalDate.from(FESTIVAL_FORMATTER.parse(startDateAsString));
                    String name = ele.get("name").asText();
                    SimpleHoliday holiday = new SimpleHoliday();
                    holiday.setStart(start);
                    holiday.setName(name);
                    holidays.add(holiday);
                }
                return holidays;
            } catch (Exception e) {
                log.error("caught an error while fetching holidays", e);
                if (e instanceof RuntimeException) {
                    throw e;
                }
                throw new RuntimeException(Throwables.getStackTraceAsString(e), e);
            } finally {
                EntityUtils.consumeQuietly(resp.getEntity());
            }
        });
    }

    private List<Holiday> resolveResponse(YearMonth yearMonth, HttpResponse resp) throws IOException {
        try {
            StatusLine statusLine = resp.getStatusLine();
            if (statusLine == null || statusLine.getStatusCode() != 200) {
                log.error("cannot fetch holidays from Juhe platform.");
                throw new RuntimeException("cannot fetch holidays from Juhe platform.");
            }

            String entity = EntityUtils.toString(resp.getEntity());
            JsonNode rootNode = objectMapper.readTree(entity);
            JsonNode errorCode = rootNode.get("error_code");
            if (errorCode.asInt() != 0) {
                log.error("cannot fetch holidays from Juhe platform. response:{}", rootNode);
                if (errorCode.asInt() == 10001) {
                    throw new IllegalStateException("wrong key of Juhe platform.");
                }
                return Collections.emptyList();
            }

            JsonNode resultNode = rootNode.get("result");
            JsonNode data = resultNode.get("data");
            ArrayNode holidayArray = (ArrayNode) data.get("holiday_array");
            if (holidayArray.isEmpty()) {
                log.info("current yearMonth({}) has no any holidays recently.", yearMonth);
                return Collections.emptyList();
            }

            List<Holiday> holidays = Lists.newArrayList();
            for (JsonNode ele : holidayArray) {
                String festival = ele.get("festival").asText();
                LocalDate festivalAsDate = LocalDate.from(FESTIVAL_FORMATTER.parse(festival));
                // skip record if not a same month.
                if (yearMonth.getMonthValue() != festivalAsDate.getMonthValue()) {
                    continue;
                }

                Holiday holiday = new Holiday();
                holiday.setName(ele.get("name").asText());
                holiday.setDate(festivalAsDate);
                holiday.setDesc(ele.get("desc").asText());
                holiday.setTips(ele.get("rest").asText());
                List<Holiday.Item> items = Lists.newArrayList();
                holiday.setItems(items);
                JsonNode list = ele.get("list");
                for (JsonNode vacation : list) {
                    items.add(new Holiday.Item(LocalDate.from(FESTIVAL_FORMATTER.parse(vacation.get("date").asText())), "1".equals(vacation.get("status").asText())));
                }
                holidays.add(holiday);
            }
            return holidays;
        } catch (Exception e) {
            log.error("caught an error while resolving response.", e);
            throw e;
        } finally {
            EntityUtils.consumeQuietly(resp.getEntity());
        }
    }
}
