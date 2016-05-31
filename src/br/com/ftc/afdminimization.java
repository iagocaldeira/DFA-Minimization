package br.com.ftc;


import java.util.List;
import java.util.Set;

public class afdminimization {
    public static void main(String argv[]) {
        Structure fa = new Structure("C:\\Users\\sala\\Desktop\\teste1.jff");
        Automaton au = fa.getAutomaton();

        Set<List<State>> eqStates = au.equivalentStates();

        for (List<State> stateTuple: eqStates)
            System.out.println("Eq States \n ["+stateTuple.get(0).getName()+","+stateTuple.get(1).getName()+"]");

        au.joinEquals(eqStates);

        fa.writeXML("C:\\Users\\sala\\Desktop\\teste1aaaaaaaaaaaaaaaaa.jff");
    }
}