package ca.forloop;


public class Main {

    public static void main(String[] args) {

        String canadaPhone= "+16135356666";
        String lebanonPhone = "+9613567890";
        String local = "06354666";
        String noPhone = "TTTTTT";
        String numberWithSpace = "+961 3 567 774";
        String numberWithBrackets = "+1 (613) 243-5424";

        PhoneToTime time  = new PhoneToTime();
        System.out.println(time.timeAtPhone(numberWithBrackets));

    }
}
