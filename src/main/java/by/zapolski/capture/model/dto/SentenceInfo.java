package by.zapolski.capture.model.dto;

import lombok.Data;

@Data
public class SentenceInfo {
    private String source;
    private String[] tokens;
    private String[] tags;
    private String[] lemmas;
    private Integer rank;
}
