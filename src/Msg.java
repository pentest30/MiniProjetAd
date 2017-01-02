import daj.Message;

/**
 * Created by pen on 01/01/2017.
 */
public class Msg extends Message {
    String val;
    public int  NbPorte =-1;
    public Msg(String  i)
    {
        val = i;
    }
    public Msg(){}
    public Msg(String  i, int j)
    {
        val = i;
        this.NbPorte =j;
    }
    public String value()
    {
        return val;
    }
    public String getValue()
    {
        return String.valueOf(val);
    }
}
