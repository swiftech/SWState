package com.github.swiftech.swstate.db;

import com.github.swiftech.swstate.StateProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.*;

/**
 * @author swiftech
 */
public class DatabaseStateProvider implements StateProvider<String> {

    public DatabaseStateProvider() {
        try {
            Connection conn = DataSource.getConnection();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("create table table_with_state (id varchar, state varchar)");
            statement.executeUpdate("CREATE UNIQUE INDEX pk_id on table_with_state (id)");
            System.out.println("DB created");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to init cache database: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error access database: " + e.getLocalizedMessage());
        }
    }

    @Override
    public String getCurrentState(String id) {
        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT state FROM table_with_state where id = ?");
            stat.setString(1, id);
            ResultSet resultSet = stat.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void initializeState(String id, String state) {
        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement("INSERT INTO table_with_state values(?, ?)");
            stat.setString(1, id);
            stat.setString(2, state);
            int i = stat.executeUpdate();
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void setState(String id, String state) {
        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement("UPDATE table_with_state SET state = ? where id = ?");
            stat.setString(1, state);
            stat.setString(2, id);
            int i = stat.executeUpdate();
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isState(String id, String state) {
        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT state FROM table_with_state where id = ? and state = ?");
            stat.setString(1, id);
            stat.setString(2, state);
            ResultSet resultSet = stat.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isStateIn(String id, String... states) {
        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT state FROM table_with_state where id = ? and state in (?)");
            stat.setString(1, id);
            stat.setString(2, StringUtils.join(states, ","));
            ResultSet resultSet = stat.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
