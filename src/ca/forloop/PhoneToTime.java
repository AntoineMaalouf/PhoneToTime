package ca.forloop;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.zone.ZoneRulesException;

import java.util.List;
import java.util.Locale;


public class PhoneToTime {


    private final PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();
    private Phonenumber.PhoneNumber numberObject;
    private boolean nanp;


    public PhoneToTime(String stringNumber, boolean nanp) {
        this.numberObject = getNumberObject(stringNumber);
        this.nanp = nanp;
    }


    private Phonenumber.PhoneNumber getNumberObject(String number) {
        String interNumber = isPhoneInternational(number);
        if (interNumber != null) {
            return parseNumber(interNumber);
        } else return null;
    }

    public String getCountryId() {
        return mPhoneUtil.getRegionCodeForNumber(numberObject);
    }

    public String getTimeZone() {
        PhoneNumberToTimeZonesMapper mapper = PhoneNumberToTimeZonesMapper.getInstance();
        List<String> map2 = mapper.getTimeZonesForNumber(numberObject);

        if (map2.size() == 1) {
            return map2.get(0);
        } else {
            return multipleZones(numberObject, map2);
        }
    }

    public String getTime() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            return ZonedDateTime.now().withZoneSameInstant(ZoneId.of(getTimeZone()))
                    .format(formatter);
        } catch (NullPointerException | ZoneRulesException e) {
            return null;
        }
    }

    public String getCountryName(){
        Locale locale = new Locale("", getCountryId());
        return locale.getDisplayName();
    }

    private Phonenumber.PhoneNumber parseNumber(String phoneNumber) {
        Phonenumber.PhoneNumber parsedNumber = null;

        try {
            parsedNumber = mPhoneUtil.parse(phoneNumber, "");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return parsedNumber;
    }

    private String australia(Phonenumber.PhoneNumber aussieNumber, List<String> timeZones) {

        if (mPhoneUtil.getNumberType(aussieNumber)
                .equals(PhoneNumberUtil.PhoneNumberType.MOBILE) || timeZones == null) {
            return "Australia/Sydney";
        } else {
            return timeZones.get(0);
        }
    }

    private String isPhoneInternational(String number) {

        number = number.replaceAll("[\\Q() -\\E]", "");

        if (number.indexOf("+") == 0) {
            if (isNumeric(number.substring(1))) {
                return number;
            } else return null;


        } else if (number.indexOf("0011") == 0) {
            if (isNumeric(number)) {
                return "+" + number.substring(4);
            } else return null;

        } else if (number.indexOf("00") == 0) {
            if (isNumeric(number)) {
                return "+" + number.substring(2);
            } else return null;

        } else {
            return null;
        }
    }

    private boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    private String multipleZones(Phonenumber.PhoneNumber number, List<String> timeZones) {

        String region;

        switch (number.getCountryCode()) {
            //Portugal
            case 351:
                region = "Europe/Lisbon";
                break;
            //Australia
            case 61:
                region = australia(number, timeZones);
                break;
            //NZ
            case 64:
                region = "Pacific/Auckland";
                break;
            //Russia
            case 7:
                region = "Europe/Moscow";
                break;
            //Denmark
            case 45:
                region = "Europe/Copenhagen";
                break;
            //Brazil
            case 55:
                region = "America/Sao_Paulo";
                break;
            //Indonesia
            case 62:
                region = "Asia/Jakarta";
                break;
            //Kiribati
            case 686:
                region = "Pacific/Tarawa";
                break;
            //Chile
            case 56:
                region = "America/Santiago";
                break;
            //Congo
            case 243:
                region = "Africa/Kinshasa";
                break;
            //Ecuador
            case 593:
                region = "America/Guayaquil";
                break;
            //Mongolia
            case 976:
                region = "Asia/Ulaanbaatar";
                break;
            //PNG
            case 675:
                region = "Pacific/Port_Moresby";
                break;
            //South Africa
            case 27:
                region = "Africa/Johannesburg";
                break;
            //Spain
            case 34:
                region = "Europe/Madrid";
                break;

            default:
                region = timeZones.get(0);
        }
        return region;
    }
}
