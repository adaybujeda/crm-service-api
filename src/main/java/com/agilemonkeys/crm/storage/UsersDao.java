package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.domain.User;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersDao {
    @SqlUpdate("INSERT INTO users (user_id, name, username, password_hash, role, version, created_date, updated_date) " +
               "VALUES (:userId, :name, :username, :passwordHash, :role, :version, :createdDate, :updatedDate)")
    public int insertUser(@BindBean User user);

    @SqlUpdate("UPDATE users SET name=:name, username=:username, password_hash=:passwordHash, role=:role, version=:version, updated_date=:updatedDate " +
               "WHERE user_id=:userId AND version=:oldVersion")
    public int updateUser(@BindBean User user, @Bind("oldVersion") Integer oldVersion);

    @SqlUpdate("UPDATE users SET password_hash=:passwordHash WHERE user_id=:userId")
    public int updatePassword(@Bind("userId") UUID userId, @Bind("passwordHash") String passwordHash);

    @SqlUpdate("DELETE FROM users WHERE user_id=:userId")
    public int deleteUser(@Bind("userId") UUID userId);

    @SqlQuery("SELECT * FROM users")
    public List<User> getUsers();

    @SqlQuery("SELECT * FROM users WHERE user_id = :userId")
    public Optional<User> getUserById(@Bind("userId") UUID userId);

    @SqlQuery("SELECT * FROM users WHERE username = :username")
    public Optional<User> getUserByUsername(@Bind("username") String username);
}
