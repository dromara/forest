package org.dromara.forest.core.test.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public abstract class JSONConverter {

    protected void assertDateEquals(String expectedDate, Date actualDate, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(expectedDate);
        assertEquals(date.getTime(), actualDate.getTime());
    }

}
