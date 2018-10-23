package com.mazenrashed.printooth.utilities;

public class UniCode864Mapping {
    short Base = 0;
    short Start = 2;
    short End = 1;
    short Middle = 3;
    short RIGHT_JOIN = 2;
    short RIGHT_LEFT_JOIN = 4;
    short NOT_JOIN = 0;
    int ArabicLetterOffset = 1569;
    boolean ARABIC = false;
    boolean ENGLISH = true;

    short ArabicLetterPreOrder = 0;
    boolean CurrentLang = ENGLISH;
    boolean AddJointCharacter = false;


    int[][] ArabicLetterIndex = {
            //  Base  End Start Middle
            {193, 193, 198, 198}, // ء  0
            {194, 000, 162, 000}, // آ  1
            {195, 000, 165, 000}, // أ  2
            {196, 000, 196, 000}, // ؤ  3
            {199, 000, 168, 000}, // إ  4
            {233, 233, 198, 198}, // ئ  5
            {199, 000, 168, 000}, // ا  6
            {169, 169, 200, 200}, // ب  7
            {201, 000, 201, 000}, // ة  8
            {170, 170, 202, 202}, // ت  9
            {171, 171, 203, 203}, // ث 10
            {173, 173, 204, 204}, // ج 11
            {174, 174, 205, 205}, // ح 12
            {175, 175, 206, 206}, // خ 13
            {207, 000, 207, 000}, // د 14
            {208, 000, 208, 000}, // ذ 15
            {209, 000, 209, 000}, // ر 16
            {210, 000, 210, 000}, // ز 17
            {188, 188, 211, 211}, // س 18
            {189, 189, 212, 212}, // ش 19
            {190, 190, 213, 213}, // ص 20
            {235, 235, 214, 214}, // ض 21
            {215, 215, 215, 215}, // ط 22
            {216, 216, 216, 216}, // ظ 23
            {223, 197, 217, 236}, // ع 24       NEW ONE
            {238, 237, 218, 247}, // غ 25
            {224, 224, 224, 224}, // كشيدة 26
            {186, 186, 225, 225}, // ف 27
            {248, 248, 226, 226}, // ق 28
            {252, 252, 227, 227}, // ك 29
            {251, 251, 228, 228}, // ل 30
            {239, 239, 229, 229}, // م 31       NEW ONE
            {242, 242, 230, 230}, // ن 32
            {243, 243, 231, 244}, // ه 33
            {232, 000, 232, 000}, // و 34
            {233, 000, 245, 000}, // ى 35
            {253, 246, 234, 234}, // ي 36
            {249, 000, 250, 000}, // لآ 37
            {153, 000, 154, 000}, // لأ 38
            {157, 000, 158, 000}, // لإ 39
            {157, 000, 158, 000}  // لا 40
    };

    private int getArabicFontLetterIndex(int letter) {
        return ((letter > 1599 && letter < 1615) ? letter - 5 : letter) - ArabicLetterOffset; // because there are cut in letter sequance (1594-1601) in windows mobile code page
    }

    private int ArabicLetterMapping(int letter, int pos) {
        if (letter < 1569 || letter > 1614) // charecter out of the rang
            return 0;
        int tmp;
        tmp = ArabicLetterIndex[getArabicFontLetterIndex(letter)][pos];
        return tmp;
    }

    private short getArabicLetterForm(int LetterIndex) {
        if (LetterIndex == 0) // hamza
            return NOT_JOIN;
        else if (ArabicLetterIndex[LetterIndex][End] == 0)
            return RIGHT_JOIN;
        return RIGHT_LEFT_JOIN;
    }

