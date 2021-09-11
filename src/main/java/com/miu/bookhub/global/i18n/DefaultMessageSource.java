package com.miu.bookhub.global.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

public class DefaultMessageSource extends ReloadableResourceBundleMessageSource {

    public DefaultMessageSource() {
        setBasename("messages");
        setDefaultLocale(Locale.ENGLISH);
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new DefaultMessageSource());
    }
}
