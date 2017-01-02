import daj.Program;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pen on 01/01/2017.
 */
public class Prog extends Program {
    int index;
    List<Neighbor> neighbors;
    List<Response> reponsesSelections;
    Msg message;
    boolean enPanne;
    boolean isMaster;

    public Prog(int k) {
        index = k;
        neighbors =  new LinkedList<>();
        reponsesSelections = new LinkedList<>();
        AddNeighbors(k);
    }



    private void AddNeighbors(int k) {
        Neighbor n;
        for (int i = k; i >0; i--) {
            n =  new Neighbor();
            n.channel =i-1;
            n.node = n.channel;
            neighbors.add(n);

        }
        for (int i = k+1; i <6; i++) {
            n =  new Neighbor();
            n.channel =i-1;
            n.node =i;
            neighbors.add(n);

        }
    }

    @Override
    protected void main() {
 if (index ==0)
        try {

            RunBullySelection(this);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
    static void  RunBullySelection(Prog p0 ) throws InterruptedException {

        //Main.progs.get(5).enPanne = true;
        //Main.progs.get(4).enPanne = true;
        for (int y = p0.index; y < Main.progs.size(); y++) {

            if (p0==Main.progs.get(y)) {
                int tmp = p0.index+1;
                int j = 0;
                boolean AnnyOk;
                // get the channles with nodes that have higher ID.
                int[] channels= new int [5];
                for (Neighbor n: p0.neighbors) {
                    if (n.node==tmp) {
                        channels[j]=n.channel;
                        tmp++;
                        j ++;
                    }

                }
                AnnyOk = false;
                p0.message =  new Msg();
                int c =0;
                int k=0;
                int x  =p0.index +1;
                while (k<channels.length) {
                    if (x > 5) break;
                    p0.message = null;
                    p0.message = new Msg("message de séléction", p0.index);
                    p0.out(channels[k]).send(p0.message);
                    System.out.println(p0.index + "Msg sent to th e process N°:" + x);
                    p0.yield();

                    Prog p = Main.progs.get(x);
                    p.message = new Msg();
                    int f = -1;
                    for (Neighbor nn : p.neighbors) {
                        if (p0.index == nn.Node()) {
                            f = nn.Channel();
                            break;
                        }

                    }
                    p.message = (Msg) p.in(f).receive();
                    System.out.println("Msg recieved by the process N°:" + x + "from  process N°: " + p0.index);
                    if (p.message.val == "") continue;
                    // traitement de message par le noeud d'emmission
                    if (p.message.val == "message de séléction") {

                        // checking the stat of the node that is about to recieve the selection msg
                        if (!p.enPanne) {
                            //int sender = message.NbPorte;
                            p.message = new Msg("Ok");

                            for (Neighbor n : p.neighbors) {
                                if (n.node == p0.index) {
                                    c = n.channel;
                                    break;
                                }

                            }
                            p.out(c).send(p.message);
                            p.yield();

                            for (Neighbor n : p0.neighbors) {
                                if (n.node == p.index) {
                                    c = n.channel;
                                    break;
                                }

                            }
                            if (c >= 0) {
                                p0.message = (Msg) p0.in(c).receive();
                                System.out.println(p0.message.getValue());
                                //sleep(5);
                                if (p0.message != null && p0.message.value() != "") {
                                    Response r = new Response();
                                    r.Response = p0.message.getValue();
                                    r.Node = x;
                                    p0.reponsesSelections.add(r);
                                }
                            }

                        }

                    }
                    x++;
                    k++;


                }
                for (Response res: p0.reponsesSelections) {
                    if (res.Response=="Ok") { AnnyOk= true; break;}

                }
                if (AnnyOk ==false ||p0.reponsesSelections.size()==0) {
                    p0.message =  new Msg("I am the condidator!" , p0.index);
                    p0.out().send(p0.message);
                    p0.isMaster = true;
                    break;
                }
                try {
                    p0 = Main.progs.get(y+1);
                }catch (Exception ex) {break;}

            }
        }



    }




}
