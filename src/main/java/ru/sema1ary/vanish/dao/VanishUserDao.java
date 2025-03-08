package ru.sema1ary.vanish.dao;

import com.j256.ormlite.dao.Dao;
import lombok.NonNull;
import ru.sema1ary.vanish.model.VanishUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface VanishUserDao extends Dao<VanishUser, Long> {
    VanishUser save(@NonNull VanishUser user) throws SQLException;

    void saveAll(@NonNull List<VanishUser> users) throws SQLException;

    Optional<VanishUser> findById(@NonNull Long id) throws SQLException;

    Optional<VanishUser> findByUsername(@NonNull String username) throws SQLException;

    List<VanishUser> findAll() throws SQLException;
}
