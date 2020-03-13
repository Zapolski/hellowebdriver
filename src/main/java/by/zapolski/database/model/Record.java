package by.zapolski.database.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString (callSuper = true)
public class Record extends Entity{
    private String word;
    private String russian;
    private String english;
    private String soundPath;
    private String rule;
}
