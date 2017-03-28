package ca.forloop.phonetotime;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRulesException;
import java.util.List;
import java.util.TimeZone;

public class PhoneToTime {

    private static final String ERROR = "e";
    private static final String LOCAL_NUMBER = "LOCAL_NUMBER";
    private static final String INVALID_NUMBER = "invalid";

    public static String localTimeZone() {
        return TimeZone.getDefault().getID();
    }

    public static ZoneId localTimeZoneId() {
        return ZoneId.of(localTimeZone());
    }

    public static ZonedDateTime overseasTime(ZoneId timeZone) {
        return ZonedDateTime.now().withZoneSameInstant(timeZone);
    }

    public static ZonedDateTime overseasTime(String timeZone) {

        return ZonedDateTime.now().withZoneSameInstant(ZoneId.of(timeZone));
    }


    public static String timeAtPhone(String number, String regionIso) {

        Phonenumber.PhoneNumber numberObject = parseNumber(number, regionIso);

        PhoneNumberToTimeZonesMapper mapper = PhoneNumberToTimeZonesMapper.getInstance();

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        if (phoneUtil.isValidNumber(numberObject)) {

            if (!numberObject.hasItalianLeadingZero()) {

                try {
                    List<String> map2 = mapper.getTimeZonesForNumber(numberObject);
                    if (map2.size() == 1) {
                        String phoneTimeZone = map2.get(0);
                        return overseasTime(phoneTimeZone).format(formatter);
                    } else {
                        return overseasTime(multipleZones(numberObject)).format(formatter);
                    }
                } catch (NullPointerException | ZoneRulesException e) {
                    e.printStackTrace();
                    return ERROR;
                }
            } else {
                return LOCAL_NUMBER;
            }
        } else {
            return INVALID_NUMBER;
        }
    }

    static Phonenumber.PhoneNumber parseNumber(String phoneNumber, String regionIso) {
        Phonenumber.PhoneNumber parsedNumber = null;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            parsedNumber = phoneUtil.parse(phoneNumber, regionIso);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return parsedNumber;
    }


    static String multipleZones(Phonenumber.PhoneNumber number) {

        String region;

        switch (number.getCountryCode()) {
            //Portugal
            case 351:
                region = "Europe/Lisbon";
                break;
            //Australia
            case 61:
                region = "Australia/Sydney";
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

            case 686:
                region = "Pacific/Tarawa";
                break;

            case 56:
                region = "America/Santiago";
                break;

            case 243:
                region = "Africa/Kinshasa";
                break;

            case 593:
                region = "America/Guayaquil";
                break;

            case 976:
                region = "Asia/Ulaanbaatar";
                break;

            case 675:
                region = "Pacific/Port_Moresby";
                break;

            case 27:
                region = "Africa/Johannesburg";
                break;

            case 34:
                region = "Europe/Madrid";
                break;

            default:
                region = "Etc/UTC";
        }
        return region;
    }
}
