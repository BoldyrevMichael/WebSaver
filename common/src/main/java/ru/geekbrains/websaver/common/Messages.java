package ru.geekbrains.websaver.common;

public class Messages {

    public static final String DELIMITER = ";";
    public static final String REGISTR_REQUEST = "/registr_request";
    public static final String LOGIN_REQUEST = "/login_request";
    public static final String AUTH_ERROR = "/auth_error";
    public static final String USERS_LIST = "/user_list";
    public static final String RECONNECT = "/reconnect";
    public static final String BROADCAST = "/bcast";
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";

    // /registr_request login password repeatepassword
    public static String getRegistrRequest(String login, String password, String repeatepassword) {
        return REGISTR_REQUEST + DELIMITER + login + DELIMITER + password + DELIMITER + repeatepassword;
    }

    // /login_request login password
    public static String getLoginRequest(String login, String password) {
        return LOGIN_REQUEST + DELIMITER + login + DELIMITER + password;
    }
}