    public String getArabicString(String str) {
        if (str == null) // if no string
            return "";
        int len = (str == null) ? 0 : str.length();
        int strSize = len + 1;
        StringBuilder retstr = new StringBuilder(strSize);
        int ArabicIndex = 0, EnglishIndex = 0;
        int NumberIndex = 0;
        boolean InNumber = false;
        ArabicLetterPreOrder = Base;

        try {
            retstr.append('\0');
            int temp;

            for (int i = 0; i < len; ++i, EnglishIndex++) { // for all string letter
                temp = Integer.valueOf(str.charAt(i));
                if (temp > 1700 || temp == 0)
                    continue;
                if (temp > 128) {
                    if (CurrentLang == ENGLISH)
                        ArabicIndex = i;
                    CurrentLang = ARABIC;
                } else if (temp > 65 || temp < 28)
                    CurrentLang = ENGLISH;
                // MF update for add Numbers
                if (!InNumber && (temp >= 46 && temp <= 57)) {
                    CurrentLang = ENGLISH;
                    InNumber = true;
                    NumberIndex = i;
                } else if (InNumber && !(temp >= 46 && temp <= 57))
                    InNumber = false;

                if (temp < 65) {
                    if (getSpaceType(str, i)) {
                        retstr.insert(EnglishIndex, str.charAt(i));
                        ArabicIndex = i + 1;
                    } else {
                        //MF update for add numbers
                        if (InNumber)
                            retstr = retstr.insert(NumberIndex++, str.charAt(i));
                        else {
                            if (AddJointCharacter && (temp < 192)) {
                                retstr = retstr.insert(ArabicIndex, (char) (159));
                                retstr = retstr.insert(ArabicIndex, str.charAt(i));
                                AddJointCharacter = false;
                            } else
                                retstr = retstr.insert(ArabicIndex, str.charAt(i));
                            if (temp == 40 || temp == 41)
                                retstr.insert(ArabicIndex, (temp == 40) ? '(' : ')');
                        }
                    }
                    ArabicLetterPreOrder = Base;
                    continue;
                }
                if (CurrentLang == ENGLISH) { // if th next letter is ASCII
                    retstr.insert(EnglishIndex, str.charAt(i));
                    ArabicLetterPreOrder = Base;
                    ArabicIndex = i;
                    continue;
                } else if (CurrentLang == ARABIC) { // Arabic mode
                    if (i < len - 1) {
                        AddJointCharacter = temp == 211 || temp == 212 || temp == 213 || temp == 214;
                        String stemp = getArabicLetterOrder(Integer.valueOf(str.charAt(i)), Integer.valueOf(str.charAt(i + 1)));
                        if (str.charAt(i) == 'ل' && (str.charAt(i + 1) == 'ا' || str.charAt(i + 1) == 'إ' || str.charAt(i + 1) == 'أ' || str.charAt(i + 1) == 'آ')) {
                            retstr = retstr.insert(ArabicIndex, " ");
                            retstr = retstr.insert(++ArabicIndex, stemp.charAt(0));

                            ++i;
                            ArabicLetterPreOrder = Base;
                        } else // for other letters
                            retstr = retstr.insert(ArabicIndex, stemp.charAt(0));
                    } // if len
                    else {
                        String tmp = getArabicLetterOrder(Integer.valueOf(str.charAt(i)), ' ');
                        retstr = retstr.insert(ArabicIndex, (tmp));
                    }
                } // else current language
            } // for statment
        } // Try
        catch (Exception ex) {
            return "in GetArabicString Function";
        }
        String temp1 = retstr.toString();
        temp1 = temp1.substring(0, temp1.indexOf('\0'));
        return temp1;
    } // getArabicString Function

