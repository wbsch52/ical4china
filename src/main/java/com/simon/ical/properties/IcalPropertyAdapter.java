package com.simon.ical.properties;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import lombok.AllArgsConstructor;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
public class IcalPropertyAdapter extends Property {

    private String value;

    private IcalPropertyAdapter() {
        this("", "");
    }

    public IcalPropertyAdapter(String name, String value) {
        super(name, new Factory(name));
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void validate() throws ValidationException {

    }

    @AllArgsConstructor
    private static class Factory extends Content.Factory implements PropertyFactory<IcalPropertyAdapter> {

        private final String name;

        @Override
        public IcalPropertyAdapter createProperty() {
            return new IcalPropertyAdapter();
        }

        @Override
        public IcalPropertyAdapter createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new IcalPropertyAdapter(this.name, value);
        }
    }
}
