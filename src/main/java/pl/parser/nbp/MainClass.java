package pl.parser.nbp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by robert on 26.05.16.
 */
public class MainClass {
    private static final Parser parser = new Parser();

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Bad input");
            return;
        }
        Currency currencyCode;
        Date dateFrom;
        Date dateTo;
        try {
            currencyCode = Currency.valueOf(args[0]);
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
            dateFrom = format.parse(args[1]);
            dateTo = format.parse(args[2]);
            if (dateFrom.after(dateTo) || dateFrom.getYear() != dateTo.getYear()) {
                System.out.println("Invalid date");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid arguments");
            return;
        }

        /*System.out.println(currencyCode);
        System.out.println(dateFrom.toString());
        System.out.println(dateTo.toString());*/

        parser.initDateList(dateFrom, dateTo);
        parser.generateUrlToSearch();
        parser.getListOfContent();
        parser.parseListOfContent(currencyCode);

        System.out.printf("%.4f%n", parser.getLastAvgBuyPrice());
        System.out.printf("%.4f%n", parser.getLastStandardDeviation());
    }
}
