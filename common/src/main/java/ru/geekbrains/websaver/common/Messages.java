package ru.geekbrains.websaver.common;

import java.util.Formatter;

public class Messages {

    public static final String DELIMITER = ";";
    public static final String REGISTR_REQUEST = "/registr_request";
    public static final String REGISTR_ERROR = "/registr_error";
    public static final String REGISTR_OK = "/registr_ok";
    public static final String LOGIN_REQUEST = "/login_request";
    public static final String LOGIN_ERROR = "/login_error";
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";

    // /registr_request login password repeatepassword
    public static String getRegistrRequest(String login, String password, String repeatepassword) {
        return REGISTR_REQUEST + DELIMITER + login + DELIMITER + password + DELIMITER + repeatepassword;
    }

    // /login_request login password
    public static String getLoginRequest(String login, String password, String date) {
        return LOGIN_REQUEST + DELIMITER + login + DELIMITER + password + DELIMITER + date;
    }

    // /registr_error description
    public static String getRegistrError(String description) {
        return REGISTR_ERROR + DELIMITER + description;
    }

    // /registr_ok description
    public static String getRegistrOk(String description) {
        return REGISTR_OK + DELIMITER + description;
    }

    // /login_error description
    public static String getLoginError(String description) {
        return LOGIN_ERROR + DELIMITER + description;
    }
}
