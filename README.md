# DFA Minimization using Myphill-Nerode Theorem

##Algorithm

###Input:	DFA
###Output:	Minimized DFA
####Step 1	
Draw a table for all pairs of states (Qi, Qj) not necessarily connected directly [All are unmarked initially]
####Step 2	
Consider every state pair (Qi, Qj) in the DFA where Qi ∈ F and Qj ∉ F or vice versa and mark them. [Here F is the set of final states].
####Step 3	
Repeat this step until we cannot mark anymore states −

If there is an unmarked pair (Qi, Qj), mark it if the pair {δ(Qi, A), δ (Qi, A)} is marked for some input alphabet.

####Step 4	
Combine all the unmarked pair (Qi, Qj) and make them a single state in the reduced DFA.

Font: http://www.tutorialspoint.com/automata_theory/dfa_minimization.htm
