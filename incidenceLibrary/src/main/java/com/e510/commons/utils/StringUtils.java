package com.e510.commons.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.e510.commons.utils.LogUtil.makeLogTag;

public class StringUtils {

    private static final String LINE_BREAK = "\n";
    private static final String SPACE = " ";
    private static final String TAG = makeLogTag(StringUtils.class);

    public static String checkAndReturnUrlFormat(String url)
    {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
        {
            url = "http://" + url;
        }

        return url;
    }

    public static String capitalizeFirst(String str)
    {
        if (str != null && str.length() > 0)
        {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        return str;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        if (email==null) return false;

        email = email.trim();

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isValidMobileNumber(String phoneNumber)
    {
        // validate phone numbers of format "123456789"
        if (phoneNumber.matches("\\d{9}"))
            return true;
            // validating phone number with -, . or spaces
        else if (phoneNumber.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
            return true;
            // validating phone number with extension length from 3 to 5
        else if (phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
            return true;
            // validating phone number where area code is in braces ()
        else if (phoneNumber.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
            return true;
            // Validation for India numbers
        else if (phoneNumber.matches("\\d{4}[-\\.\\s]\\d{3}[-\\.\\s]\\d{3}"))
            return true;
        else if (phoneNumber.matches("\\(\\d{5}\\)-\\d{3}-\\d{3}"))
            return true;

        else if (phoneNumber.matches("\\(\\d{4}\\)-\\d{3}-\\d{3}"))
            return true;
            // return false if nothing matches the input
        else
            return false;
    }

    public static String formatNullStringBase64(String str)
    {
        str = Base64Coder.encodeStringUTF8(str);
        return str;
    }

    public static String getStringFromBase64(String str)
    {
        str = Base64Coder.decodeStringISO(str);
        return str;
    }

    public static boolean isValidURL(String url)
    {
        String regex = "^(https?://)?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*)$";

        try {
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(url);
            return matcher.matches();

        } catch (RuntimeException e) {
            return false;
        }
    }

    public static SpannableString getSpannableText(Context context, String text, final OnClickURLItem onClickItemWithObject, int textColor, int urlColor) {
        String[] splitText = text.replace(LINE_BREAK, SPACE + LINE_BREAK + SPACE).split(SPACE);
        int currentStringPosition = 0;
        int restLineBreakValue = 0;

        SpannableString spannableString = new SpannableString(text);

        for(final String value: splitText) {

            if (StringUtils.isValidURL(value)) {

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (onClickItemWithObject != null) {
                            onClickItemWithObject.onClick(value);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                };

                spannableString.setSpan(clickableSpan, currentStringPosition, currentStringPosition + value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Utils.getColor(context, urlColor)), currentStringPosition, currentStringPosition + value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                if (value.equals(LINE_BREAK)) {
                    restLineBreakValue = 1;
                }

                spannableString.setSpan(new ForegroundColorSpan(Utils.getColor(context, textColor)), currentStringPosition - restLineBreakValue
                        , currentStringPosition + value.length() - restLineBreakValue, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            currentStringPosition = (currentStringPosition - restLineBreakValue)+ (value.length() + 1 - restLineBreakValue);
            restLineBreakValue = 0;
        }

        return  spannableString;
    }
}
