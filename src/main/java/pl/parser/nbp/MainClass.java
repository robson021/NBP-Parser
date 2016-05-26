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
    private static Currency currencyCode;
    private static Date dateFrom;
    private static Date dateTo;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Bad input");
            return;
        }
        try {
            currencyCode = Currency.valueOf(args[0]);
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
            dateFrom = format.parse(args[1]);
            dateTo = format.parse(args[2]);
            if (dateFrom.after(dateTo)) {
                System.out.println("Invalid date");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid arguments");
            return;
        }

        System.out.println(currencyCode);
        System.out.println(dateFrom.toString());
        System.out.println(dateTo.toString());

        parser.initDateList(dateFrom, dateTo);


    }
}
