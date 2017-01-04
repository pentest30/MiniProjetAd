import daj.Program;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by pen on 01/01/2017.
 */
public class Prog extends Program {
    int index;
    List<Neighbor> neighbors;
    List<Response> reponsesElections;
    List<Ressource> ressources;
    List<Ressource> queue;
    Msg message;
    boolean enPanne;
    boolean isMaster;
    boolean masterElected;


    public Prog(int k) {
        index = k;
        neighbors =  new LinkedList<>();
        reponsesElections = new LinkedList<>();
        ressources = new LinkedList<>();
        addRessources();
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
            //

        }
    }

    @Override
    protected void main() {

        if (!masterElected) {
            if (this.index == 0) {
                try {


                    RunBullyElection(this);

                    while (true) {
                   /*  try {
                         Main.progs.stream().filter(x->!x.isMaster).parallel().forEach(e-> e.GenerateDemande(e));
                     }catch (Exception ex) {continue;}*/
                        Random r = new Random();
                        Prog p = Main.progs.get(r.nextInt(Main.progs.size()));
                        this.yield();

                        if (p.isMaster) {
                            continue;

                        }
                        p.GenerateDemande(p);


                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


    }
    void GenerateDemande(Prog init){

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("   Génération Aléatoire des Demandes par le Noeud P "+init.index);
        System.out.println("-------------------------------------------------------------------------");
        Random rand = new Random();
        int chance = rand.nextInt(TypeResource.values().length);

        for (TypeResource rs:TypeResource.values()) {
            if (chance==0) break;
            chance --;
            int periode = rand.nextInt(20);
            // le msg contient une demande de ressource il contient le processus id le temp , etle type de ressource
            init.message =  new Msg ("demande ressource" , init.index,1 ,periode , rs );
            int channel= -1;
            int masterId=0;
            for (Neighbor n: init.neighbors) {
                if (n.isMaster) {
                    masterId = n.node;
                    channel=n.channel;
                    break;
                }

            }
            if (channel>-1)init.out(channel).send(init.message);
            Prog master = Main.progs.get(masterId);
            if (master!= null ){
                for (Neighbor n: master.neighbors)
                    if (n.node==init.index) {
                        channel = n.channel; break;

                    }
                master.message = null;
                master.message=(Msg)master.in(channel).receive();

                if (master.message!=null) System.out.println(master.message.getValue() +" "+ master.message.typeResource +" "+ master.message.TimeSpan);
                if (master.message.getValue()!="demande ressource") continue;
                List<Ressource> remainResources = new LinkedList<>();
                for (Ressource resr:master.ressources) {
                    if (resr.type== master.message.typeResource&& resr.Occupied==false) remainResources.add(resr);

                }
                //Wound-wait algo
                // the principe of the algo https://sites.google.com/site/projectcodebank/computer-engineering-notes/concurrency-control/6-wait-die-and-wound-wait-schemes
                if (remainResources.size() ==0) {
                    List<Ressource> listOfWoundedProcess = master.ressources.stream()
                            .filter(x->(x.TimeSpan)>master.message.TimeSpan&&
                                    x.Start.isBefore( Instant.now()) &&
                                    x.type ==master.message.typeResource&&
                                    x.Occupied)
                            .sorted()
                            .collect(Collectors.toList());

                    if (listOfWoundedProcess!=null&& listOfWoundedProcess.size()>0){
                        //rolle Back the process
                        Ressource woundedRs= listOfWoundedProcess.get(0);
                        woundedRs.Start = Instant.now();
                        woundedRs.Remain =  woundedRs.TimeSpan;
                        //add  the rolled back proc to the queue
                        master.queue.add(woundedRs);
                        master.ressources.remove(woundedRs);
                        Ressource comming = initRessource( woundedRs.id ,master.message.NbPorte, master.message.TimeSpan , Instant.now(),  message.typeResource );
                        master.ressources.add(comming);

                    }
                    //the comming demande has to wait
                    else {
                        Ressource comming = initRessource( -1 ,master.message.NbPorte, master.message.TimeSpan , Instant.now() , message.typeResource);
                        master.queue.add(comming);

                    }


                }
                // where the ressources are available
                else {
                     List<Ressource> tmp  = master.ressources.stream().filter(x->!x.Occupied &&x.type ==master.message.typeResource ).collect(Collectors.toList());
                     int id = tmp.get(0).Id();
                    for (Ressource comming:master.ressources) {
                        if (comming.id ==id){
                            //update the state of the ressource that will be used
                            comming.Occupied = true;
                            comming.Start = Instant.now();
                            comming.TimeSpan = master.message.TimeSpan;
                            comming.NodeId = master.message.NbPorte;
                            System.out.println("la demande avec ID "  + comming.Id() + " est en phase d'exec pendant " + comming.TimeSpan);
                            break;

                        }


                    }

                }


            }

        }

    }
    private Ressource initRessource (int id , int procId , int timeSpan ,Instant instat , TypeResource type ){
        Ressource r1 = new Ressource();
        r1.id = id;
        r1.name= "Ressource N° " + id;
        r1.type = type;
        r1.Start = instat;
        r1.TimeSpan = timeSpan;
        r1.NodeId = procId;
        r1.Occupied =true;
        //Main.ressources.add(r1);
        return  r1;

    }
     // reference http://www.cs.colostate.edu/~cs551/CourseNotes/Synchronization/BullyExample.html
    void  RunBullyElection(Prog p0 ) throws InterruptedException {

        //Main.progs.get(5).enPanne = true;
        for (int y = p0.index; y < Main.progs.size(); y++) {
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
            if (p0==Main.progs.get(y)) {
                int x  =p0.index +1;
                while (k<channels.length) {
                    if (x > 5) break;
                    p0.message = null;
                    p0.message = new Msg("message de séléction", p0.index);
                    p0.out(channels[k]).send(p0.message);
                    System.out.println(p0.index + " Msg sent to the process N°:" + x);
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
                                    p0.reponsesElections.add(r);
                                }
                            }

                        }

                    }
                    x++;
                    k++;


                }
                for (Response res: p0.reponsesElections) {
                    if (res.Response=="Ok") { AnnyOk= true; break;}

                }
                //
                if (AnnyOk ==false ||p0.reponsesElections.size()==0) {

                    // le leader envoi le resultat d'éléction aux autres processs
                    p0.message =  new Msg("I am the leader ! " +p0.index  , p0.index);
                    System.out.println(p0.message.getValue());
                    p0.out().send(p0.message);
                    p0.isMaster = true;
                    Main.progs.forEach((p) -> {
                        p.masterElected = true;
                    });
                    p0.ressources=Main.ressources;
                    p0.queue = new LinkedList<>();
                    if (Main.queue.size()!=0) p0.queue = Main.queue;
                    p0.yield();
                    for(Prog node: Main.progs){
                        if (node!= p0){
                            node.message=(Msg)node.in(p0.index-1).receive();
                            for(Neighbor b:node.neighbors)
                                if (b.node== p0.index)b.isMaster =true;

                        }


                    }
                    break;

                }
                try {
                    p0 = Main.progs.get(y+1);
                }catch (Exception ex) {break;}

            }
        }




    }

    private void addRessources() {
        Random r = new Random();
        int s =0;
        for (TypeResource rs:TypeResource.values()) {
            for(int k =s; k<r.nextInt(5 ) ; k++){
                Ressource r1  = new Ressource();
                r1.id = s;
                r1.name= "Ressource N° " + s;
                r1.type = rs;
                Main.ressources.add(r1);
                s++;

            }    //To change body of gener
        }
      
    }




}
