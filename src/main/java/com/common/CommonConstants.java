package com.common;

public interface CommonConstants {
    int CONTENT_HEADER_LENGTH = 8;
    int GID_LENGTH = 32;
    int IF_SERVICE_CODE_LENGTH = 12;
    int SYNC_CODE_LENGTH = 1;
    int REQ_AND_RESP_CODE_LENGTH = 1;
    int CONTENT_LENGTH = 10;

    // int MAX_TRY_LOOP_CNT = 5;
    int FULL_TEXT_LENGTH = 64; // 길이 전문 포함 전문 총 길이
    String IF_SERVICE_CODE = "ISMONSS00004";
    String DEFAULT_UUID = "ECEEB1E234E94226BD959C1D1B0A2921";
    String SYNC_CODE = "S";
    String REQUEST_CODE = "S";
    String RESPONSE_CODE = "R";
    String DEFAULT_CONTENT = "0123456789";
    int BODY_LENGTH = 56; // 길이전문 길이를 뺀 나머지 전문 길이

}
