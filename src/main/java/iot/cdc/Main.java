package iot.cdc;

import iot.cdc.service.MysqlCdcService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new MysqlCdcService().start();
    }
}
