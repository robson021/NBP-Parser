package pl.parser.nbp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by robert on 26.05.16.
 */
public class Parser {
    private List<Date> dateList = new ArrayList<>();

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

        for (Date d : dateList) {
            System.out.println(d.toString());
        }

    }

    public void checkOutForFiles() {
        if (dateList.isEmpty()) {
            throw new RuntimeException("Nothing to search for. The list is empty.");
        }


    }
}
