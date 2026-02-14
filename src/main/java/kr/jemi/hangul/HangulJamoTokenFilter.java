package kr.jemi.hangul;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public final class HangulJamoTokenFilter extends TokenFilter {

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

    public HangulJamoTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        char[] buffer = termAttr.buffer();
        int length = termAttr.length();

        if (!HangulUtil.containsHangulSyllable(buffer, length)) {
            return true;
        }

        char[] result = HangulUtil.decompose(buffer, length);
        termAttr.copyBuffer(result, 0, result.length);
        return true;
    }
}
