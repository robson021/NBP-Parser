package pl.parser.nbp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by robert on 26.05.16.
 */
public class Parser {
    private static final String CURRENT_YEAR_URL = "http://www.nbp.pl/kursy/xml/dir.txt";
    private static final String URL_1 = "http://www.nbp.pl/kursy/xml/dir";
    private static final String URL_2 = ".txt";
    private static final String XML_URL = "http://www.nbp.pl/kursy/xml/c073z070413";

    private List<Date> dateList = new ArrayList<>();
    private List<String> patterns;
    private List<String> fileNameList;
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

    public void checkUrlToSearch() throws MalformedURLException {
        if (dateList.isEmpty()) {
            throw new RuntimeException("Nothing to search for. The list is empty.");
        }
        int year = dateList.get(0).getYear() + 1900;
        if (year == Calendar.getInstance().getTime().getYear()) {
            url = new URL(CURRENT_YEAR_URL);
        } else {
            url = new URL(URL_1 + year + URL_2);
        }
    }

    public void getListOfContent() throws IOException {
        int d, m, y;
        String yy, dd, mm;
        String pattern, line;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateList.get(0));
        y = calendar.get(Calendar.YEAR) - 2000;
        if (y < 10) {
            yy = "0" + y;
        } else {
            yy = String.valueOf(y);
        }

        patterns = new ArrayList<>();
        for (Date date : dateList) {
            calendar.setTime(date);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH) + 1;
            if (d < 10) {
                dd = "0" + d;
            } else dd = String.valueOf(d);
            if (m < 10) {
                mm = "0" + m;
            } else mm = String.valueOf(m);
            pattern = "";
            pattern += yy + mm + dd;
            patterns.add(pattern);
        }
        Scanner sc;
        sc = new Scanner(url.openStream());
        fileNameList = new ArrayList<>();
        while (sc.hasNext()) {
            line = sc.nextLine();
            if (matchPattern(line)) {
                //System.out.println(line);
                fileNameList.add(line);
            }
        }
        patterns = null;
        sc.close();
    }

    private boolean matchPattern(String line) {
        String substr = line.substring(line.length() - 6);
        if (line.charAt(0) == 'c' && patterns.contains(substr))
            return true;
        return false;
    }

}
