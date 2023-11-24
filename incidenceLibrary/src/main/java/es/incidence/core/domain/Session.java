package es.incidence.core.domain;

public class Session
{
    public String id;
    public String manufacturer;
    public String model;

    public String getName()
    {
        String res = "";

        if (manufacturer != null)
        {
            res = (manufacturer.equalsIgnoreCase("apple")) ? "" : manufacturer;
        }

        if (model != null)
        {
            if (res.length() > 0) {
                res += " " + model;
            }
            else
            {
                res = model;
            }
        }

        return res;
    }
}
