package pl.parser.nbp;

/**
 * Created by robert on 26.05.16.
 */
public enum Currency {
    USD("USD"), EUR("EUR"), CHF("CHF"), GBP("GBP");

    private final String text;

    Currency(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
