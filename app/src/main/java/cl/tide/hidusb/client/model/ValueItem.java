package cl.tide.hidusb.client.model;

/**
 * Created by eDelgado on 11-08-14.
 */
public class ValueItem {

    public int integer;

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public int decimal;


    public ValueItem(){
        this.decimal = 0;
    }
    public ValueItem(double v){
        long i = (long) v;
        double d =  v - i;

        this.decimal =  (int) (d * 10);
        this.integer = (int) i;
        //System.out.println("PARTE ENTERA " + integer +" PARTE DECIMAL "+ decimal);
    }
}
