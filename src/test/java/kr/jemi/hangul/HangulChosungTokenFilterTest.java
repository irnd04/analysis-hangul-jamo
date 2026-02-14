package kr.jemi.hangul;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HangulChosungTokenFilterTest {

    private List<String> tokenize(TokenStream stream) throws IOException {
        List<String> tokens = new ArrayList<>();
        CharTermAttribute termAttr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            tokens.add(termAttr.toString());
        }
        stream.end();
        return tokens;
    }

    private String analyze(String input) throws IOException {
        try (KeywordTokenizer tokenizer = new KeywordTokenizer()) {
            tokenizer.setReader(new StringReader(input));
            List<String> tokens = tokenize(new HangulChosungTokenFilter(tokenizer));
            return tokens.isEmpty() ? "" : tokens.get(0);
        }
    }

    @Test
    void chosungExtraction_이재국() throws IOException {
        assertEquals("ㅇㅈㄱ", analyze("이재국"));
    }

    @Test
    void asciiPassthrough() throws IOException {
        assertEquals("hello", analyze("hello"));
    }

    @Test
    void bareJamoPassthrough() throws IOException {
        assertEquals("ㅇㅈㄱ", analyze("ㅇㅈㄱ"));
    }

    @Test
    void mixedContent() throws IOException {
        assertEquals("ㄱAㅊ", analyze("김A치"));
    }

    @Test
    void singleSyllable() throws IOException {
        assertEquals("ㅎ", analyze("한"));
    }

    @Test
    void emptyInput() throws IOException {
        assertEquals("", analyze(""));
    }
}
