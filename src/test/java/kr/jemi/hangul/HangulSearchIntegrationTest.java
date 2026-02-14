package kr.jemi.hangul;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * "아디다스 운동화" 검색 시나리오 통합 테스트.
 */
class HangulSearchIntegrationTest {

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

    private List<String> analyzeWithJamo(String text) throws IOException {
        try (StandardTokenizer tokenizer = new StandardTokenizer()) {
            tokenizer.setReader(new StringReader(text));
            return tokenize(new HangulJamoTokenFilter(tokenizer));
        }
    }

    private List<String> analyzeWithChosung(String text) throws IOException {
        try (StandardTokenizer tokenizer = new StandardTokenizer()) {
            tokenizer.setReader(new StringReader(text));
            return tokenize(new HangulChosungTokenFilter(tokenizer));
        }
    }

    private String searchWithJamo(String query) throws IOException {
        try (KeywordTokenizer tokenizer = new KeywordTokenizer()) {
            tokenizer.setReader(new StringReader(query));
            List<String> tokens = tokenize(new HangulJamoTokenFilter(tokenizer));
            return tokens.isEmpty() ? "" : tokens.get(0);
        }
    }

    private String searchWithChosung(String query) throws IOException {
        try (KeywordTokenizer tokenizer = new KeywordTokenizer()) {
            tokenizer.setReader(new StringReader(query));
            List<String> tokens = tokenize(new HangulChosungTokenFilter(tokenizer));
            return tokens.isEmpty() ? "" : tokens.get(0);
        }
    }

    @Test
    void 초성검색_ㅇㄷㅎ으로_운동화_매칭() throws IOException {
        // 인덱싱: "아디다스 운동화" → chosung 필터 → 토큰별 초성 추출
        List<String> indexedTokens = analyzeWithChosung("아디다스 운동화");
        assertEquals(List.of("ㅇㄷㄷㅅ", "ㅇㄷㅎ"), indexedTokens);

        // 검색: "ㅇㄷㅎ" → chosung 필터 → 이미 자모라 그대로
        String searchTerm = searchWithChosung("ㅇㄷㅎ");
        assertEquals("ㅇㄷㅎ", searchTerm);

        // 매칭: 인덱싱된 "ㅇㄷㅎ"과 검색어 "ㅇㄷㅎ" 일치
        assertTrue(indexedTokens.contains(searchTerm));
    }

    @Test
    void 자모검색_운ㄷ으로_운동화_매칭() throws IOException {
        // 인덱싱: "아디다스 운동화" → jamo 필터 → 토큰별 자모 분해
        List<String> indexedTokens = analyzeWithJamo("아디다스 운동화");
        assertEquals(List.of("ㅇㅏㄷㅣㄷㅏㅅㅡ", "ㅇㅜㄴㄷㅗㅇㅎㅘ"), indexedTokens);

        // 검색: "운ㄷ" → jamo 필터 → "ㅇㅜㄴㄷ"
        String searchTerm = searchWithJamo("운ㄷ");
        assertEquals("ㅇㅜㄴㄷ", searchTerm);

        // 매칭: "ㅇㅜㄴㄷ"은 "ㅇㅜㄴㄷㅗㅇㅎㅘ"의 prefix → edge_ngram으로 매칭
        assertTrue(indexedTokens.stream().anyMatch(token -> token.startsWith(searchTerm)));
    }
}
