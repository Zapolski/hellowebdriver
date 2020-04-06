package by.zapolski.database.model;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
public class Example extends Entity {
    private Integer wordId;
    private String russian;
    private String english;
    private String sound;
    private Integer ruleId;
    private Integer rank;
}
