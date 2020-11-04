package model;

public class Component {
    private String id;
    private String label;
    private String partialTrigger;
    public Component(String id, String label, String partialTrigger)
    {
        this.id=id;
        this.label=label;
        this.partialTrigger=partialTrigger;
    }
    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getPartialTrigger()
    {
        return partialTrigger;
    }

}
