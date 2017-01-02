import java.util.LinkedList;
import java.util.List;

/**
 * Created by pen on 01/01/2017.
 */
public class Toppology {
    public Toppology (){
        this.neighbors =  new LinkedList<>();
    }
    int currentPros ;
    List<Neighbor> neighbors;
    public  int CurrentNode () { return  currentPros;}
    public List<Neighbor> Neighbors (){return neighbors;}
    public  void AddNeighbors (Neighbor neighbor){
        neighbors.add(neighbor);
    }

}

