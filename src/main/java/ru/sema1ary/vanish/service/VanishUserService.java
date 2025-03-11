package ru.sema1ary.vanish.service;

import lombok.NonNull;
import org.bukkit.entity.Player;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vedrocraftapi.service.Service;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface VanishUserService extends Service {
    VanishUser save(@NonNull VanishUser user);

    void saveAll(@NonNull List<VanishUser> users);

    Optional<VanishUser> findById(@NonNull Long id);

    Optional<VanishUser> findByUsername(@NonNull String username);

    List<VanishUser> findAll();

    VanishUser getUser(@NonNull String username);

    void hidePlayer(@NonNull Player player);

    void showPlayer(@NonNull Player player);
}
