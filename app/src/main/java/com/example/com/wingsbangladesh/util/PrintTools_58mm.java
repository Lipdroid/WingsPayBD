package com.example.com.wingsbangladesh.util;

/**
 * Created by sabbir on 3/10/17.
 */


        import java.io.File;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.UnsupportedEncodingException;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.List;

        import android.content.Context;
        import android.content.res.AssetManager;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Environment;
        import android.util.Log;

        import com.example.com.wingsbangladesh.util.ConvertUtil;


@SuppressWarnings("all")
public class PrintTools_58mm {
    public static List<String> commandList = new ArrayList<String>();


    public static final byte HT = 0x9; // 水平制表
    public static final byte LF = 0x0A; // 打印并换行
    public static final byte CR = 0x0D; // 打印回车
    public static final byte ESC = 0x1B;
    public static final byte DLE = 0x10;
    public static final byte GS = 0x1D;
    public static final byte FS = 0x1C;
    public static final byte STX = 0x02;
    public static final byte US = 0x1F;
    public static final byte CAN = 0x18;
    public static final byte CLR = 0x0C;
    public static final byte EOT = 0x04;
    public static final byte[] LOCATION = {0x1B, 0x44, 0x04, 0x00};
    /* 默认颜色字体指令 */
    public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[] { ESC, 'r',0x00 };
    /* 标准大小 */
    public static final byte[] FS_FONT_ALIGN = new byte[] { FS, 0x21, 1, ESC,
            0x21, 1 };
    /* 靠左打印命令 */
    public static final byte[] ESC_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };

    /* 靠右打印命令 */
    public static final byte[] ESC_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };

    /* 居中打印命令 */
    public static final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };

    /* 取消字体加粗 */
    public static final byte[] ESC_CANCEL_BOLD = new byte[] { ESC, 0x45, 0 };


    /*********************************************/
	/* 水平定位 */
    public static final byte[] ESC_HORIZONTAL_CENTERS = new byte[] { ESC, 0x44, 20, 28, 00};

    /* 取消水平定位 */
    public static final byte[] ESC_CANCLE_HORIZONTAL_CENTERS = new byte[] { ESC, 0x44, 00 };
    /*********************************************/


    // 进纸
    public static final byte[] ESC_ENTER = new byte[] { 0x1B, 0x4A, 0x40 };

    // 自检
    public static final byte[] PRINTE_TEST = new byte[] { 0x1D, 0x28, 0x41 };

    // 测试输出Unicode Pirit Message
    public static final byte[] UNICODE_TEXT = new byte[] {0x00, 0x50, 0x00,
            0x72, 0x00, 0x69, 0x00, 0x6E, 0x00, 0x74, 0x00, 0x20, 0x00, 0x20,
            0x00, 0x20, 0x00, 0x4D, 0x00, 0x65, 0x00, 0x73, 0x00, 0x73, 0x00,
            0x61, 0x00, 0x67, 0x00, 0x65};

    /**print test 打印机自检*/


    public static void HT(){

    }




    /**
     * print photo with path 根据图片路径打印图片
     *
     * @param 图片在SD卡路径，如:photo/pic.bmp
     * */


    /**
     * print photo in assets 打印assets里的图片
     *
     * @param 图片在assets目录，如:pic.bmp
     * */


    /**
     * decode bitmap to bytes 解码Bitmap为位图字节流
     * */
    public static byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        try {

            List<String> list = new ArrayList<String>(); //binaryString list
            StringBuffer sb;

            // 每行字节数(除以8，不足补0)
            int bitLen = bmpWidth / 8;
            int zeroCount = bmpWidth % 8;
            // 每行需要补充的0
            String zeroStr = "";
            if (zeroCount > 0) {
                bitLen = bmpWidth / 8 + 1;
                for (int i = 0; i < (8 - zeroCount); i++) {
                    zeroStr = zeroStr + "0";
                }
            }
            // 逐个读取像素颜色，将非白色改为黑色
            for (int i = 0; i < bmpHeight; i++) {
                sb = new StringBuffer();
                for (int j = 0; j < bmpWidth; j++) {
                    int color = bmp.getPixel(j, i); // 获得Bitmap 图片中每一个点的color颜色值
                    //颜色值的R G B
                    int r = (color >> 16) & 0xff;
                    int g = (color >> 8) & 0xff;
                    int b = color & 0xff;

                    // if color close to white，bit='0', else bit='1'
                    if (r > 160 && g > 160 && b > 160)
                        sb.append("0");
                    else
                        sb.append("1");
                }
                // 每一行结束时，补充剩余的0
                if (zeroCount > 0) {
                    sb.append(zeroStr);
                }
                list.add(sb.toString());
            }
            // binaryStr每8位调用一次转换方法，再拼合
            List<String> bmpHexList = ConvertUtil.binaryListToHexStringList(list);
            String commandHexString = "1D763000";
            // 宽度指令
            String widthHexString = Integer
                    .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                            : (bmpWidth / 8 + 1));
            if (widthHexString.length() > 2) {
                Log.e("decodeBitmap error", "宽度超出 width is too large");
                return null;
            } else if (widthHexString.length() == 1) {
                widthHexString = "0" + widthHexString;
            }
            widthHexString = widthHexString + "00";

            // 高度指令
            String heightHexString = Integer.toHexString(bmpHeight);
            if (heightHexString.length() > 2) {
                Log.e("decodeBitmap error", "高度超出 height is too large");
                return null;
            } else if (heightHexString.length() == 1) {
                heightHexString = "0" + heightHexString;
            }
            heightHexString = heightHexString + "00";

            commandList.clear();
            commandList.add(commandHexString + widthHexString + heightHexString);
            commandList.addAll(bmpHexList);



        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ConvertUtil.hexList2Byte(commandList);
    }


    /**
     * print photo with bytes 根据指令打印图片
     * */


    /**reset 重置格式*/




    /**
     * 输出
     * @param  byte[]指令
     * */

    /**
     * 输出
     *
     * @param int指令
     * */

    /**
     * EnterLine 进纸
     *
     * @param 进纸行数
     * */


    public static String getEnterLine(int count) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ESC_ENTER);
        return sBuilder.toString();
    }



}
