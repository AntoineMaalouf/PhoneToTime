package ca.forloop;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRulesException;
import java.util.List;

public class PhoneToTime {

    private final String ERROR = "ERROR";
    private final String LOCAL_NUMBER = "LOCAL NUMBER";
    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();


    //public static String localTimeZone() {
    //      return TimeZone.getDefault().getID();
    // }

    //  public static ZoneId localTimeZoneId() {
    //      return ZoneId.of(localTimeZone());
    //  }

    //public static ZonedDateTime overseasTime(ZoneId timeZone) {
    //    return ZonedDateTime.now().withZoneSameInstant(timeZone);
    //}

    private static ZonedDateTime overseasTime(String timeZone) {
        return ZonedDateTime.now().withZoneSameInstant(ZoneId.of(timeZone));
    }


    public String timeAtPhone(String number) {

        String interNumber = isPhoneInternational(number);

        if (!(interNumber.equals(ERROR) || interNumber.equals(LOCAL_NUMBER))) {

            Phonenumber.PhoneNumber numberObject = parseNumber(interNumber);

            PhoneNumberToTimeZonesMapper mapper = PhoneNumberToTimeZonesMapper.getInstance();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

            try {
                List<String> map2 = mapper.getTimeZonesForNumber(numberObject);
                if (map2.size() == 1) {
                    String phoneTimeZone = map2.get(0);
                    return overseasTime(phoneTimeZone).format(formatter) + " * " + phoneTimeZone
                            + " * " + mPhoneUtil.getRegionCodeForNumber(numberObject);
                } else {
                    String multiple = multipleZones(numberObject, map2);
                    return overseasTime(multiple).format(formatter) + " " + multiple
                            + " * " + mPhoneUtil.getRegionCodeForNumber(numberObject);
                }
            } catch (NullPointerException | ZoneRulesException e) {
                e.printStackTrace();
                return ERROR;
            }
        } else return interNumber;
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
            } else return ERROR;


        } else if (number.indexOf("0011") == 0) {
            if (isNumeric(number)) {
                return "+" + number.substring(4);
            } else return ERROR;

        } else if (number.indexOf("00") == 0) {
            if (isNumeric(number)) {
                return "+" + number.substring(2);
            } else return "ERROR1";

        } else {
            return LOCAL_NUMBER;
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
