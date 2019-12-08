package by.zapolski.database.model;

import lombok.Data;

@Data
public class Record {
    private String word;
    private String russian;
    private String english;
    private String soundPath;
    private String rule;
}
