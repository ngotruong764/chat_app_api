package com.usth.chat_app_api.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class Helper {
    public static boolean isValidImg(byte[] imgData){
        if(imgData == null || imgData.length == 0){
            return false;
        }
        try(ByteArrayInputStream byteArrayInputStream =  new ByteArrayInputStream(imgData)) {
            BufferedImage image = ImageIO.read(byteArrayInputStream);
            return image != null;
        } catch (Exception e){
            return false;
        }
    }
}
