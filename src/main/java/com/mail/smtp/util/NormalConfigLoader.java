package com.mail.smtp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class NormalConfigLoader implements IConfigLoader
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ConfigMap load(String filePath) throws IOException
    {
        Optional< Map<String, String> > optConfig;
        Map<String, String> config;
        try(
                Stream<String> lines = Files.lines(Paths.get(filePath), Charset.defaultCharset())
        )
        {
            optConfig = Optional.of(
                    lines.filter(
                            (line) ->
                            !((line.trim().isEmpty() || line.startsWith("#") || line.indexOf('=') == -1 || line.startsWith("=")))
                    ).collect(toMap(
                             line -> line.substring(0, line.indexOf('=')).trim(),
                             line -> line.substring(line.indexOf('=') + 1).trim(),
                             (k1, k2) -> k1     //동일한 key 가 존재하는 경우 처음 나온 key 값을 사용.
                             )));

            config = optConfig.orElseGet(() -> {
               logger.info("in load config, data empty, path : {}", filePath);
               return Collections.emptyMap();
            });
        }
        catch(IOException ie)
        {
            logger.error("fail to load config, {}, {}", filePath, ie.getMessage());
            throw ie;
        }

        return new ConfigMap(filePath, config);
    }
}
