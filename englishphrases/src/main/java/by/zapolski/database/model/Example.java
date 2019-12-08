package by.zapolski.database.model;

import lombok.ToString;

@ToString(callSuper = true)
public class Example extends Entity{
    private Integer wordId;
    private String russian;
    private String english;
    private String sound;
    private Integer ruleId;

    public Example(int id, Integer wordId, String russian, String english, String sound, Integer ruleId) {
        super(id);
        this.wordId = wordId;
        this.russian = russian;
        this.english = english;
        this.sound = sound;
        this.ruleId = ruleId;
    }

    public Example() {
    }

    public Integer getWordId() {
        return wordId;
    }

    public void setWordId(Integer wordId) {
        this.wordId = wordId;
    }

    public String getRussian() {
        return russian;
    }

    public void setRussian(String russian) {
        this.russian = russian;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }
}
