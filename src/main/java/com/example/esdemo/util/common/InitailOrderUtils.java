package com.example.esdemo.util.common;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class InitailOrderUtils {
    public void initailOrder(List<HashMap<String,Object>> list){
        //存储的时候存一组全拼音
        //查询的时候判断是中文还是英文
        //中文匹配模糊查询name字段，英文查询新增拼音串字段

    }




    //获取汉字全拼
    public static String getPingYin(String inputString) {
        if (!StringUtils.isBlank(inputString)) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            char[] input = inputString.trim().toCharArray();
            StringBuilder output = new StringBuilder();
            try {
                for (char anInput : input) {
                    if (Character.toString(anInput).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(anInput, format);
                        output.append(temp[0]);
                    } else {
                        output.append(Character.toString(anInput));
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            return output.toString();
        }
        return "";
    }



    //获取汉字首字母
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int i = 0; i < str.length(); i++)
        {
            char word = str.charAt(i);
            String[] pinYinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinYinArray != null)
            {
                convert += pinYinArray[0].charAt(0);

            }
            else
            {
                convert += word;
            }
        }
        return convert;
    }






    public static Boolean checkChineseWord(String realName) {
        Boolean flag = true;
        if (StringUtils.isEmpty(realName)) {
            throw new RuntimeException("参数为空");
        }
        char[] ch = realName.toCharArray();
        for (char c : ch) {
            if (c < 0x4E00 || c > 0x9FA5) {
                //非全汉字
                flag = false;
            }
        }
        return flag;
    }

    public static String getCnASCII(String cnStr)
    {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();

        for (int i = 0; i < bGBK.length; i++)
        {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();

    }

}
