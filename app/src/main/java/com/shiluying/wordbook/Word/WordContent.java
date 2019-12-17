package com.shiluying.wordbook.Word;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample word for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class WordContent {
    public static final List<WordItem> ITEMS = new ArrayList<WordItem>();

    public static class WordItem {
        public final String word;
        public String meaning="";
        public WordItem(String word) {
            this.word = word;
        }
        public WordItem(String word,String meaning) {
            this.word = word;
            this.meaning=meaning;
        }
        @Override
        public String toString() {
            return word;
        }
    }
}
