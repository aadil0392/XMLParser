package model;

public class PanelFormContainer {
    private String id;

    public PanelFormContainer(String id, String partialTrigger) {
        this.id = id;
        this.partialTrigger = partialTrigger;
    }

    private String partialTrigger;

    public String getId() {
        return id;
    }

    public String getPartialTrigger() {
        return partialTrigger;
    }
}
