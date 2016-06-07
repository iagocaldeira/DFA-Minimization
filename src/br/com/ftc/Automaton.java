package br.com.ftc;

import com.sun.org.apache.xpath.internal.operations.And;

import javax.print.DocFlavor;
import java.util.*;

import static javafx.scene.input.KeyCode.T;

public class Automaton
{
    private List<State> states;

    private List<Transition> transitions;

    private Set<String> alphabet;

    private Set<State> initialStates;

    private Set<State> finalStates;

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }
    
    //verify if the state is a deadState
    private boolean isDeadState(List<Transition> transitions, State state){

        List<Transition> stateTransitionsList = new ArrayList<Transition>();
        for (Transition tr : transitions) {
            if(tr.getFrom().equals(state.getId())){
                stateTransitionsList.add(tr);
            }
        }

        for (Transition tt : stateTransitionsList) {
            if(!(tt.getFrom().equals(tt.getTo())) || state.getIsFinal()){
                return false;
            }
        }

        return true;
    }

    //look at the transitions and verify is there's some transition missing
    //if so, create the remaining transitions pointing to the dead state
    public void remainingTransitions() {
        List<Transition> transitionsList = new ArrayList<>(this.transitions);
        List<State> statesList = new ArrayList<>(this.states);
        State deadState = null;

        for(ListIterator<State>iteratorS = statesList.listIterator(); iteratorS.hasNext();) {
            State s = iteratorS.next();
            if(this.isDeadState(transitionsList, s)){
                deadState = s;
            }
        }

        if(deadState == null){
            String deadStateId = Integer.toString(this.states.size());
            deadState = new State(deadStateId, false, false, deadStateId);
            statesList.add(deadState);
        }

        Iterator iteratorA = alphabet.iterator();
        while(iteratorA.hasNext()){
            boolean flag = false;
            String symbol = (String) iteratorA.next();
            List<Transition> tran = transitionsList;
            Collections.sort(tran, new Comparator<Transition>() {
                public int compare(Transition t1, Transition t2) {
                    if(Integer.parseInt(t1.getFrom()) > Integer.parseInt(t2.getFrom()))
                        return +1;
                    else if(Integer.parseInt(t1.getFrom()) < Integer.parseInt(t2.getFrom()))return -1;
                    else return 0;
                }
            });
            for(ListIterator<State>iteratorS = statesList.listIterator(); iteratorS.hasNext();) {
                State s = iteratorS.next();
                flag = false;
                for(Transition t : transitionsList){
                    if((t.getFrom().equals(s.getId())) && (t.getRead().equals(symbol))){
                        flag = true;
                    }
                }
                if(!flag){
                    transitionsList.add(new Transition(s.getId(), deadState.getId(), symbol));
                }
            }

        }

        this.setTransitions(transitionsList);
        this.setStates(statesList);
    }

    public void removeTransitionsDuplicates() {
        List<Transition> list = new ArrayList<>(this.transitions);
        for(ListIterator<Transition>iterator = list.listIterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            if(Collections.frequency(list, t) > 1) {
                iterator.remove();
            }
        }
        this.setTransitions(list);
    }


    public void removeStatesWithoutTransitions() {
        Set<String> usefulStates = new HashSet<>();
        List<State> listS = new ArrayList<>(this.states);
        List<Transition> listT = new ArrayList<>(this.transitions);

        for(ListIterator<Transition>iterator = listT.listIterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            usefulStates.add(t.getFrom());
            usefulStates.add(t.getTo());
        }

        for(ListIterator<State>iterator = listS.listIterator(); iterator.hasNext();) {
            State t = iterator.next();
            if(t.getLabel().length() > 0 ){
                Integer size = 0;
                for( String s : usefulStates){
                    if(t.getLabel().contains(s)){
                        size++;
                    }
                }
                if(size == 0){
                    iterator.remove();
                }
            }
        }

        this.setStates(listS);
    }

    public void removeStatesDuplicates() {
        List<State> list = new ArrayList<>(this.states);
        for(ListIterator<State>iterator = list.listIterator(); iterator.hasNext();) {
            State t = iterator.next();
            Integer size = 0;
            State auxState = new State();
            for(ListIterator<State>iterator2 = list.listIterator(); iterator2.hasNext();) {
                State t2 =iterator2.next();
                if(t2.toString().equals(t.toString())){
                    System.out.println("Duped States \n ["+t.toString()+","+t2.toString()+"]");
                    size++;
                    auxState = t2;
                }
            }
            if(size > 1) {
                auxState.setLabel(auxState.getLabel()+"|"+t.getLabel());
                iterator.remove();
            }
        }
        this.setStates(list);
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<String> alphabet) {
        this.alphabet = alphabet;
    }

    public Set<State> getInitialStates() {
        return initialStates;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    @Override
    public String toString()
    {
        return "[state = "+ states +", transition = "+ transitions +"]";
    }

    public Automaton(List<State> states, List<Transition> transitions, Set<String> alphabet, Set<State> initialStates,Set<State> finalStates) {
        this.states = states;
        this.transitions = transitions;
        this.alphabet = alphabet;
        this.finalStates = finalStates;
        this.initialStates = initialStates;

    }

    public State findState(String id){
        for(State s : this.states) {
            if(s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

    // Returns State after transition
    public State deltaTransition(String from, String input) {
        for(Transition tr: this.transitions) {
            if(tr.getFrom().equals(from) && tr.getRead().equals(input)) {
                return findState(tr.getTo());
            }
        }
        return null;
    }

//    public List transitionTable(){
//        List<List<String>> trTable = new ArrayList<>();
//        for(State s : this.states) {
//            List<String> row = new ArrayList<>();
//            row.add(s.getId());
//            Iterator<String> a = this.alphabet.iterator();
//            while (a.hasNext()) {
//                String i = a.next();
//                row.add(deltaTransition(s.getId(), i).getId());
//            }
//            trTable.add(row);
//        }
//        return trTable;
//    }

    // equivalentStates
    public Set equivalentStates(){
        Boolean[][] hopcroftTable = new Boolean[this.states.size()][this.states.size()];
        Set<List<State>> eqStates = new HashSet<>();

        // Step 1 - fill table with false
        for (int i = 0; i < this.states.size() ; i++) {
            Arrays.fill(hopcroftTable[i], false);
        }

        // Step 2 - mark all final pair states (Qi ∈ F and Qj ∉ F)||(Qi ∉ F and Qj ∈ F)
        for (int i = 1; i < this.states.size(); i++) {
            State qi = findState(Integer.toString(i));
            for (int j = 0; j < i; j++) {
                State qj = findState(Integer.toString(j));
                if ((this.finalStates.contains(qi) && !this.finalStates.contains(qj)) ||
                        (!this.finalStates.contains(qi) && this.finalStates.contains(qj))) {
                    hopcroftTable[i][j] = true;
                }
            }
        }

        // Step 3 - mark it if the pair {δ(Qi, A), δ (Qj, A)} is marked for some input alphabet.
        for (int i = 0; i < this.states.size(); i++) {
            String qi = findState(Integer.toString(i)).getId();
            for (int j = 0; j < i; j++) {
                String qj = findState(Integer.toString(j)).getId();
                Iterator<String> a = this.alphabet.iterator();
                while (a.hasNext()) {
                    String input = a.next();
                    Integer dqj = Integer.parseInt(deltaTransition(qj,input).getId());
                    Integer dqi = Integer.parseInt(deltaTransition(qi,input).getId());
                    if(hopcroftTable[dqi][dqj]){
                        hopcroftTable[i][j] = true;
                    }
                }
            }
        }

        // Step 4 - add equivalent states to list
        for (int i = 0; i < this.states.size(); i++) {
            for (int j = 0; j < i; j++) {
                if(!hopcroftTable[i][j]){
                    eqStates.add(Arrays.asList(findState(Integer.toString(i)),findState(Integer.toString(j))));
                }
            }
        }

        for (int i = 0; i < this.states.size(); i++) {
            System.out.println();
            for (int j = 0; j < this.states.size(); j++)
                System.out.print("|"+hopcroftTable[i][j]+"\t|");
        }
        System.out.println();
        return eqStates;
    }

    public void joinStates(List<State> stateTuple){
        List<State> list = new ArrayList<>(this.states);
        State joinSt = new State(
                stateTuple.get(1).getId(),
                (stateTuple.get(0).getIsInitial() || stateTuple.get(1).getIsInitial()),
                (stateTuple.get(0).getIsFinal() || stateTuple.get(1).getIsFinal()),
                stateTuple.get(1).getName()
        );
        joinSt.setLabel(stateTuple.get(0).getId()+","+stateTuple.get(1).getId());
        list.add(joinSt);

        for(ListIterator<State>iterator = list.listIterator(); iterator.hasNext();) {
            State t = iterator.next();
            if(Collections.frequency(list, t) > 1) {
                iterator.remove();
            }
        }
        list.removeAll(stateTuple);

        this.states = list;
    }

    public void joinEquals(Set<List<State>> eqStates) {
        for (List<State> stateTuple : eqStates) {
            // Find and rewrite states from tuple
            this.joinStates(stateTuple);

            removeStatesDuplicates();
            removeStatesWithoutTransitions();

            // Find and rewrite transitions
            for(Transition t : this.transitions) {
                if (stateTuple.get(0).getId().equals(t.getTo()) || stateTuple.get(1).getId().equals(t.getTo())) {
                    t.setTo(stateTuple.get(1).getId());
                }
                if (stateTuple.get(0).getId().equals(t.getFrom()) || stateTuple.get(1).getId().equals(t.getFrom())) {
                    t.setFrom(stateTuple.get(1).getId());
                }
            }
            this.removeTransitionsDuplicates();
        }
    }
}
