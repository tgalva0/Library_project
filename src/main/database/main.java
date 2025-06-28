package main.database;

import java.sql.SQLException;

public class main {
    public static void main(String[] args) throws SQLException {
        var app = new AppLoop_TerminalUI();
        app.loop();
    }
}
