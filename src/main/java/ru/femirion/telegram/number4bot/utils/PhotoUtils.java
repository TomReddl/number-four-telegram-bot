package ru.femirion.telegram.number4bot.utils;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class PhotoUtils {

    public static File getImage(String photoId) {
        try {
            var path = "/" + PhotoUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()  + photoId;
            return new File(path);
        } catch (Exception ex) {
            throw new RuntimeException("can not read " + photoId + ", cause=%s" + ex.getMessage());
        }
    }
}
