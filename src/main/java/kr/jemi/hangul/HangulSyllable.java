package kr.jemi.hangul;

/**
 * 한글 음절을 초성+중성+종성으로 분해한 도메인 객체.
 */
public class HangulSyllable {

    private static final char HANGUL_SYLLABLE_START = '가';
    private static final char HANGUL_SYLLABLE_END = '힣';

    private static final int JUNGSUNG_COUNT = 21;
    private static final int JONGSUNG_COUNT = 28;
    private static final int SYLLABLE_BLOCK = JUNGSUNG_COUNT * JONGSUNG_COUNT; // 588

    // 초성 19자
    private static final char[] CHOSUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',
            'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
            'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    // 중성 21자
    private static final char[] JUNGSUNG = {
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ',
            'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ',
            'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ'
    };

    // 종성 28자 (index 0 = 종성 없음)
    private static final char[] JONGSUNG = {
            '\0',
            'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ',
            'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ',
            'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',
            'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ',
            'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ',
            'ㅍ', 'ㅎ'
    };

    private final char chosung;
    private final char jungsung;
    private final char jongsung;

    private HangulSyllable(char chosung, char jungsung, char jongsung) {
        this.chosung = chosung;
        this.jungsung = jungsung;
        this.jongsung = jongsung;
    }

    /**
     * 한글 음절(가~힣) 여부를 판별한다.
     */
    public static boolean isHangulSyllable(char c) {
        return c >= HANGUL_SYLLABLE_START && c <= HANGUL_SYLLABLE_END;
    }

    /**
     * 한글 음절을 분해하여 HangulSyllable 객체를 생성한다.
     */
    public static HangulSyllable of(char c) {
        if (!isHangulSyllable(c)) {
            throw new IllegalArgumentException("한글 음절이 아닙니다: " + c);
        }
        int idx = c - HANGUL_SYLLABLE_START;
        return new HangulSyllable(
                CHOSUNG[idx / SYLLABLE_BLOCK],
                JUNGSUNG[(idx % SYLLABLE_BLOCK) / JONGSUNG_COUNT],
                JONGSUNG[idx % JONGSUNG_COUNT]
        );
    }

    public char getChosung() {
        return chosung;
    }

    public char getJungsung() {
        return jungsung;
    }

    public char getJongsung() {
        if (!hasJongsung()) {
            throw new IllegalStateException("종성이 없는 음절입니다.");
        }
        return jongsung;
    }

    public boolean hasJongsung() {
        return jongsung != '\0';
    }

    /**
     * 자모 배열로 변환한다. (종성이 없으면 2자, 있으면 3자)
     */
    public char[] toJamo() {
        if (hasJongsung()) {
            return new char[]{chosung, jungsung, jongsung};
        }
        return new char[]{chosung, jungsung};
    }
}
