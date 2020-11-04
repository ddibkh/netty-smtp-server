package com.mail.smtp.util;

import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class ConfigMap
{
    private final String filePath;
    private Map<String, String> config;

    public ConfigMap(String filePath, Map< String, String > config)
    {
        this.filePath = filePath;
        this.config = config;
    }

    public String getConfigStr(String key, String defaultValue)
    {
        Optional<String> optValue;
        optValue = Optional.ofNullable(config.get(key));
        return optValue.orElse(defaultValue);
    }

    public Integer getConfigInt(String key, Integer defaultValue)
    {
        Optional<String> optValue;
        optValue = Optional.ofNullable(config.get(key));
        String value = optValue.orElse(defaultValue.toString());
        return Integer.parseInt(value);
    }
}
