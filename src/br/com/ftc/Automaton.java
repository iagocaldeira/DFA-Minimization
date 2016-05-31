package br.com.ftc;

import java.util.*;

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

    public void removeTransitionsDuplicates() {
        List<Transition> list = new ArrayList<>(this.transitions);
        for(ListIterator<Transition>iterator = list.listIterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            if(Collections.frequency(list, t) > 1) {
                iterator.remove();
            }
        }
        this.transitions = list;
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
//                    Integer dqj = Integer.parseInt(deltaTransition(qj,input).getId());
//                    Integer dqi = Integer.parseInt(deltaTransition(qi,input).getId());
                    if(!hopcroftTable[i][j]){
//                        System.out.println("{"+qi+","+qj+"}");
//                        System.out.println("(qi,"+input+") -> "+dqi);
//                        System.out.println("(qj,"+input+") -> "+dqj);
//                        System.out.println("--");
                        eqStates.add(Arrays.asList(findState(Integer.toString(i)),findState(Integer.toString(j))));
                    }
                }
            }
        }
//        for (int i = 0; i < this.states.size(); i++) {
//            System.out.println();
//            for (int j = 0; j < this.states.size(); j++)
//                System.out.print("|"+hopcroftTable[i][j]+"\t|");
//        }
//        System.out.println();
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