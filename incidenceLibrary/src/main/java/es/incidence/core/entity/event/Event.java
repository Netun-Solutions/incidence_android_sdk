package es.incidence.core.entity.event;

public class Event
{
    public EventCode code;
    public Object object;

    public Event(EventCode code)
    {
        this.code = code;
        this.object = null;
    }

    public Event(EventCode code, Object object)
    {
        this.code = code;
        this.object = object;
    }
}
