package org.coder.design.patterns._1_oop._cases.authentication;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface ApiAuthenticator {
    void auth(String url);

    void auth(ApiRequest apiRequest);
}
