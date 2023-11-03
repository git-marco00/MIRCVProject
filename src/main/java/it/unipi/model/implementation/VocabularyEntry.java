package it.unipi.model.implementation;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class VocabularyEntry {

    private Integer frequency;
    private PostingList postingList;
    private Double upperBound;

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Double getUpperBound() {
        return upperBound;
    }

    public PostingList getPostingList() {
        return postingList;
    }

    public void setPostingList(PostingList postingList) {
        this.postingList = postingList;
    }

    public void setUpperBound(Double upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public String toString(){
        return "Frequency: "+frequency+" UpperBound: "+upperBound+" PostingList: "+postingList.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VocabularyEntry that = (VocabularyEntry) o;
        return Objects.equals(frequency, that.frequency) && Objects.equals(postingList, that.postingList) && Objects.equals(upperBound, that.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frequency, postingList, upperBound);
    }
}
