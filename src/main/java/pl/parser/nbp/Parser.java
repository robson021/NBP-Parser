package pl.parser.nbp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by robert on 26.05.16.
 */
public class Parser {
    private static final String CURRENT_YEAR_URL = "http://www.nbp.pl/kursy/xml/dir.txt";
    private static final String URL_1 = "http://www.nbp.pl/kursy/xml/dir";
    private static final String URL_2 = ".txt";
    private List<Date> dateList = new ArrayList<>();
    private URL url;

    public Parser() {
    }

    public void initDateList(Date from, Date to) {
        long diff = to.getTime() - from.getTime();
        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

        Calendar c = Calendar.getInstance();
        c.setTime(from);

        for (int i = 0; i < days; i++) {
            dateList.add(c.getTime());
            c.add(Calendar.DATE, 1);
        }

        /*for (Date d : dateList) {
            System.out.println(d.toString());
        }*/

    }

    public void checkFileName() throws MalformedURLException {
        if (dateList.isEmpty()) {
            throw new RuntimeException("Nothing to search for. The list is empty.");
        }
        int year = dateList.get(0).getYear() + 1900;
        if (year == Calendar.getInstance().getTime().getYear()) {
            url = new URL(CURRENT_YEAR_URL);
        } else {
            url = new URL(URL_1 + (year - 2000) + URL_2);
        }
    }
}
