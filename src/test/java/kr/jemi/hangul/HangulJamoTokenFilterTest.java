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

class HangulJamoTokenFilterTest {

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
            List<String> tokens = tokenize(new HangulJamoTokenFilter(tokenizer));
            return tokens.isEmpty() ? "" : tokens.get(0);
        }
    }

    @Test
    void fullDecomposition_이재국() throws IOException {
        assertEquals("ㅇㅣㅈㅐㄱㅜㄱ", analyze("이재국"));
    }

    @Test
    void partialSyllable_잊() throws IOException {
        assertEquals("ㅇㅣㅈ", analyze("잊"));
    }

    @Test
    void asciiPassthrough() throws IOException {
        assertEquals("hello", analyze("hello"));
    }

    @Test
    void mixedContent() throws IOException {
        assertEquals("ㄱㅣㅁAㅊㅣ", analyze("김A치"));
    }

    @Test
    void alreadyJamoPassthrough() throws IOException {
        assertEquals("ㅇㅈㄱ", analyze("ㅇㅈㄱ"));
    }

    @Test
    void emptyInput() throws IOException {
        assertEquals("", analyze(""));
    }
}
