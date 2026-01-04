package util;

public class Base10Encoder implements BaseEncoder {

    private final int base;
    private final String characters;

    public final static BaseEncoder BASE_62 = new Base10Encoder(62, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

    public Base10Encoder(int base, String characters) {
        this.base = base;
        this.characters = characters;
    }

    @Override
    public String encode(long number) {
        if (number == 0) {
            return String.valueOf(characters.charAt(0));
        }

        StringBuilder builder = new StringBuilder(1);
        while (number > 0) {
            builder.append(characters.charAt((int) (number % base)));
            number /= base;
        }

        return builder.reverse().toString();
    }

    @Override
    public long decode(String number) {
        long result = 0L;
        int length = number.length();
        for (int index = 0; index < length; index++) {
            int digit = characters.indexOf(number.charAt(index));
            if (digit < 0) {
                throw new IllegalArgumentException("Invalid character: " + number.charAt(index));
            }
            result = result * base + digit;
        }

        return result;
    }
}
