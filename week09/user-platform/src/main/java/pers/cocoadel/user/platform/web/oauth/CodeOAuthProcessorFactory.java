package pers.cocoadel.user.platform.web.oauth;

import java.util.LinkedList;
import java.util.List;

public class CodeOAuthProcessorFactory {

    private static final List<CodeOAuth2Processor> PROCESSORS = new LinkedList<>();
    static {
        PROCESSORS.add(new GiteeCodeOAuth2Processor());
    }

    public static CodeOAuth2Processor getCodeOAuth2Processor(String oAuthServiceName) {
        for (CodeOAuth2Processor processor : PROCESSORS) {
            if (processor.isMatch(oAuthServiceName)) {
                return processor;
            }
        }
        return null;
    }
}
