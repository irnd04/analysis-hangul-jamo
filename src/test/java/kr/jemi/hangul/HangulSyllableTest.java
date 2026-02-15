package kr.jemi.hangul;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HangulSyllableTest {

    @Test
    void isHangulSyllable() {
        assertTrue(HangulSyllable.isHangulSyllable('가'));
        assertTrue(HangulSyllable.isHangulSyllable('힣'));
        assertTrue(HangulSyllable.isHangulSyllable('이'));
        assertFalse(HangulSyllable.isHangulSyllable('A'));
        assertFalse(HangulSyllable.isHangulSyllable('ㄱ'));
        assertFalse(HangulSyllable.isHangulSyllable('ㅏ'));
    }

    @Test
    void of_종성없는음절() {
        HangulSyllable s = HangulSyllable.of('이');
        assertEquals('ㅇ', s.getChosung());
        assertEquals('ㅣ', s.getJungsung());
        assertFalse(s.hasJongsung());
    }

    @Test
    void of_종성있는음절() {
        HangulSyllable s = HangulSyllable.of('국');
        assertEquals('ㄱ', s.getChosung());
        assertEquals('ㅜ', s.getJungsung());
        assertEquals('ㄱ', s.getJongsung());
        assertTrue(s.hasJongsung());
    }

    @Test
    void hasJongsung_종성있음() {
        assertTrue(HangulSyllable.of('한').hasJongsung());   // ㄴ
        assertTrue(HangulSyllable.of('국').hasJongsung());   // ㄱ
        assertTrue(HangulSyllable.of('잊').hasJongsung());   // ㅈ
        assertTrue(HangulSyllable.of('닭').hasJongsung());   // ㄺ (겹받침)
    }

    @Test
    void hasJongsung_종성없음() {
        assertFalse(HangulSyllable.of('이').hasJongsung());
        assertFalse(HangulSyllable.of('가').hasJongsung());
        assertFalse(HangulSyllable.of('하').hasJongsung());
    }

    @Test
    void getJongsung_종성없으면_예외() {
        assertThrows(IllegalStateException.class, () -> HangulSyllable.of('이').getJongsung());
    }

    @Test
    void of_비음절이면_예외() {
        assertThrows(IllegalArgumentException.class, () -> HangulSyllable.of('A'));
    }

    @Test
    void toJamo_종성없음() {
        char[] jamo = HangulSyllable.of('이').toJamo();
        assertArrayEquals(new char[]{'ㅇ', 'ㅣ'}, jamo);
    }

    @Test
    void toJamo_종성있음() {
        char[] jamo = HangulSyllable.of('국').toJamo();
        assertArrayEquals(new char[]{'ㄱ', 'ㅜ', 'ㄱ'}, jamo);
    }
}
