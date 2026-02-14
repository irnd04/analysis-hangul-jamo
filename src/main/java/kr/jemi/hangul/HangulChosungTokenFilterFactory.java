package kr.jemi.hangul;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.plugin.NamedComponent;
import org.elasticsearch.plugin.analysis.TokenFilterFactory;

@NamedComponent("hangul_chosung")
public class HangulChosungTokenFilterFactory implements TokenFilterFactory {

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HangulChosungTokenFilter(tokenStream);
    }
}
