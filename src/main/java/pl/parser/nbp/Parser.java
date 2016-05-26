package pl.parser.nbp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by robert on 26.05.16.
 */
public class Parser {
    private static final String CURRENT_YEAR_URL = "http://www.nbp.pl/kursy/xml/dir.txt";
    private static final String URL_PREFIX = "http://www.nbp.pl/kursy/xml/dir";
    private static final String URL_SUFIX = ".txt";
    private static final String XML_URL = "http://www.nbp.pl/kursy/xml/"; // + "########.xml"

    private double lastAvgBuyPrice;
    private double lastStandardDeviation;

    private List<Date> dateList;
    private List<String> patterns;
    private List<String> fileNameList;
    private URL url;


    public void initDateList(Date from, Date to) {
        long diff = to.getTime() - from.getTime();
        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

        Calendar c = Calendar.getInstance();
        c.setTime(from);

        dateList = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            dateList.add(c.getTime());
            c.add(Calendar.DATE, 1);
        }
    }

    public void generateUrlToSearch() throws Exception {
        if (dateList.isEmpty()) {
            throw new Exception("Nothing to search for. The list is empty.");
        }
        int year = dateList.get(0).getYear() + 1900;
        if (year == (Calendar.getInstance().getTime().getYear() + 1900)) {
            url = new URL(CURRENT_YEAR_URL);
        } else {
            url = new URL(URL_PREFIX + year + URL_SUFIX);
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

        Scanner sc = new Scanner(url.openStream());
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
        return line.charAt(0) == 'c' && patterns.contains(substr);
    }


    /**
     * Parses XML documents. <br>
     * See: http://www.tutorialspoint.com/java_xml/java_dom_parser.htm
     *
     * @param currencyCode The code of the currency you want to find (@see pl.parser.nbp.Currency).
     */
    public void parseListOfContent(Currency currencyCode) {
        List<BuyAndSell> buyAndSellList = new ArrayList<>();
        for (String fileName : fileNameList) {
            Document doc = this.loadDocument(XML_URL + fileName + ".xml");
            if (doc == null) continue;
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("pozycja");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                //System.out.println("\nCurrent Element: " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) nNode;
                    String code = e.getElementsByTagName("kod_waluty").item(0).getTextContent();
                    if (currencyCode.toString().equals(code)) {
                        double buy = Double.parseDouble(e.getElementsByTagName("kurs_kupna").item(0).getTextContent().replace(',', '.'));
                        double sell = Double.parseDouble(e.getElementsByTagName("kurs_sprzedazy").item(0).getTextContent().replace(',', '.'));
                        //System.out.println(buy + "; " + sell);
                        buyAndSellList.add(new BuyAndSell(buy, sell));
                    }
                }
            }
        }
        //System.out.println(buyAndSellList.toString());
        calculateAvgBuyPrice(buyAndSellList);
        calculateStandardDeviation(buyAndSellList);
    }

    private void calculateStandardDeviation(List<BuyAndSell> buyAndSellList) {
        if (buyAndSellList.isEmpty()) return;
        double avg = 0;
        for (BuyAndSell bs : buyAndSellList) {
            avg += bs.getSell();
        }
        avg /= buyAndSellList.size();
        double v = 0;
        for (BuyAndSell bs : buyAndSellList) {
            double a = bs.getSell();
            v += (avg - a) * (avg - a);
        }
        v /= buyAndSellList.size();
        this.lastStandardDeviation = Math.sqrt(v);
    }

    private void calculateAvgBuyPrice(List<BuyAndSell> buyAndSellList) {
        if (buyAndSellList.isEmpty()) return;
        double sum = 0;
        for (BuyAndSell bs : buyAndSellList) {
            sum += bs.getBuy();
        }
        this.lastAvgBuyPrice = sum / buyAndSellList.size();
    }

    private Document loadDocument(String url) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            return factory.newDocumentBuilder().parse(new URL(url).openStream());
        } catch (Exception e) {
            return null;
        }
    }

    public double getLastAvgBuyPrice() {
        return lastAvgBuyPrice;
    }

    public double getLastStandardDeviation() {
        return lastStandardDeviation;
    }

    private class BuyAndSell {
        private final double buy, sell;

        public BuyAndSell(double buy, double sell) {
            this.buy = buy;
            this.sell = sell;
        }

        public double getBuy() {
            return buy;
        }

        public double getSell() {
            return sell;
        }

        @Override
        public String toString() {
            return "BuyAndSell{" +
                    "buy=" + buy +
                    ", sell=" + sell +
                    '}';
        }
    }

}
