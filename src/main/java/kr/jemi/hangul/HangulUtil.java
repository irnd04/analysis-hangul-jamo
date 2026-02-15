package kr.jemi.hangul;

/**
 * HangulSyllable을 활용한 배치 변환 유틸리티.
 */
public final class HangulUtil {

    private HangulUtil() {
    }

    /**
     * char 배열에 한글 음절이 포함되어 있는지 확인한다.
     */
    public static boolean containsHangulSyllable(char[] input, int inputLen) {
        for (int i = 0; i < inputLen; i++) {
            if (HangulSyllable.isHangulSyllable(input[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * char 배열의 전체 자모 분해.
     * 분해된 결과를 char[]로 반환한다.
     */
    public static char[] decompose(char[] input, int inputLen) {
        char[] buf = new char[inputLen * 3];
        int pos = 0;
        for (int i = 0; i < inputLen; i++) {
            char c = input[i];
            if (HangulSyllable.isHangulSyllable(c)) {
                char[] jamo = HangulSyllable.of(c).toJamo();
                System.arraycopy(jamo, 0, buf, pos, jamo.length);
                pos += jamo.length;
            } else {
                buf[pos++] = c;
            }
        }
        char[] result = new char[pos];
        System.arraycopy(buf, 0, result, 0, pos);
        return result;
    }

    /**
     * char 배열의 각 음절에서 초성을 추출한다.
     * 음절이 아닌 문자는 그대로 통과한다.
     * 추출된 결과를 char[]로 반환한다.
     */
    public static char[] extractChosung(char[] input, int inputLen) {
        char[] result = new char[inputLen];
        for (int i = 0; i < inputLen; i++) {
            char c = input[i];
            if (HangulSyllable.isHangulSyllable(c)) {
                result[i] = HangulSyllable.of(c).getChosung();
            } else {
                result[i] = c;
            }
        }
        return result;
    }
}
