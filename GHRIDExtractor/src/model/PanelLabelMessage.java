package model;

public class PanelLabelMessage {
    private String id;
    private String partialTrigger;
    private String label;
    public PanelLabelMessage(String id, String label, String partialTrigger) {
        this.id = id;
        this.label=label;
        this.partialTrigger = partialTrigger;
    }


    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getPartialTrigger() {
        return partialTrigger;
    }
}
