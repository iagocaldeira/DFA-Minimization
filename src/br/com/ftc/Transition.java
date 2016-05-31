package br.com.ftc;

public class Transition
{
    private String from;

    private String to;

    private String read;

    public String getTo ()
    {
        return to;
    }

    public void setTo (String to)
    {
        this.to = to;
    }

    public String getRead ()
    {
        return read;
    }

    public void setRead (String read)
    {
        this.read = read;
    }

    public String getFrom ()
    {
        return from;
    }

    public void setFrom (String from)
    {
        this.from = from;
    }

    @Override
    public String toString()
    {
        return "[from = "+from+", to = "+to+", read = "+read+"]";
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) //Check if they're the same object
            return true;
        else if(this.toString().equals(obj.toString()))
            return true;
        else if(!(obj instanceof Transition))
            return false;
        else
            return false;
    }

    public Transition(String from, String to, String read) {
        this.from = from;
        this.to = to;
        this.read = read;
    }
}