    private String getArabicLetterOrder(int PreLetter, int CurrLetter) {
        String str = "";
        short PreNOF, CurrNOF;

        if (PreLetter < 1569 || PreLetter > 1610) // for out of rang character
            return str + " ";
        if (CurrLetter < 1569 || CurrLetter > 1610)//for out of rang character
            CurrLetter = 32;
        try {
            if (CurrLetter < 128) {
                PreNOF = getArabicLetterForm(getArabicFontLetterIndex(PreLetter));
                str = String.valueOf(Character.toChars(ArabicLetterMapping(PreLetter, (ArabicLetterPreOrder == Base) ? Base : (PreNOF == 4) ? End : Start)));
                return str;
            }
            PreNOF = getArabicLetterForm(getArabicFontLetterIndex(PreLetter));
            CurrNOF = getArabicLetterForm(getArabicFontLetterIndex(CurrLetter));

            if (PreNOF == NOT_JOIN || PreNOF == RIGHT_JOIN) {
                str += (char) (ArabicLetterMapping(PreLetter, ArabicLetterPreOrder));
                str += (char) (ArabicLetterMapping(CurrLetter, Base));
                ArabicLetterPreOrder = Base;
                return str;
            } else if (CurrNOF == NOT_JOIN) {
                str += (char) (ArabicLetterMapping(PreLetter, ArabicLetterPreOrder));
                str += (char) (ArabicLetterMapping(CurrLetter, Base));
                ArabicLetterPreOrder = Base;
                return str;
            } else if (PreNOF == RIGHT_LEFT_JOIN) {
                if (ArabicLetterPreOrder == Base) {
                    if (PreLetter == 'ل' && (CurrLetter == 'ا' || CurrLetter == 'إ' || CurrLetter == 'أ' || CurrLetter == 'آ'))  // for لا
                    { // for لا
                        if (CurrLetter == 'ا')
                            str += (char) (ArabicLetterMapping(1614, Base));
                        if (CurrLetter == 'إ')
                            str += (char) (ArabicLetterMapping(1613, Base));
                        if (CurrLetter == 'أ')
                            str += (char) (ArabicLetterMapping(1612, Base));
                        if (CurrLetter == 'آ')
                            str += (char) (ArabicLetterMapping(1611, Base));
                    } else { // for other letter
                        str += (char) (ArabicLetterMapping(PreLetter, Start));
                        str += (char) (ArabicLetterMapping(CurrLetter, (PreNOF == 4) ? End : Start));//if a has 2 form
                    }
                    ArabicLetterPreOrder = Start;
                    return str;
                } else if (ArabicLetterPreOrder == Start) {
                    if (PreLetter == 'ل' && (CurrLetter == 'ا' || CurrLetter == 'إ' || CurrLetter == 'أ' || CurrLetter == 'آ'))  // for لا
                    { // for لا
                        if (CurrLetter == 'ا')
                            str += (char) (ArabicLetterMapping(1614, Start));
                        if (CurrLetter == 'إ')
                            str += (char) (ArabicLetterMapping(1613, Start));
                        if (CurrLetter == 'أ')
                            str += (char) (ArabicLetterMapping(1612, Start));
                        if (CurrLetter == 'آ')
                            str += (char) (ArabicLetterMapping(1611, Start));
                    } else {//for other letter
                        str += (char) (ArabicLetterMapping(PreLetter, Middle));
                        str += (char) (ArabicLetterMapping(CurrLetter, (PreNOF == 4) ? End : Start));
                    }
                    ArabicLetterPreOrder = Start;
                    return str;
                } //else ArabicLetterOrder
            } // else PreNOF == RIGHT_LEFT_JOIN
        } // try
        catch (Exception ex) {
            return "the getArabicLetterOrder";
        }

        return str;
    } // End of getArabicLetterOrder function

    private boolean getSpaceType(String str, int pos) {
        int len = str.length();
        if (pos < len - 1 && pos > 0) {
            int letterPre = Integer.valueOf(str.charAt(pos - 1));
            int letterAfr = Integer.valueOf(str.charAt(pos + 1));

            int tmp = Integer.valueOf(str.charAt(pos));
            if (tmp == 58)
                return ENGLISH; // For : symbole
            if (letterPre == 32) {
                int i = pos;
                while (i < len - 1 && str.charAt(i++) == ' ') ;
                if (Integer.valueOf(str.charAt(i)) > 128)
                    return ARABIC;
                else
                    return ENGLISH;
            } else if (letterPre < 65 && letterAfr < 65)
                return CurrentLang;
            else if (letterPre > 128 && letterAfr > 128) // 128 for English letter
                return ARABIC;
            else if (letterPre > 128 && letterAfr < 65) // 65 for space and other symbols
                return ARABIC;
            else if (letterPre > 128 && letterAfr < 128)
                return ENGLISH;
            else if (letterPre < 128 && letterAfr > 128)
                return ARABIC;
        }
        return CurrentLang;
    }
}