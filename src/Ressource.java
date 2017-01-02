/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Time;
import java.time.Instant;

/**
 *
 * @author Khaled
 */
public class Ressource {
    int id;
    TypeResource type;
    String name;
    public int NodeId=-1;
    public boolean Occupied;
    public Instant Start;

    public int TimeSpan;
    public int Remain;
    public int  Id (){return id;}
    public TypeResource  Type (){return type;}
    public String  Name (){return name;}
}
enum TypeResource {
    Imprimante,
    Scanner,
    Photocopieuse,
    ThreeDPrinter

}