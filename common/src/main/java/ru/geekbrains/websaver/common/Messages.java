package ru.geekbrains.websaver.common;

import java.util.Formatter;

public class Messages {

    public static final String DELIMITER = ";";
    public static final String REGISTR_REQUEST = "/registr_request";
    public static final String REGISTR_ERROR = "/registr_error";
    public static final String REGISTR_OK = "/registr_ok";
    public static final String LOGIN_REQUEST = "/login_request";
    public static final String LOGIN_ERROR = "/login_error";
    public static final String LOGIN_OK = "/login_ok";
    public static final String GET_FILES = "/get_files";
    public static final String DEL_ERROR = "/del_error";
    public static final String DEL_OK = "/del_ok";
    public static final String RECEIVE_ERROR = "/receive_error";
    public static final String RECEIVE_OK = "/receive_ok";


    public static final String DIVIDE = "/divide";

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

    // /login_ok description
    public static String getLoginOk(String login) {
        return LOGIN_OK + DELIMITER + login;
    }

    // /get_files description
    public static String getFiles(String login) {
        return GET_FILES + DELIMITER + login;
    }

    // /del_error description
    public static String getDelError(String description) {
        return DEL_ERROR + DELIMITER + description;
    }

    // /del_ok description
    public static String getDelOk(String description, String fileName) {
        return DEL_OK + DELIMITER + description + DELIMITER + fileName;
    }

    // /receive_error description
    public static String getReceiveError(String description) {
        return RECEIVE_ERROR + DELIMITER + description;
    }

    // /receive_ok description
    public static String getReceiveOk(String description, String fileName) {
        return RECEIVE_OK + DELIMITER + description + DELIMITER + fileName;
    }

    // /divide description
    public static String getDivide() {
        return DIVIDE;
    }
}