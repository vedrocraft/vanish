package ru.sema1ary.vanish.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.sema1ary.joiner.Joiner;
import ru.sema1ary.joiner.service.JoinerUserService;
import ru.sema1ary.vanish.Vanish;
import ru.sema1ary.vanish.dao.VanishUserDao;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class VanishUserServiceImpl implements VanishUserService {
    private final Vanish plugin;
    private final boolean isJoinerEnabled;
    private final VanishUserDao vanishUserDao;

    @Override
    public VanishUser save(@NonNull VanishUser user) {
        try {
            return vanishUserDao.save(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAll(@NonNull List<VanishUser> users) {
        try {
            vanishUserDao.saveAll(users);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<VanishUser> findById(@NonNull Long id) {
        try {
            return vanishUserDao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<VanishUser> findByUsername(@NonNull String username) {
        try {
            return vanishUserDao.findByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<VanishUser> findAll() {
        try {
            return vanishUserDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VanishUser getUser(@NonNull String username) {
        return findByUsername(username).orElseGet(() -> save(VanishUser.builder()
                .username(username)
                .isVanishActive(false)
                .build()));
    }

    @Override
    public void hidePlayer(@NonNull Player player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if(!onlinePlayer.hasPermission("vanish.see")) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        });

        if(isJoinerEnabled) {
            Vanish.getPlugin(Joiner.class).getService(JoinerUserService.class).sendFakeQuitMessage(player);
        }
    }

    @Override
    public void showPlayer(@NonNull Player player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.showPlayer(plugin, player));

        if(isJoinerEnabled) {
            Vanish.getPlugin(Joiner.class).getService(JoinerUserService.class).sendFakeJoinMessage(player);
        }
    }
}
