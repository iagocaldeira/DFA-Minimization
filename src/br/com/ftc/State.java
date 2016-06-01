package br.com.ftc;

public class State
{
    private String id;

    private Boolean isInitial;
    
    private Boolean isFinal;

    private String name;

    private String label;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Boolean getIsInitial ()
    {
        return isInitial;
    }

    public void setIsInitial (Boolean isInitial)
    {
        this.isInitial = isInitial;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return "[id = "+id+", initial = "+isInitial+", final = "+isFinal+", name = "+name+"]";
    }

    public State(String id, Boolean isInitial, Boolean isFinal, String name) {
        this.id = id;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        this.name = name;
        this.label = "";
    }
    
    public State(){
    }
    
}
