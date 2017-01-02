import daj.Application;
import daj.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pen on 01/01/2017.
 */
public class Main extends Application
{

    public static List<Prog> progs;
    public static List<Ressource> ressources;
    public static void main(String[] args) throws InterruptedException {



        // TODO code application logic here
        new Main().run();
       // RunBullySelection(progs.get(0));
    }
         public Main()
    {
        super("Mini projet", 1000, 1600);
        // ListeOfNodes =
    }
    @Override
    public void construct() {
        progs =  new LinkedList<>();
        Prog p0= new Prog(0);
        Prog p1= new Prog(1);
        Prog p2= new Prog(2);
        Prog p3= new Prog(3);
        Prog p4= new Prog(4);
        Prog p5= new Prog(5);
        Node node0 = node(p0, "0", 20, 100);
        Node node1 = node(p1, "1",505, 100);
        Node node2 = node(p2, "2", 20, 300);
        Node node3 = node(p3, "3", 300, 50);
        Node node4 = node(p4, "4", 350, 300);
        Node node5 = node(p5, "5", 240, 170);


        link(node0, node1);
        link(node0, node2);
        link(node0, node3);
        link(node0, node4);
        link(node0, node5);

        link(node1, node0);
        link(node1, node2);
        link(node1, node3);
        link(node1, node4);
        link(node1, node5);

        link(node2, node0);
        link(node2, node1);
        link(node2, node3);
        link(node2, node4);
        link(node2, node5);

        link(node3, node0);
        link(node3, node1);
        link(node3, node2);
        link(node3, node4);
        link(node3, node5);

        link(node4, node0);
        link(node4, node1);
        link(node4, node2);
        link(node4, node3);
        link(node4, node5);

        link(node5, node0);
        link(node5, node1);
        link(node5, node2);
        link(node5, node3);
        link(node5, node4);
        progs.add(p0);
        progs.add(p1);
        progs.add(p2);
        progs.add(p3);
        progs.add(p4);
        progs.add(p5);


    }
}
