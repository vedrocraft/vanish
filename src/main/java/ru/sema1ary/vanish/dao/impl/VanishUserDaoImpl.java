package ru.sema1ary.vanish.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import lombok.NonNull;
import ru.sema1ary.vanish.dao.VanishUserDao;
import ru.sema1ary.vanish.model.VanishUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class VanishUserDaoImpl extends BaseDaoImpl<VanishUser, Long> implements VanishUserDao {
    public VanishUserDaoImpl(ConnectionSource connectionSource, Class<VanishUser> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public VanishUser save(@NonNull VanishUser user) throws SQLException {
        createOrUpdate(user);
        return user;
    }

    @Override
    public void saveAll(@NonNull List<VanishUser> users) throws SQLException {
        callBatchTasks((Callable<Void>) () -> {
            for (VanishUser user : users) {
                createOrUpdate(user);
            }
            return null;
        });
    }

    @Override
    public Optional<VanishUser> findById(@NonNull Long id) throws SQLException {
        VanishUser result = queryForId(id);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<VanishUser> findByUsername(@NonNull String username) throws SQLException {
        QueryBuilder<VanishUser, Long> queryBuilder = queryBuilder();
        Where<VanishUser, Long> where = queryBuilder.where();
        String columnName = "username";

        SelectArg selectArg = new SelectArg(SqlType.STRING, username.toLowerCase());
        where.raw("LOWER(" + columnName + ")" + " = LOWER(?)", selectArg);
        return Optional.ofNullable(queryBuilder.queryForFirst());
    }

    @Override
    public List<VanishUser> findAll() throws SQLException {
        return queryForAll();
    }
}
