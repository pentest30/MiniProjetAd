

import daj.Message;

/**
 * Created by pen on 01/01/2017.
 */
public class Msg extends Message {
    String val;
    public int  NbPorte =-1;
    public int QntRessource=0;
    public int TimeSpan;
    public TypeResource typeResource;

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
    public Msg(String  i, int j, int qnt , int timeSpan ,TypeResource type)
    {
        val = i;
        this.NbPorte =j;
        QntRessource = qnt;
        TimeSpan = timeSpan;
        typeResource = type;
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
