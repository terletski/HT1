package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBWorker {
    private Statement statement;
    private Connection connect;
    private StringBuilder dataBaseErrorMessage = new StringBuilder("");

    // Количество рядов таблицы, затронутых последним запросом.
    private Integer affected_rows = 0;

    // Значение автоинкрементируемого первичного ключа, полученное после
    // добавления новой записи.
    private Integer last_insert_id = 0;
    private Integer last_insert_phone_id = 0;

    // Указатель на экземпляр класса.
    private static DBWorker instance = null;

    // Метод для получения экземпляра класса (реализован Singleton).
    public static DBWorker getInstance() {
        if (instance == null) {
            instance = new DBWorker();
        }
        return instance;
    }

    // "Заглушка", чтобы экземпляр класса нельзя было получить напрямую.
    private DBWorker() {
        // Просто "заглушка".
    }

    //метод создания подключения
    private void getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection("jdbc:mysql://localhost/phonebook?user=root&password=seadogs89&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            dataBaseErrorMessage.append("Error in Method [getConnection] method: ").append(e.getMessage());
        }
    }

    // Выполнение запросов на выборку данных.
    public ResultSet getDBData(String query) {
        getConnection();
        try {
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            dataBaseErrorMessage.append("Error in Method [getDBData] method: ").append(e.getMessage());
        }
        return null;
    }

    // Выполнение запросов на модификацию данных.
    public Integer changeDBData(String query) {
        getConnection();
        try {
            statement = connect.createStatement();
            this.affected_rows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            // Получаем last_insert_id() для операции вставки.
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                this.last_insert_id = rs.getInt(1);
            }

            return this.affected_rows;
        } catch (SQLException e) {
            e.printStackTrace();
            dataBaseErrorMessage.append("Error in Method [changeDBData] method: ").append(e.getMessage());
        }
        return null;
    }

    public Integer addPhoneToDBData(String query) {
        getConnection();
        try {
            statement = connect.createStatement();
            this.affected_rows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            // Получаем last_insert_id() для операции вставки.
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                this.last_insert_phone_id = rs.getInt(1);
            }

            return this.affected_rows;
        } catch (SQLException e) {
            e.printStackTrace();
            dataBaseErrorMessage.append("Error in Method [addPhoneToDBData] method: ").append(e.getMessage());
        }
        return null;
    }

    public Integer editPhoneToDBData(String query) {
        getConnection();
        try {
            statement = connect.createStatement();
            this.affected_rows = statement.executeUpdate(query);
            return this.affected_rows;
        } catch (SQLException e) {
            e.printStackTrace();
            dataBaseErrorMessage.append("Error in Method [editPhoneToDBData] method: ").append(e.getMessage());
        }
        return null;
    }

    public boolean deletePhoneFromDBData(String query) {
        getConnection();
        try {
            statement = connect.createStatement();
            this.affected_rows = statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            dataBaseErrorMessage.append("Error in Method [deletePhoneFromDBData] method: ").append(e.getMessage());
        }

        return false;
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++
    // Геттеры и сеттеры.
    public Integer getAffectedRowsCount() {
        return this.affected_rows;
    }

    public Integer getLastInsertId() {
        return this.last_insert_id;
    }

    public Integer getLast_insert_phone_id() {
        return last_insert_phone_id;
    }

    public StringBuilder getDataBaseErrorMessage() {
        return dataBaseErrorMessage;
    }

// Геттеры и сеттеры.
    // -------------------------------------------------
}
