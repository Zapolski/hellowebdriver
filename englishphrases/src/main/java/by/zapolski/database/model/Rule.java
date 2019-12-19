package by.zapolski.database.model;

import lombok.ToString;

@ToString(callSuper = true)
public class Rule extends Entity {
    private String value;

    public Rule() {
    }

    public Rule(int id, String value) {
        super(id);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
