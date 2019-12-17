package com.shiluying.wordbook.enity;

public class Record {
    private String word;
    private String wordMeaning;
    private String wordSample;
    private String wordphonetic;
    private String wordtype;
    public  Record(){

    }
    public Record(String word,String wordMeaning,String wordphonetic,String wordSample,String wordtype){
        this.word = word;
        this.wordMeaning = wordMeaning;
        this.wordphonetic = wordphonetic;
        this.wordSample = wordSample;
        this.wordtype = wordtype;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }

    public String getWordphonetic() {
        return wordphonetic;
    }

    public void setWordphonetic(String wordphonetic) {
        this.wordphonetic = wordphonetic;
    }

    public String getWordSample() {
        return wordSample;
    }

    public void setWordSample(String wordSample) {
        this.wordSample = wordSample;
    }
    @Override
    public String toString() {
        return "Record{" +
                ", word='" + word + '\'' +
                ", wordmeaning='" + wordMeaning + '\'' +
                ", wordphonetic='" + wordphonetic + '\'' +
                ", wordsample='" + wordSample + '\'' +
                ", wordtype='" + wordtype + '\'' +
                '}';
    }

    public String getWordtype() {
        return wordtype;
    }

    public void setWordtype(String wordtype) {
        this.wordtype = wordtype;
    }
}

