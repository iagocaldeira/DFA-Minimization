package br.com.ftc;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Structure
{
    private Automaton automaton;

    private String type;

    public Automaton getAutomaton ()
    {
        return automaton;
    }

    public void setAutomaton (Automaton automaton)
    {
        this.automaton = automaton;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "[automaton = "+automaton+", type = "+type+"]";
    }

    public Structure(String fileURI){
        try {
            File fXmlFile = new File(fileURI);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            String type = doc.getElementsByTagName("type").item(0).getTextContent();
            NodeList stateNList = doc.getElementsByTagName("state");
            NodeList transitionNList = doc.getElementsByTagName("transition");

            List<State> states = new ArrayList<>();
            List<Transition> transitions = new ArrayList<>();
            Set<String> alphabet = new HashSet<>();
            Set<State> initialStates = new HashSet<>();
            Set<State> finalStates = new HashSet<>();


            //State loop
            for (int x = 0; x < stateNList.getLength(); x++) {
                Node nNode = stateNList.item(x);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    states.add( new State(
                            eElement.getAttribute("id"),
                            (eElement.getElementsByTagName("initial").getLength() > 0),
                            (eElement.getElementsByTagName("final").getLength() > 0),
                            eElement.getAttribute("name")
                    ));
                    if((eElement.getElementsByTagName("final").getLength() > 0)){
                        finalStates.add(states.get(states.size()-1));
                    }
                    if((eElement.getElementsByTagName("initial").getLength() > 0)){
                        initialStates.add(states.get(states.size()-1));
                    }

//                    System.out.println(states.get(states.size()-1).toString());
                }
            }

            //Transition loop
            for (int y = 0; y < transitionNList.getLength(); y++) {
                Node nNode = transitionNList.item(y);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    transitions.add(new Transition(
                            eElement.getElementsByTagName("from").item(0).getTextContent(),
                            eElement.getElementsByTagName("to").item(0).getTextContent(),
                            eElement.getElementsByTagName("read").item(0).getTextContent()
                    ));
                    alphabet.add(eElement.getElementsByTagName("read").item(0).getTextContent());
                }
//                System.out.println(transitions.get(transitions.size()-1).toString());
            }

            this.setAutomaton(new Automaton(states,transitions,alphabet,initialStates,finalStates));
            this.setType(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeXML(String fileURI){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // structure
            Document doc = dBuilder.newDocument();
            Element structureElement = doc.createElement("structure");
            doc.appendChild(structureElement);

            // type
            Element typeElement = doc.createElement("type");
            typeElement.appendChild(doc.createTextNode("fa"));
            structureElement.appendChild(typeElement);

            // automaton
            Element automatonElement = doc.createElement("automaton");
            structureElement.appendChild(automatonElement);

            // comment
            automatonElement.appendChild(doc.createComment("The list of states."));

            // states

            for (State st : this.automaton.getStates()) {
                Element stateElement = doc.createElement("state");
                stateElement.setAttribute("id",st.getId());
                stateElement.setAttribute("name",st.getName());
                automatonElement.appendChild(stateElement);

                if(!st.getLabel().equals("")) {
                    Element label = doc.createElement("label");
                    label.appendChild(doc.createTextNode(st.getLabel()));
                    stateElement.appendChild(label);
                }
                if(st.getIsInitial()) {
                    stateElement.appendChild(doc.createElement("initial"));
                }
                if(st.getIsFinal()) {
                    stateElement.appendChild(doc.createElement("final"));
                }

            }

            // comment
            automatonElement.appendChild(doc.createComment("The list of transitions."));

            for (Transition t : this.automaton.getTransitions()) {
                Element transitionElement = doc.createElement("transition");
                automatonElement.appendChild(transitionElement);

                //from
                Element from = doc.createElement("from");
                from.appendChild(doc.createTextNode(t.getFrom()));
                transitionElement.appendChild(from);
                //to
                Element to = doc.createElement("to");
                to.appendChild(doc.createTextNode(t.getTo()));
                transitionElement.appendChild(to);
                //read
                Element read = doc.createElement("read");
                read.appendChild(doc.createTextNode(t.getRead()));
                transitionElement.appendChild(read);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileURI));
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}