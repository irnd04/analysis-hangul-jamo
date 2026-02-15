package kr.jemi.hangul;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HangulUtilTest {

    @Test
    void decompose_이재국() {
        char[] input = "이재국".toCharArray();
        assertEquals("ㅇㅣㅈㅐㄱㅜㄱ", new String(HangulUtil.decompose(input, input.length)));
    }

    @Test
    void decompose_이재수_종성없는음절포함() {
        char[] input = "이재수".toCharArray();
        char[] result = HangulUtil.decompose(input, input.length);
        assertEquals("ㅇㅣㅈㅐㅅㅜ", new String(result));
    }

    @Test
    void decompose_김점숙_모든음절에종성있음() {
        char[] input = "김점숙".toCharArray();
        char[] result = HangulUtil.decompose(input, input.length);
        assertEquals("ㄱㅣㅁㅈㅓㅁㅅㅜㄱ", new String(result));
    }

    @Test
    void decompose_mixed() {
        char[] input = "A이B".toCharArray();
        assertEquals("AㅇㅣB", new String(HangulUtil.decompose(input, input.length)));
    }

    @Test
    void decompose_alreadyJamo() {
        char[] input = "ㄱㅏ".toCharArray();
        assertEquals("ㄱㅏ", new String(HangulUtil.decompose(input, input.length)));
    }

    @Test
    void decompose_empty() {
        char[] result = HangulUtil.decompose(new char[0], 0);
        assertEquals(0, result.length);
    }

    @Test
    void extractChosung_이재국() {
        char[] input = "이재국".toCharArray();
        assertEquals("ㅇㅈㄱ", new String(HangulUtil.extractChosung(input, input.length)));
    }

    @Test
    void extractChosung_bareJamoPassthrough() {
        char[] input = "ㅇㅈㄱ".toCharArray();
        assertEquals("ㅇㅈㄱ", new String(HangulUtil.extractChosung(input, input.length)));
    }

    @Test
    void extractChosung_mixed() {
        char[] input = "A이B재".toCharArray();
        assertEquals("AㅇBㅈ", new String(HangulUtil.extractChosung(input, input.length)));
    }
}
