package com.simon.ical.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.ical.conf.JuheProperties;
import com.simon.ical.service.ICalendarService;
import com.simon.ical.service.JuheApiService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.Cleanup;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
public class CommandLineTools {

    private static final Options OP;
    private static final String HELP_MESSAGE;

    static {
        try {
            Option key = Option.builder("k").longOpt("key").longOpt("key").hasArg().desc("Juhe platform api key.").build();
            Option path = Option.builder("o").argName("path").longOpt("out").hasArg().desc("use given path for out").build();
            Option help = Option.builder("h").longOpt("help").hasArg(false).desc("print help message").build();

            OP = new Options();
            OP.addOption(key).addOption(path).addOption(help);

            @Cleanup StringWriter sw = new StringWriter();
            @Cleanup PrintWriter writer = new PrintWriter(sw);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(writer, HelpFormatter.DEFAULT_WIDTH, "ical4china", null, OP, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
            writer.flush();
            sw.flush();
            HELP_MESSAGE = sw.toString();

            // turn off logging.
            Logger logger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("ROOT");
            logger.setLevel(Level.OFF);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(OP, args);
            if (cmd.hasOption("h")) {
                System.out.println(HELP_MESSAGE);
            } else {
                checkRequiredOptions(cmd);

                String key = cmd.getOptionValue("k");
                String out = cmd.getOptionValue("o");
                process(key, out);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.out.println(HELP_MESSAGE);
        }
    }

    private static void checkRequiredOptions(CommandLine cmd) throws ParseException {
        if (!cmd.hasOption("k") && !cmd.hasOption("o")) {
            throw new ParseException("Missing required options: k, o");
        } else if (!cmd.hasOption("k")) {
            throw new ParseException("Missing required option: k");
        } else if (!cmd.hasOption("o")) {
            throw new ParseException("Missing required option: o");
        }
    }

    private static void process(String key, String out) {
        JuheApiService juheApiService = new JuheApiService();
        JuheProperties juheProperties = new JuheProperties();
        juheProperties.setAppKey(key);
        juheApiService.setJuheProperties(juheProperties);
        juheApiService.setObjectMapper(new ObjectMapper());

        ICalendarService iCalendarService = new ICalendarService();
        iCalendarService.setJuheApiService(juheApiService);

        try {
            String result = iCalendarService.write(out);
            System.out.println("iCalendar file is written on:" + result);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


}
