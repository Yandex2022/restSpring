package restapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO_8601 {
    private final static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static Date strToDate(String str) throws ParseException {
        return dt.parse(str);
    }

    public static String dateToStr(Date date){
        return dt.format(date);
    }

    public static Date getPrevDay(Date date){
        return new Date(Math.max(date.getTime() - (1000 * 60 * 60 * 24), 0));
    }
}
