package com.example.desensitize.handler;

import com.example.desensitize.annotation.SliderDesensitize;

/**
 * {@link SliderDesensitize} 的脱敏处理器
 */
public class DefaultDesensitizationHandler extends AbstractSliderDesensitizationHandler<SliderDesensitize> {
    @Override
    Integer getPrefixKeep(SliderDesensitize annotation) {
        return annotation.prefixKeep();
    }
    
    @Override
    Integer getSuffixKeep(SliderDesensitize annotation) {
        return annotation.suffixKeep();
    }
    
    @Override
    String getReplacer(SliderDesensitize annotation) {
        return annotation.replacer();
    }
}